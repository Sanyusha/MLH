package android.mlh.widgets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.mlh.constants.FileConstatns;

/**
 * Class that controls almost all the interactions with Widgets
 *
 */
public class WidgetManager {

	private static WidgetManager _instance;

	private Context mContext;

	/**
	 * Standard and the only accessible singleton instance getter, needs to
	 * receive context to work properly. It uses the context in order to write
	 * Context specific file that holds the WidgetMapping
	 * 
	 * @param context
	 *            Application Context to work with
	 * @return an instance
	 */
	public static WidgetManager getInstance(Context context) {
		if (_instance == null) {
			_instance = new WidgetManager();
		}

		_instance.switchContext(context);

		return _instance;
	}

	/**
	 * Function for switching context
	 * 
	 * @param context
	 */
	private void switchContext(Context context) {
		mContext = context;
	}

	/**
	 * Function for reading and getting the WidgetMapping, the Exceptions are
	 * non-related to user input, aside from the context that is supplied to the
	 * instance getter, so are not supposed to happen
	 * 
	 * @return WidgetMapping
	 */
	private Map<Integer, String> readWidgetMapping()
			throws StreamCorruptedException, IOException,
			ClassNotFoundException {
		// Getting the file
		String filename = FileConstatns.WIDGET_ADD_EXPERIMENT_FILE_NAME
				+ FileConstatns.OBJECT_FILE_NAME_EXT;
		FileInputStream fileIn;
		fileIn = mContext.openFileInput(filename);
		ObjectInputStream in = new ObjectInputStream(fileIn);

		// reading the map from the file
		@SuppressWarnings("unchecked")
		Map<Integer, String> widgetMapping = (Map<Integer, String>) in
				.readObject();

		// Closing the streams
		in.close();
		fileIn.close();

		return widgetMapping;
	}

	/**
	 * Function for writing the WidgetMapping, the Exceptions are non-related to
	 * user input, aside from the context that is supplied to the instance
	 * getter, so are not supposed to happen
	 * 
	 * @param widgetMapping
	 *            WidgetMapping to be written
	 */
	private void writeWidgetMapping(Map<Integer, String> widgetMapping)
			throws IOException {
		// Getting the file
		String filename = FileConstatns.WIDGET_ADD_EXPERIMENT_FILE_NAME
				+ FileConstatns.OBJECT_FILE_NAME_EXT;
		FileOutputStream fileOut;
		fileOut = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);

		// writing new map in the file
		out.writeObject(widgetMapping);

		// Closing the streams
		out.close();
		fileOut.close();
	}

	/**
	 * Function for creating a new WidgetFile to hold an empty WidgetMapping. If
	 * the function called and there are widgets mapped to the old WidgetMapping
	 * the Mapping will be overwritten and old will be undefined or empty
	 */
	@SuppressLint("UseSparseArrays")
	public void createWidgetFile() {
		try {
			// Creating or opening existing file
			String filename = FileConstatns.WIDGET_ADD_EXPERIMENT_FILE_NAME
					+ FileConstatns.OBJECT_FILE_NAME_EXT;
			FileOutputStream fileOut;
			fileOut = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
			// Creating empty map
			HashMap<Integer, String> widget_map = new HashMap<Integer, String>();
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			// Writing an empty map to the file
			out.writeObject(widget_map);
			out.close();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Function for checking if the WidgetFile exists
	 * 
	 * @return
	 */
	public boolean widgetFileExists() {
		File file = mContext
				.getFileStreamPath(FileConstatns.WIDGET_ADD_EXPERIMENT_FILE_NAME
						+ FileConstatns.OBJECT_FILE_NAME_EXT);
		if (file == null || !file.exists()) {
			return false;
		}
		return true;
	}

	/**
	 * Exclusively deleting WidgetFile, calling this function with bound
	 * widgets existing will cause hazards
	 */
	public void deleteWidgetFile() {
		String filename = FileConstatns.WIDGET_ADD_EXPERIMENT_FILE_NAME
				+ FileConstatns.OBJECT_FILE_NAME_EXT;
		File filesDir = mContext.getFilesDir();
		File widget_map_file = new File(filesDir.getPath() + filename);
		widget_map_file.delete();
	}

	/**
	 * Assigning of WidgetId to a Taskname in the mapping
	 * 
	 * @param taskname
	 * @param widget_id
	 */
	public void setTasknameForWidgetId(String taskname, int widget_id) {
		try {

			Map<Integer, String> widgetMapping = readWidgetMapping();

			widgetMapping.put(widget_id, taskname);

			writeWidgetMapping(widgetMapping);

		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Fetching a bound Taskname to the given WidgetId
	 * 
	 * @param widget_id
	 * @return bound Taskname or null if it doesn't exist
	 */
	public String getTasknameForWidgetId(int widget_id) {
		String taskname = null;
		try {

			Map<Integer, String> widgetMapping = readWidgetMapping();

			taskname = widgetMapping.get(widget_id);

		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return taskname;
	}

	/**
	 * Deleting an entry with the given WidgetId
	 * 
	 * @param widget_id
	 */
	public void deleteWidgetId(int widget_id) {
		try {

			Map<Integer, String> widgetMapping = readWidgetMapping();

			widgetMapping.remove(widget_id);

			writeWidgetMapping(widgetMapping);

		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Fetcing all the Ids for the given Taskname
	 * 
	 * @param taskname
	 * @return A list holding all the Ids with the given Taskname
	 */
	public List<Integer> getIdsForTaskname(String taskname) {
		List<Integer> widget_ids = new LinkedList<Integer>();

		try {

			Map<Integer, String> widgetMapping = readWidgetMapping();

			for (Map.Entry<Integer, String> entry : widgetMapping.entrySet()) {
				if (entry.getValue().equals(taskname)) {
					widget_ids.add(entry.getKey());
				}
			}

		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return widget_ids;
	}

	// notifyTaskRemoved

	//

}

// TODO
// // // // // // // // // The read and write   procedure design // // // // // // // // // //
// Map readWidgetMapping()
// ...something with map...
// void writeWidgetMapping(Map<Integer, String>)

// // // // // // // // // Functions to be coded // // // // // // // // // 
// [V] create(file) delete(file) read(file) write(file)

// [V] int [] getIdsForTaskname (String taskname)

// [X] notifyTaskRemoved
//// // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // 