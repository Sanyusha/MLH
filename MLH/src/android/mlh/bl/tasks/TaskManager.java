package android.mlh.bl.tasks;

import android.mlh.aidl.Experiment;

public class TaskManager {
	
	private static TaskManager _instance = null;
	
	private Task currTask;
	
	private TaskManager() {
	}
	
	public static TaskManager getInstance() {
		if (_instance == null) {
			_instance = new TaskManager();
		}
		
		return _instance;
	}
	
	public void setCurrentTask(Task task) {
		currTask = task;
	}
	
	public Task getCurrentTask() {
		return currTask;
	}
}
