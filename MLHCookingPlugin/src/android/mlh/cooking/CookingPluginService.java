package android.mlh.cooking;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.mlh.plugin.IMLHPlugin;

public class CookingPluginService extends Service {
	static final String CATEGORY_ADD_IF = "aexp.intent.category.ADD_PLUGIN";
	static final String PLUGIN_TYPE = "Cooking task plugin";
	
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
    };
}

