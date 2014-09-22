package android.mlh.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.mlh.R;

public class NewTaskActivity extends ListActivity{
	
	public static final String ACTION_PICK_PLUGIN = "aexp.intent.action.PICK_PLUGIN";
	static final String KEY_PKG = "pkg";
	static final String KEY_SERVICENAME = "servicename";
	static final String KEY_ACTIONS = "actions";
	static final String KEY_CATEGORIES = "categories";
	static final String BUNDLE_EXTRAS_CATEGORY = "category";
	
	private PackageBroadcastReceiver packageBroadcastReceiver;
	private IntentFilter packageFilter;
	private ArrayList<HashMap<String,String>> services;
	private ArrayList<String> categories;
	private SimpleAdapter itemAdapter;
	
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        
		fillPluginList();
		
		itemAdapter =
            new SimpleAdapter(this, 
				services,
				R.layout.services_row,
				new String[] { KEY_PKG,KEY_SERVICENAME,KEY_ACTIONS,KEY_CATEGORIES },
				new int[] { R.id.pkg, R.id.servicename, R.id.actions, R.id.categories }
				);
        setListAdapter(itemAdapter);

		packageBroadcastReceiver = new PackageBroadcastReceiver();
		packageFilter = new IntentFilter();
		packageFilter.addAction( Intent.ACTION_PACKAGE_ADDED  );
		packageFilter.addAction( Intent.ACTION_PACKAGE_REPLACED );
		packageFilter.addAction( Intent.ACTION_PACKAGE_REMOVED );
		packageFilter.addCategory( Intent.CATEGORY_DEFAULT ); 
		packageFilter.addDataScheme( "package" );
    }
	
    protected void onStart() {
		super.onStart();
		registerReceiver( packageBroadcastReceiver, packageFilter );
	}

	protected void onStop() {
		super.onStop();
		unregisterReceiver( packageBroadcastReceiver );
	}

    protected void onListItemClick (ListView l, View v, int position, long id) {
		String category = categories.get( position );
		if( category.length() > 0 ) {
			Intent intent = new Intent();
			intent.setClassName( 
                    "aexp.pluginapp", 
                    "aexp.pluginapp.InvokeOp" );
			intent.putExtra( BUNDLE_EXTRAS_CATEGORY, category );
			startActivity( intent );
		}
    }
    
	private void fillPluginList() {
		services = new ArrayList<HashMap<String,String>>();
		
		categories = new ArrayList<String>();
		
        PackageManager packageManager = getPackageManager();
        
        Intent baseIntent = new Intent(ACTION_PICK_PLUGIN);
		baseIntent.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
		
        List<ResolveInfo> list = packageManager.queryIntentServices(baseIntent,
                PackageManager.GET_RESOLVED_FILTER );
        
        for( int i = 0 ; i < list.size() ; ++i ) {
            ResolveInfo info = list.get( i );
            ServiceInfo sinfo = info.serviceInfo;
			IntentFilter filter = info.filter;
			
            if( sinfo != null ) {
                HashMap<String,String> item = new HashMap<String,String>();
                item.put( KEY_PKG, sinfo.packageName );
                item.put( KEY_SERVICENAME, sinfo.name );
				String firstCategory = null;
				if( filter != null ) {
					StringBuilder actions = new StringBuilder();
					for( Iterator<String> actionIterator = filter.actionsIterator() ; actionIterator.hasNext() ; ) {
						String action = actionIterator.next();
						if( actions.length() > 0 )
							actions.append( "," );
						actions.append( action );
					}
					StringBuilder categories = new StringBuilder();
					for( Iterator<String> categoryIterator = filter.categoriesIterator() ;
							categoryIterator.hasNext() ; ) {
						String category = categoryIterator.next();
						if( firstCategory == null )
							firstCategory = category;
						if( categories.length() > 0 )
							categories.append( "," );
						categories.append( category );
					}
					item.put( KEY_ACTIONS,new String( actions ) );
					item.put( KEY_CATEGORIES,new String ( categories ) );
				} else {
					item.put( KEY_ACTIONS,"<null>" );
					item.put( KEY_CATEGORIES,"<null>" );
				}
				if( firstCategory == null )
					firstCategory = "";
				categories.add( firstCategory );
                services.add( item );
            }
        }
    }
	
	class PackageBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			services.clear();
			fillPluginList();
			itemAdapter.notifyDataSetChanged();
		}
	}
}
