package android.mlh.bl.tasks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.mlh.aidl.Experiment;

/**
 * Basic task class. 
 * Stores list of experiments.
 * Instance is serialized into a file by FileManager.
 * 
 * This class is intended to store all the information for some task
 * that user defined for himself.
 * A task has a name, a type and a number of 'tries' which are called Experiments.
 * User creates a task of some type, gives it a name and eventually fills it with 
 * Experiments - descriptions of attempts to accomplish the Task. 
 * Since there could be several experiments for the Task, it holds the Experiment
 * currently selected by the user. 
 */
public class Task implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String mName, mType, mPluginKey;
	private List<Experiment> mExperiments;
	
	/** Holds the index of currently selected Experiment (if any)
	 *  This value is -1 by default/when no Experiment is selected.
	 */
	private int mCurrExperiment = -1;
	
	public static final int CURRENT_EXPERIMENT_NOT_DEFINED = -1;
	
	/**
	 * Default constructor. Constructs a new Task.
	 * @param name - task name
	 * @param type - task type (what do you mean by 'type'?)
	 * @param aPluginKey - what is that?
	 */
	public Task(String name, String type, String aPluginKey) {
		setName(name);
		setType(type);
		setPluginKey(aPluginKey);
		
		mExperiments = new ArrayList<Experiment>();
	}
	
	/**
	 * Append the Experiment to the task.
	 * @param experiment - given Experiment
	 */
	public void addExperiment(Experiment experiment) {
		mExperiments.add(experiment);
	}
	
	/**
	 * Removes Experiment from list of experiments.
	 * If index is out of bounds - doesn't do anything.
	 * @param index - index of experiment
	 */
	public void deleteExperiment(int index)
	{
		if(0 <= index && index < mExperiments.size())
			mExperiments.remove(index);
		
	}
	
	/**
	 * Get list of all Experiments associated with this task
	 * @return list with all Experiments
	 */
	public List<Experiment> getExperiments() {
		return mExperiments;
	}
	
	/**
	 * Sets the type of the Task
	 * @return
	 */
	public String getType() {
		return mType;
	}

	public void setType(String aType) {
		this.mType = aType;
	}
	
	/**
	 * Returns the name of this Task
	 * @return name of this Task
	 */
	public String getName() {
		return mName;
	}
	
	/**
	 * Sets new name for this Task
	 * @param aName - name for the Task
	 */
	public void setName(String aName) {
		this.mName = aName;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPluginKey() {
		return mPluginKey;
	}
	
	/**
	 * TODO: I don't know what this [parameter does!
	 * @param aPluginKey
	 */
	public void setPluginKey(String aPluginKey) {
		this.mPluginKey = aPluginKey;
	}
	
	/**
	 * Get the Experiment which is currently selected by the user
	 * @return Experiment that is selected - the current experiment
	 */
	public int getCurrentExperiment() {
		return mCurrExperiment;
	}
	
	/**
	 * Sets given experiment to current (as currently selected by the user)
	 * @param experiment - index of the Experiment in the Experiments list
	 */
	public void setCurrentExperiment(int experiment) {
		this.mCurrExperiment = experiment;
	}
}
