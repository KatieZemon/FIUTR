package com.example.fiutr;

import android.content.*;
import android.location.*;
import android.widget.*;

public class GPSHandler {
	
	private double latitude;
	private double longitude;
	private LocationManager locMan;
	private Context gpsContext;
	private String provider;
	private Criteria gpsCriteria;
	
	public GPSHandler(Context subContext)
	{
		gpsContext = subContext;
		locMan = (LocationManager) gpsContext.getSystemService(gpsContext.LOCATION_SERVICE);
	    gpsCriteria = new Criteria();
		provider = locMan.getBestProvider(gpsCriteria, false);
		
	}
	
	public boolean updateLocation()
	{
		Location location = locMan.getLastKnownLocation(provider);
		if(location != null)
		{
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			return true;
		}
		else
			return false;
	}
	
	public double getLat()
	{
		return latitude;
	}
	
	public double getLon()
	{
		return longitude;
	}
	
	public void updateLocation(double lat, double lon)
	{
		latitude = lat;
		longitude = lon;
	}
	
	public void disable(LocationListener listener)
	{
		locMan.removeUpdates(listener);
	}
	
	public void enable(LocationListener listener)
	{
		locMan.requestLocationUpdates(provider,400,1,listener);
		updateLocation();
	}
	
}