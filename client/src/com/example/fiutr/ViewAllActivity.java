package com.example.fiutr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

/**
 * ViewAllActivity creates a page which displays a list of networks. This is used for viewing
 * all of the networks in the database or for viewing a list of networks when performing a search.
 */
public class ViewAllActivity extends PreferenceActivity {

	/**
	 * Function automatically called with the ViewAllActivity page is created.
	 * It sets the activity_viewall layout containing a list of the networks 
	 * to be displayed
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PrefsFragment()).commit();
		PreferenceManager.setDefaultValues(ViewAllActivity.this,
				R.layout.activity_viewall, false);

		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
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

	@SuppressLint("ValidFragment")
	public class PrefsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {

			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.layout.activity_viewall);

			// Get the custom preference
			Preference customPref = (Preference) findPreference("customPref");
			customPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							Toast.makeText(getBaseContext(),
									"The custom preference has been clicked",
									Toast.LENGTH_LONG).show();
							SharedPreferences customSharedPreference = getSharedPreferences(
									"myCustomSharedPrefs",
									Activity.MODE_PRIVATE);
							SharedPreferences.Editor editor = customSharedPreference
									.edit();
							editor.putString("myCustomPref",
									"The preference has been clicked");
							editor.commit();
							return true;
						}

					});

		}

	}
}
