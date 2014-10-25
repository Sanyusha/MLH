package android.mlh.cooking;

import android.app.Service;
import android.content.Intent;
import android.mlh.aidl.Experiment;
import android.mlh.aidl.IMLHPlugin;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class CookingPluginService extends Service {
	static final String CATEGORY_ADD_IF = "aexp.intent.category.ADD_PLUGIN";
	static final String PLUGIN_TYPE = "Cooking task plugin";
	
	static final String GENERAL_TEXT = "general_text";
	
	public void onStart(Intent intent, int startId) {
		super.onStart( intent, startId );
	}

	public void onDestroy() {
		super.onDestroy();
	}

	public IBinder onBind(Intent intent) {
      	return addBinder;
	}

    private final IMLHPlugin.Stub addBinder = 
			new IMLHPlugin.Stub() {
    	
    	Experiment mExperiment;
    	
    	public String getPluginType() {
			return PLUGIN_TYPE;
		}
		
		public Bundle onClick(int id, Bundle state) {
			Bundle update = new Bundle();
			/*
			Log.d("plugin", "onClick !!!");
			update.putString("sokol", "champion");
			if (id == R.id.bt_foo) {
				String o = state.getString(Integer.toString(R.id.edit1));
				if( o != null ) {
					o += ":::" + PLUGIN_TYPE;
					update.putString(Integer.toString( R.id.text1 ), o);
				}
			}
			*/
			return update;
		}
		
		public void setExperiment(Experiment experiment) {
			mExperiment = experiment;
			Log.d("Plugin", "Sokol");
		}
		
		public Experiment getExperiment(Bundle state) {
			Experiment result = new Experiment();
			
			result.setResult("55");
			String o = state.getString(Integer.toString(R.id.edit1));
			
			if( o != null ) {
				result.addParameter(GENERAL_TEXT, o);
				result.setResult("100");
			} else {
				result.setResult("55");
			}
			
			return result;
		}
    };
}

