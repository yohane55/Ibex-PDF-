package com.ibexreader.mibooksi;

import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;

/**
 * Created by john on 6/14/16.
 */
public class MiBooksApplication extends Application implements NativeAdsManager.Listener {
	float[] inkColor = {1,0,0};
	float inkWidth = 5f;

	static MiBooksApplication instance;
	private float[] highlightColor = {1,1,0,0.4f};
	private final int RELOAD_NATIVE_ADS=938;
	private Handler mHandler =  new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case RELOAD_NATIVE_ADS:
					reloadNative();

			}
		}
	};
	private long nativeMessageTime=0;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		adsManager =  new NativeAdsManager(this,"752294898208611_756042847833816",10);
		adsManager.setListener(this);
//		reloadNative();
	}

	private void reloadNative() {



		adsManager.loadAds();



	}

	public NativeAd getNextAd(){
		NativeAd ad =  adsManager.nextNativeAd();


		if(ad==null && (System.currentTimeMillis()-nativeMessageTime)>=10000){
			mHandler.removeMessages(RELOAD_NATIVE_ADS);
			mHandler.sendMessage(mHandler.obtainMessage(RELOAD_NATIVE_ADS));
			nativeMessageTime = System.currentTimeMillis();
		}
		return ad;
	}


	@Override
	public void onAdsLoaded() {

	}

	@Override
	public void onAdError(AdError adError) {
		Log.e("AMH", "Oxapp adsmanger error: "+adError.getErrorMessage());
		if(adError.getErrorCode() != AdError.LOAD_TOO_FREQUENTLY_ERROR_CODE || adError.getErrorCode() != AdError.NO_FILL_ERROR_CODE){
			mHandler.sendMessageDelayed(mHandler.obtainMessage(RELOAD_NATIVE_ADS), 10 * 1000);

		}else{
			mHandler.sendMessageDelayed(mHandler.obtainMessage(RELOAD_NATIVE_ADS), 30*1000);

		}
	}

	private NativeAdsManager adsManager;

	public static MiBooksApplication getInstance() {
		return instance;
	}



	public float[] getInkColor() {
		return inkColor;
	}

	public void setInkColor(float[] inkColor) {
		this.inkColor = inkColor;
	}

	public float getInkWidth() {
		return inkWidth;
	}

	public void setInkWidth(float inkWidth) {
		this.inkWidth = inkWidth;
	}

	public void setHighlightColor(float[] highlightColor) {
		this.highlightColor = highlightColor;
	}

	public float[] getHighlightColor() {
		return highlightColor;
	}
}
