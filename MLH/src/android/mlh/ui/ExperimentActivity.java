package android.mlh.ui;

import java.io.IOException;
import java.util.HashMap;

import android.mlh.aidl.Experiment;
import android.mlh.aidl.IMLHPlugin;
import android.mlh.bl.ScoreCalculation;
import android.mlh.bl.files.FileManager;
import android.mlh.bl.plugins.PluginManager;
import android.mlh.bl.tasks.Task;
import android.mlh.bl.tasks.TaskManager;
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

import com.example.mlh.R;

public class ExperimentActivity extends FragmentActivity {
	/**
	 * Current experiment that we are working on in the activity
	 */
	private int m_iCurrExperiment;
	private Experiment m_CurrExperiment;

	private Task m_CurrTask;

	private IMLHPlugin m_CurrPlugin;

	private MyPagerAdapter pageAdapter;

	private final static String LOG_D = "ExperimentActivity";
	
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
		
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams. SOFT_INPUT_STATE_ALWAYS_HIDDEN );
		
		m_CurrTask = TaskManager.getInstance().getCurrentTask();

		setActivityTitle();
		
		setTaskName();
		
		setSaveButtonListener();

		setCancelButtonListener();

		setExperiment();
	}

	private void setActivityTitle() {
		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);

		try {
			txtTitle.setText(m_CurrPlugin.getPluginType());
			Log.d(LOG_D, "setActivityTitle: " + m_CurrPlugin.getPluginType());
		} catch (RemoteException e) {
			Log.e(LOG_D, "setActivityTitle: " + getString(R.string.err_plugin_connection) + ": " + e.getMessage());
		}
	}
	
	private void setTaskName() {
		TextView txtTaskName = (TextView) findViewById(R.id.txtTaskName);

		txtTaskName.setText(m_CurrTask.getName());
		Log.d(LOG_D, "setTaskTitle: " + m_CurrTask.getName());
	}
	
	/**
	 * Sets the current experiment and displays it on the screen.
	 */
	private void setExperiment() {
		m_iCurrExperiment = m_CurrTask.getCurrentExperiment();

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

		private ExperimentParamsFragment m_PF;
		private ExperimentResultsFragment m_RF;

		public Fragment getCurrentFragment() {
			return mCurrentFragment;
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
				m_RF = ExperimentResultsFragment.newInstance(mResults);
				return m_RF;
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return 2;
		}
	}

	private void setSaveButtonListener() {
		Button btn = (Button) findViewById(R.id.btnSave);

		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				try {

					updateExperimentParams();

					updateExperimentResults();

					updateResultScore();

					updateCurrentTask();

					Toast.makeText(getApplicationContext(),
							getString(R.string.experiment_saved),
							Toast.LENGTH_LONG).show();

					Log.d(LOG_D, "Experiment added: " + m_CurrExperiment.toString());

				} catch (RemoteException e) {
					Log.e(LOG_D, "save experiment: " + getString(R.string.err_plugin_connection) + ": " + e.getMessage());
				} catch (IOException e) {
					Log.e(LOG_D, "save experiment: " + ": " + e.getMessage());
				}
			}
		});
	}

	private void updateCurrentTask() throws IOException {
		if (m_iCurrExperiment == Task.CURRENT_EXPERIMENT_NOT_DEFINED) {
			m_CurrTask.addExperiment(m_CurrExperiment);

			m_iCurrExperiment = m_CurrTask.getExperiments().size() - 1;

			m_CurrTask.setCurrentExperiment(m_iCurrExperiment);

			FileManager.getInstance(getApplicationContext()).saveTask(m_CurrTask);
		}
	}

	private void updateExperimentParams() throws RemoteException {
		Bundle currState = pageAdapter.m_PF.captureParametersState();
		Log.d(LOG_D, "Parameters state captured: " + currState);
		Log.d(LOG_D, m_CurrPlugin.updateExperimentParams(currState, m_CurrExperiment).getParameters().toString());
		
		Experiment updatedExperiment = m_CurrPlugin.updateExperimentParams(currState, m_CurrExperiment);
		
		m_CurrExperiment.setParameters(updatedExperiment.getParameters());
	}

	private void updateExperimentResults() throws RemoteException {
		HashMap<String, String> results = pageAdapter.m_RF.captureResultsState();
		m_CurrExperiment.setResults(results);
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
