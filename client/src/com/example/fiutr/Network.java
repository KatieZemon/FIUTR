package com.example.fiutr;

import com.google.android.gms.maps.model.LatLng;

public class Network
{
	String name;
	String details;
	int signalLevel;
	LatLng loc;
	
	public Network(String name, int level, Double lat, Double lon, String details)
	{
		this.name = name;
		this.signalLevel = level;
		this.loc = new LatLng(lat,lon);
		if(details != "")
			this.details = details;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDetails()
	{
		return details;
	}
	
	public LatLng getLocation()
	{
		return loc;
	}
	
	public int getBars()
	{
		int signalStrength = Math.abs(signalLevel);
		if(signalStrength <= 76)
			return 5;
		else if(signalStrength <= 87)
				return 4;
		else if(signalStrength <= 98)
				return 3;
		else if(signalStrength <= 107)
				return 2;
		else
			return 1;
	}
	
	public double calculateDistance(double lat1, double lon1, char unit) 
	{
	      double theta = lon1 - loc.longitude;
	      double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(loc.latitude)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(loc.latitude)) * Math.cos(deg2rad(theta));
	      dist = Math.acos(dist);
	      dist = rad2deg(dist);
	      dist = dist * 60 * 1.1515;
	      if (unit == 'K') {
	        dist = dist * 1.609344;
	      } else if (unit == 'N') {
	        dist = dist * 0.8684;
	        }
	      return (dist);
	    }

    private double deg2rad(double deg) {
      return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
      return (rad * 180.0 / Math.PI);
    }
}