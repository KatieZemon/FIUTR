package com.example.fiutr;

import android.content.*;
import android.net.*;
import android.net.wifi.*;
import android.os.*;
import android.view.*;
import android.widget.Toast;
import android.app.Activity;

import java.util.Iterator;
import java.util.List;

public class WiFiHandler {
	
	private WifiManager mainWifi;
	private List<ScanResult> wifiResults;
	private Context wifiContext;
	private String[] securityModes = {"WEP", "PSK", "EAP"};
	
	public WiFiHandler(Context passedContext)
	{
		wifiContext = passedContext;
		mainWifi = (WifiManager) wifiContext.getSystemService(wifiContext.WIFI_SERVICE);
	}
	
	public List<ScanResult> getWifiNetworks()
	{
		if(mainWifi.isWifiEnabled() == false)
		{
			Toast.makeText(wifiContext, "WiFi is disabled! Enabling...", Toast.LENGTH_LONG).show();
			mainWifi.setWifiEnabled(true);
		}
		mainWifi.startScan();
		wifiResults = mainWifi.getScanResults();
		//Figuring out which are unsecured versus ones that aren't.
		Iterator<ScanResult> listIterator = wifiResults.iterator();
		while(listIterator.hasNext())
		{
			ScanResult result = listIterator.next();
			for(int i = 0; i < securityModes.length; i++)
			{
				if((result.capabilities).contains(securityModes[i]))
				{
					listIterator.remove();
				}
			}
		}
		return wifiResults;
	}
	
	public boolean connectToNetwork(ScanResult subject)
	{
		WifiConfiguration config = new WifiConfiguration();
		config.SSID = "\""+subject.SSID+"\"";
		config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		int netId = mainWifi.addNetwork(config);
		if(!mainWifi.enableNetwork(netId, true))
		{
			Toast.makeText(wifiContext,"Connecting to the WiFi network failed!",Toast.LENGTH_LONG).show();
			return false;
		}
		mainWifi.setWifiEnabled(true);
		return true;
	}
	
	/*
	public boolean testNetwork(ScanResult subject)
	{
		
	}
	
	*/
}