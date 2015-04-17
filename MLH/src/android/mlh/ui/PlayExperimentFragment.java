package android.mlh.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.mlh.aidl.Experiment;
import android.mlh.bl.tasks.TaskManager;
import android.mlh.constants.UIConstatns;
import android.mlh.logger.Logger;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	private final static int STEP_IMG_MARGIN_PX = 40;
	
	private TextSwitcher mStepDescSwitcher, mStepTimeSwitcher;

	at.markushi.ui.CircleButton btnNext;

	// to keep current index of step
	int currentStep; 

	private View mView;

	private Context mContext;

	private static PlayExperimentFragment f;

	private ArrayList<HashMap<String, String>> m_Steps;

	private ArrayList<ImageSwitcher> imgSteps = new ArrayList<ImageSwitcher>();
	
	private CountDownTimer cdt = null;
	
	private TextView txtMinutes;
	
	private Experiment m_CurrExperiment;
	
	public static PlayExperimentFragment newInstance() {
		if (f == null) {
			f = new PlayExperimentFragment();
		}

		// Start with the first step when the fragment is created
		f.currentStep = 0;
		f.imgSteps = new ArrayList<ImageSwitcher>();
		f.cdt = null;
		f.m_Steps = null;
		
		return f;
	}
	
	public static PlayExperimentFragment newInstance(ArrayList<HashMap<String, String>> a_Steps) {
		f = newInstance();
		
		f.m_Steps = a_Steps;
		
		return f;
	}
	
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		
		if (cdt != null) cdt.cancel();
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		// Request a window without the title
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		return dialog;
	}

	public void onResume() {
		ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
		params.width = LayoutParams.MATCH_PARENT;
		//params.height = LayoutParams.MATCH_PARENT;
		params.height = (int) (UIUtils.getDisplayHeight(getActivity()) / 2);
		getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

		super.onResume();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.log(LOG_TAG, Logger.INFO_PRIORITY, "Fragment started");

		// Set title for this dialog
		//getDialog().setTitle(getString(R.string.steps));

		mView = inflater.inflate(R.layout.fragment_play_experiment, container, false);

		// get The references 
		btnNext=(at.markushi.ui.CircleButton) mView.findViewById(R.id.buttonNext);
		
		txtMinutes = (TextView) mView.findViewById(R.id.textView3);
		
		m_CurrExperiment = TaskManager.getInstance().getCurrentExperiment();
		
		if (m_Steps == null) {
			if (m_CurrExperiment != null) {
				m_Steps = TaskManager.getInstance().getCurrentExperiment().getSteps();
			}
		}
		
		final Context currContext = getActivity();

		mContext = getActivity();

		setSwitchers(currContext);

		returnToFirstStep();

		// Declare the in and out animations and initialize them  
		Animation in = AnimationUtils.loadAnimation(currContext,android.R.anim.slide_in_left);
		//Animation out = AnimationUtils.loadAnimation(currContext,android.R.anim.slide_out_right);

		// set the animation type of textSwitcher
		mStepDescSwitcher.setInAnimation(in);
		//mSwitcher.setOutAnimation(out);

		setListeners(currContext);

		return mView;
	}

	private void setStepsSequence() {
		LinearLayout linearLayout = (LinearLayout) mView.findViewById(R.id.stepsLL);

		for (int i = 0; i < m_Steps.size(); i++) {
			ImageSwitcher imgStep = new ImageSwitcher(mContext);
			imgSteps.add(imgStep);

			linearLayout.addView(imgStep);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(150, 100);
			lp.setMargins(0, 0, STEP_IMG_MARGIN_PX, 0);
			imgStep.setLayoutParams(lp);
			
			imgStep.setFactory(new ViewFactory() {
				public View makeView() {
					ImageView myView = new ImageView(mContext);
					myView.setImageResource(R.drawable.step);
					
					return myView;
				}
			});

			Animation in = AnimationUtils.loadAnimation(mContext,
					android.R.anim.fade_in);
			//imgStep.setInAnimation(in);
		}
	}

	private void returnToFirstStep() {
		currentStep = 0;

		if (imgSteps.size() == 0) {
			setStepsSequence();
		} else {
			for (int i = 0; i < m_Steps.size(); i++) {
				imgSteps.get(i).setImageResource(R.drawable.step);
			}
		}

		showCurrentStep();
	}

	private void setSwitchers(final Context currContext) {
		mStepDescSwitcher = (TextSwitcher) mView.findViewById(R.id.stepDescSwitcher);
		mStepTimeSwitcher = (TextSwitcher) mView.findViewById(R.id.stepTimeSwitcher);

		// Set the ViewFactory of the TextSwitcher that will create TextView object when asked
		mStepDescSwitcher.setFactory(new ViewFactory() {

			public View makeView() {
				// TODO Auto-generated method stub
				// create new textView and set the properties like color, size etc
				TextView myText = new TextView(currContext);
				myText.setGravity(Gravity.TOP | Gravity.LEFT);
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
				myText.setGravity(Gravity.TOP | Gravity.LEFT);
				//myText.setTypeface(null, Typeface.BOLD);
				myText.setTextColor(getResources().getColor(R.color.blue));
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

				// If index reaches maximum reset it
				if (currentStep >= m_Steps.size()) {
					// meanwhile return to the beginning
					returnToFirstStep();
					return;
				}

				showCurrentStep();
			}
		});
	}

	/**
	 * Displays the current step data on the screen.
	 */
	private void showCurrentStep() {
		txtMinutes.setVisibility(View.INVISIBLE);
		
		if (m_Steps == null || m_Steps.size() == 0) {
			mStepDescSwitcher.setText(mContext.getString(R.string.no_steps));
			btnNext.setVisibility(View.INVISIBLE);
			return;
		}
		
		btnNext.setVisibility(View.VISIBLE);
		
		String stepDesc;

		if (m_Steps.get(currentStep).get(Experiment.STEP_DESCRIPTION) != null &&
				m_Steps.get(currentStep).get(Experiment.STEP_DESCRIPTION).length() != 0) {
			stepDesc = m_Steps.get(currentStep).get(Experiment.STEP_DESCRIPTION);
		} else {
			stepDesc = getString(R.string.no_description);
		}
		
		mStepDescSwitcher.setText(stepDesc);
		
		mStepTimeSwitcher.setText("");
		
		// Run a countdown timer
		if (m_Steps.get(currentStep).get(Experiment.STEP_TIME) != null) {
			try {
				long stepTime = (long) (Float.parseFloat(m_Steps.get(currentStep).get(Experiment.STEP_TIME)) * 60 * 1000);

				if (stepTime > 0) {
					btnNext.setVisibility(View.INVISIBLE);
					txtMinutes.setVisibility(View.VISIBLE);
					
					if (cdt != null) cdt.cancel();
					
					cdt = new CountDownTimer(stepTime, 5 * 1000) {

						public void onTick(long millisUntilFinished) {
							float seconds = millisUntilFinished / 1000;
							float minutes = seconds / 60;
							
							mStepTimeSwitcher.setText("" + UIUtils.round(minutes, 2));
						}

						public void onFinish() {
							mStepTimeSwitcher.setText(getString(R.string.timeout));
							btnNext.setVisibility(View.VISIBLE);
							txtMinutes.setVisibility(View.INVISIBLE);
						}
						
					};
					
					cdt.start();
				}
			} catch (NumberFormatException e) {
				Logger.log(LOG_TAG, Logger.DEBUG_PRIORITY, "Can't start a countdown timer. Step time is '" + 
						m_Steps.get(currentStep).get(Experiment.STEP_TIME) + "'");
			}
		}

		imgSteps.get(currentStep).setImageResource(R.drawable.step_current);

		if (currentStep > 0) {
			imgSteps.get(currentStep - 1).setImageResource(R.drawable.step_done);
		}
	}
}