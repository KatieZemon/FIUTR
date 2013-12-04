package com.example.fiutr;

import com.google.android.gms.maps.model.LatLng;

public class Network
{
	String name;
	String details;
	LatLng loc;
	
	public Network(String name, String details, Double lat, Double lon)
	{
		this.name = name;
		this.details = details;
		this.loc = new LatLng(lat,lon);
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
}