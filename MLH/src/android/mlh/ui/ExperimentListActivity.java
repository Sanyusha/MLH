package android.mlh.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.mlh.aidl.Experiment;
import android.mlh.bl.tasks.TaskManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.mlh.R;


public class ExperimentListActivity extends ListActivity {
	private SimpleAdapter itemAdapter;
	private ArrayList<HashMap<String,String>> savedTasks;
	static final String KEY_PKG = "pkg";
	static final String KEY_SERVICENAME = "servicename";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_list);
        
        savedTasks = new ArrayList<HashMap<String,String>>();
        
        for (Experiment el: TaskManager.getInstance().getCurrentTask().getExperiments()) {
        	
        	HashMap<String,String> item = new HashMap<String,String>();
            item.put( KEY_PKG, el.toString());
            item.put( KEY_SERVICENAME, "" );
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
            
        Button btnNewTask = (Button) findViewById(R.id.btn_new_experiment);
        btnNewTask.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				openNewTaskActivity();
			}
		});
    }
    
    protected void onListItemClick (ListView l, View v, int position, long id) {
    	final TextView tv = (TextView) v.findViewById(R.id.servicename);
    	
    	
    	Intent intent = new Intent(this, ExperimentActivity.class);
		
		startActivity(intent);
    	
    }

    /** Called when the user clicks the Send button */
    public void openNewTaskActivity() {
        Intent intent = new Intent(this, NewTaskActivity.class);
        startActivity(intent);
    }
}
