package com.a5starcompany.flutteremv.topwise;

import android.app.Application;
import android.util.Log;

import com.a5starcompany.flutteremv.topwise.app.PosApplication;


public class TopApp extends Application{
	private  final String TAG = TopApp.class.getSimpleName();
	public static TopApp mPosApp;
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");
		mPosApp = this;
		DeviceManager.getInstance().bindService();
		PosApplication.init(this);
		PosApplication.initApp();
		PosApplication.getApp().setConsumeData();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		PosApplication.cancelCheckCard();
	}

	public static Application getApp(){
		return  mPosApp;
   }



}
