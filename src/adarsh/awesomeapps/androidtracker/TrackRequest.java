package adarsh.awesomeapps.androidtracker;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class TrackRequest extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_request);
		
		populateRequests();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.track_request, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/* This function displays on the screen the list of track requests. */
	public void populateRequests()
	{	
		ContactDetails contactDetails = new ContactDetails(getContentResolver());
		
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		
		SharedPreferences prefs = getSharedPreferences("TRACKING_REQUESTS_preferences", Context.MODE_PRIVATE);
		int contacts = prefs.getInt("Count", 0);
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int height = metrics.heightPixels;
		
		for(int i=0; i<contacts; i++)
		{
			TextView textView = new TextView(this);
			textView.setText(contactDetails.getContactName(prefs.getString(String.valueOf(i+1), "")));
			textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int)height/9));
			textView.setTextSize(10 * getResources().getDisplayMetrics().density);
			textView.setGravity(Gravity.CENTER_VERTICAL);
			/*textView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(activity, User.class);
					intent.putExtra(EXTRA_MESSAGE, ((TextView)v).getText());
					startActivity(intent);
				}
			});*/
			linearLayout.addView(textView);
			
			TextView blank_view = new TextView(this);
			blank_view.setText("");
			blank_view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
			blank_view.setBackgroundColor(Color.GRAY);
			linearLayout.addView(blank_view);
		}
		
		//scrollView.addView(scrollView);
		this.setContentView(linearLayout);
	}
}
