package android.mlh.ui;

import java.io.IOException;
import java.util.HashMap;

import com.example.mlh.R;
import android.mlh.aidl.Experiment;
import android.mlh.aidl.IMLHPlugin;
import android.mlh.bl.ScoreCalculation;
import android.mlh.bl.files.FileManager;
import android.mlh.bl.plugins.PluginManager;
import android.mlh.bl.tasks.Task;
import android.mlh.bl.tasks.TaskManager;
import android.mlh.constants.UIConstatns;
import android.mlh.logger.Logger;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ExperimentActivity extends FragmentActivity {
	/**
	 * Current experiment that we are working on in the activity
	 */
	private int m_iCurrExperiment;
	private Experiment m_CurrExperiment;

	private Task m_CurrTask;

	private IMLHPlugin m_CurrPlugin;

	private MyPagerAdapter pageAdapter;

	private final static String LOG_D = UIConstatns.LOG_PREFIX + "ExperimentActivity";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_experiment);

		Log.d(LOG_D, "Starting loading ExperimentActivity...");

		m_CurrPlugin = PluginManager.getInstance().getCurrentPlugin();

		// Current plugin existence validation.
		if (m_CurrPlugin == null) {
			Toast.makeText(this, getString(R.string.err_current_plugin), Toast.LENGTH_LONG).show();
			finish();
		}

		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );

		m_CurrTask = TaskManager.getInstance().getCurrentTask();

		setSaveButtonListener();

		setCancelButtonListener();

		setExperiment();
	}



	/**
	 * Sets the current experiment and displays it on the screen.
	 */
	private void setExperiment() {
		m_iCurrExperiment = m_CurrTask.getCurrentExperimentIndex();

		Bundle state = new Bundle();

		if (m_iCurrExperiment == Task.CURRENT_EXPERIMENT_NOT_DEFINED) {
			m_CurrExperiment = new Experiment();
		} else {
			m_CurrExperiment = m_CurrTask.getExperiments().get(m_iCurrExperiment);

			try {
				state = m_CurrPlugin.getState(m_CurrExperiment);

			} catch (RemoteException e) {
				Log.e(LOG_D, "setExperiment: " + getString(R.string.err_plugin_connection) + ": " + e.getMessage());
			}
		}

		HashMap<String, String> results = new HashMap<String, String>();

		// get results from experiment
		results = m_CurrExperiment.getResults();

		if (results.isEmpty()) { // experiment just created, fill the results
			String[] resultNames;
			try {
				resultNames = m_CurrPlugin.getResultNames();

				for (String string : resultNames) {
					results.put(string, "-1"); // put zero as default, will be changed
				}
			} catch (RemoteException e) {
				Log.e(LOG_D, "setExperiment: " + getString(R.string.err_plugin_connection) + ": " + e.getMessage());
			}
		}

		ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
		pageAdapter = new MyPagerAdapter(getSupportFragmentManager(), state,
				results);
		pager.setAdapter(pageAdapter);

		if (m_CurrExperiment.getResultScore() != null) {
			TextView tv = (TextView) findViewById(R.id.txtScore);
			tv.setText(m_CurrExperiment.getResultScore());
		}
	}

	/**
	 * Updates the result score.
	 */
	private void updateResultScore() {
		HashMap<String, String> results = pageAdapter.m_RF
				.captureResultsState();
		HashMap<String, String> resultPriorities = null;

		Log.d(LOG_D, "Update result score according to the following results: "
				+ results);

		TextView tv = (TextView) findViewById(R.id.txtScore);

		String resScore = ScoreCalculation.calculate(results, resultPriorities);

		tv.setText(resScore);

		m_CurrExperiment.setResultScore(resScore);
	}

	/**
	 * Manages the pager element in the activity.
	 * Creates 2 pages: one for the parameters and one for the results of the experiment.
	 */
	private class MyPagerAdapter extends FragmentPagerAdapter {
		private Fragment mCurrentFragment;
		private Bundle mInitState;
		private HashMap<String, String> mResults;

		private ExperimentParamsFragment m_PF = null;
		private ExperimentResultsFragment m_RF = null;
		private StepsFragment m_SF = null;

		public Fragment getCurrentFragment() {
			return mCurrentFragment;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.experiment_parameters);
			case 1:
				return getString(R.string.steps);
			case 2:
				return getString(R.string.experiment_rate);
			default:
				return null;
			}
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			if (getCurrentFragment() != object) {
				mCurrentFragment = ((Fragment) object);
			}
			super.setPrimaryItem(container, position, object);
		}

		public MyPagerAdapter(FragmentManager fm, Bundle aInitState,
				HashMap<String, String> results) {
			super(fm);

			mInitState = aInitState;
			mResults = results;
		}

		@Override
		public Fragment getItem(int pos) {
			switch (pos) {

			case 0:
				m_PF = ExperimentParamsFragment.newInstance(PluginManager
						.getInstance().getCurrentPluginName(), mInitState);
				return m_PF;
			case 1:
				m_SF = StepsFragment.newInstance();
				return m_SF;
			case 2:
				m_RF = ExperimentResultsFragment.newInstance(mResults);
				return m_RF;
			
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return 3;
		}
	}

	private void setSaveButtonListener() {
		Button btn = (Button) findViewById(R.id.btnSave);

		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				try {

					updateExperimentParams();

					updateExperimentResults();

					updateExperimentSteps();

					updateResultScore();

					updateCurrentTask();

					Toast.makeText(getApplicationContext(),
							getString(R.string.experiment_saved),
							Toast.LENGTH_LONG).show();

					Logger.log(LOG_D, Logger.DEBUG_PRIORITY, "Experiment saved: " + m_CurrExperiment.toString());

					// exit the experiment activity
					finish();

				} catch (RemoteException e) {
					Log.e(LOG_D, "save experiment: " + getString(R.string.err_plugin_connection) + ": " + e + " " + e.getStackTrace());
				} catch (IOException e) {
					Log.e(LOG_D, "save experiment: " + ": " + e.getMessage());
				}
			}
		});
	}

	/**
	 * Saves the current changes of the experiment in the current task that we are working with.
	 * If the experiment is new and was just created we add it to the task.
	 * @throws IOException
	 */
	private void updateCurrentTask() throws IOException {
		if (m_iCurrExperiment == Task.CURRENT_EXPERIMENT_NOT_DEFINED) {
			m_CurrTask.addExperiment(m_CurrExperiment);

			m_iCurrExperiment = m_CurrTask.getExperiments().size() - 1;

			m_CurrTask.setCurrentExperiment(m_iCurrExperiment);
		}

		// Now save the updated task on the file system.
		FileManager.getInstance(getApplicationContext()).saveTask(m_CurrTask);
	}

	private void updateExperimentParams() throws RemoteException {
		if (pageAdapter.m_PF != null) {
			Bundle currState = pageAdapter.m_PF.captureParametersState();
			Logger.log(LOG_D, Logger.DEBUG_PRIORITY, "Parameters state captured: " + currState);

			Experiment updatedExperiment = m_CurrPlugin.updateExperimentParams(currState, m_CurrExperiment);

			m_CurrExperiment.setParameters(updatedExperiment.getParameters());
			Logger.log(LOG_D, Logger.DEBUG_PRIORITY, "Current experiment updated with parameters: " + m_CurrExperiment.getParameters());
		}
	}

	private void updateExperimentResults() {
		if (pageAdapter.m_RF != null) {
			HashMap<String, String> results = pageAdapter.m_RF.captureResultsState();
			m_CurrExperiment.setResults(results);
			Logger.log(LOG_D, Logger.DEBUG_PRIORITY, "Current experiment updated with results: " + m_CurrExperiment.getResults());
		}
	}

	private void updateExperimentSteps() {
		if (pageAdapter.m_SF != null) {
			m_CurrExperiment.setSteps(pageAdapter.m_SF.captureSteps());
			Logger.log(LOG_D, Logger.DEBUG_PRIORITY, "Current experiment updated with " + m_CurrExperiment.getStepsCount() + "steps");
		}
	}

	/**
	 * The current behavior of the cancel button is identical to Android back
	 * button.
	 */
	private void setCancelButtonListener() {
		Button btn = (Button) findViewById(R.id.btnCancel);

		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

}
