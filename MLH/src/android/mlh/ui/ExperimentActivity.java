package android.mlh.ui;

import java.io.IOException;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.mlh.aidl.Experiment;
import android.mlh.bl.files.FileManager;
import android.mlh.bl.plugins.PluginManager;
import android.mlh.bl.tasks.TaskManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mlh.R;

public class ExperimentActivity extends Activity{
	
	private LayoutInflater inflater;
	int idOffset = 100;
	private OnClickListenerProxy listener;
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);
        
        inflater = LayoutInflater.from( this );
        listener = new OnClickListenerProxy();
        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        
        try {
			txtTitle.setText(PluginManager.getInstance().getCurrentPlugin().getPluginType());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        inflateToView();
        
        registerButtonListener();
        
        setSaveButtonListener();
    }
    
    private void setSaveButtonListener() {
    	Button btn = (Button) findViewById(R.id.btnSave);
    	
    	btn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Experiment result = new Experiment();
				
				try {
					PluginManager.getInstance().getCurrentPlugin().setExperiment(result);
					
					Log.d("ExperimentActivity", "result set in plugin");
					
					result = PluginManager.getInstance().getCurrentPlugin().getExperiment(captureState());
					
					TaskManager.getInstance().getCurrentTask().addExperiment(result);
					
					FileManager.getInstance(getApplicationContext()).saveTask(TaskManager.getInstance().getCurrentTask());
					if (result == null) {
						Log.d("ExperimentActivity", "getExperiment() returned null");
					} else {
						Log.d("ExperimentActivity", "getExperiment() returned result");
					}
					
				} catch (RemoteException e) {
					Log.d("ExperimentActivity", e.getMessage());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Log.d("ExperimentActivity", "Experiment added: " + result.toString());
			}
		});
    }
    
    private Bundle captureState() {
    	Bundle state = new Bundle();
		ViewGroup parent = (ViewGroup)findViewById( R.id.expLL );
		
		captureState(parent, state);
		
		return state;
    }
    
    private void captureState(ViewGroup parent, Bundle state) {
        for( int i = 0 ; i < parent.getChildCount() ; ++i ) {
            View v = parent.getChildAt( i );
            if( v instanceof ViewGroup ) 
                captureState( (ViewGroup)v, state );
            else
            if( v instanceof TextView ) {
                TextView e = (TextView)v;
                int id = e.getId() - idOffset;
                state.putString( Integer.toString(id), e.getText().toString() );
            }
        }        
    }
    private void registerButtonListener() {
    	ViewGroup parentView = (ViewGroup)findViewById( R.id.expLL );
    	
        registerButtonListener( parentView, listener);
    }

    private void registerButtonListener(ViewGroup parent, View.OnClickListener listener) {
        for( int i = 0 ; i < parent.getChildCount() ; ++i ) {
            View v = parent.getChildAt(i);
            if( v instanceof ViewGroup ) 
                registerButtonListener( (ViewGroup)v, listener );
            else
            if( v instanceof Button ) {
                Button b = (Button)v;
                b.setOnClickListener( listener );
            }
        }
    }
    
    private void inflateToView( 
            
             ) {
		try {
			String packageName = "android.mlh.cooking";
		ApplicationInfo info = getPackageManager().getApplicationInfo( packageName, 0 );
		
		Resources res = getPackageManager().getResourcesForApplication( info );
		XmlResourceParser xres = res.getLayout( 0x7f030000 );
		
		ViewGroup parentView = (ViewGroup)findViewById( R.id.expLL );
		parentView.removeAllViews();
		
		View view = inflater.inflate( xres, parentView );
		
		adjustSubViewIds( parentView);
		} catch( NameNotFoundException ex ) {
		//Log.e( LOG_TAG, "NameNotFoundException", ex );
		}
	}
    
    class OnClickListenerProxy implements View.OnClickListener {
        public void onClick( View v ) {
        	int id = v.getId() - idOffset;
        	
            if( PluginManager.getInstance().getCurrentPlugin() != null ) {
            	ViewGroup parent = (ViewGroup)findViewById( R.id.expLL );
            	Bundle state = new Bundle();
				captureState(parent, state);
				Bundle result = null;
				
                try {
                    result = PluginManager.getInstance().getCurrentPlugin().onClick( id,state );
                } catch( RemoteException ex ) {
                    
                }
				
                
                
				if( result != null ) {
					
						Log.d("result", "not null");
						applyUpdates(result);
					
				} else {
					Log.d("result", "null");
				}
			}
        }

        
        
    }
    
    private void adjustSubViewIds( ViewGroup parent ) {
        for( int i = 0 ; i < parent.getChildCount() ; ++i ) {
            View v = parent.getChildAt( i );
            if( v instanceof ViewGroup ) 
                adjustSubViewIds( (ViewGroup)v);
            else {
                int id = v.getId();
                if( id != View.NO_ID )
                    v.setId( id+idOffset );
            }
        }
    }
    
    
    
    private void applyUpdates(Bundle update) {
    	ViewGroup parent = (ViewGroup)findViewById( R.id.expLL );
		applyUpdates(parent, update );		
	}

	private void applyUpdates( ViewGroup parent, Bundle update ) {
        for( int i = 0 ; i < parent.getChildCount() ; ++i ) {
            View v = parent.getChildAt( i );
            if( v instanceof ViewGroup ) 
                applyUpdates( (ViewGroup)v, update );
            else
            if( v instanceof TextView ) {
                TextView tv = (TextView)v;
				int id = tv.getId() - idOffset;
				if( id != View.NO_ID ) {
					String updateObj = update.getString( Integer.toString( id ) );
					if( updateObj != null )
						tv.setText( updateObj );
				}
            }
        }        		
	}
}
