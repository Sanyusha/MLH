package android.mlh.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.mlh.R;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button btnNewTask = (Button) findViewById(R.id.btn_new_task);
        btnNewTask.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				openNewTaskActivity();
			}
		});
    }
    
    /** Called when the user clicks the Send button */
    public void openNewTaskActivity() {
        Intent intent = new Intent(this, NewTaskActivity.class);
        startActivity(intent);
    }
}
