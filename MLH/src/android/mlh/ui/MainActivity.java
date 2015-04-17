package android.mlh.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.mlh.bl.files.FileManager;
import android.mlh.bl.plugins.PluginManager;
import android.mlh.bl.tasks.Task;
import android.mlh.bl.tasks.TaskManager;
import android.mlh.constants.UIConstatns;
import android.mlh.logger.Logger;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.example.mlh.R;

/**
 * This is the main activity. 
 * @author everyone
 *
 */
public class MainActivity extends Activity {

	/** It is adapter from data structure to the view (XML)*/
	private SimpleAdapter itemAdapter;

	/** List of all tasks. Maps filenames to file paths.
	 * Each element in the list represents a task.
	 * The element maps filename of serialized Task instance to a path to that file 
	 */
	private ArrayList<HashMap<String,String>> savedTasks = new ArrayList<HashMap<String,String>>();

	private SwipeMenuListView listView;

	private final static String LOG_D = UIConstatns.LOG_PREFIX + "MainActivity";

	protected void onCreate(Bundle savedInstanceState) {
		Logger.log(LOG_D, Logger.INFO_PRIORITY, "Activity started");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		prepareList();

		setOnListItemLongClick();

		Button btnNewTask = (Button) findViewById(R.id.btn_new_task);
		btnNewTask.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				openNewTaskActivity();
			}
		});
	}

	/**
	 * Provide the cursor for the list view (UI)
	 * Basically adds the list of tasks to our view. 
	 * Like updating XML view, but from a data structure.
	 */
	private void prepareList() {
		listView = (SwipeMenuListView) findViewById(R.id.listView);

		fillSavedTasks();
		
		/*
		itemAdapter =
				new SimpleAdapter(this, 
						savedTasks,
						R.layout.list_item,
						new String[] {UIConstatns.ITEM_KEY, UIConstatns.ITEM_VALUE},
						new int[] { R.id.text1, R.id.text2}
						);
		*/
		
		//listView.setAdapter(itemAdapter);
		
		listView.setAdapter(new MainActivity.ListAdapter());
		
		listView.setMenuCreator(new ListMenuCreator());

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final TextView tv = (TextView) view.findViewById(R.id.text1);

				Task task;

				try {

					task = FileManager.getInstance(getApplicationContext())
							.getTask(tv.getText().toString());

					PluginManager.getInstance().setCurrentPlugin(task.getPluginKey());

					TaskManager.getInstance().setCurrentTask(task);

					Intent intent = new Intent(view.getContext(), ExperimentListActivity.class);

					startActivity(intent);

				} catch (ClassNotFoundException e) {
					Log.d("MainActivity", e.getMessage());
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

				} catch (IOException e) {
					Log.d("MainActivity", e.getMessage());
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

				}
			}
		});

		listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				final TextView tv = (TextView) UIUtils.getViewByPosition(position, listView)
						.findViewById(R.id.text1);
				
				final String taskName = tv.getText().toString();
				
				switch (index) {
				case 0:
					// edit: prompt a dialog to change task name
					AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

					alert.setTitle(getString(R.string.new_task_name));
					alert.setMessage(getString(R.string.enter_task_name));

					// Set an EditText view to get user input 
					final EditText input = new EditText(MainActivity.this);
					alert.setView(input);

					alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							String value = input.getText().toString();

							if (value != null && value.length() != 0) {
								try {
									Task task = FileManager.getInstance(getApplicationContext()).getTask(tv.getText().toString());
									task.setName(value);
									FileManager.getInstance(getApplicationContext()).saveTask(task);
									FileManager.getInstance(getApplicationContext()).deleteTask(tv.getText().toString());
									fillSavedTasks();
									itemAdapter.notifyDataSetChanged();
								} catch (ClassNotFoundException e) {
									Log.d(LOG_D, e.getMessage());
								} catch (IOException e) {
									Log.d(LOG_D, e.getMessage());
								}
							}
						}
					});

					alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// Canceled.
						}
					});

					alert.show();

					break;
					
				case 1:
					// delete
					try {
						FileManager.getInstance(getApplicationContext()).deleteTask(taskName);
						savedTasks.remove(position);
						itemAdapter.notifyDataSetChanged();

						Logger.log(LOG_D, Logger.DEBUG_PRIORITY, "The task <" + taskName + "> removed");

					} catch (ClassNotFoundException e) {
						Log.d(LOG_D, e.getMessage());
					} catch (IOException e) {
						Log.d(LOG_D, e.getMessage());
					}
					
					break;
				}
				// false : close the menu; true : not close the menu
				return false;
			}
		});
	}

	/**
	 * Fill the this.savedTasks field - the list of Tasks.
	 * These tasks are shown in the activity list.
	 */
	private void fillSavedTasks() {
		savedTasks.clear();

		for (String el: FileManager.getInstance(getApplicationContext()).getSavedTasks()) {
			HashMap<String,String> item = new HashMap<String,String>();
			item.put(UIConstatns.ITEM_KEY, el);// el - filename , value - path to file
			//item.put(UIConstatns.ITEM_VALUE, FileManager.getInstance(getApplicationContext()).getTaskPath(el));
			item.put(UIConstatns.ITEM_VALUE, "Brief task description");

			savedTasks.add( item );
		}
	}
	
	/*
	private void releasePluginServices() {
		for( int i = 0 ; i < PluginManager.getInstance().getServices().size() ; ++i ) {
			unbindService( pluginServiceConnection.get(i) );
		}
	}
	 */
	
	@Override
	public void onRestart() { 
		super.onRestart();

		fillSavedTasks();

		itemAdapter.notifyDataSetChanged();
	}

	protected void onStop() {
		super.onStop();
		//releasePluginServices();
	}

	private void setOnListItemLongClick() {
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				return false;
			}
		});
	}

	/** Called when the user clicks the Send button */
	public void openNewTaskActivity() {
		Intent intent = new Intent(this, NewTaskActivity.class);
		startActivity(intent);
	}

	/**
	 * Represents the menu that appears after swiping on the list item.
	 */
	private final class ListMenuCreator implements SwipeMenuCreator {
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
	 * Inner class for an adapter to the listview of tasks.
	 */
	private final class ListAdapter extends BaseAdapter {
		
		public int getCount() {
			return savedTasks.size();
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

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder v;
			
			if (view == null) {
				// This a new view we inflate the new layout
				Context currContext = MainActivity.this;
				LayoutInflater inflater = (LayoutInflater) currContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.list_item, null);
				
				v = new ViewHolder();
				v.text1 = (TextView) view.findViewById(R.id.text1);
				v.text2 = (TextView) view.findViewById(R.id.text2);
				
				view.setTag(v);
			} else {
				v = (ViewHolder) view.getTag();
			}
			
			v.text1.setText(savedTasks.get(position).get(UIConstatns.ITEM_KEY));
			v.text2.setText(savedTasks.get(position).get(UIConstatns.ITEM_VALUE));
			
			return view;
		}
	}
	
	/**
	 * A ViewHolder object stores each of the component views inside the tag field
	 * of the Layout, so you can immediately access them without the need 
	 * to look them up repeatedly.
	 */
	private static final class ViewHolder {
		TextView text1, text2;
	}
}
