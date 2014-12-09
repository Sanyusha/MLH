package android.mlh.bl.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.mlh.bl.tasks.Task;
import android.mlh.utils.constants.FileConstatns;
import android.util.Log;

/** 
 * FileManger is used to serialize/de-serialize Task instances.
 * File naming convention: TASK_FILE_NAME_PREFIX%taskName%OBJECT_FILE_NAME_EXT
 * is used for serialization.
 * PREFIX, EXT constants are defined in android.mlh.utils.constants.
 * Also deletes Serialized Tasks from the storage.
 */
public class FileManager {
	
	private static FileManager _instance = null;
	private Context mContext;
	
	private FileManager() {
	}
	
	private void switchContext(Context context) {
		mContext = context;
	}
	
	public void saveTask(Task task) throws IOException {
		String filename = FileConstatns.TASK_FILE_NAME_PREFIX + task.getName() + 
				FileConstatns.OBJECT_FILE_NAME_EXT;
		
		FileOutputStream fileOut;

		fileOut = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		
        out.writeObject(task);
        
        out.close();
        fileOut.close();
	}
	
	public Task getTask(String taskName) throws IOException, ClassNotFoundException {
		String filename = taskName + 
				FileConstatns.OBJECT_FILE_NAME_EXT;
		
		FileInputStream fileIn;
		fileIn = mContext.openFileInput(filename);
		
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Task task = (Task) in.readObject();
        in.close();
        fileIn.close();
        
        return task;
	}
	
	public void deleteTask(String taskName) throws IOException, ClassNotFoundException {
		File file = new File(getTaskPath(taskName));
        
        file.delete();
	}
	
	public String getTaskPath(String taskName) {
		String filename = taskName + 
				FileConstatns.OBJECT_FILE_NAME_EXT;
		
        File[] fileList = getTasksDir().listFiles();
		
		for(File file: fileList){
			Log.d("FileManager", file.getAbsolutePath());
			
			if (file.isFile() && 
					(file.getName().endsWith(FileConstatns.OBJECT_FILE_NAME_EXT) 
					&& file.getName().startsWith(FileConstatns.TASK_FILE_NAME_PREFIX)
					&& file.getName().indexOf(taskName) >= 0)) {
				return file.getAbsolutePath();
			}
		}
		
		return "PATH NOT FOUND";
	}
	
	private File getTasksDir() {
		return mContext.getFilesDir();
	}
	
	/**
	 * Reads and returns list of files that 
	 * - read list of files in task_dir 
	 * - from these files filter ones that match TASK_FILE_NAME_PREFIX%name%OBJECT_FILE_NAME_EXT
	 * - each file is a serialized task (e.g. list of Experiments)
	 * - returns list of filenames (i.e. tasks)
	 * @return 
	 */
	public List<String> getSavedTasks() {
		ArrayList<String> savedTasks = new ArrayList<String>();
		
		File[] fileList = getTasksDir().listFiles();
		
		for(File file: fileList){
			if (file.isFile() && 
					(file.getName().endsWith(FileConstatns.OBJECT_FILE_NAME_EXT) 
					&& file.getName().startsWith(FileConstatns.TASK_FILE_NAME_PREFIX))) {
				savedTasks.add(file.getName().substring(0, file.getName().length() -
						FileConstatns.OBJECT_FILE_NAME_EXT.length()));
			}
		}
		
		return savedTasks;
	}
	
	public static FileManager getInstance(Context context) {
		if (_instance == null) {
			_instance = new FileManager();
		}
		
		_instance.switchContext(context);
		
		return _instance;
	}
}
