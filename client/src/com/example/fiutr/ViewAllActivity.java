package com.example.fiutr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * ViewAllActivity creates a page which displays a list of networks. This is used for viewing
 * all of the networks in the database or for viewing a list of networks when performing a search.
 */
public class ViewAllActivity extends ListActivity {

	/**
	 * Method automatically called with the ViewAllActivity page is created.
	 * It sets the activity_viewall layout containing a list of the networks 
	 * to be displayed
	 */
	
	NetworkAdapterItem adapter;
	ArrayList<Network> networkList = new ArrayList<Network>();
	private String filePath;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewall);	
		
		Bundle extras = getIntent().getExtras();
		if(extras != null)
		{
			filePath = extras.getString("FILE_PATH");
		}
		
		getData();
		adapter = new NetworkAdapterItem(this, R.layout.list_viewall, networkList);
		setListAdapter(adapter);
		
		
		
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	public void getData()
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
					networkList.add(new Network(parsedLine[0], parsedLine[1], Double.parseDouble(parsedLine[2]), Double.parseDouble(parsedLine[3])));
				}
				else
				{
					System.out.println("Caught blankspace!");
				}
			}
			reader.close();
		}
		catch (Exception e)
		{
			System.err.println("Unable to populate view all!\n");
			e.printStackTrace();
		}
	}

	/**
	 * The user will return to the home page when the back button
	 * at the top of the screen is pressed
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
