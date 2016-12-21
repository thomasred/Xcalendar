package com.xcalendar;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;  
import android.content.Context;
import android.content.Intent;  
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle; 
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;  
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
 
public class Control extends Activity implements OnClickListener, OnItemSelectedListener {
	private TextView _LCD;
	private Button _confirm, _back;
	private EditText _hour, _af_hour, _min, _af_min, _hr; // 時間
    private EditText _event; // 事件內容
    private RadioButton _ch;
    private Spinner spinner;
    private String[] list = {"Meeting", "吃飯", "洗澡", "睡覺", "打咚咚"};
    private ArrayAdapter<String> listAdapter;
    
	// uriPath: 後端網址
    private static String uriPath = "http://140.123.107.181:8003/echo.aspx";
    //private static String uriPath = "http://10.0.2.2:1622/WebTest/echo.aspx";
    static{ 
        if( Build.VERSION.SDK_INT > 8 ){  
        	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.permitAll()
				.build());
        } 
    } 
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control);
		
		// 取得網路連線的狀態
		NetworkInfo mNetworkInfo = 
				((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();

		// 如果未連線的話，mNetworkInfo會等於null
		if(mNetworkInfo == null) {
			Toast.makeText(Control.this, "無網路連線!", Toast.LENGTH_LONG).show();
			To_menu();
		}
		else {
			init();
			_LCD.setText( SendHttpPost("#getLCD") );
		}
	}

	/**
	 * 初始化
	 */
	public void init(){
		_LCD = (TextView)findViewById(R.id.LCD);
		_hour = (EditText)findViewById(R.id.hour2);
		_af_hour = (EditText)findViewById(R.id.af_hour2);
		_min = (EditText)findViewById(R.id.min2);
		_af_min = (EditText)findViewById(R.id.af_min2);
		_ch = (RadioButton)findViewById(R.id.t_ch1);
		_hr = (EditText)findViewById(R.id.hr);
		_event = (EditText)findViewById(R.id.event2);
		_confirm = (Button)findViewById(R.id.confirm2);
		_confirm.setOnClickListener(this);
		_back = (Button)findViewById(R.id.back2);
		_back.setOnClickListener(this);

		spinner = (Spinner)findViewById(R.id.list);
        listAdapter = new ArrayAdapter<String>(this,
        		          android.R.layout.simple_spinner_item, list);
        spinner.setAdapter(listAdapter);
        spinner.setOnItemSelectedListener(this);
	}
	
	/**
	 * 按鈕設定
	 */
	@Override
	public void onClick(View v){
		// TODO Auto-generated method stub
		switch( v.getId() ){
		case R.id.back2:
			To_menu();
		    break;
		case R.id.confirm2:
			if(_ch.isChecked()) { 
				_Control();
	        }else{
	        	_Control1();
	        }
		    break;
		default:
			break; 
		}
	}
	
	/**
	 * spinner選擇設定
	 */
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		_event.setText(list[arg2]);
	}

	/**
	 * spinner沒有選擇設定
	 */
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * 回到主選單
	 */
	void To_menu() {
		Intent intent = new Intent();
		intent.setClass(Control.this, MainActivity.class);
		startActivity(intent); 
		Control.this.finish();
	}
	
	/**
	 * 選擇自訂時間
	 */
	void _Control(){
		if( "".equals( _hour.getText().toString().trim()) ||
			"".equals( _min.getText().toString().trim()) ||
			"".equals( _af_hour.getText().toString().trim()) ||
			"".equals( _af_min.getText().toString().trim())	){
			Toast.makeText(Control.this, 
					"錯誤!!!\n請輸入時間", Toast.LENGTH_LONG).show();	
		}
		else{
			int hour = Integer.parseInt( _hour.getText().toString() );
			int min = Integer.parseInt( _min.getText().toString() ); 
			int af_hour = Integer.parseInt( _af_hour.getText().toString() ); 
			int af_min = Integer.parseInt( _af_min.getText().toString() );
			if( hour >= 24 || af_hour >= 24 || min >= 60 || af_min >= 60 
			    || (hour+min/60.0) > (af_hour+af_min/60.0) )
				Toast.makeText(Control.this, 
						"錯誤!!!\n請重新check時間", Toast.LENGTH_LONG).show();
			else{
				// 處理傳送資料
				// 處理時間
				Calendar mCalendar = Calendar.getInstance();  
				mCalendar.set(Calendar.HOUR_OF_DAY,hour);
				mCalendar.set(Calendar.MINUTE,min);
				long start = mCalendar.getTime().getTime();  
				mCalendar.set(Calendar.HOUR_OF_DAY,af_hour);
				mCalendar.set(Calendar.MINUTE,af_min);  
				long end = mCalendar.getTime().getTime();
				_LCD.setText(SendHttpPost(process_data(start, end)));
			}
		}
	}
	
	/**
	 * 選擇由目前~?小時候
	 */
	void _Control1(){
		if( "".equals( _hr.getText().toString().trim()) ){
			Toast.makeText(Control.this, 
						"錯誤!!!\n請輸入時間", Toast.LENGTH_LONG).show();	
		}
		else{
		    Calendar mCalendar = Calendar.getInstance();  
	        long start = mCalendar.getTime().getTime();
	        mCalendar.add(Calendar.HOUR_OF_DAY, 
	                      Integer.parseInt( _hr.getText().toString()) );
	        long end = mCalendar.getTime().getTime();
	        _LCD.setText(SendHttpPost(process_data(start, end)));
		}
	}
	
	/**
	 * 處理要傳送回Server的資料
	 */
	String process_data( long start, long end ){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm", 
                Locale.getDefault());
		return sdf.format(start) + "|" + sdf.format(end) + "|" +
				_event.getText() + "|";
	}
	/**
	 * 傳送資料回Server 
	 * data: 資料
	 */
	public static String SendHttpPost( String data) {
	   
		HttpPost httpRequest = new HttpPost(uriPath);
		
	    // Post運作傳送變數必須用NameValuePair[]陣列儲存
	    List<NameValuePair> params = new ArrayList<NameValuePair>();

	    params.add(new BasicNameValuePair("data", data));

	    try {
	    	//發出HTTP request  
	        httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));  
	        //取得HTTP response
	        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
	        defaultHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
	        HttpResponse httpResponse = defaultHttpClient.execute(httpRequest);  
	         
	        //若狀態碼為200 ok   
	        if(httpResponse.getStatusLine().getStatusCode() == 200) {  
	        	//取出回應字串 
	        	String strResult = EntityUtils.toString(httpResponse.getEntity());  
	        	return strResult;  
	        }
	        else   
	        	return "Error Response" ;//+ httpResponse.getStatusLine().toString();  
	        
	    } catch(ClientProtocolException e) {  
	    	e.printStackTrace(); 
	    	return e.getMessage().toString(); 
	    } catch (UnsupportedEncodingException e) {  
	        e.printStackTrace();
	        return e.getMessage().toString(); 
	    } catch (IOException e) {  
	        e.printStackTrace();
	        return e.getMessage().toString();
	    } 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
}
