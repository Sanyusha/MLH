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
	private String mName, mType;
	private List<Experiment> mExperiments;
	
	public Task(String name, String type) {
		setName(name);
		setType(type);
		
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

	public void setType(String mType) {
		this.mType = mType;
	}
	
	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}
}
