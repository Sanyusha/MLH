package android.mlh.ui;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
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
import android.mlh.utils.constants.FileConstatns;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.mlh.R;

public class NewTaskActivity extends ListActivity{
	
	public static final String ACTION_PICK_PLUGIN = "aexp.intent.action.PICK_PLUGIN";
	
	private static final String LOG_TAG = "NewTaskActivity";
	
	static final String KEY_PKG = "pkg";
	static final String KEY_SERVICENAME = "servicename";
	static final String KEY_ACTIONS = "actions";
	static final String KEY_CATEGORIES = "categories";
	static final String BUNDLE_EXTRAS_CATEGORY = "category";
	
	private ArrayList<PluginServiceConnection> pluginServiceConnection = 
			new ArrayList<PluginServiceConnection>();
	
	private PackageBroadcastReceiver packageBroadcastReceiver;
	private IntentFilter packageFilter;
	private ArrayList<HashMap<String,String>> services;
	private SimpleAdapter itemAdapter;
	
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        
		fillPluginList();
		
		itemAdapter =
            new SimpleAdapter(this, 
				services,
				R.layout.services_row,
				new String[] {KEY_PKG, KEY_SERVICENAME},
				new int[] { R.id.pkg, R.id.servicename}
				);
        setListAdapter(itemAdapter);

		packageBroadcastReceiver = new PackageBroadcastReceiver();
		packageFilter = new IntentFilter();
		packageFilter.addAction( Intent.ACTION_PACKAGE_ADDED  );
		packageFilter.addAction( Intent.ACTION_PACKAGE_REPLACED );
		packageFilter.addAction( Intent.ACTION_PACKAGE_REMOVED );
		packageFilter.addCategory( Intent.CATEGORY_DEFAULT ); 
		packageFilter.addDataScheme( "package" );
    }

    private void releasePluginServices() {
        for( int i = 0 ; i < services.size() ; ++i ) {
		    unbindService( pluginServiceConnection.get(i) );
        }
	}
	
    protected void onStart() {
		super.onStart();
		registerReceiver( packageBroadcastReceiver, packageFilter );
        bindPluginServices();
	}
    
    private void bindPluginServices() {
        for( int i = 0 ; i < services.size() ; ++i ) {
			pluginServiceConnection.add(new PluginServiceConnection(i));
			Intent intent = new Intent();
            HashMap<String,String> data = services.get( i );
            
            intent.setClassName( data.get( KEY_PKG ),data.get( KEY_SERVICENAME ) );
			bindService( intent, pluginServiceConnection.get(i), Context.BIND_AUTO_CREATE );
        }
	}
    
	protected void onStop() {
		super.onStop();
		unregisterReceiver( packageBroadcastReceiver );
        releasePluginServices();
	}

    protected void onListItemClick (ListView l, View v, int position, long id) {
    	
    	final TextView tv = (TextView) v.findViewById(R.id.servicename);
    	
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
			  			String taskType = PluginManager.getInstance().getCurrentPlugin().getPluginType();
			  			
			  			Log.d("NewTaskActivity", "create and save task with name = <" +
			  					value + "> and type = <" + taskType + ">");
			  			
						Task task = new Task(value, taskType);
						
						TaskManager.getInstance().setCurrentTask(task);
						
						FileManager.getInstance(getApplicationContext()).saveTask(task);
					  
						Intent intent = new Intent(NewTaskActivity.this, ExperimentActivity.class);
						
						startActivity(intent);
						
					}
			  		catch (RemoteException e) {
			  			
			  		}
			  		catch (Exception e) {
			  			Log.d("NewTaskActivity", "failed to save task");
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
    
	private void fillPluginList() {
		services = new ArrayList<HashMap<String,String>>();
        PackageManager packageManager = getPackageManager();
        Intent baseIntent = new Intent( ACTION_PICK_PLUGIN );
		baseIntent.setFlags( Intent.FLAG_DEBUG_LOG_RESOLUTION );
        List<ResolveInfo> list = packageManager.queryIntentServices(baseIntent,
                PackageManager.GET_RESOLVED_FILTER );
        int i;
        for( i = 0 ; i < list.size() ; ++i ) {
            ResolveInfo info = list.get( i );
            ServiceInfo sinfo = info.serviceInfo;
            Log.d( "fillPluginList", "fillPluginList: i: "+i+"; packageName: "+sinfo.packageName );
            if( sinfo != null ) {
                HashMap<String,String> item = new HashMap<String,String>();
                item.put( KEY_PKG, sinfo.packageName );
                item.put( KEY_SERVICENAME, sinfo.name );
                services.add( item );
                
            }
        }
    }
	
	class PackageBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			services.clear();
			fillPluginList();
			itemAdapter.notifyDataSetChanged();
		}
	}
	
	class PluginServiceConnection implements ServiceConnection {
		//private int serviceID;
		private String serviceName;
		
		public PluginServiceConnection(int serviceID) {
			//this.serviceID = serviceID;
			this.serviceName = services.get(serviceID).get(KEY_SERVICENAME);
		}
		
        public void onServiceConnected(ComponentName className, 
			IBinder boundService ) {
        	
        	Log.d("NewTaskActivity", "service <" + serviceName + "> connected");
        	
        	PluginManager.getInstance().addPlugin(serviceName, 
        			IMLHPlugin.Stub.asInterface((IBinder)boundService));
        }

        public void onServiceDisconnected(ComponentName className) {
        	//iMLHPlugin.remove(serviceName);
        }
    };
}
