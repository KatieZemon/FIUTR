package com.example.fiutr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

/**
 * SearchActivity creates the Search page in which users can modify the values of each 
 * search criteria (distance, signal strength, and number of results). This page also includes
 * a button to perform the search and return a list of the networks according to the user
 * preferences
 */
public class SearchActivity extends Activity {

	// SeekBars for setting preferences
	private SeekBar prefDistSeekbar; // Seekbar for setting the distance
	private SeekBar prefSignalSeekbar; // Seekbar for setting the minimal signal strength
	private SeekBar prefResultsSeekbar; // Seekbar for setting the maximum number of results returned
	
	// Values of our preferences
	private TextView prefDistVal; // The maximum distance away from the user's current location
	private TextView prefSignalVal; // The minimum signal strength of the networks returned from a search
	private TextView prefResultsVal; // The maximum number of networks returned from a search
	
	// Search button
	private Button searchButton;
	
	// File to send to the intent
	private String filePath;
	
	/**
	 * Method automatically called when the SearchActivity page is created.
	 * It initializes the seekbars (and values of seekBars according to what has been
	 * saved from previously using the application) and initializes the searchButton
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null)
		{
			filePath = extras.getString("FILE_PATH");
		}

		SharedPreferences customSharedPreference = getSharedPreferences("myCustomSharedPrefs", Activity.MODE_PRIVATE);

		// Distance preference
		prefDistSeekbar = (SeekBar) findViewById(R.id.seek_distance);
		prefDistSeekbar.setProgress(customSharedPreference.getInt("myDistPref",50));
		prefDistSeekbar.setOnSeekBarChangeListener(new SeekBarListener());
		
		prefDistVal = (TextView) findViewById(R.id.text_distVal);
		prefDistVal.setText(""+customSharedPreference.getInt("myDistPref",50)+" miles");

		// Signal Strength Preference
		prefSignalSeekbar = (SeekBar) findViewById(R.id.seek_signal);
		prefSignalSeekbar.setProgress(customSharedPreference.getInt("mySignalPref", 50));
		prefSignalSeekbar.setOnSeekBarChangeListener(new SeekBarListener());
		
		prefSignalVal = (TextView) findViewById(R.id.text_signalVal);
		prefSignalVal.setText(""+customSharedPreference.getInt("mySignalPref",50));

		// Results Preference
		prefResultsSeekbar = (SeekBar) findViewById(R.id.seek_results);
		prefResultsSeekbar.setProgress(customSharedPreference.getInt("myResultsPref", 50));
		prefResultsSeekbar.setOnSeekBarChangeListener(new SeekBarListener());
		
		prefResultsVal = (TextView) findViewById(R.id.text_resultsVal);
		prefResultsVal.setText(""+customSharedPreference.getInt("myResultsPref",50));
		
		// Search Button
		searchButton = (Button) findViewById(R.id.save);
		searchButton.setOnClickListener(new OnClickListener() {
			/**
			 * Stores the preference values set by the user and opens a page to display
			 * the search results
			 */
			public void onClick(View v) {
				savePreferences();
				Intent intent = new Intent(SearchActivity.this, ViewAllActivity.class);
				intent.putExtra("FILE_PATH",filePath);
				intent.putExtra("BOOL_VIEW_ALL",false); // Viewing only data pertaining to search results
				startActivity(intent);
				finish();
			}
		});
		

		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	/**
	 * Listener class for when one of the seekbar values is changed. This will update the
	 * textfield which displays the value of each seekbar
	 */
	private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
		/**
		 * Updates the textfield displaying the value of the seekbar being used
		 */
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar == prefDistSeekbar)
            	prefDistVal.setText(""+progress+ " miles");
            else if (seekBar == prefSignalSeekbar)
            	prefSignalVal.setText(""+progress);
            else if (seekBar == prefResultsSeekbar)
            	prefResultsVal.setText(""+progress);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {}

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
	

	/**
	 * Stores the values of each seekbar so that the next time this page is opened,
	 * these saved values will be loaded. This is called when the user presses the "Search"
	 * button
	 */
	private void savePreferences() {
		SharedPreferences customSharedPreference = getSharedPreferences(
				"myCustomSharedPrefs", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = customSharedPreference.edit();

		editor.putInt("myDistPref", prefDistSeekbar.getProgress());
		editor.putInt("mySignalPref", prefSignalSeekbar.getProgress());
		editor.putInt("myResultsPref", prefResultsSeekbar.getProgress());
		editor.commit();
	}
}