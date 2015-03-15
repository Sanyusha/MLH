package android.mlh.orderFood;

import android.app.Service;
import android.content.Intent;
import android.mlh.aidl.Experiment;
import android.mlh.aidl.IMLHPlugin;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

public class OrderFoodPluginService extends Service {
	static final String CATEGORY_ADD_IF = "aexp.intent.category.ADD_PLUGIN";
	static final String PLUGIN_TYPE = "Order food plugin";
	
	static final String RESTAURANT_NAME = "Restaurant name";
	static final String DISH_NAME = "Dish name";
	static final String NOTES = "Notes";
	
	private static final String[] RESULT_NAMES = {"Taste", "Dish size", "Price", "Delivery time"};
	
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
			// TO DO
			// Change the if-s to HashMap of Parameter name - Appropriate textbox id
			
			Experiment retValue = new Experiment();
			
			String o = state.getString(Integer.toString(R.id.txt1));
			
			if( o != null && o.length() > 0) {
				retValue.addParameter(RESTAURANT_NAME, o);
			}
			
			o = state.getString(Integer.toString(R.id.txt2));
			
			if( o != null && o.length() > 0) {
				retValue.addParameter(DISH_NAME, o);
			}
			
			o = state.getString(Integer.toString(R.id.txt3));
			
			if( o != null && o.length() > 0) {
				retValue.addParameter(NOTES, o);
			}
			
			return retValue;
		}
		
		public Bundle getState(Experiment experiment) {
			Bundle state = new Bundle();
			
			if (experiment != null) {
				state.putString(Integer.toString(R.id.txt1), experiment.getParameter(RESTAURANT_NAME));
				state.putString(Integer.toString(R.id.txt2), experiment.getParameter(DISH_NAME));
				state.putString(Integer.toString(R.id.txt3), experiment.getParameter(NOTES));
			} else {
				state.putString(Integer.toString(R.id.txt1), "null");
				state.putString(Integer.toString(R.id.txt2), "null");
				state.putString(Integer.toString(R.id.txt3), "null");
			}
			
			return state;
		}
		
		public String[] getResultNames() throws RemoteException {
			return RESULT_NAMES;
		}

		public boolean hasSteps() throws RemoteException {
			return false;
		}
    };
}

