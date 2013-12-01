package com.example.fiutr;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

/**
 * AboutActivity creates the About page which displays information about the app,
 * including the authors and a list of references
 */
public class AboutActivity extends Activity {
	
	/**
	 * Function automatically called with the About page is created.
	 * It loads the activity_about layout which contains a textview
	 * containing all of the information that will be displayed on this page
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

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
}