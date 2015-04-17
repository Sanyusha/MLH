package android.mlh.widgets;

import java.io.IOException;

import com.example.mlh.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.mlh.bl.files.FileManager;
import android.mlh.bl.plugins.PluginManager;
import android.mlh.bl.tasks.Task;
import android.mlh.bl.tasks.TaskManager;
import android.mlh.ui.ExperimentListActivity;
import android.widget.RemoteViews;

public class AddExperimentWidgetProvider extends AppWidgetProvider {

	private static final String START_ACTIVITY_ACTION = "com.android.mlh.widgets.START_ACTIVITY_ACTION";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		ComponentName thisWidget = new ComponentName(context,
				AddExperimentWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int widgetId : allWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.add_experiment_widget);

			// AddExperimentWidgetProvider
			// Sending to Self
			Intent intent = new Intent(context, AddExperimentWidgetProvider.class);
			intent.setAction(START_ACTIVITY_ACTION);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetId, intent, 0);
			remoteViews.setOnClickPendingIntent(R.id.goto_task_widget_tv,
					pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);

			// AddExperimentWidgetConfigurationActivity
			Intent intent2 = new Intent(context,
					AddExperimentWidgetConfigurationActivity.class);
			intent2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

			PendingIntent pendingIntent2 = PendingIntent.getActivity(context,
					widgetId, intent2, 0);
			remoteViews.setOnClickPendingIntent(R.id.set_task_tv,
					pendingIntent2);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if(START_ACTIVITY_ACTION.equals(intent.getAction())){
			Task task = null;
			try {
				int widget_id = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
						AppWidgetManager.INVALID_APPWIDGET_ID);
				String taskname = (WidgetManager.getInstance(context.getApplicationContext())).getTasknameForWidgetId(widget_id);
				task = FileManager.getInstance(context.getApplicationContext())
						.getTask(taskname);
				PluginManager.getInstance().setCurrentPlugin(task.getPluginKey());
				TaskManager.getInstance().setCurrentTask(task);
				Intent start_activity_intent = new Intent(context.getApplicationContext(), ExperimentListActivity.class);
				start_activity_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
				(context.getApplicationContext()).startActivity(start_activity_intent);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}	

	@Override
	public void onDisabled(Context context) {
		WidgetManager.getInstance(context.getApplicationContext()).deleteWidgetFile();
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		WidgetManager widgetManager = WidgetManager.getInstance(context.getApplicationContext());
		for (int i = 0; i < appWidgetIds.length; i++) {
			widgetManager.deleteWidgetId(appWidgetIds[i]);
		}
	}
}
