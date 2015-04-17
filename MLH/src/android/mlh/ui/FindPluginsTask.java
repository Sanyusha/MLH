package android.mlh.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.mlh.R;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.mlh.aidl.IMLHPlugin;
import android.mlh.bl.plugins.PluginManager;
import android.mlh.constants.UIConstatns;
import android.mlh.logger.Logger;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.TextView;

/**
 * Finding and connecting to plugins.
 */
public class FindPluginsTask extends AsyncTask<Void, Void, Long>{
	private static final String LOG_TAG = UIConstatns.LOG_PREFIX + "FindPluginsTask";

	private Context m_Context;
	
	private TextView m_StatusText = null;
	
	//private PackageBroadcastReceiver packageBroadcastReceiver;
	private IntentFilter packageFilter;
	
	private ArrayList<PluginServiceConnection> pluginServiceConnection = 
			new ArrayList<PluginServiceConnection>();
	
	public FindPluginsTask(Context a_Context) {
		this.m_Context = a_Context;
	}
	
	/**
	 * @param a_IndicationText
	 * Specifies a textview where status messages will be put.
	 */
	public FindPluginsTask(Context a_Context, TextView a_StatusText) {
		this(a_Context);
		m_StatusText = a_StatusText;
	}
	
	protected Long doInBackground(Void... params) {
		Logger.log(LOG_TAG, Logger.DEBUG_PRIORITY, "Starting to find plugins...");

		PluginManager.getInstance().clearServices();

		fillPluginList();
		
		bindPluginServices();
		
		return (long) PluginManager.getInstance().getServices().size();
	}
	
	protected void onPostExecute(Long result) {
		if (m_StatusText != null) {
			m_StatusText.setText(result + " " + m_Context.getString(R.string.plugins_founded));
		}
    }
	
	private void fillPluginList() {
		PackageManager packageManager = m_Context.getPackageManager();

		Intent baseIntent = new Intent(PluginManager.ACTION_PICK_PLUGIN);

		baseIntent.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);

		List<ResolveInfo> list = packageManager.queryIntentServices(baseIntent,
				PackageManager.GET_RESOLVED_FILTER );

		for(int i = 0 ; i < list.size(); ++i) {
			ResolveInfo info = list.get(i);
			ServiceInfo sinfo = info.serviceInfo;
			
			if(sinfo != null ) {
				HashMap<String,String> item = new HashMap<String,String>();
				item.put( UIConstatns.ITEM_KEY, sinfo.packageName );
				item.put( UIConstatns.ITEM_VALUE, sinfo.name);
				
				PluginManager.getInstance().addService(item);
				
				Logger.log(LOG_TAG, Logger.DEBUG_PRIORITY, "Service added to PluginManager: "
						+ item.toString());
			}
		}
	}
	
	private void bindPluginServices() {
		for( int i = 0 ; i < PluginManager.getInstance().getServices().size() ; ++i ) {
			pluginServiceConnection.add(new PluginServiceConnection(i));
			Intent intent = new Intent();
			HashMap<String,String> data = PluginManager.getInstance().getServices().get(i);

			intent.setClassName( data.get( UIConstatns.ITEM_KEY ),data.get( UIConstatns.ITEM_VALUE ) );
			
			m_Context.bindService(intent, pluginServiceConnection.get(i), 
					Context.BIND_AUTO_CREATE);
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

			Logger.log(LOG_TAG, Logger.DEBUG_PRIORITY, "Service <" + serviceName + "> connected");

			PluginManager.getInstance().addPlugin(serviceName, 
					IMLHPlugin.Stub.asInterface((IBinder)boundService));
		}

		public void onServiceDisconnected(ComponentName className) {
			//iMLHPlugin.remove(serviceName);
		}
	};
}
