package android.mlh.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.mlh.aidl.IMLHPlugin;
import android.mlh.bl.files.FileManager;
import android.mlh.bl.plugins.PluginManager;
import android.mlh.bl.tasks.Task;
import android.mlh.bl.tasks.TaskManager;
import android.mlh.ui.constants.UIConstatns;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mlh.R;


/**
 * This is the main activity. 
 * @author everyone
 *
 */
public class MainActivity extends ListActivity {
	
	/** It is adapter from data structure to the view (XML)*/
	private SimpleAdapter itemAdapter;
	
	/** List of all tasks. Maps filenames to file paths.
	 * Each element in the list represents a task.
	 * The element maps filename of serialized Task instance to a path to that file 
	 */
	private ArrayList<HashMap<String,String>> savedTasks = new ArrayList<HashMap<String,String>>();

	private ArrayList<PluginServiceConnection> pluginServiceConnection = 
			new ArrayList<PluginServiceConnection>();

	private PackageBroadcastReceiver packageBroadcastReceiver;
	private IntentFilter packageFilter;

	private final static String LOG_D = "MainActivity";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		PluginManager.getInstance().clearServices();

		setListAdapter();

		setOnListItemLongClick();

		Button btnNewTask = (Button) findViewById(R.id.btn_new_task);
		btnNewTask.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				openNewTaskActivity();
			}
		});

		fillPluginList();

		packageBroadcastReceiver = new PackageBroadcastReceiver();
		packageFilter = new IntentFilter();
		packageFilter.addAction( Intent.ACTION_PACKAGE_ADDED  );
		packageFilter.addAction( Intent.ACTION_PACKAGE_REPLACED );
		packageFilter.addAction( Intent.ACTION_PACKAGE_REMOVED );
		packageFilter.addCategory( Intent.CATEGORY_DEFAULT ); 
		packageFilter.addDataScheme( "package" );
	}

	/**
	 * Provide the cursor for the list view (UI)
	 * Basically adds the list of tasks to our view. 
	 * Like updating XML view, but from a data structure.
	 */
	private void setListAdapter() {
		fillSavedTasks();

		itemAdapter =
				new SimpleAdapter(this, 
						savedTasks,
						R.layout.list_item,
						new String[] {UIConstatns.ITEM_KEY, UIConstatns.ITEM_VALUE},
						new int[] { R.id.text1, R.id.text2}
						);

		setListAdapter(itemAdapter);
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
			item.put(UIConstatns.ITEM_VALUE, FileManager.getInstance(getApplicationContext()).getTaskPath(el));

			savedTasks.add( item );
		}
	}

	private void releasePluginServices() {
		for( int i = 0 ; i < PluginManager.getInstance().getServices().size() ; ++i ) {
			unbindService( pluginServiceConnection.get(i) );
		}
	}

	protected void onStart() {
		super.onStart();
		registerReceiver( packageBroadcastReceiver, packageFilter );
		bindPluginServices();
	}

	@Override
	public void onRestart() { 
		super.onRestart();

		fillSavedTasks();

		itemAdapter.notifyDataSetChanged();

		Log.d(LOG_D, "onRestart");
	}

	private void bindPluginServices() {
		for( int i = 0 ; i < PluginManager.getInstance().getServices().size() ; ++i ) {
			pluginServiceConnection.add(new PluginServiceConnection(i));
			Intent intent = new Intent();
			HashMap<String,String> data = PluginManager.getInstance().getServices().get(i);

			intent.setClassName( data.get( UIConstatns.ITEM_KEY ),data.get( UIConstatns.ITEM_VALUE ) );
			bindService( intent, pluginServiceConnection.get(i), Context.BIND_AUTO_CREATE );
		}
	}

	protected void onStop() {
		super.onStop();
		unregisterReceiver( packageBroadcastReceiver );
		releasePluginServices();
	}

	private void setOnListItemLongClick() {
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				final TextView tv = (TextView) arg1.findViewById(R.id.text1);
				final int pos = arg2;

				// build a list dialog
				AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);

				builderSingle.setTitle(tv.getText());

				final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
						MainActivity.this,
						android.R.layout.select_dialog_item);

				arrayAdapter.add(getString(R.string.edit));
				arrayAdapter.add(getString(R.string.delete));

				builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String strName = arrayAdapter.getItem(which);

						if (strName.equalsIgnoreCase(getString(R.string.delete))) {
							try {
								FileManager.getInstance(getApplicationContext()).deleteTask(tv.getText().toString());
								savedTasks.remove(pos);
								itemAdapter.notifyDataSetChanged();

								Log.d(LOG_D, "The task <" + tv.getText().toString() + "> removed");

							} catch (ClassNotFoundException e) {
								Log.d(LOG_D, e.getMessage());
							} catch (IOException e) {
								Log.d(LOG_D, e.getMessage());
							}
						}

						if (strName.equalsIgnoreCase(getString(R.string.edit))) {
							dialog.dismiss();

							// prompt a dialog to change task name
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

	protected void onListItemClick (ListView l, View v, int position, long id) {
		final TextView tv = (TextView) v.findViewById(R.id.text1);

		Task task;

		try {

			task = FileManager.getInstance(getApplicationContext())
					.getTask(tv.getText().toString());

			PluginManager.getInstance().setCurrentPlugin(task.getPluginKey());

			TaskManager.getInstance().setCurrentTask(task);

			Intent intent = new Intent(this, ExperimentListActivity.class);

			startActivity(intent);

		} catch (ClassNotFoundException e) {
			Log.d("MainActivity", e.getMessage());
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

		} catch (IOException e) {
			Log.d("MainActivity", e.getMessage());
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

		}
	}

	/** Called when the user clicks the Send button */
	public void openNewTaskActivity() {
		Intent intent = new Intent(this, NewTaskActivity.class);
		startActivity(intent);
	}

	private void fillPluginList() {
		PackageManager packageManager = getPackageManager();
		Intent baseIntent = new Intent(PluginManager.ACTION_PICK_PLUGIN);

		baseIntent.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);

		List<ResolveInfo> list = packageManager.queryIntentServices(baseIntent,
				PackageManager.GET_RESOLVED_FILTER );

		for(int i = 0 ; i < list.size(); ++i) {
			ResolveInfo info = list.get( i );
			ServiceInfo sinfo = info.serviceInfo;
			Log.d( "fillPluginList", "fillPluginList: i: "+i+"; packageName: "+sinfo.packageName );
			if( sinfo != null ) {
				HashMap<String,String> item = new HashMap<String,String>();
				item.put( UIConstatns.ITEM_KEY, sinfo.packageName );
				item.put( UIConstatns.ITEM_VALUE, sinfo.name );
				PluginManager.getInstance().addService(item);
			}
		}
	}

	class PackageBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			PluginManager.getInstance().clearServices();
			fillPluginList();
			itemAdapter.notifyDataSetChanged();
		}
	}

	class PluginServiceConnection implements ServiceConnection {
		//private int serviceID;
		private String serviceName;

		public PluginServiceConnection(int serviceID) {
			//I took the packageName of the plugin as 
			//the identifier for the plugin in the PluginManager
			this.serviceName = PluginManager.getInstance().getServices()
					.get(serviceID).get(UIConstatns.ITEM_KEY);
		}

		public void onServiceConnected(ComponentName className, 
				IBinder boundService ) {

			Log.d(LOG_D, "service <" + serviceName + "> connected");

			PluginManager.getInstance().addPlugin(serviceName, 
					IMLHPlugin.Stub.asInterface((IBinder)boundService));
		}

		public void onServiceDisconnected(ComponentName className) {
			//iMLHPlugin.remove(serviceName);
		}
	};
}
