package android.mlh.bl.plugins;

import java.util.HashMap;

import android.mlh.aidl.IMLHPlugin;
import android.util.Log;

public class PluginManager {
	
	private static PluginManager _instance = null;
	
	public static HashMap<String, IMLHPlugin> plugins; 
	public static String currPluginName;
	
	private PluginManager() {
		plugins = new HashMap<String, IMLHPlugin>();
		currPluginName = "";
	}
	
	public static PluginManager getInstance() {
		if (_instance == null) {
			_instance = new PluginManager();
		}
		
		return _instance;
	}
	
	public void addPlugin(String pluginName, IMLHPlugin pluginClass) {
		plugins.put(pluginName, pluginClass);
	}
	
	public void setCurrentPlugin(String pluginName) {
		currPluginName = pluginName;
		
		Log.d("PluginManager", "current plugin set to <" + currPluginName + ">");
	}
	
	public IMLHPlugin getCurrentPlugin() {
		return plugins.get(currPluginName);
	}
}
