package android.mlh.cooking;

import java.util.HashMap;

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
	
	static final String INGRIDIENTS = "ingridients";
	static final String GENERAL_TEXT = "general_text";
	
	private static final String[] RESULT_NAMES = {"Tasty", "Cheap", "Quick"};
	
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
		
		public Experiment updateExperimentParams(Bundle state, Experiment experiment) {
			Experiment retValue = new Experiment();
			
			String o = state.getString(Integer.toString(R.id.edit1));
			
			if( o != null && o.length() > 0) {
				retValue.addParameter(INGRIDIENTS, o);
			}
			
			o = state.getString(Integer.toString(R.id.edit2));
			
			if( o != null && o.length() > 0) {
				retValue.addParameter(GENERAL_TEXT, o);
			}
			
			return retValue;
		}
		
		public Bundle getState(Experiment experiment) {
			Bundle state = new Bundle();
			
			if (experiment != null) {
				state.putString(Integer.toString(R.id.edit1), experiment.getParameter(INGRIDIENTS));
				state.putString(Integer.toString(R.id.edit2), experiment.getParameter(GENERAL_TEXT));
			}
			
			return state;
		}
		
		public String[] getResultNames() throws RemoteException {
			return RESULT_NAMES;
		}

		public boolean hasSteps() throws RemoteException {
			return true;
		}
    };
}

