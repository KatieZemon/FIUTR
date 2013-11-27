package com.example.fiutr;

import android.content.*;
import android.net.*;
import android.net.wifi.*;
import android.os.*;
import android.view.*;
import android.widget.Toast;
import android.app.Activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WiFiHandler {
	
	private WifiManager mainWifi;
	private ArrayList<ScanResult> wifiResults = new ArrayList<ScanResult>();
	private Context wifiContext;
	private String[] securityModes = {"WEP", "PSK", "EAP"};
	
	public WiFiHandler(Context passedContext)
	{
		wifiContext = passedContext;
		mainWifi = (WifiManager) wifiContext.getSystemService(wifiContext.WIFI_SERVICE);
	}
	
	public ArrayList<ScanResult> getWifiNetworks()
	{
		//Clear the list, making sure no remnants exist.
		wifiResults.clear();
		//Checking if WiFi is enabled...
		if(mainWifi.isWifiEnabled() == false)
		{
			Toast.makeText(wifiContext, "WiFi is disabled! Enabling...", Toast.LENGTH_LONG).show();
			mainWifi.setWifiEnabled(true);
		}
		mainWifi.startScan();
		List<ScanResult> tempList = mainWifi.getScanResults();
		//Figuring out which are unsecured versus ones that aren't.
		Iterator<ScanResult> listIterator = tempList.iterator();
		while(listIterator.hasNext())
		{
			ScanResult result = listIterator.next();
			for(int i = 0; i < securityModes.length; i++)
			{
				// If the security capabilities contain anything with security, remove it from the list.
				if((result.capabilities).contains(securityModes[i]))
				{
					listIterator.remove();
				}
			}
		}
		// Another loop removing duplicated SSIDs
		for(ScanResult result : tempList)
		{
			// If it has been added to the list already, remove it.
			Iterator<ScanResult> it = wifiResults.iterator();
			boolean dup = false;
			while(it.hasNext())
			{
				ScanResult current = it.next();
				if(result.SSID.equals(current.SSID))
				{
					dup = true;
					break;
				}
				else
				{
					continue;
				}
			}
			if(dup == false)
			{
				wifiResults.add(result);
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
	
	public List<ScanResult> returnWifiResults()
	{
		return wifiResults;
	}
	
	public int returnBars(ScanResult subject)
	{
		return WifiManager.calculateSignalLevel(mainWifi.getConnectionInfo().getRssi(),6);
	}
	
	/*
	public boolean testNetwork(ScanResult subject)
	{
		
	}
	
	*/
}