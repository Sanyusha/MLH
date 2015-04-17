package android.mlh.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.mlh.aidl.Experiment;
import android.mlh.aidl.IMLHPlugin;
import android.mlh.bl.files.FileManager;
import android.mlh.bl.plugins.PluginManager;
import android.mlh.bl.tasks.Task;
import android.mlh.bl.tasks.TaskManager;
import android.mlh.constants.UIConstatns;
import android.mlh.logger.Logger;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.example.mlh.R;

public class ExperimentListActivity extends Activity {
	private SimpleAdapter itemAdapter;
	private ArrayList<HashMap<String,String>> savedExperiments = new ArrayList<HashMap<String,String>>();

	private String LOG_TAG = UIConstatns.LOG_PREFIX + "ExperimentListActivity";

	private Task m_CurrTask;

	private IMLHPlugin m_CurrPlugin;

	// The main list of experiments.
	private SwipeMenuListView listView;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_experiment_list);

		Logger.log(LOG_TAG, Logger.INFO_PRIORITY, "Activity started");

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
		Log.d(LOG_TAG, "setTaskTitle: " + m_CurrTask.getName());
	}

	private void setTaskName() {
		TextView txtTaskName = (TextView) findViewById(R.id.txtTaskName);

		try {
			txtTaskName.setText(m_CurrPlugin.getPluginType());
			Log.d(LOG_TAG, "setActivityTitle: " + m_CurrPlugin.getPluginType());
		} catch (RemoteException e) {
			Log.e(LOG_TAG, "setActivityTitle: " + getString(R.string.err_plugin_connection) + ": " + e.getMessage());
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

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TaskManager.getInstance().getCurrentTask().setCurrentExperiment(position);

				Intent intent = new Intent(ExperimentListActivity.this, ExperimentActivity.class);

				startActivity(intent);
			}
		});

		listView.setOnMenuItemClickListener(new ListMenuClickListener());
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

		listView = (SwipeMenuListView) findViewById(R.id.listView);

		listView.setAdapter(itemAdapter);

		listView.setMenuCreator(new ListMenuCreator());
	}

	private void setOnListItemLongClick() {
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				return false;
			}
		});
	}



	/**
	 * Represents the menu that appears after swiping on the list item.
	 */
	private class ListMenuCreator implements SwipeMenuCreator {
		public void create(SwipeMenu menu) {
			// create "open" item
			SwipeMenuItem editItem = new SwipeMenuItem(
					getApplicationContext());
			// set item background
			editItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
					0xCE)));
			// set item width
			editItem.setWidth(UIUtils.dp2px(getApplicationContext(), 90));
			// set item title
			editItem.setTitle(getString(R.string.edit));
			// set item title fontsize
			editItem.setTitleSize(18);
			// set item title font color
			editItem.setTitleColor(Color.WHITE);
			// add to menu
			menu.addMenuItem(editItem);

			// create "delete" item
			SwipeMenuItem deleteItem = new SwipeMenuItem(
					getApplicationContext());
			// set item background
			deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
					0x3F, 0x25)));
			// set item width
			deleteItem.setWidth(UIUtils.dp2px(getApplicationContext(), 90));
			// set item title
			deleteItem.setTitle(getString(R.string.delete));
			// set item title fontsize
			deleteItem.setTitleSize(18);
			// set item title font color
			deleteItem.setTitleColor(Color.WHITE);
			// add to menu
			menu.addMenuItem(deleteItem);
		}		
	}

	/**
	 *  Menu items click listener.
	 */
	private class ListMenuClickListener implements OnMenuItemClickListener {
		@Override
		public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
			final TextView tv = (TextView) UIUtils.getViewByPosition(position, listView)
					.findViewById(R.id.text1);

			final String taskName = tv.getText().toString();

			switch (index) {
			case 0:
				TaskManager.getInstance().getCurrentTask().setCurrentExperiment(position);

				Intent intent = new Intent(ExperimentListActivity.this, ExperimentActivity.class);

				startActivity(intent);

				break;

			case 1:
				// delete
				try {

					//From selected task delete selected experiment, set experiment selected to -1
					Task currentTask = TaskManager.getInstance().getCurrentTask();
					currentTask.deleteExperiment(currentTask.getCurrentExperimentIndex());
					currentTask.setCurrentExperiment(Task.CURRENT_EXPERIMENT_NOT_DEFINED);
					FileManager.getInstance(getApplicationContext()).saveTask(currentTask);
					itemAdapter.notifyDataSetChanged();
					fillSavedExperiments();

					Log.d(LOG_TAG, "The task <" + tv.getText().toString() + "> removed");

				} catch (IOException e) {
					Log.d(LOG_TAG, e.getMessage());
				}

				break;
			}
			// false : close the menu; true : not close the menu
			return false;
		}
	}
}
