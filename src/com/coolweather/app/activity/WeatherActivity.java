package com.coolweather.app.activity;


import com.coolweather.app.R;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.lang.Exception;
public class WeatherActivity extends Activity implements OnClickListener{
	private LinearLayout weatherInfoLayout;
	/**
	 * 用于显示城市名
	 */
	private TextView cityNameText;
	/**
	 * 用于发布时间
	 */
	private TextView publishText;
	/**
	 * 用于显示天气信息，是否感冒等
	 */
	private TextView ganmaoText;
	
	/**
	 * 用于温度1
	 */
	private TextView wenduText;
	
	/**
	 * 用于温度2	
	 */
	private TextView wendu2Text;
	
	/**
	 * 用于当前时间
	 */
	private TextView currentDateText;
	
	/**
	 * 切换城市按钮
	 */
	private Button switchCity;
	
	/**
	 * 更新天气按钮
	 */
	private Button refreshWeather;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		//初始化各个控件
		weatherInfoLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		wenduText = (TextView) findViewById(R.id.wendu);
		wendu2Text = (TextView) findViewById(R.id.wendu2);
		publishText = (TextView)findViewById(R.id.publish_text);
		currentDateText = (TextView)findViewById(R.id.current_date);
		ganmaoText = (TextView)findViewById(R.id.ganmao);
		
		String countryCode = getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(countryCode)){
			//有县级代号时就去查询天气
			publishText.setText("同步中。。。"+countryCode);
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countryCode);
		}else {
			//没有县级代号时直接显示本地天气
			showWeather();
		}
		switchCity = (Button)findViewById(R.id.switch_city);
		refreshWeather = (Button)findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v){
		switch (v.getId()){
		case R.id.switch_city:
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中。。。");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
		
	}
	
	
	/**
	 * 查询县级代号所对应的天气代号
	 * @param countryCode
	 */
	private void queryWeatherCode(String countryCode) {
		// TODO Auto-generated method stub
		String addres = "http://www.weather.com.cn/data/list3/city" + countryCode +".xml";
		queryFromServer(addres,"countryCode");
		System.out.println(countryCode);
	}
	
	/**
	 * 查询天气代号所对应的天气
	 */
	private void queryWeatherInfo(String weatherCode){
		/**String address = "http://wthrcdn.etouch.cn/weather_mini?citykey=" + 
	weatherCode;*/
		String address = "http://www.weather.com.cn/data/cityinfo/" +weatherCode +".html";
		Log.e("queryWeatherCode()", weatherCode);
		Log.e("查询天气的address", address);
		queryFromServer(address, "weatherCode");
		
	}
	
	/**根据传入的地址去服务器查询天气代号或者天气信息
	 * @param addres
	 * @param string
	 */
	private void queryFromServer(final String address, final String type) {
		// TODO Auto-generated method stub
		
		Log.e("type", type);
		Log.e("传入的address是:", address);
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(final String response) {
				Log.e("response", response);
				// TODO Auto-generated method stub
				if("countryCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						//从服务器返回的数据中解析出天气代号
						String[] array = response.split("\\|");
						if(array !=null&&array.length ==2){
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
							Log.e("查看weathercode", weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
						//处理服务器返回的天气信息

						Log.e("进入到处理服务器返回天气的函数", "handleWeatherResponse");
						Utility.handleWeatherResponse(WeatherActivity.this,
								response);
						Log.e("天气信息", response);
						runOnUiThread(new Runnable() {				
							@Override                                               
							public void run() {
								// TODO Auto-generated method stub
								Log.e("进入到showWeather", "进入到WeatherActiviy,queryFromServer的显示天气的函数");
								showWeather();
							}
						});
					}
				}
			

			@Override
			public void onError (final Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						publishText.setText("同步失败!"+e.getMessage());
					}
				});
			}
		});
	}

	private void showWeather() {
		// TODO Auto-generated method stub
		Log.e("showWeather", "进入到showWeather函数：");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		Log.e("CityName=", cityNameText.toString());
		ganmaoText.setText(prefs.getString("ganmao", ""));
		wenduText.setText(prefs.getString("wendu", ""));
		wendu2Text.setText(prefs.getString("wendu2",""));
		publishText.setText("今日凌晨发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
	}		
}
