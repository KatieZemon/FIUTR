package com.example.fiutr;

import android.net.wifi.ScanResult;

/**
 * LocationNetwork represents a network displayed on the map
 */
public class LocationNetwork
{
	private ScanResult wifiData; // Information about a returned network
	private double longitude; // Longitude location of the network on the map
	private double latitude; // Latitude location of the network on the map
	
	/**
	 * Constructor for initializing network values
	 * @param result The network produced from scanning for available networks
	 * @param lat The latitude value of the network's location on the map
	 * @param lon The longitude value of the network's location on the map
	 */
	public LocationNetwork(ScanResult result, double lat, double lon)
	{
		wifiData = result;
		longitude = lon;
		latitude = lat;
	}
	
	/**
	 * Sets the network wifiData
	 */
	public void setData(ScanResult result)
	{
		wifiData = result;
	}
	
	/**
	 * Get the network wifiData
	 */
	public ScanResult returnData()
	{
		return wifiData;
	}
	
	/**
	 * Get the network's SSID
	 */
	public String returnName()
	{
		return wifiData.SSID;
	}
	
	/**
	 * Set the set the network's latitude
	 */
	public void setLatitude(double lat)
	{
		latitude = lat;
	}
	
	/**
	 * Get the network's Latitude
	 */
	public double returnLatitude()
	{
		return latitude;
	}
	
	/**
	 * Set the set the network's longitude
	 */
	public void setLongitude(double lon)
	{
		longitude = lon;
	}
	
	/**
	 * Get the network's Longitude
	 */
	public double returnLongitude()
	{
		return longitude;
	}
	
	public int getBars()
	{
		int signalStrength = Math.abs(wifiData.level);
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
	
	/**
	 * Return a string of information for this network. 
	 * This information includes the SSID, signal strength, latitude, and longitude
	 */
	public String toString()
	{
		return wifiData.SSID + "|" + wifiData.level + "|" + latitude + "|" + longitude;
	}
	
	public double calculateDistance(double lat1, double lon1, char unit) 
	{
	      double theta = lon1 - longitude;
	      double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(latitude)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(latitude)) * Math.cos(deg2rad(theta));
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