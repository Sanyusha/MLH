package android.mlh.bl.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.mlh.aidl.IMLHPlugin;
import android.util.Log;

/** Holds and manages information about Plugins and services.
 *  Also, at runtime holds the Plugin that is currently in use
 *   - i.e. selected by the user, and is used to create new Tasks.
 *  So, basically, Plugin manager holds a piece of runtime information
 *  about currently available services and plugins, as well as 
 *  the plugin which is in use right now.
 *  Implemented as Singleton, with lazy instantiation.
 */
public class PluginManager {
	
	public static final String ACTION_PICK_PLUGIN = "aexp.intent.action.PICK_PLUGIN";
	
	private static PluginManager _instance = null;
	
	/** list of all the services,  */
	private List<HashMap<String,String>> services;
	
	/** loaded plugins, mapped to their names */
	private HashMap<String, IMLHPlugin> plugins;
	
	
	private String currPluginName;
	private String currService;
	
	private PluginManager() {
		plugins = new HashMap<String, IMLHPlugin>();
		services = new ArrayList<HashMap<String, String>>();
		
		currPluginName = "";
	}
	
	/** 
	 * Returns reference to the PluginManager, using lazy instantiation.
	 * @return reference to the PluginManager
	 */
	public static PluginManager getInstance() {
		if (_instance == null) {
			_instance = new PluginManager();
		}
		
		return _instance;
	}
	
	/**
	 * Adds a new Plugin, which is then could be accessed/retrieved by given name.
	 * @param pluginName - name of the Plugin, used as identifier
	 * @param pluginClass - Plugin itself (type?)
	 */
	public void addPlugin(String pluginName, IMLHPlugin pluginClass) {
		plugins.put(pluginName, pluginClass);
	}
	
	/**
	 * Set the Plugin as currently selected. I.E. - as the plugin which user has selected.
	 * @param pluginName - the name of the Plugin to select
	 */
	public void setCurrentPlugin(String pluginName) {
		currPluginName = pluginName;
		
		Log.d("PluginManager", "current plugin set to <" + currPluginName + ">");
	}
	
	/**
	 * Returns Plugin currently selected by the user
	 * @return Plugin currently selected by the user
	 */
	public IMLHPlugin getCurrentPlugin() {
		return plugins.get(currPluginName);
	}
	

	public String getCurrentPluginName() {
		return currPluginName;
	}
	
	/**
	 * Sets the service as selected
	 * @param service - name of the service
	 */
	public void setCurrentService(String service) {
		currService = service;
	}
	
	/**
	 * Returns the name of currently selected service
	 * @return
	 */
	public String getCurrentService() {
		return currService;
	}
	
	/**
	 * Returns list of available services
	 * @return list of available services
	 */
	public List<HashMap<String,String>> getServices() {
		return services;
	}
	
	/**
	 * Add new service to the manager
	 * @param service - given service
	 */
	public void addService(HashMap<String,String> service) {
		services.add(service);
	}
	
	/**
	 * Clears lost of available services
	 */
	public void clearServices() {
		services.clear();
	}
}
