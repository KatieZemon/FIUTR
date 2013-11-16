package com.example.fiutr;

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


public class MainActivity extends Activity implements LocationListener{

	private GoogleMap googleMap;
	private CameraPosition camPos;
	private GPSHandler gpsHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gpsHandler = new GPSHandler(this);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_search:
			intent = new Intent(this, SearchActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_scan:
			intent = new Intent(this, ScanActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_viewAll:
			intent = new Intent(this, ViewAllActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_about:
			intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		gpsHandler.disable(this);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		gpsHandler.enable(this);
		updateMap();
	}
	
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
	private void updateMap()
	{
		if(gpsHandler.updateLocation())
		{
			camPos = new CameraPosition.Builder()
				.target(new LatLng(gpsHandler.getLat(),gpsHandler.getLon()))
				.zoom(17)
				.build();
			googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
			addMarker("Current Location", new LatLng(gpsHandler.getLat(),gpsHandler.getLon()));
		}
		else
		{
			Toast.makeText(this, "Unable to update GPS Position!", Toast.LENGTH_LONG).show();
		}
	}
	
	public void addMarker(String title, LatLng loc)
	{
		googleMap.addMarker(new MarkerOptions()
			.position(loc)
			.title(title)
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
	}
	
	public void addMarker(ScanResult result, LatLng loc)
	{
		googleMap.addMarker(new MarkerOptions()
			.position(loc)
			.title(result.SSID)
			.snippet(result.toString())
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
	}
	
	@Override
	public void onLocationChanged(Location location)
	{
		camPos = new CameraPosition.Builder()
			.target(new LatLng(location.getLatitude(),location.getLongitude()))
			.zoom(17)
			.build();
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		
	}
	
	@Override
	public void onProviderEnabled(String provider)
	{
		
	}
	
	@Override
	public void onProviderDisabled(String provider)
	{
		
	}


}
