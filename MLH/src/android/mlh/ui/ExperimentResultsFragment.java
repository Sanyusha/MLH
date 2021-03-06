package android.mlh.ui;

import java.util.HashMap;

import android.content.Context;
import android.mlh.aidl.Experiment;
import android.mlh.aidl.IMLHPlugin;
import android.mlh.bl.plugins.PluginManager;
import android.mlh.bl.tasks.TaskManager;
import android.mlh.constants.UIConstatns;
import android.mlh.logger.Logger;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.mlh.R;

/**
 * Represents the results of an experiment.
 * The layout and the functionality of this fragment are created by MLH main app
 * and not by the plugin.
 */
public class ExperimentResultsFragment extends ListFragment {
	private final static String LOG_TAG = UIConstatns.LOG_PREFIX + "ExperimentResultsFragment";
	
	private final static String NO_SCORE = "-1";
	
	private static ExperimentResultsFragment f;
	
	private ListAdapter m_ListAdapter;
	
	private final int MAX_SCORE = 100;
	private final int DEFAULT_SCORE = 80;
	
	private IMLHPlugin m_CurrPlugin;
	private Experiment m_CurrExperiment;
	
	private HashMap<String, String> m_Results;
	
	public static ExperimentResultsFragment newInstance() {
		if (f == null) {
			f = new ExperimentResultsFragment();
		}

		return f;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.log(LOG_TAG, Logger.INFO_PRIORITY, "Fragment started");
		
		View v = inflater.inflate(R.layout.fragment_experiment_results, container, false);
		
		m_CurrPlugin = PluginManager.getInstance().getCurrentPlugin();
		m_CurrExperiment = TaskManager.getInstance().getCurrentExperiment();
		
		m_Results = new HashMap<String, String>();

		// get results from experiment
		if (m_CurrExperiment != null) {
			m_Results = m_CurrExperiment.getResults();
		}
		
		if (m_Results.isEmpty()) { // experiment just created, fill the results
			String[] resultNames;
			try {
				resultNames = m_CurrPlugin.getResultNames();

				for (String string : resultNames) {
					m_Results.put(string, NO_SCORE); // put zero as default, will be changed
				}
			} catch (RemoteException e) {
				Logger.log(LOG_TAG, Logger.WARN_PRIORITY, 
						"setExperiment: " + getString(R.string.err_plugin_connection) + ": " + e.getMessage());
			}
		}
		
		Logger.log(LOG_TAG, Logger.DEBUG_PRIORITY, "onCreateView: experiment results are: " + m_Results);
		
		// You can't simply cast an Object[] array to a String[] array. 
		// You should instead use the generic version of toArray, which should work better.
		m_ListAdapter = new ListAdapter(v.getContext(), m_Results.keySet()
				.toArray(new String[m_Results.size()]));

		setListAdapter(m_ListAdapter);

		return v;
	}

	/**
	 * Returns the results state that is displayed on the screen.
	 * @return
	 * the hashmap of results
	 */
	public HashMap<String, String> captureResultsState() {
		Log.d(LOG_TAG, "Capturing results state from fragment.");

		return m_ListAdapter.m_NewResults;
	}

	public class ListAdapter extends BaseAdapter {

		private Context m_Context;
		private String[] m_Items;

		private HashMap<String, String> m_NewResults;
		
		public ListAdapter(Context context, String[] items) {
			this.m_Context = context;
			this.m_Items = items;

			this.m_NewResults = new HashMap<String, String>();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			PlanetHolder holder = new PlanetHolder();

			// First let's verify the convertView is not null
			if (convertView == null) {
				// This a new view we inflate the new layout
				LayoutInflater inflater = (LayoutInflater) m_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				v = inflater.inflate(R.layout.results_list_item, null);

				// Now we can fill the layout with the right values
				final TextView tv = (TextView) v.findViewById(R.id.text10);
				final RatingBar rb = (RatingBar) v.findViewById(R.id.ratingBar1);
				
				// In the case that there is no score for a result
				// put the default score.
				if (m_Results.get(m_Items[position]).equals(NO_SCORE)) {
					m_NewResults.put(m_Items[position], DEFAULT_SCORE + "");
				} else {
					m_NewResults.put(m_Items[position], m_Results.get(m_Items[position]));
				}
				
				Log.d(LOG_TAG, "The current result for " + m_Items[position] + " is " + m_NewResults.get(m_Items[position]));
				
				rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
					public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
						int grade = (int) (rating * (MAX_SCORE / rb.getNumStars()));
						m_NewResults.put(tv.getText().toString(), grade + "");
					}
				}
				);
				
				holder.tv = tv;
				holder.rb = rb;

				v.setTag(holder);
			}
			else 
				holder = (PlanetHolder) v.getTag();

			holder.tv.setText(m_Items[position]);
			holder.rb.setRating(Float.parseFloat(m_NewResults.get(holder.tv.getText()))
					* holder.rb.getNumStars() / MAX_SCORE);
			
			
			
			return v;
		}

		@Override
		public int getCount() {
			return m_Items.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	private static class PlanetHolder {
		public TextView tv;
		public RatingBar rb;
	}
}