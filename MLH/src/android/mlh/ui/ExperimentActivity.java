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
	private final static String LOG_TAG = UIConstatns.LOG_PREFIX + "ExperimentActivity";

	/**
	 * Current experiment that we are working on in the activity
	 */
	private int m_iCurrExperiment;
	private IMLHPlugin m_CurrPlugin;
	private Experiment m_CurrExperiment;

	private Task m_CurrTask;

	private MyPagerAdapter pageAdapter;

	private FragmentManager manager;

	protected void onCreate(Bundle savedInstanceState) {
		Logger.log(LOG_TAG, Logger.INFO_PRIORITY, "Activity started");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_experiment);

		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );

		setCurrentPlugin();

		setSaveButtonListener();

		setCancelButtonListener();

		setExperiment();
		
		setPager();
	}

	private void setCurrentPlugin() {
		m_CurrPlugin = PluginManager.getInstance().getCurrentPlugin();

		// Current plugin existence validation.
		if (m_CurrPlugin == null) {
			Toast.makeText(this, getString(R.string.err_current_plugin), Toast.LENGTH_LONG).show();
			finish();
		}

		m_CurrTask = TaskManager.getInstance().getCurrentTask();
	}

	/**
	 * Sets the current experiment and displays it on the screen.
	 */
	private void setExperiment() {
		m_iCurrExperiment = m_CurrTask.getCurrentExperimentIndex();

		Logger.log(LOG_TAG, Logger.DEBUG_PRIORITY, "Current experiment: " + m_iCurrExperiment);

		if (m_iCurrExperiment == Task.CURRENT_EXPERIMENT_NOT_DEFINED) {
			m_CurrExperiment = new Experiment();
		} else {
			m_CurrExperiment = m_CurrTask.getExperiments().get(m_iCurrExperiment);
		}
	}

	private void setPager() {
		manager = getSupportFragmentManager();

		ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
		pageAdapter = new MyPagerAdapter(manager);
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
		if (pageAdapter.m_RF == null) {
			Logger.log(LOG_TAG, Logger.DEBUG_PRIORITY, "Results fragment is null");
			return;
		}

		HashMap<String, String> results = pageAdapter.m_RF
				.captureResultsState();
		HashMap<String, String> resultPriorities = null;

		Log.d(LOG_TAG, "Update result score according to the following results: "
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

		private ExperimentParamsFragment m_PF = null;
		private ExperimentResultsFragment m_RF = null;
		private StepsFragment m_SF = null;

		private boolean bShowSteps;

		public Fragment getCurrentFragment() {
			return mCurrentFragment;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.experiment_parameters);
			case 1:
				if (bShowSteps)
					return getString(R.string.steps);
				else
					return getString(R.string.experiment_rate);
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

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);

			try {
				bShowSteps = m_CurrPlugin.hasSteps();
				Logger.log(LOG_TAG, Logger.DEBUG_PRIORITY, 
						"Current plugin has steps: " + bShowSteps);
			} catch (RemoteException e) {
				Logger.log(LOG_TAG, Logger.WARN_PRIORITY, 
						getString(R.string.err_plugin_connection));
				bShowSteps = false;
			}
		}

		@Override
		public Fragment getItem(int pos) {
			switch (pos) {

			case 0:
				Logger.log(LOG_TAG, Logger.DEBUG_PRIORITY, pos + "");
				m_PF = ExperimentParamsFragment.newInstance();
				return m_PF;
			case 1:
				Logger.log(LOG_TAG, Logger.DEBUG_PRIORITY, pos + "");
				if (bShowSteps) {
					m_SF = StepsFragment.newInstance();
					return m_SF;
				} else {
					m_RF = ExperimentResultsFragment.newInstance();
					return m_RF;
				}
			case 2:
				Logger.log(LOG_TAG, Logger.DEBUG_PRIORITY, pos + "");
				m_RF = ExperimentResultsFragment.newInstance();
				return m_RF;

			default:
				return null;
			}
		}


		/* 
		 * Returns 3 if the plugin has steps otherwise 2.
		 */
		public int getCount() {
			return (bShowSteps ? 3 : 2);
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

					Logger.log(LOG_TAG, Logger.DEBUG_PRIORITY, "Experiment saved: " + m_CurrExperiment.toString());

					// exit the experiment activity
					finish();

				} catch (RemoteException e) {
					Log.e(LOG_TAG, "save experiment: " + getString(R.string.err_plugin_connection) + ": " + e + " " + e.getStackTrace());
				} catch (IOException e) {
					Log.e(LOG_TAG, "save experiment: " + ": " + e.getMessage());
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
			Logger.log(LOG_TAG, Logger.DEBUG_PRIORITY, "Parameters state captured: " + currState);

			Experiment updatedExperiment = m_CurrPlugin.updateExperimentParams(currState, m_CurrExperiment);

			m_CurrExperiment.setParameters(updatedExperiment.getParameters());
			Logger.log(LOG_TAG, Logger.DEBUG_PRIORITY, "Current experiment updated with parameters: " + m_CurrExperiment.getParameters());
		}
	}

	private void updateExperimentResults() {
		if (pageAdapter.m_RF != null) {
			HashMap<String, String> results = pageAdapter.m_RF.captureResultsState();
			m_CurrExperiment.setResults(results);
			Logger.log(LOG_TAG, Logger.DEBUG_PRIORITY, "Current experiment updated with results: " + m_CurrExperiment.getResults());
		}
	}

	private void updateExperimentSteps() {
		if (pageAdapter.m_SF != null) {
			m_CurrExperiment.setSteps(pageAdapter.m_SF.captureSteps());
			Logger.log(LOG_TAG, Logger.DEBUG_PRIORITY, "Current experiment updated with " + m_CurrExperiment.getStepsCount() + "steps");
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
