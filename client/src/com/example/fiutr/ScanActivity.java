package com.example.fiutr;
import java.util.ArrayList;

<<<<<<< HEAD
import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ScanActivity extends ListActivity
{
	WiFiHandler tester;
	ArrayList<ScanResult> currentNetworks = new ArrayList<ScanResult>();
	ArrayList<LocationNetwork> gpsWireless = new ArrayList<LocationNetwork>();
	WifiAdapterItem adapter;
	Button connectButton;
=======
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.net.*;
import android.net.wifi.*;
import java.util.List;
>>>>>>> 0f78118f47c313e825a4fd0a7df585cdb3417016

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		tester = new WiFiHandler(this);
		currentNetworks = tester.getWifiNetworks();
		gpsWireless.clear();
		
		for(ScanResult item : currentNetworks)
		{
			gpsWireless.add(new LocationNetwork(item, 35, 24));
		}
		
		adapter = new WifiAdapterItem(this, R.layout.list_networks, gpsWireless);
		setListAdapter(adapter);
		
		connectButton = (Button) findViewById(R.id.connectButton);
		connectButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg)
			{
				ArrayList<ScanResult> result = new ArrayList<ScanResult>();
				for(int i=0; i<adapter.checked.size(); i++)
				{
					if(adapter.checked.get(i) == true)
					{
						result.add(adapter.stuff.get(i).returnData());
					}
				}
				for(int i = 0; i < result.size(); i++)
				{
					tester.connectToNetwork(result.get(i));
				}
			}}	
		);
	}
	
	public void onUpdate()
	{
		currentNetworks = tester.getWifiNetworks();
		ArrayList<ScanResult> currentNetworks = tester.getWifiNetworks();
		gpsWireless.clear();
		
		for(ScanResult item : currentNetworks)
		{
			gpsWireless.add(new LocationNetwork(item, 35, 24));
		}
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scan, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_refresh:
			onUpdate();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
<<<<<<< HEAD
	}	
=======
		if(currentWifiNetworks.size() > 0 && (!tester.connectToNetwork(currentWifiNetworks.get(0))))
		{
			Toast.makeText(this, "No networks available!", Toast.LENGTH_LONG).show();
		}
		
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo myWifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(!myWifi.isConnected())
		{
			Toast.makeText(this, "Not connected to a network!", Toast.LENGTH_LONG).show();
		}
		
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
>>>>>>> 0f78118f47c313e825a4fd0a7df585cdb3417016
}


