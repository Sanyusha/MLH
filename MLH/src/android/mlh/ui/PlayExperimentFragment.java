package android.mlh.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.mlh.aidl.Experiment;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
	
	private Experiment m_CurrExperiment;
	
	private TextSwitcher mSwitcher;
	
	at.markushi.ui.CircleButton btnNext;
    
    // Array of String to Show In TextSwitcher 
    String textToShow[]={"Main HeadLine","Your Message","New In Technology","New Articles","Business News","What IS New"};
    int messageCount=textToShow.length;
    // to keep current Index of text
    int currentIndex=-1; 
    
    private static PlayExperimentFragment f;
    
    private ArrayList<HashMap<String, String>> m_Steps;
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_play_experiment, container, false);
		
		// get The references 
        btnNext=(at.markushi.ui.CircleButton)v.findViewById(R.id.buttonNext);
        mSwitcher = (TextSwitcher) v.findViewById(R.id.textSwitcher);
        
        
        //final Context currContext = container.getContext();
        final Context currContext = getActivity();
        
        // Set the ViewFactory of the TextSwitcher that will create TextView object when asked
        mSwitcher.setFactory(new ViewFactory() {
            
            public View makeView() {
                // TODO Auto-generated method stub
                // create new textView and set the properties like color, size etc
                TextView myText = new TextView(currContext);
                myText.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                myText.setTextSize(36);
                myText.setTextColor(getResources().getColor(R.color.FireBrick));
                return myText;
            }
        });

        // Declare the in and out animations and initialize them  
        Animation in = AnimationUtils.loadAnimation(currContext,android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(currContext,android.R.anim.slide_out_right);
        
        // set the animation type of textSwitcher
        mSwitcher.setInAnimation(in);
        //mSwitcher.setOutAnimation(out);
        
        // ClickListener for NEXT button
        // When clicked on Button TextSwitcher will switch between texts 
        // The current Text will go OUT and next text will come in with specified animation
        btnNext.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {
                // TODO Auto-generated method stub
                currentIndex++;
                // If index reaches maximum reset it
                if(currentIndex==messageCount)
                    currentIndex=0;
                mSwitcher.setText(textToShow[currentIndex]);
            }
        });


		return v;
	}

	public static PlayExperimentFragment newInstance() {
		if (f == null) {
			f = new PlayExperimentFragment();
		}

		return f;
	}
}