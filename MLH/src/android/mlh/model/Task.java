package android.mlh.model;

/**
 * Basic task class. 
 * Conducted experiment is related to a specific task.
 *
 */
public class Task {
	private String type;
	
	public Task() {
		
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
