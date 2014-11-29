package android.mlh.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mlh.R;

/**
 * Represents results of an experiment.
 * The layout and the functionality of this fragment are created by MLH main app
 * and not by the plugin.
 */
public class ExperimentResultsFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dummy, container, false);

        TextView tv = (TextView) v.findViewById(R.id.textView1);
        tv.setText("Results");

        return v;
    }

    public static ExperimentResultsFragment newInstance(String text) {

        ExperimentResultsFragment f = new ExperimentResultsFragment();

        return f;
    }
}