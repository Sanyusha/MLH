package android.mlh.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.mlh.aidl.Experiment;
import android.mlh.bl.files.FileManager;
import android.mlh.bl.tasks.Task;
import android.mlh.bl.tasks.TaskManager;
import android.mlh.constants.UIConstatns;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.mlh.R;

public class ExperimentListActivity extends ListActivity {
	private SimpleAdapter itemAdapter;
	private ArrayList<HashMap<String,String>> savedExperiments = new ArrayList<HashMap<String,String>>();
	
	private String LOG_D = "ExperimentListActivity";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_experiment_list);

		setListAdapter();

		setListeners();
	}
	
	@Override
	public void onRestart() { 
	    super.onRestart();
	    
	    fillSavedExperiments();
	    
	    itemAdapter.notifyDataSetChanged();
	}
	
	private void setListeners() {
		Button btnNewExperiment = (Button) findViewById(R.id.btn_new_experiment);
		btnNewExperiment.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				TaskManager.getInstance().getCurrentTask().setCurrentExperiment(Task.CURRENT_EXPERIMENT_NOT_DEFINED);
				
				Intent intent = new Intent(ExperimentListActivity.this, ExperimentActivity.class);

				startActivity(intent);
			}
		});
	}
	
	private void fillSavedExperiments() {
		savedExperiments.clear();

		for (Experiment el: TaskManager.getInstance().getCurrentTask().getExperiments()) {
			HashMap<String,String> item = new HashMap<String,String>();
			item.put(UIConstatns.ITEM_KEY, el.toString());
			item.put(UIConstatns.ITEM_VALUE, "");
			
			savedExperiments.add(item);
		}
	}
	
	private void setListAdapter() {
		fillSavedExperiments();
		
		itemAdapter = new SimpleAdapter(this, savedExperiments, R.layout.list_item,
				new String[] {UIConstatns.ITEM_KEY, UIConstatns.ITEM_VALUE},
				new int[] { R.id.text1, R.id.text2}
				);

		setListAdapter(itemAdapter);
	}
	
	protected void onListItemClick (ListView l, View v, int position, long id) {
		TaskManager.getInstance().getCurrentTask().setCurrentExperiment(position);
		
		Log.d(LOG_D, "Current task has " + 
				TaskManager.getInstance().getCurrentTask().getExperiments().size() +
				" experiments, selected: " + position);
		
		Intent intent = new Intent(this, ExperimentActivity.class);

		startActivity(intent);
	}
}
