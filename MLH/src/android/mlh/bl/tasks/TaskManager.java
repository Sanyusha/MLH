package android.mlh.bl.tasks;

import android.mlh.aidl.Experiment;

/** Holds reference to the Task that is currently selected by the user.
 * If no Task is selected - reference is null.
 * Holds only one task at the time.
 * This class is a singleton with lazy instantiation.
 */
public class TaskManager {
	
	/** Single instance */
	private static TaskManager _instance = null;
	
	private Task currTask;
	
	private TaskManager() {
	}
	
	/**
	 * Returns reference to the TaskManager instance.
	 * Instantiates TaskManager in lazy manner.
	 * @return reference to the TaskManager instance.
	 */
	public static TaskManager getInstance() {
		if (_instance == null) {
			_instance = new TaskManager();
		}
		
		return _instance;
	}
	
	/**
	 * Saves given task as current - i.e. selected by the user.
	 * @param task - given Task
	 */
	public void setCurrentTask(Task task) {
		currTask = task;
	}
	
	/**
	 * Returns reference to currently selected Task
	 * @return currently selected Task
	 */
	public Task getCurrentTask() {
		return currTask;
	}
	
	/**
	 * Returns reference to currently selected Experiment
	 * @return currently selected Experiment
	 */
	public Experiment getCurrentExperiment() {
		return currTask.getCurrentExperiment();
	}
}
