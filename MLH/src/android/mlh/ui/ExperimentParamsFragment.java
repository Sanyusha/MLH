package android.mlh.ui;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.mlh.bl.plugins.PluginManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mlh.R;

/**
 * Represents parameters of an experiment.
 * The layout and the functionality of this fragment are created by the plugin
 * and not by by MLH main app.
 */
public class ExperimentParamsFragment extends Fragment {

	private final static String LOG_D = "ExperimentParamsFragment";
	private final static int idOffset = 100;

	private static ExperimentParamsFragment f;

	private LayoutInflater mInflater;
	private OnClickListenerProxy listener;
	private View mView;
	private Bundle mInitState;
	private String mPackageName;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mInflater = inflater;

		mView = mInflater.inflate(R.layout.fragment_experiment_params, container, false);

		listener = new OnClickListenerProxy();

		inflateToView();

		registerButtonListener();

		populateExperimentForm(mInitState);

		return mView;
	}

	/**
	 * Inflate plugin layout to the fragment
	 */
	private void inflateToView() {
		try {
			Log.d(LOG_D, PluginManager.getInstance().getCurrentPluginName());

			String packageName = mPackageName;
			ApplicationInfo info = getActivity().getPackageManager().getApplicationInfo( packageName, 0 );

			Resources res = getActivity().getPackageManager().getResourcesForApplication( info );

			// The following line takes the first layout created in the plugin.
			// 0x7f030000 - is the integer id of the first layout
			XmlResourceParser xres = res.getLayout( 0x7f030000 );

			ViewGroup parentView = (ViewGroup) mView.findViewById( R.id.expLL );
			parentView.removeAllViews();

			mInflater.inflate(xres, parentView);

			adjustSubViewIds(parentView);
		} catch (NameNotFoundException e) {
			Log.d(LOG_D, e.getMessage());
		}
	}

	/**
	 * Creates a new fragment that represents an experiment.
	 * Gets a bundle with current state as parameter.
	 */
	public static ExperimentParamsFragment newInstance(String aPackageName, Bundle aInitState) {
		if (f == null) {
			f = new ExperimentParamsFragment();
		}

		f.mInitState = aInitState;
		f.mPackageName = aPackageName;

		return f;
	}

	public Bundle captureParametersState() {
		Log.d(LOG_D, "Capturing parameters state from fragment.");

		Bundle state = new Bundle();
		ViewGroup parent = (ViewGroup) mView.findViewById(R.id.expLL);

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
					Log.d(LOG_D, "tv id = " + e.getId());
					state.putString( Integer.toString(id), e.getText().toString() );
				}
		}        
	}

	class OnClickListenerProxy implements View.OnClickListener {
		public void onClick( View v ) {
			int id = v.getId() - idOffset;

			if( PluginManager.getInstance().getCurrentPlugin() != null ) {
				ViewGroup parent = (ViewGroup) mView.findViewById( R.id.expLL );
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

	private void applyUpdates(Bundle update) {
		ViewGroup parent = (ViewGroup) mView.findViewById( R.id.expLL );
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

	/**
	 * Populates the field of the Experiment form
	 * with the bundle received from the current plugin.
	 */
	public void populateExperimentForm(Bundle state) {
		Log.d(LOG_D, "populating experiment form from " + state);

		for (String key: state.keySet()) {
			//TextView tv = (TextView) findViewById(Integer.getInteger(key) + idOffset);
			int viewID = Integer.parseInt(key) + idOffset;

			Log.d(LOG_D, "key = " + viewID + ", value = " + state.getString(key));
			TextView tv = (TextView) mView.findViewById(viewID);

			tv.setText(state.getString(key));
		}     
	}

	private void registerButtonListener() {
		ViewGroup parentView = (ViewGroup) mView.findViewById( R.id.expLL );

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
}