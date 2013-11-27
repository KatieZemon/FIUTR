package com.example.fiutr;

import android.app.AlertDialog;
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
		if(!locMan.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(gpsContext);
			builder.setMessage("GPS is disabled. Do you want to enable it?")
				   .setCancelable(false)
				   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					   public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id)
					   {
						   gpsContext.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					   }
				   })
				   .setNegativeButton("No", new DialogInterface.OnClickListener() {
					   public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id)
					   {
						   dialog.cancel();
					   }
				   });
			final AlertDialog alert = builder.create();
			alert.show();
		}
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