package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.coolweather.app.model.City;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class Utility {
	/**
	 * 解析和处理服务器返回的省级数据
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,
			String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");
			if(allProvinces!= null&& allProvinces.length>0){
				for (String p : allProvinces) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//将解析出来的数据存储到Province表
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的市级数据
	 */
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
			String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");
			if(allCities!=null && allCities.length>0 ){
				for(String c:allCities){
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.SetProvinceId(provinceId);
					//将解析出来的数据存储到City表
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的县级数据
	 */
	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,
			String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");
			if(allCounties!=null&&allCounties.length>0){
				for(String c:allCounties){
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.SetCityId(cityId);
					//将解析出来的数据存储到County类
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
	return false;
	}
	
	/**
	 * 解析服务器返回的json数据，并将解析出的数据存储到本地
	 */
	public static void handleWeatherResponse(Context context,String response){

		Log.e("处理天气的函数传入的信息", response.toString());
		try{
			JSONObject jsonObject = new JSONObject(response);
			JSONObject data  = jsonObject.getJSONObject("weatherinfo");
			String cityName = data.getString("city");
			String ganmao = data.getString("weather");
			String wendu = data.getString("temp1");
			String wendu2 = data.getString("temp2");
			String weatherCode = data.getString("cityid");
			saveWeatherInfo(context,cityName,wendu,wendu2,ganmao,weatherCode);	
			Log.e("wendu", wendu);
			}catch(JSONException e){
				e.printStackTrace();
			}
	}

	public static void saveWeatherInfo(Context context, String cityName,
			String wendu,String wendu2,String ganmao,String weatherCode) {
		// TODO Auto-generated method stub
		Log.e("saveWeatherInfo", "进入到保存数据的函数");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		SharedPreferences.Editor editor =PreferenceManager.
				getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected",true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("ganmao", ganmao);
		editor.putString("wendu", wendu);
		editor.putString("wendu2", wendu2);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}	
}
