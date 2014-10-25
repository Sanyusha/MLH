package android.mlh.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.mlh.bl.files.FileManager;
import android.mlh.bl.plugins.PluginManager;
import android.mlh.bl.tasks.Task;
import android.mlh.bl.tasks.TaskManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mlh.R;


public class MainActivity extends ListActivity {
	private SimpleAdapter itemAdapter;
	private ArrayList<HashMap<String,String>> savedTasks;
	static final String KEY_PKG = "pkg";
	static final String KEY_SERVICENAME = "servicename";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        savedTasks = new ArrayList<HashMap<String,String>>();
        
        for (String el: FileManager.getInstance(getApplicationContext()).getSavedTasks()) {
        	Log.d("MainActivity", el);
        	HashMap<String,String> item = new HashMap<String,String>();
            item.put( KEY_PKG, el );
            item.put( KEY_SERVICENAME, FileManager.getInstance(getApplicationContext()).getTaskPath(el));
            savedTasks.add( item );
        }
        
        itemAdapter =
                new SimpleAdapter(this, 
                	savedTasks,
    				R.layout.services_row,
    				new String[] {KEY_PKG, KEY_SERVICENAME},
    				new int[] { R.id.pkg, R.id.servicename}
    				);
            setListAdapter(itemAdapter);
            
        Button btnNewTask = (Button) findViewById(R.id.btn_new_task);
        btnNewTask.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				openNewTaskActivity();
			}
		});
    }
    
    protected void onListItemClick (ListView l, View v, int position, long id) {
    	final TextView tv = (TextView) v.findViewById(R.id.pkg);
    	
    	
    	
    	Task task;
    	
		try {
			
			task = FileManager.getInstance(getApplicationContext())
					.getTask(tv.getText().toString());
			
			
			
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
}
