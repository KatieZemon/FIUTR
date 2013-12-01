package com.example.fiutr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.location.*;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

/**
 * MainActivity serves as the home page for the application. It
 * contains the map which shows the user's location and surrounding WiFi points
 * stored in our database.
 */
public class MainActivity extends Activity implements LocationListener{	
	private GoogleMap googleMap;
	private CameraPosition camPos;
	private GPSHandler gpsHandler;
	private WiFiHandler wifiHandler;
	private final ArrayList<LocationNetwork> wifiGPS = new ArrayList<LocationNetwork>();
	private final ArrayList<Marker> markerList = new ArrayList<Marker>();
	
	/**
	 * Method automatically called when the MainActivity page is created.
	 * It creates a new GPSHandler and wifiHandler in order to display the map
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gpsHandler = new GPSHandler(this);
		wifiHandler = new WiFiHandler(this);
		gpsHandler.updateLocation();  

		try
		{
			initializeMap();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Adds all of our options to the main menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * This is the default method called when the user selects one of the
	 * menu options. It will open a new page based on the selected option.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		int itemId = item.getItemId();
		if (itemId == R.id.action_search) {
			intent = new Intent(this, SearchActivity.class);
			startActivity(intent);
			return true;
		} else if (itemId == R.id.action_scan) {
			intent = new Intent(this, ScanActivity.class);
			startActivity(intent);
			return true;
		} else if (itemId == R.id.action_viewAll) {
			intent = new Intent(this, ViewAllActivity.class);
			startActivity(intent);
			return true;
		} else if (itemId == R.id.action_about) {
			intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}

	}
	
	/**
	 * 
	 */
	@Override
	protected void onPause()
	{
		super.onPause();
		gpsHandler.disable(this);
	}
	
	/**
	 * 
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		gpsHandler.enable(this);
		updateMap();
	}
	
	/**
	 * initializes the map with a marker at the user's location
	 */
	private void initializeMap()
	{
		if(googleMap == null)
		{
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			updateMap();
		}
		if(googleMap == null)
		{
			Toast.makeText(getApplicationContext(), "Unable to create maps!", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Updates the map with the user's current location. It removes the marker placed at the user's
	 * old location, gets the users current location, and places a new marker at that current location.
	 * If the current location was unable to be found, an error message will be displayed
	 */
	private void updateMap()
	{
		if(gpsHandler.updateLocation())
		{
			removeMarker("Current Location");
			camPos = new CameraPosition.Builder()
				.target(new LatLng(gpsHandler.getLat(),gpsHandler.getLon()))
				.zoom(17)
				.build();
			googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
			addMarker("Current Location", new LatLng(gpsHandler.getLat(),gpsHandler.getLon()));
			processWiFiLocations(wifiHandler.getWifiNetworks(),gpsHandler.getLat(), gpsHandler.getLon());
		}
		
		// Write an error message if the user's current GPS location was not found
		else
		{
			Toast.makeText(this, "Unable to update GPS Position!", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Updates the map with the user's current location. It removes the marker placed at the user's
	 * old location, gets the users current location, and places a new marker at that current location.
	 * If the current location was unable to be found, an error message will be displayed
	 */
	public void processWiFiLocations(List<ScanResult> wifiNetworks, double latitude, double longitude)
	{
		for(ScanResult result : wifiNetworks)
		{
			// Check to see if it is already in there. If so, remove it.
			removeMarker(result.SSID);
			// Add it to our array of LocationNetworks, and to the map.
			wifiGPS.add(new LocationNetwork(result, latitude, longitude));
			addMarker(result, new LatLng(latitude, longitude));
		}
	}
	
	/**
	 * Adds a new marker to the map
	 * @param title The title to be displayed above the marker's position on the map
	 * @param loc The latitude/longitude values of the marker's location
	 */
	public void addMarker(String title, LatLng loc)
	{
		Marker marker = googleMap.addMarker(new MarkerOptions()
								 .position(loc)
								 .title(title)
								 .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
		markerList.add(marker);
	}
	
	/**
	 * Adds a new marker to the map based on results from performing a scan
	 * @param result The result from performing a scan. result.SSID will be used to set the title
	 *        of the marker
	 * @param loc The latitude/longitude values of the marker's location
	 */
	public void addMarker(ScanResult result, LatLng loc)
	{
		Marker marker = googleMap.addMarker(new MarkerOptions()
								 .position(loc)
								 .title(result.SSID)
								 .snippet(result.toString())
								 .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
		markerList.add(marker);	
	}
	
	/**
	 * Removes a marker from the map. The marker is specified by its unique title.
	 * @param title The marker's title which can be represented as the SSID of a specific network
	 *        to be removed from the map 
	 */
	public void removeMarker(String title)
	{
		Iterator<Marker> i = markerList.iterator();
		while(i.hasNext())
		{
			Marker currentItem = i.next();
			if(currentItem.getTitle().equals(title))
			{
				currentItem.remove();
			}
		}
	}
	
	/**
	 * Method automatically called to get the location of the user
	 * and zoom in on that location 
	 */
	@Override
	public void onLocationChanged(Location location)
	{
		camPos = new CameraPosition.Builder()
			.target(new LatLng(location.getLatitude(),location.getLongitude()))
			.zoom(15)
			.build();
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
	}
	
	// TODO: Remove?
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		
	}
	
	// TODO: Remove?
	@Override
	public void onProviderEnabled(String provider)
	{
		
	}
	
	// TODO: Remove?
	@Override
	public void onProviderDisabled(String provider)
	{
		
	}


}
