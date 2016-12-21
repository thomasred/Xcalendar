package com.xcalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar; 
import java.util.Locale;

import android.app.Activity;  
import android.content.ContentValues;  
import android.content.Intent;
import android.database.Cursor;  
import android.net.Uri;  
import android.os.Build;  
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.View;  
import android.view.View.OnClickListener;  
import android.widget.Button; 
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
 
public class Modify extends Activity implements OnClickListener, OnCheckedChangeListener {
    private Button _confirm, _back;
	private EditText _hour, _af_hour, _min, _af_min, _hr;
    private EditText _event, _ps;
    private CheckBox _check;
    private TextView _date;
    private RadioButton _ch;
    private static String calendarUri = "";  
    private static String calendarEventUri = "";  
    //private static String calendarReminderUri = "";
    //String sdk = Build.VERSION.SDK;
    //���F�ݮe���P���������,2.2�H��Uri�o�ͧ���  
    static{ 
        if( Build.VERSION.SDK_INT >= 8 ){  
            calendarUri = "content://com.android.calendar/calendars";  
            calendarEventUri = "content://com.android.calendar/events";  
            //calendarReminderUri = "content://com.android.calendar/reminders";  
  
        }else{  
            calendarUri = "content://calendar/calendars";  
            calendarEventUri = "content://calendar/events";  
            //calendarReminderUri = "content://calendar/reminders";          
        }  
    } 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify);
		_date = (TextView)findViewById(R.id.date);
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy�~MM��dd��  EEE", 
				                                     Locale.getDefault());
		_date.setText(sdf.format(calendar.getTime()));
		init();
		_confirm = (Button)findViewById(R.id.confirm);
		_confirm.setOnClickListener(this);
		_back = (Button)findViewById(R.id.back);
		_back.setOnClickListener(this);
	}
	
	void init(){
		_hour = (EditText)findViewById(R.id.hour);
		_af_hour = (EditText)findViewById(R.id.af_hour);
		_min = (EditText)findViewById(R.id.min);
		_af_min = (EditText)findViewById(R.id.af_min);
		_event = (EditText)findViewById(R.id.event);
		_check = (CheckBox) findViewById(R.id.check); 
		_check.setOnCheckedChangeListener(this); 
		_ps = (EditText)findViewById(R.id.ps);
		_ch = (RadioButton)findViewById(R.id.timech1);
		_hr = (EditText)findViewById(R.id.hr);
	}

	 /*
	 *���s�]�w
	 */
	@Override
	public void onClick(View v){
		// TODO Auto-generated method stub
		switch( v.getId() ){
		case R.id.back:
			Intent intent = new Intent();
			intent.setClass(Modify.this, MainActivity.class);
			startActivity(intent); 
			Modify.this.finish(); 
		    break;
		case R.id.confirm: // �ק蘆���{
			if(_ch.isChecked()){
				_Modify();  
	        }else{
	            _Modify1();
	        }
		    break;
		default:
			break; 
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
		// TODO Auto-generated method stub
		if( isChecked )
			Toast.makeText(Modify.this, "��ܫO�K!", Toast.LENGTH_LONG).show();
		else
			Toast.makeText(Modify.this, "�����O�K!", Toast.LENGTH_LONG).show();
	}
    
	void _Modify(){
		if( "".equals( _hour.getText().toString().trim()) ||
			"".equals( _min.getText().toString().trim()) ||
			"".equals( _af_hour.getText().toString().trim()) ||
			"".equals( _af_min.getText().toString().trim())	){
			Toast.makeText(Modify.this, 
					"���~!!!\n�п�J�ɶ�", Toast.LENGTH_LONG).show();	
		}
		else{
			int hour = Integer.parseInt( _hour.getText().toString() );
			int min = Integer.parseInt( _min.getText().toString() ); 
			int af_hour = Integer.parseInt( _af_hour.getText().toString() ); 
			int af_min = Integer.parseInt( _af_min.getText().toString() );
			if( hour >= 24 || af_hour >= 24 || min >= 60 || af_min >= 60 
			    || (hour+min/60.0) > (af_hour+af_min/60.0) )
				Toast.makeText(Modify.this, 
						"���~!!!\n�Э��scheck�ɶ�", Toast.LENGTH_LONG).show();
			else{
				// �B�z�ɶ�
				Calendar mCalendar = Calendar.getInstance();  
				mCalendar.set(Calendar.HOUR_OF_DAY,hour);
				mCalendar.set(Calendar.MINUTE,min);
				long start = mCalendar.getTime().getTime();  
				mCalendar.set(Calendar.HOUR_OF_DAY,af_hour);
				mCalendar.set(Calendar.MINUTE,af_min);  
				long end = mCalendar.getTime().getTime(); 
				_InputEvent(start, end);
			}
		}
	}
	
	void _Modify1(){
		if( "".equals( _hr.getText().toString().trim()) ){
			Toast.makeText(Modify.this, 
						"���~!!!\n�п�J�ɶ�", Toast.LENGTH_LONG).show();	
		}
		else{
		    Calendar mCalendar = Calendar.getInstance();  
	        long start = mCalendar.getTime().getTime();
	        mCalendar.add(Calendar.HOUR_OF_DAY, 
	                      Integer.parseInt( _hr.getText().toString()) );
	        long end = mCalendar.getTime().getTime();
	        _InputEvent(start, end);
		}
	}
	
	void _InputEvent( long start, long end ){
		// ����n�X�J��gmail�b�᪺id  
		long calId = 0;  
        Cursor userCursor = getContentResolver().query(Uri.parse(calendarUri), null,   
                null, null, null);  
        if(userCursor.getCount() > 0){  
            userCursor.moveToLast();  
            calId = userCursor.getLong(userCursor.getColumnIndex("_id"));  
        } 
        
        ContentValues event = new ContentValues();
       
        // �O�K�]�w
        if( _check.isChecked() )
            event.put("title", "($)" + _event.getText().toString()); 
        else
        	event.put("title", _event.getText().toString());
        
        event.put("description", _ps.getText().toString()); 
        // ���J���o��gmail�b��  
        event.put("calendar_id", calId);

        event.put("dtstart", start);  
        event.put("dtend", end);  
        event.put("allDay", 0); // 0 for false, 1 for true 
        event.put("hasAlarm",1);// 0 for false, 1 for true  
        event.put("eventTimezone", Time.getCurrentTimezone());

        /*Uri newEvent =*/
        getContentResolver().insert(Uri.parse(calendarEventUri), event);
        
        //����ҲK�[��event���D��_id
        /*long id = Long.parseLong( newEvent.getLastPathSegment() );
        ContentValues values = new ContentValues(); 
        values.put("event_id", id);  
        //���e10����������  
        values.put("minutes", 10);
        values.put("method", 1);
        getContentResolver().insert(Uri.parse(calendarReminderUri), values);//*/  
        Toast.makeText(Modify.this, "�s�W�ƥ󦨥\!!!", Toast.LENGTH_LONG).show();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

}

