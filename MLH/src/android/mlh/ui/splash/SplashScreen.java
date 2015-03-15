package android.mlh.ui.splash;

import android.app.Activity;
import android.content.Intent;
import android.mlh.constants.UIConstatns;
import android.mlh.logger.Logger;
import android.mlh.ui.FindPluginsTask;
import android.mlh.ui.MainActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import com.example.mlh.R;

public class SplashScreen extends Activity {
	private static final String LOG_TAG = UIConstatns.LOG_PREFIX + "SplashScreen";
	
	/**
	 * The thread to process splash screen events
	 */
	private Thread mSplashThread;    
	
	private TextView m_StatusText;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		Logger.log(LOG_TAG, Logger.INFO_PRIORITY, "Activity started");
		
		m_StatusText = (TextView) findViewById(R.id.textView1);
		
		final SplashScreen sPlashScreen = this;
		
		new FindPluginsTask(getApplicationContext(), m_StatusText).execute();
		
		// The thread to wait for splash screen events
		mSplashThread =  new Thread(){
			@Override
			public void run(){
				try {
					synchronized(this){
						// Wait given period of time or exit on touch
						wait(5000);
					}
				}
				catch(InterruptedException ex){                    
				}

				// Run next activity
				Intent intent = new Intent(SplashScreen.this, MainActivity.class);

				startActivity(intent);

				finish();
			}
		};

		mSplashThread.start();        
	}

	/**
	 * Processes splash screen touch events
	 */
	@Override
	public boolean onTouchEvent(MotionEvent evt)
	{
		if(evt.getAction() == MotionEvent.ACTION_DOWN)
		{
			synchronized(mSplashThread){
				mSplashThread.notifyAll();
			}
		}
		return true;
	}   

}
