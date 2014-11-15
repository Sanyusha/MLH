package android.mlh.bl.tasks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.mlh.aidl.Experiment;

/**
 * Basic task class. 
 * Stores list of experiments.
 *
 */
public class Task implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String mName, mType, mPluginKey;
	private List<Experiment> mExperiments;
	private int mCurrExperiment = -1;
	
	public static final int CURRENT_EXPERIMENT_NOT_DEFINED = -1;
	
	public Task(String name, String type, String aPluginKey) {
		setName(name);
		setType(type);
		setPluginKey(aPluginKey);
		
		mExperiments = new ArrayList<Experiment>();
	}
	
	public void addExperiment(Experiment experiment) {
		mExperiments.add(experiment);
	}
	
	public List<Experiment> getExperiments() {
		return mExperiments;
	}
	
	public String getType() {
		return mType;
	}

	public void setType(String aType) {
		this.mType = aType;
	}
	
	public String getName() {
		return mName;
	}

	public void setName(String aName) {
		this.mName = aName;
	}
	
	public String getPluginKey() {
		return mPluginKey;
	}

	public void setPluginKey(String aPluginKey) {
		this.mPluginKey = aPluginKey;
	}
	
	public int getCurrentExperiment() {
		return mCurrExperiment;
	}

	public void setCurrentExperiment(int experiment) {
		this.mCurrExperiment = experiment;
	}
}
