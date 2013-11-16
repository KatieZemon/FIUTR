package com.example.fiutr;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.net.*;
import android.net.wifi.*;
import java.util.List;

public class ScanActivity extends Activity {
    
	/*
	ArrayList<CheckBox> SSIDS; //an ArrayList of type CheckBox, currently hand-populated, will eventually be populated from the list of SSIDs available
    ScrollView SSID_View = (ScrollView)findViewById(R.id.SSIDV); //variable to reference the ScrollView
    CheckBox C_Scan = (CheckBox)findViewById(R.id.checkBox1); //variable to reference the checkbox for continuous scanning
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		
		for(int i = 0; i < 10; i++){ //add the SSIDS to the ListView
			SSIDS.add(new CheckBox(this));
			SSIDS.get(i).setText("SSID "+i);
			SSID_View.addView(SSIDS.get(i));
			}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scan, menu);
		return true;
	}
	
	public void OnClick(View v){ //when the continuous scan checkbox is checked, the SSID's personal checkboxes are disabled
		if (C_Scan.isChecked()){
			for (CheckBox cb : SSIDS){
				cb.setEnabled(false);
			}
		}
		else{ //if unchecked reenabled
			for (CheckBox cb : SSIDS){
				cb.setEnabled(true);
			}
		}
	}
	*/
	
	List<ScanResult> currentWifiNetworks;
	TextView mainText;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		WiFiHandler tester = new WiFiHandler(this);
		currentWifiNetworks = tester.getWifiNetworks();
		StringBuilder sb = new StringBuilder();
		mainText = (TextView) findViewById(R.id.mainText);
		sb.append("\n        Number Of Wifi connections :"+currentWifiNetworks.size()+"\n\n");
		for(ScanResult result : currentWifiNetworks)
		{
            sb.append((result.SSID)+" ");
            sb.append((result.capabilities).toString());
            sb.append("\n\n");
            mainText.setText(sb);
		}
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
		
		
	}
}
