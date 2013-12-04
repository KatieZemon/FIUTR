package com.example.fiutr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.location.*;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;


public class MainActivity extends Activity implements LocationListener
{
	
	private GoogleMap googleMap;
	private CameraPosition camPos;
	private GPSHandler gpsHandler;
	private WiFiHandler wifiHandler;
	private final ArrayList<LocationNetwork> wifiGPS = new ArrayList<LocationNetwork>();
	private final ArrayList<Marker> markerList = new ArrayList<Marker>();
	private static String filePath;
	private String fileName = "gpsmaps.txt";
	private FileOutputStream writer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		filePath = getFilesDir().toString()+"/gpsmaps.txt";
		System.out.println("The file path is: "+filePath);
		gpsHandler = new GPSHandler(this);
		wifiHandler = new WiFiHandler(this);
		gpsHandler.updateLocation();

		try
		{
			checkFileForDuplicates();
			initializeMap();
			parseFile();
			openFile();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		Intent intent;
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_search:
			intent = new Intent(this, SearchActivity.class);
			intent.putExtra("FILE_PATH",filePath);
			startActivity(intent);
			return true;
		case R.id.action_scan:
			intent = new Intent(this, ScanActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_viewAll:
			intent = new Intent(this, ViewAllActivity.class);
			intent.putExtra("FILE_PATH",filePath);
			intent.putExtra("BOOL_VIEW_ALL",true); // Viewing all data
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
		// Close File IO
		
		try {
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		gpsHandler.enable(this);
		updateMap();
		openFile();
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
			removeMarker("Current Location");
			camPos = new CameraPosition.Builder()
				.target(new LatLng(gpsHandler.getLat(),gpsHandler.getLon()))
				.zoom(17)
				.build();
			googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
			addMarker("Current Location", new LatLng(gpsHandler.getLat(),gpsHandler.getLon()));
			processWiFiLocations(wifiHandler.getWifiNetworks(),gpsHandler.getLat(), gpsHandler.getLon());
		}
		else
		{
			Toast.makeText(this, "Unable to update GPS Position!", Toast.LENGTH_SHORT).show();
		}
	}
	
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
	
	public void addMarker(String title, LatLng loc)
	{
		Marker marker = googleMap.addMarker(new MarkerOptions()
								 .position(loc)
								 .title(title)
								 .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
		markerList.add(marker);
	}
	
	public void addMarker(ScanResult result, LatLng loc)
	{
		
		Marker marker = googleMap.addMarker(new MarkerOptions()
								 .position(loc)
								 .title(result.SSID)
								 .snippet(result.toString())
								 .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
		if(!markerList.contains(marker))
		{
			markerList.add(marker);
			writeToFile(result, loc);
		}
		
	}
	
	public void addMarker(String nameOfNetwork, String info, LatLng loc)
	{
		Marker marker = googleMap.addMarker(new MarkerOptions()
								 .position(loc)
								 .title(nameOfNetwork)
								 .snippet(info)
								 .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
		if(!markerList.contains(marker))
			markerList.add(marker);
	}
	
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
	
	@Override
	public void onLocationChanged(Location location)
	{
		camPos = new CameraPosition.Builder()
			.target(new LatLng(location.getLatitude(),location.getLongitude()))
			.zoom(15)
			.build();
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
	}
	
	// Remove duplicate files displayed in viewall activity
	public static void checkFileForDuplicates()
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			Set<String> lines = new LinkedHashSet<String>(10000);
			String line;
			while ((line = reader.readLine()) != null)
			{
				lines.add(line);
			}
			reader.close();
			BufferedWriter tempWriter = new BufferedWriter(new FileWriter(filePath));
			for(String uniqueLines : lines)
			{
				tempWriter.write(uniqueLines);
				tempWriter.newLine();
			}
			tempWriter.close();
		}
		catch (FileNotFoundException e)
		{
			try
			{
				System.err.println("Creating a new file!\n");
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "utf-8"));
				writer.close();
				checkFileForDuplicates();
			}
			catch (Exception f)
			{
				f.printStackTrace();
			}
			
		}
		catch (Exception e)
		{
			System.err.println("Unable to check file for duplicates!\n");
			e.printStackTrace();
		}
	}
	
	public void parseFile()
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line;
			while((line = reader.readLine()) != null)
			{
				if(line.contains("|"))
				{
					System.out.println("Parsing line: [ "+line+" ]");
					// Format is: NAMEOFNETWORK|INFO_OF_NETWORK|LATITUDE|LONGITUDE
					String[] parsedLine = line.split("\\|");
					for(String s : parsedLine)
						System.out.println(s);
					addMarker(parsedLine[0],parsedLine[1],new LatLng(Double.parseDouble(parsedLine[2]),Double.parseDouble(parsedLine[3])));
				}
				else
					System.out.println("Caught newline!");
			}
			reader.close();
		}
		catch (FileNotFoundException e)
		{
			try
			{
				System.err.println("Creating a new file!\n");
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "utf-8"));
				writer.close();
				parseFile();
			}
			catch (Exception f)
			{
				f.printStackTrace();
			}
			
		}
		catch (Exception e)
		{
			System.err.println("Unable to parse file for networks!\n");
			e.printStackTrace();
		}
	}
	
	public void writeToFile(ScanResult result, LatLng loc)
	{
		// FORMAT is: NAMEOFNETWORK|INFO_OF_NETWORK|LATITUDE|LONGITUDE
		if(writer == null)
			openFile();
		try
		{
			writer.write((result.SSID+"|"+result.level+"|"+loc.latitude+"|"+loc.longitude).getBytes());
			writer.write(System.getProperty("line.separator").getBytes());
		}
		catch (FileNotFoundException e)
		{
			try
			{
				System.err.println("Creating a new file!\n");
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "utf-8"));
				writer.close();
				writeToFile(result,loc);
			}
			catch (Exception f)
			{
				f.printStackTrace();
			}
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void openFile()
	{
		if(writer != null)
			return;
		try
		{
			writer = openFileOutput(fileName, Context.MODE_APPEND);
		}
		catch (FileNotFoundException e)
		{
			try
			{
				System.err.println("Creating a new file!\n");
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "utf-8"));
				writer.close();
				openFile();
			}
			catch (Exception f)
			{
				f.printStackTrace();
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
