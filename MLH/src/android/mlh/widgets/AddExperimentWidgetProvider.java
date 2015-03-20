package android.mlh.widgets;

import com.example.mlh.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.mlh.ui.NewTaskActivity;
import android.widget.RemoteViews;

public class AddExperimentWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		ComponentName thisWidget = new ComponentName(context,
				AddExperimentWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int widgetId : allWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.add_experiment_widget);
		
			
			Intent intent = new Intent(context, NewTaskActivity.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
			
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			remoteViews.setOnClickPendingIntent(R.id.set_task_tv, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
			
			PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent, 0);
			remoteViews.setOnClickPendingIntent(R.id.goto_task_widget_tv, pendingIntent2);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
			
		}
	}
	
}
