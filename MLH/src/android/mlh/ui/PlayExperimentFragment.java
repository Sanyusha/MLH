package android.mlh.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.mlh.aidl.Experiment;
import android.mlh.bl.tasks.TaskManager;
import android.mlh.constants.UIConstatns;
import android.mlh.logger.Logger;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.example.mlh.R;

/**
 * Represents the results of an experiment.
 * The layout and the functionality of this fragment are created by MLH main app
 * and not by the plugin.
 */
public class PlayExperimentFragment extends DialogFragment {
	private final static String LOG_TAG = UIConstatns.LOG_PREFIX + "PlayExperimentFragment";

	private TextSwitcher mStepDescSwitcher, mStepTimeSwitcher;

	at.markushi.ui.CircleButton btnNext;

	// to keep current index of step
	int currentStep; 

	private static PlayExperimentFragment f;

	private ArrayList<HashMap<String, String>> m_Steps;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.log(LOG_TAG, Logger.INFO_PRIORITY, "Fragment started");
		
		// Set title for this dialog
        getDialog().setTitle(getString(R.string.steps));
        
		View v = inflater.inflate(R.layout.fragment_play_experiment, container, false);

		// get The references 
		btnNext=(at.markushi.ui.CircleButton)v.findViewById(R.id.buttonNext);

		m_Steps = TaskManager.getInstance().getCurrentExperiment().getSteps();

		final Context currContext = getActivity();

		setSwitchers(v, currContext);

		showCurrentStep(currContext);

		// Declare the in and out animations and initialize them  
		Animation in = AnimationUtils.loadAnimation(currContext,android.R.anim.slide_in_left);
		//Animation out = AnimationUtils.loadAnimation(currContext,android.R.anim.slide_out_right);

		// set the animation type of textSwitcher
		mStepDescSwitcher.setInAnimation(in);
		//mSwitcher.setOutAnimation(out);

		setListeners(currContext);

		return v;
	}

	private void setSwitchers(View v, final Context currContext) {
		mStepDescSwitcher = (TextSwitcher) v.findViewById(R.id.stepDescSwitcher);
		mStepTimeSwitcher = (TextSwitcher) v.findViewById(R.id.stepTimeSwitcher);

		// Set the ViewFactory of the TextSwitcher that will create TextView object when asked
		mStepDescSwitcher.setFactory(new ViewFactory() {

			public View makeView() {
				// TODO Auto-generated method stub
				// create new textView and set the properties like color, size etc
				TextView myText = new TextView(currContext);
				myText.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
				myText.setTextAppearance(currContext, R.style.TitleFont);
				return myText;
			}
		});

		// Set the ViewFactory of the TextSwitcher that will create TextView object when asked
		mStepTimeSwitcher.setFactory(new ViewFactory() {

			public View makeView() {
				// TODO Auto-generated method stub
				// create new textView and set the properties like color, size etc
				TextView myText = new TextView(currContext);
				myText.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
				myText.setTextAppearance(currContext, R.style.ListItemFont);
				return myText;
			}
		});
	}

	private void setListeners(final Context currContext) {
		// ClickListener for NEXT button
		// When clicked on Button TextSwitcher will switch between texts 
		// The current Text will go OUT and next text will come in with specified animation
		btnNext.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				currentStep++;

				showCurrentStep(currContext);
			}
		});
	}

	private void showCurrentStep(final Context currContext) {
		if (m_Steps == null || m_Steps.size() == 0) {
			mStepDescSwitcher.setText(currContext.getString(R.string.no_steps));
			return;
		}

		// If index reaches maximum reset it
		if (currentStep == m_Steps.size())
			currentStep = 0;

		String stepDesc = "", stepTime = "";

		if (m_Steps.get(currentStep).get(Experiment.STEP_DESCRIPTION) != null)
			stepDesc += m_Steps.get(currentStep).get(Experiment.STEP_DESCRIPTION);

		if (m_Steps.get(currentStep).get(Experiment.STEP_TIME) != null)
			stepTime += m_Steps.get(currentStep).get(Experiment.STEP_TIME);

		mStepDescSwitcher.setText(stepDesc);
		mStepTimeSwitcher.setText(stepTime);
	}

	public static PlayExperimentFragment newInstance() {
		if (f == null) {
			f = new PlayExperimentFragment();
		}

		// Start with the first step when the fragment is created
		f.currentStep = 0;

		return f;
	}
}