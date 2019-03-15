package com.polytech.pear2pear.prototype;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

public final class WifiApManager {
	private static final int WIFI_AP_STATE_FAILED = 4;
	private final WifiManager mWifiManager;
	private Method wifiControlMethod;
	private Method wifiApConfigurationMethod;
	private Method wifiApState;
	public WifiApManager(Context context) throws SecurityException, 
	NoSuchMethodException { 
		mWifiManager = (WifiManager) 
				context.getSystemService(Context.WIFI_SERVICE);
		wifiControlMethod = 
				mWifiManager.getClass().getMethod("setWifiApEnabled", 
						WifiConfiguration.class,boolean.class);
		wifiApConfigurationMethod = 
				mWifiManager.getClass().getMethod("getWifiApConfiguration",null);
		wifiApState = mWifiManager.getClass().getMethod("getWifiApState");
	}   
	public boolean setWifiApState(WifiConfiguration config, boolean enabled) {
		try {
			if (enabled) {
				mWifiManager.setWifiEnabled(!enabled);
			}
			return (Boolean) wifiControlMethod.invoke(mWifiManager, config, 
					enabled);
		} catch (Exception e) { 
			return false;
		}
	}      public WifiConfiguration getWifiApConfiguration()
	{
		try{
			return 
					(WifiConfiguration)wifiApConfigurationMethod.invoke(mWifiManager, null);
		}
		catch(Exception e)
		{
			return null;
		}
	}
	public int getWifiApState() {
		try {
			return (Integer)wifiApState.invoke(mWifiManager);
		} catch (Exception e) {
			return WIFI_AP_STATE_FAILED;
		}
	}
}
