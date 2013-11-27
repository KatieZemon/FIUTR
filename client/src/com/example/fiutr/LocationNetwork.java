package com.example.fiutr;

import android.net.wifi.ScanResult;

public class LocationNetwork
{
	private ScanResult wifiData;
	private double longitude;
	private double latitude;
	
	public LocationNetwork(ScanResult stuff, double lat, double lon)
	{
		wifiData = stuff;
		longitude = lon;
		latitude = lat;
	}
	
	public void setData(ScanResult stuff)
	{
		wifiData = stuff;
	}
	
	public ScanResult returnData()
	{
		return wifiData;
	}
	
	public String returnName()
	{
		return wifiData.SSID;
	}
	
	public void setLatitude(double lat)
	{
		latitude = lat;
	}
	
	public double returnLatitude()
	{
		return latitude;
	}
	
	public void setLongitude(double lon)
	{
		longitude = lon;
	}
	
	public double returnLongitude()
	{
		return longitude;
	}
	
	public String toString()
	{
		return wifiData.SSID + "|" + wifiData.level + "|" + latitude + "|" + longitude;
	}
	
}