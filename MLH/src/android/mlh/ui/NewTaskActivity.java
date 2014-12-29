package android.mlh.ui;

import java.io.IOException;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.mlh.bl.files.FileManager;
import android.mlh.bl.plugins.PluginManager;
import android.mlh.bl.tasks.Task;
import android.mlh.bl.tasks.TaskManager;
import android.mlh.constants.UIConstatns;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.mlh.R;

/**
 * This activity describes the way of adding new Task.
 * 
 * @author michael
 *
 */
public class NewTaskActivity extends ListActivity{

	private static final String LOG_D = "NewTaskActivity";
	
	private SimpleAdapter itemAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_task);

		itemAdapter =
				new SimpleAdapter(this, 
						PluginManager.getInstance().getServices(),
						R.layout.list_item,
						new String[] {UIConstatns.ITEM_KEY, UIConstatns.ITEM_VALUE},
						new int[] { R.id.text1, R.id.text2}
						);
		setListAdapter(itemAdapter);
	}

	protected void onListItemClick (ListView l, View v, int position, long id) {

		final TextView tv = (TextView) v.findViewById(R.id.text1);

		PluginManager.getInstance().setCurrentPlugin(tv.getText().toString());

		// prompt a dialog to enter task name
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(getString(R.string.new_task));
		alert.setMessage(getString(R.string.enter_task_name));

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				
				if (value != null && value.length() != 0) {
					try {
						Log.d(LOG_D, "currPlugin = " + PluginManager.getInstance().getCurrentPlugin());
						
						String taskType = PluginManager.getInstance().getCurrentPlugin().getPluginType();

						Log.d(LOG_D, "create and save task with name = <" +
								value + "> and type = <" + taskType + ">");

						Task task = new Task(value, taskType, tv.getText().toString());

						TaskManager.getInstance().setCurrentTask(task);

						FileManager.getInstance(getApplicationContext()).saveTask(task);

						Intent intent = new Intent(NewTaskActivity.this, ExperimentActivity.class);

						startActivity(intent);

					}
					catch (IOException e) {
						Log.d(LOG_D, "Failed to save task. " + e.getMessage());
					} catch (RemoteException e) {
						Log.d(LOG_D, "Remote interface exception. " + e.getMessage());
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
