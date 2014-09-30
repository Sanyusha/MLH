package android.mlh.bl;

/**
 * Basic task class. 
 * Conducted experiment is related to specific task.
 *
 */
public class Task {
	private String mName, mType;
	
	public Task() {
		
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
