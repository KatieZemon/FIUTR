package com.example.fiutr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends Activity {

	// SeekBars for setting preferences
	private SeekBar prefDistSeekbar;
	private SeekBar prefSignalSeekbar;
	private SeekBar prefResultsSeekbar;
	
	// Values of our preferences
	private TextView prefDistVal;
	private TextView prefSignalVal;
	private TextView prefResultsVal;
	
	// Search button
	private Button searchButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

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
			public void onClick(View v) {
				savePreferences();
				finish();
			}
		});
		

		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

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
 

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//NavUtils.navigateUpFromSameTask(this);
			finish();
			//Intent intent = new Intent(this, MainActivity.class);
			//startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

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