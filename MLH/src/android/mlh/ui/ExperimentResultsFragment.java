package android.mlh.ui;

import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.mlh.R;

/**
 * Represents the results of an experiment.
 * The layout and the functionality of this fragment are created by MLH main app
 * and not by the plugin.
 */
public class ExperimentResultsFragment extends ListFragment {
	
	private static ExperimentResultsFragment f;
	private String[] mResults;
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_experiment_results, container, false);

        TextView tv = (TextView) v.findViewById(R.id.text1);
        tv.setText("Results results results");
        
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(v.getContext(), 
        		R.layout.results_list_item, R.id.text10, mResults);
        
        setListAdapter(listAdapter);
        
        return v;
    }

    public static ExperimentResultsFragment newInstance(String[] aResults) {
    	if (f == null) {
			f = new ExperimentResultsFragment();
		}
		
		f.mResults = aResults;
		
		return f;
    }
}