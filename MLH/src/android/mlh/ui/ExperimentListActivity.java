package android.mlh.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.mlh.aidl.Experiment;
import android.mlh.aidl.IMLHPlugin;
import android.mlh.bl.files.FileManager;
import android.mlh.bl.plugins.PluginManager;
import android.mlh.bl.tasks.Task;
import android.mlh.bl.tasks.TaskManager;
import android.mlh.constants.UIConstatns;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import com.example.mlh.R;

public class ExperimentListActivity extends ListActivity {
	private SimpleAdapter itemAdapter;
	private ArrayList<HashMap<String,String>> savedExperiments = new ArrayList<HashMap<String,String>>();
	
	private String LOG_D = "ExperimentListActivity";
	
	private Task m_CurrTask;

	private IMLHPlugin m_CurrPlugin;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_experiment_list);
		
		m_CurrPlugin = PluginManager.getInstance().getCurrentPlugin();

		m_CurrTask = TaskManager.getInstance().getCurrentTask();
		
		setActivityTitle();

		setTaskName();
		
		setListAdapter();

		setListeners();
	}
	
	private void setActivityTitle() {
		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		
		txtTitle.setText(m_CurrTask.getName());
		Log.d(LOG_D, "setTaskTitle: " + m_CurrTask.getName());
	}

	private void setTaskName() {
		TextView txtTaskName = (TextView) findViewById(R.id.txtTaskName);

		try {
			txtTaskName.setText(m_CurrPlugin.getPluginType());
			Log.d(LOG_D, "setActivityTitle: " + m_CurrPlugin.getPluginType());
		} catch (RemoteException e) {
			Log.e(LOG_D, "setActivityTitle: " + getString(R.string.err_plugin_connection) + ": " + e.getMessage());
		}
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
		
		setOnListItemLongClick();
	}
	
	private void fillSavedExperiments() {
		savedExperiments.clear();

		for (Experiment el: TaskManager.getInstance().getCurrentTask().getExperiments()) {
			HashMap<String,String> item = new HashMap<String,String>();
			item.put(UIConstatns.ITEM_KEY, "Experiment: " + el.getResultScore());
			item.put(UIConstatns.ITEM_VALUE, el.getDate());
			
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
	
	
	private void setOnListItemLongClick() {
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final TextView tv = (TextView) view.findViewById(R.id.text1);
				final int pos = position;
				TaskManager.getInstance().getCurrentTask().setCurrentExperiment(position);

				// build a list dialog
				AlertDialog.Builder builderSingle = new AlertDialog.Builder(ExperimentListActivity.this);

				builderSingle.setTitle(tv.getText());

				final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
						ExperimentListActivity.this,
						android.R.layout.select_dialog_item);

				arrayAdapter.add(getString(R.string.edit));
				arrayAdapter.add(getString(R.string.delete));

				builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String strName = arrayAdapter.getItem(which);

						if (strName.equalsIgnoreCase(getString(R.string.delete))) {
							try {
								
								//From selected task delete selected experiment, set experiment selected to -1
								Task currentTask = TaskManager.getInstance().getCurrentTask();
								currentTask.deleteExperiment(currentTask.getCurrentExperiment());
								currentTask.setCurrentExperiment(Task.CURRENT_EXPERIMENT_NOT_DEFINED);
								FileManager.getInstance(getApplicationContext()).saveTask(currentTask);
								itemAdapter.notifyDataSetChanged();
								fillSavedExperiments();

								Log.d(LOG_D, "The task <" + tv.getText().toString() + "> removed");

							} catch (IOException e) {
								Log.d(LOG_D, e.getMessage());
							}
						}

						if (strName.equalsIgnoreCase(getString(R.string.edit))) {
							dialog.dismiss();


							
							
							Log.d(LOG_D, "Current task has " + 
									TaskManager.getInstance().getCurrentTask().getExperiments().size() +
									" experiments, selected: " + pos);
							
							Intent intent = new Intent(ExperimentListActivity.this, ExperimentActivity.class);

							startActivity(intent);
						
						}
					}
				});

				builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				builderSingle.show();

				return true;
			}
		});
	}
}
