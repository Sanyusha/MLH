package android.mlh.ui;

import java.io.IOException;

import android.mlh.aidl.Experiment;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mlh.R;

public class ExperimentActivity extends FragmentActivity{
	private Experiment experiment;
	private int currExperiment;
	private MyPagerAdapter pageAdapter;
	
	private final static String LOG_D = "ExperimentListActivity";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_experiment);

		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		TextView txtTaskName = (TextView) findViewById(R.id.txtTaskName);

		if (PluginManager.getInstance().getCurrentPlugin() == null) {
			Log.d(LOG_D, "current plugin not selected");
			txtTitle.setText("current plugin not selected");
			return;
		}

		try {
			txtTitle.setText(PluginManager.getInstance().getCurrentPlugin().getPluginType());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		txtTaskName.setText(TaskManager.getInstance().getCurrentTask().getName());
		
		setSaveButtonListener();
		
		setCancelButtonListener();

		currExperiment = TaskManager.getInstance().getCurrentTask().getCurrentExperiment();
		
		Bundle state = new Bundle();
		
		if (currExperiment == Task.CURRENT_EXPERIMENT_NOT_DEFINED) {
			experiment = new Experiment();
		} else {
			experiment = TaskManager.getInstance().getCurrentTask().getExperiments().get(currExperiment);
			
			try {
				state = PluginManager.getInstance().getCurrentPlugin().getState(experiment);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
		pageAdapter = new MyPagerAdapter(getSupportFragmentManager(), state);
		pager.setAdapter(pageAdapter);
		
		//captureState();

		//TaskManager.getInstance().getCurrentTask().addExperiment(experiment);
	}

	private class MyPagerAdapter extends FragmentPagerAdapter {
		private Fragment mCurrentFragment;
		private Bundle mInitState;
		
        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }
    
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }
        
		public MyPagerAdapter(FragmentManager fm, Bundle aInitState) {
			super(fm);
			
			mInitState = aInitState;
		}

		@Override
		public Fragment getItem(int pos) {
			switch(pos) {

			case 0: return ExperimentParamsFragment.newInstance("android.mlh.cooking", mInitState);
			case 1: return ExperimentResultsFragment.newInstance("SecondFragment, Instance 1");

			default: return ExperimentResultsFragment.newInstance("FirstFragment, Instance 1");
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
					PluginManager.getInstance().getCurrentPlugin().setExperiment(experiment);
					Log.d("ExperimentActivity", "experiment set");
					Bundle currState = ((ExperimentParamsFragment) pageAdapter.getCurrentFragment()).captureState();
					
					experiment = PluginManager.getInstance().getCurrentPlugin().getExperiment(currState);
					
					if (experiment == null) {
						Log.d("ExperimentActivity", "getExperiment() returned null");
					} else {
						if (currExperiment == Task.CURRENT_EXPERIMENT_NOT_DEFINED) {
							TaskManager.getInstance().getCurrentTask().addExperiment(experiment);
							currExperiment = TaskManager.getInstance().getCurrentTask().getExperiments().size() - 1;

							TaskManager.getInstance().getCurrentTask().setCurrentExperiment(currExperiment);
						} else {
							TaskManager.getInstance().getCurrentTask().getExperiments().set(currExperiment, experiment);
						}

						FileManager.getInstance(getApplicationContext()).saveTask(TaskManager.getInstance().getCurrentTask());

						Toast.makeText(getApplicationContext(), getString(R.string.experiment_saved), Toast.LENGTH_LONG).show();
						
						finish();
					}

				} catch (RemoteException e) {
					Log.d("ExperimentActivity", e.getMessage());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Log.d("ExperimentActivity", "Experiment added: " + experiment.toString());
			}
			
		});
		
	}

	/**
	 * The current behavior of the cancel button is identical to Android back button
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
