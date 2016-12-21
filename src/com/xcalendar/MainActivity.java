package com.xcalendar;

import android.app.Activity;    
import android.content.Intent;  
import android.os.Build;
import android.os.Bundle; 
import android.view.Menu;
import android.view.View;  
import android.view.View.OnClickListener;  
import android.widget.Button; 
 
public class MainActivity extends Activity implements OnClickListener {
    private Button _modify, _Control, _read ; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		_read = (Button)findViewById(R.id.read);
		_read.setOnClickListener(this);
		_modify = (Button)findViewById(R.id.modify);
		_modify.setOnClickListener(this);
		_Control = (Button)findViewById(R.id.control);
		_Control.setOnClickListener(this);
	}

	 /*
	 *«ö¶s³]©w
	 */
	@Override
	public void onClick(View v){
		// TODO Auto-generated method stub
		switch( v.getId() ){
		case R.id.read:
			viewAllCalender();
			/*Intent intent = getPackageManager()
					        getLaunchIntentForPackage("com.android.calendar");*/
			
		    break;
		case R.id.modify:
			Intent intent1 = new Intent();
			intent1.setClass(MainActivity.this, Modify.class);
			startActivity(intent1); 
			MainActivity.this.finish(); 
		    break; 
		case R.id.control:
			Intent intent2 = new Intent();
			intent2.setClass(MainActivity.this, Control.class);
			startActivity(intent2); 
			MainActivity.this.finish(); 
		    break; 
		default:
			break; 
		}
	}
	
	void viewAllCalender() {
        Intent i = new Intent();
        if(Build.VERSION.SDK_INT >= 8 && Build.VERSION.SDK_INT <= 14){
            i.setClassName("com.android.calendar","com.android.calendar.LaunchActivity");
        }else if(Build.VERSION.SDK_INT >= 15){    
            i.setClassName("com.google.android.calendar", "com.android.calendar.LaunchActivity");
        }else{
            i.setClassName("com.android.calendar","com.android.calendar.LaunchActivity");
        }
        startActivity(i);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

}
