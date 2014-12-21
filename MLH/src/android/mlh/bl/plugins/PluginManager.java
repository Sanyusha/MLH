package android.mlh.bl.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.mlh.aidl.IMLHPlugin;
import android.util.Log;

public class PluginManager {
	
	public static final String ACTION_PICK_PLUGIN = "aexp.intent.action.PICK_PLUGIN";
	
	private static PluginManager _instance = null;
	
	private List<HashMap<String,String>> services;
	
	private HashMap<String, IMLHPlugin> plugins;
	
	private String currPluginName;
	private String currService;
	
	private PluginManager() {
		plugins = new HashMap<String, IMLHPlugin>();
		services = new ArrayList<HashMap<String, String>>();
		
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
	
	public String getCurrentPluginName() {
		return currPluginName;
	}
	
	public void setCurrentService(String service) {
		currService = service;
	}
	
	public String getCurrentService() {
		return currService;
	}
	
	public List<HashMap<String,String>> getServices() {
		return services;
	}
	
	public void addService(HashMap<String,String> service) {
		services.add(service);
	}
	
	public void clearServices() {
		services.clear();
	}
}
