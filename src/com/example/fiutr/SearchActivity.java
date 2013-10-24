package com.example.fiutr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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

public class SearchActivity extends Activity {

	// private EditText prefEditText;
	private SeekBar prefDistSeekbar, prefSignalSeekbar, prefResultsSeekbar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_pref);

		SharedPreferences customSharedPreference = getSharedPreferences(
				"myCustomSharedPrefs", Activity.MODE_PRIVATE);

		prefDistSeekbar = (SeekBar) findViewById(R.id.seek_distance);
		prefDistSeekbar.setProgress(customSharedPreference.getInt("myDistPref",
				50));

		prefSignalSeekbar = (SeekBar) findViewById(R.id.seek_signal);
		prefSignalSeekbar.setProgress(customSharedPreference.getInt(
				"mySignalPref", 50));

		prefResultsSeekbar = (SeekBar) findViewById(R.id.seek_results);
		prefResultsSeekbar.setProgress(customSharedPreference.getInt(
				"myResultsPref", 50));
		/*
		 * Button mClose = (Button) findViewById(R.id.close);
		 * mClose.setOnClickListener(new OnClickListener() { public void
		 * onClick(View v) { finish(); } });
		 */

		Button mSave = (Button) findViewById(R.id.save);
		mSave.setOnClickListener(new OnClickListener() {
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// NavUtils.navigateUpFromSameTask(this);
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
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