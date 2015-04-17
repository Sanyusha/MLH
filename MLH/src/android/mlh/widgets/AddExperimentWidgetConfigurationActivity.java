package android.mlh.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.mlh.bl.files.FileManager;
import android.mlh.constants.UIConstatns;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mlh.R;

public class AddExperimentWidgetConfigurationActivity extends ListActivity {

	private static final int HIGHLIGHTED = 0xaa227361;
	private static final int UNDEFINED_POSITION = -1;

	private int widgetId;


	/** It is adapter from data structure to the view (XML) */
	private SpecialAdapter itemAdapter;

	/**
	 * List of all tasks. Maps filenames to file paths. Each element in the list
	 * represents a task. The element maps filename of serialized Task instance
	 * to a path to that file
	 */
	private ArrayList<HashMap<String, String>> savedTasks = new ArrayList<HashMap<String, String>>();

	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// initialization
		setContentView(R.layout.add_experiment_widget_configuration);
		setResult(RESULT_CANCELED);

		setListAdapter();

		// wipe fields
		widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

		context = this;

		// getting widget id
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		
		String taskName = null;
		WidgetManager widgetManager = WidgetManager.getInstance(context.getApplicationContext());
		if(!widgetManager.widgetFileExists()){
			widgetManager.createWidgetFile();
		}
		taskName = widgetManager.getTasknameForWidgetId(widgetId);
		
		// if taskName is not null, than widget is binded to task and task
		// exists
		if (taskName != null) {
			int i = 0;
			// highlightening the item
			for (Map<String, String> m : savedTasks) {
				String str = m.get(UIConstatns.ITEM_KEY);
				if (taskName.equals(str)) {
					// found I let the adapter know what is it and use it when
					itemAdapter.setHighlightedPosition(i);

					break;
				}
				++i;
			}
		}

		//
		TextView aewcwidget_id = (TextView) findViewById(R.id.AEWCWidgetId);
		aewcwidget_id.setText(Integer.toString(widgetId));

		Button aewcbutton = (Button) findViewById(R.id.AEWCButton);

		aewcbutton.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {

				if (itemAdapter.getHighlightedPosition() != UNDEFINED_POSITION) {
					WidgetManager widgetManager = WidgetManager.getInstance(context.getApplicationContext());
					
					@SuppressWarnings("rawtypes")
					Map <String, String> m = (Map)itemAdapter.getItem(itemAdapter.getHighlightedPosition());
					
					String task_name = m.get(UIConstatns.ITEM_KEY);
					
					widgetManager.setTasknameForWidgetId(task_name, widgetId);
				}

				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
						widgetId);

				setResult(RESULT_OK, resultValue);
				finish();
			}
		});

	}

	private class SpecialAdapter extends SimpleAdapter {

		private int _highlighted_position = UNDEFINED_POSITION;

		public SpecialAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
		}

		public void setHighlightedPosition(int position) {
			_highlighted_position = position;
		}
		
		public int getHighlightedPosition(){
			return _highlighted_position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = super.getView(position, convertView, parent);

			if (_highlighted_position != UNDEFINED_POSITION
					&& _highlighted_position == position) {
				v.setBackgroundColor(HIGHLIGHTED);
			} else {
				v.setBackgroundColor(Color.TRANSPARENT);
			}

			return v;
		}
	}

	/**
	 * Provide the cursor for the list view (UI) Basically adds the list of
	 * tasks to our view. Like updating XML view, but from a data structure.
	 */
	private void setListAdapter() {
		fillSavedTasks();

		itemAdapter = new SpecialAdapter(this, savedTasks, R.layout.list_item,
				new String[] { UIConstatns.ITEM_KEY, UIConstatns.ITEM_VALUE },
				new int[] { R.id.text1, R.id.text2 });

		setListAdapter(itemAdapter);
	}

	/**
	 * Fill the this.savedTasks field - the list of Tasks. These tasks are shown
	 * in the activity list.
	 */
	private void fillSavedTasks() {
		savedTasks.clear();

		for (String el : FileManager.getInstance(getApplicationContext())
				.getSavedTasks()) {
			HashMap<String, String> item = new HashMap<String, String>();
			item.put(UIConstatns.ITEM_KEY, el);// el - filename , value - path
												// to file
			// item.put(UIConstatns.ITEM_VALUE,
			// FileManager.getInstance(getApplicationContext()).getTaskPath(el));
			item.put(UIConstatns.ITEM_VALUE, "Brief task description");

			savedTasks.add(item);
		}
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {

		final TextView tv = (TextView) v.findViewById(R.id.text1);

		if (itemAdapter.getHighlightedPosition() != UNDEFINED_POSITION) {
			getListView().getChildAt(itemAdapter.getHighlightedPosition()).setBackgroundColor(Color.TRANSPARENT);
		}

		v.setBackgroundColor(HIGHLIGHTED);

		itemAdapter.setHighlightedPosition(position);

		String task_name = (tv.getText().toString());

		Context context = getApplicationContext();

		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, task_name, duration);
		toast.show();
	}

}
