package android.mlh.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.mlh.aidl.Experiment;
import android.mlh.bl.tasks.TaskManager;
import android.mlh.constants.UIConstatns;
import android.mlh.logger.Logger;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mlh.R;

/**
 * Represents the results of an experiment.
 * The layout and the functionality of this fragment are created by MLH main app
 * and not by the plugin.
 */
public class StepsFragment extends Fragment {

	at.markushi.ui.CircleButton btnNext;

	// to keep current step
	private int currentStep; 

	private static StepsFragment f;

	private ArrayList<HashMap<String, String>> m_Steps = new ArrayList<HashMap<String,String>>();

	private Experiment m_CurrExperiment;

	private View mView;

	private Button playButton, prevButton, nextButton;

	private final static String LOG_D = UIConstatns.LOG_PREFIX + "StepsFragment";
	
	private EditText edit1;
	private EditText edit2;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_steps, container, false);
		
		edit1 = (EditText) mView.findViewById(R.id.edit1);
		edit2 = (EditText) mView.findViewById(R.id.edit2);
		
		m_CurrExperiment = TaskManager.getInstance().getCurrentExperiment();

		if (m_CurrExperiment != null) {
			m_Steps = copySteps(m_CurrExperiment.getSteps());
		} 

		Logger.log(LOG_D, Logger.DEBUG_PRIORITY, 
				"Experiment has the following steps: " + printSteps(m_Steps));

		setButtons();

		populateStep();

		return mView;
	}

	private void setButtons() {
		playButton = (Button) mView.findViewById(R.id.btnPlay);

		// Capture button clicks
		playButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				PlayExperimentFragment dFragment = PlayExperimentFragment.newInstance();
				// Show DialogFragment
				dFragment.show(getFragmentManager(), "Dialog Fragment");
			}
		});

		nextButton = (Button) mView.findViewById(R.id.btnNext);

		nextButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (edit1.getText().toString().length() == 0 
						&& edit2.getText().toString().length() == 0) {
					Toast.makeText(mView.getContext(), getString(R.string.step_empty), Toast.LENGTH_LONG).show();
					return;
				}
				
				saveStep();
				currentStep++;
				populateStep();
			}
		});

		prevButton = (Button) mView.findViewById(R.id.btnPrev);

		prevButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				saveStep();
				currentStep--;
				populateStep();
			}
		});
	}

	public ArrayList<HashMap<String, String>> captureSteps() {
		return copySteps(m_Steps);
	}

	public static StepsFragment newInstance() {
		if (f == null) {
			f = new StepsFragment();
		}
		
		// Start with the first step when the fragment is created
		f.currentStep = 0;

		return f;
	}

	/**
	 * Populates the fields on the screen with the current step.
	 */
	private void populateStep() {
		if (currentStep == 0) {
			prevButton.setEnabled(false);
		} else {
			prevButton.setEnabled(true);
		}
		
		if (currentStep >= m_Steps.size()) {
			edit1.setText("");
			edit2.setText("");
			return;
		}

		HashMap<String, String> stepMap = m_Steps.get(currentStep);

		edit1.setText(stepMap.get(Experiment.STEP_DESCRIPTION));
		edit2.setText(stepMap.get(Experiment.STEP_TIME));
	}

	/**
	 * Performs a deep copy of the array list of steps.
	 */
	public static ArrayList<HashMap<String, String>> copySteps(ArrayList<HashMap<String, String>> steps) {
		ArrayList<HashMap<String, String>> retValue = new ArrayList<HashMap<String, String>>();

		if (steps != null) {
			for (int i = 0; i < steps.size(); i++) {
				retValue.add(new HashMap<String, String>());

				Iterator<Entry<String, String>> it = steps.get(i).entrySet().iterator();

				while (it.hasNext()) {
					Entry<String, String> entry = it.next();
					retValue.get(i).put(entry.getKey(), entry.getValue());
				}
			}
		}

		return retValue;
	}

	/**
	 * Saves the fields on the screen to the current step.
	 */
	private void saveStep() {
		// check if we are on the new step
		if (currentStep == m_Steps.size()) {
			m_Steps.add(new HashMap<String, String>());
			currentStep = m_Steps.size() - 1;
		}

		HashMap<String, String> stepMap = m_Steps.get(currentStep);

		EditText edit1 = (EditText) mView.findViewById(R.id.edit1);
		stepMap.put(Experiment.STEP_DESCRIPTION, edit1.getText().toString());

		EditText edit2 = (EditText) mView.findViewById(R.id.edit2);
		stepMap.put(Experiment.STEP_TIME, edit2.getText().toString());
		
		Logger.log(LOG_D, Logger.DEBUG_PRIORITY, 
				"Saving current step. The steps are " + printSteps(m_Steps));
	}

	/**
	 * For debug use. Returns a string representation of the steps array.
	 */
	private String printSteps(ArrayList<HashMap<String, String>> a_Steps) {
		String retValue = "";
		
		for (int i = 0; i < a_Steps.size(); i++) {
			retValue += a_Steps.get(i);
		}
		
		return retValue;
	}
}