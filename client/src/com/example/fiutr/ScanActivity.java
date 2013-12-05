package com.example.fiutr;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.support.v4.app.NavUtils;

/**
 * ScanActivity creates the page for users to conduct a scan for local networks.
 * It displays a list of the local networks and gives users the choice to test
 * any of these networks or choose to continually scan for all local networks.
 */
public class ScanActivity extends ListActivity {
	WiFiHandler tester;
	ArrayList<ScanResult> currentNetworks = new ArrayList<ScanResult>();
	ArrayList<LocationNetwork> gpsWireless = new ArrayList<LocationNetwork>();
	WifiAdapterItem adapter;
	Button connectButton;
	ToggleButton continuousConnectionButton;
	private AsyncTimer timer;

	/**
	 * This class will create execute the AsyncTimer used for running
	 * the continuous scan in the background
	 */
	private void startThread() {
		Toast.makeText(ScanActivity.this, "Scanning for networks",
				Toast.LENGTH_SHORT).show();
		timer = new AsyncTimer(); // need to make this every time because it can
									// only be executed once
		timer.execute(); // start the thread
	}

	/**
	 * This class is used for implementing a continuous scan whenever the
	 * continuous scan button is pressed
	 */
	private void stopThread() {
		timer.cancel(true);
		Toast.makeText(ScanActivity.this, "Done scanning for networks",
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * This class is used for implementing a continuous scan whenever the
	 * continuous scan button is pressed
	 */
	public class AsyncTimer extends AsyncTask<Void, Integer, Boolean> {
		private boolean isRunning;
		private boolean stop;

		/**
		 * Background thread called when the continuous scan button is pressed.
		 * Until the continuous scan option is turned off, this will scan for local networks
		 * every 3 seconds.
		 */
		@Override
		protected Boolean doInBackground(Void... arg0) {
			isRunning = true;

			while (isRunning) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					Log.e("Thread Interrupted", e.getMessage());
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onUpdate();
					}
				});
			}
			if (stop == false)
				return true;
			else
				return false;
		}

		@Override
		protected void onCancelled() {
			stop = true;
			isRunning = false;
		}

		public boolean getIsRunning() {
			return isRunning;
		}

	}

	/**
	 * Method automatically called when the ScanActivity page is created. It
	 * creates a new GPSHandler and wifiHandler in order to display the map
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);

		// Create the WiFiHandler and use it to grab the current list of
		// networks
		tester = new WiFiHandler(this);
		currentNetworks = tester.getWifiNetworks();
		gpsWireless.clear();

		GPSHandler gpsLoc = new GPSHandler(this);
		gpsLoc.updateLocation();
		// For each local network found, add a new location on the map
		// for this network
		for (ScanResult item : currentNetworks) {
			gpsWireless.add(new LocationNetwork(item, gpsLoc.getLat(), gpsLoc
					.getLon()));
		}

		adapter = new WifiAdapterItem(this, R.layout.list_networks, gpsWireless);
		setListAdapter(adapter);

		connectButton = (Button) findViewById(R.id.connectButton);
		continuousConnectionButton = (ToggleButton) findViewById(R.id.togglebutton);

		/**
		 * This is the listener for the connect button. It iterates through all
		 * networks resulting from the scan and it will attempt to connect to
		 * the networks that have been selected by the user.
		 */
		connectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg) {
				ArrayList<ScanResult> result = new ArrayList<ScanResult>();
				// Iterate through all results
				for (int i = 0; i < adapter.checked.size(); i++) {
					// If the network is selected, add it to a list
					if (adapter.checked.get(i) == true) {
						result.add(adapter.stuff.get(i).returnData());
					}
				}
				// Iterate through the list of local networks and
				// establish a connection
				for (int i = 0; i < result.size(); i++) {
					tester.connectToNetwork(result.get(i));
				}
				if (result.size() == 0) {
					Toast.makeText(ScanActivity.this,
							"No networks were selected!", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(ScanActivity.this, "Connecting to network",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		/**
		 * This is the listener for the continuous scan toggle button.
		 * When the toggle button is pressed, it will continually scan for
		 * networks every 3 seconds and display the text "On."
		 * When the toggle button is pressed again, it will stop the continuous
		 * scan and display the text "Off" indicating that we are no longer scanning
		 * for networks.
		 */
		continuousConnectionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				// Is the toggle on?
				boolean on = ((ToggleButton) v).isChecked();

				if (on) {
					startThread();
				} else {
					stopThread();
				}
			}
		});

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	/**
	 * This method will rescan for all local networks and store them into
	 * an ArrayList
	 */
	public void onUpdate() {
		// Store a list of all local networks
		currentNetworks = tester.getWifiNetworks();
		ArrayList<ScanResult> currentNetworks = tester.getWifiNetworks();
		gpsWireless.clear();

		// Iterate through all local WiFi networks and add them
		// to the map
		for (ScanResult item : currentNetworks) {
			gpsWireless.add(new LocationNetwork(item, 35, 24));
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * Adds all of our options to the menu at the top of the screen
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scan, menu);
		return true;
	}

	/**
	 * This method is automatically called when the user presses either the back
	 * button at the top of the screen or one of the menu items.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		int itemID = item.getItemId();
		// The refresh button used to manually update the list of
		// local networks
		if (itemID == R.id.action_refresh) {
			onUpdate();
			return true;
		}
		// The user returns to the home page when the back button at the
		// top of the screen is pressed
		else if (itemID == android.R.id.home) {
			NavUtils.navigateUpFromSameTask(this);
			return true;
		} else
			return super.onOptionsItemSelected(item);
	}

}
