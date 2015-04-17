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
import android.mlh.constants.FileConstatns;
import android.mlh.constants.UIConstatns;
import android.util.Log;

/**
 * FileManger is used to serialize/de-serialize Task instances. File naming
 * convention: TASK_FILE_NAME_PREFIX%taskName%OBJECT_FILE_NAME_EXT is used for
 * serialization. PREFIX, EXT constants are defined in
 * android.mlh.utils.constants. Also deletes Serialized Tasks from the storage.
 * We are using Singleton pattern. GetInstance static method receives Context,
 * so the FileManager will always be with the right context.
 */
public class FileManager {

	private static final String LOG_D = UIConstatns.LOG_PREFIX + "FileManager";

	/** FileManager instance - only one */
	private static FileManager _instance = null;

	/** context - so the FileManager will be updated constantly */
	private Context mContext;

	private FileManager() {
	}

	/**
	 * Switching context of file manager
	 * 
	 * @param context
	 *            - Context
	 */
	private void switchContext(Context context) {
		mContext = context;
	}

	/**
	 * Serializes the given Task, and writes it to a file. Basically, saves the
	 * Task obj as a file.
	 * 
	 * @param task
	 *            - Task
	 * @throws IOException
	 */
	public void saveTask(Task task) throws IOException {
		String filename = FileConstatns.TASK_FILE_NAME_PREFIX + task.getName()
				+ FileConstatns.OBJECT_FILE_NAME_EXT;

		Log.d(LOG_D, "saveTask(): " + filename + " started");

		FileOutputStream fileOut;

		fileOut = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);

		out.writeObject(task);

		Log.d(LOG_D, "saveTask(): " + filename + " completed");

		out.close();

		fileOut.close();
	}

	/**
	 * De-serializes Task from file to obj, given the Task's name.
	 * 
	 * @param taskName
	 * @return the Task that was de-serialized
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Task getTask(String taskName) throws IOException,
			ClassNotFoundException {
		String filename = FileConstatns.TASK_FILE_NAME_PREFIX + taskName
				+ FileConstatns.OBJECT_FILE_NAME_EXT;

		FileInputStream fileIn;
		fileIn = mContext.openFileInput(filename);

		ObjectInputStream in = new ObjectInputStream(fileIn);
		Task task = (Task) in.readObject();
		in.close();
		fileIn.close();

		return task;
	}

	/**
	 * Deletes the serialized Task - e.i. deletes file that holds serialized
	 * Task.
	 * 
	 * @param taskName
	 *            - the name of the Task
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void deleteTask(String taskName) throws IOException,
			ClassNotFoundException {
		File file = new File(getTaskPath(taskName));

		file.delete();
	}

	/**
	 * Returns path to the file which holds serialized Task
	 * 
	 * @param taskName
	 * @return on success - path to the file which holds serialized Task, or
	 *         'PATH NOT FOUND' on failure
	 */
	public String getTaskPath(String taskName) {
		String filename = taskName + FileConstatns.OBJECT_FILE_NAME_EXT;

		File[] fileList = getTasksDir().listFiles();

		for (File file : fileList) {
			Log.d(LOG_D,
					"File found for <" + taskName + ">: "
							+ file.getAbsolutePath());

			if (file.isFile()
					&& (file.getName().endsWith(
							FileConstatns.OBJECT_FILE_NAME_EXT)
							&& file.getName().startsWith(
									FileConstatns.TASK_FILE_NAME_PREFIX) && file
							.getName().indexOf(taskName) >= 0)) {
				return file.getAbsolutePath();
			}
		}

		return null;
	}

	/**
	 * Returns current directory where serialized Tasks are stored
	 * 
	 * @return current directory where serialized Tasks are stored
	 */
	private File getTasksDir() {
		return mContext.getFilesDir();
	}

	/**
	 * Reads and returns list of files that - read list of files in task_dir -
	 * from these files filter ones that match
	 * TASK_FILE_NAME_PREFIX%name%OBJECT_FILE_NAME_EXT - each file is a
	 * serialized task (e.g. list of Experiments) - returns list of filenames
	 * (i.e. tasks)
	 * 
	 * @return List of filenames in the current Task directory
	 */
	public List<String> getSavedTasks() {
		ArrayList<String> savedTasks = new ArrayList<String>();

		File[] fileList = getTasksDir().listFiles();

		for (File file : fileList) {
			if (file.isFile()
					&& (file.getName().endsWith(
							FileConstatns.OBJECT_FILE_NAME_EXT) && file
							.getName().startsWith(
									FileConstatns.TASK_FILE_NAME_PREFIX))) {

				String taskName = file.getName();
				int extLength = FileConstatns.OBJECT_FILE_NAME_EXT.length();
				int prefixLength = FileConstatns.TASK_FILE_NAME_PREFIX.length();

				taskName = taskName.substring(0, taskName.length() - extLength);

				taskName = taskName.substring(prefixLength, taskName.length());

				savedTasks.add(taskName);
			}
		}

		return savedTasks;
	}

	/**
	 * Returns reference to the FileManager. Uses lazy instantiation.
	 * 
	 * @param context
	 *            - given context
	 * @return reference to the FileManager
	 */
	public static FileManager getInstance(Context context) {
		if (_instance == null) {
			_instance = new FileManager();
		}

		_instance.switchContext(context);

		return _instance;
	}
}
