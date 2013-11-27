package com.example.fiutr;
import java.util.ArrayList;

import android.app.ListActivity;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ScanActivity extends ListActivity
{
	WiFiHandler tester;
	ArrayList<ScanResult> currentNetworks = new ArrayList<ScanResult>();
	ArrayList<LocationNetwork> gpsWireless = new ArrayList<LocationNetwork>();
	WifiAdapterItem adapter;
	Button connectButton;

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
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
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
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}	
}


