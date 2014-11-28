package adarsh.awesomeapps.androidtracker;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class User extends ActionBarActivity {

	GPS gps;
	TextView lattitude;
	TextView longitude;
	Location location;
	Handler handler;
	Runnable runnableThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		
		lattitude = (TextView)findViewById(R.id.user_text_lattitude);
		longitude = (TextView)findViewById(R.id.user_text_longitude);
		handler = new Handler();
		
		runnableThread = new Runnable()
		{	
			@Override
			public void run()
			{
				updateLocation();
				handler.postDelayed(runnableThread, 1000);
			}
		};
		
		handler.postDelayed(runnableThread, 1000);
		
		//checkPlayServices();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user, menu);
		return true;
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		handler.removeCallbacks(runnableThread);
		finish();
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
	
	public boolean checkPlayServices()
	{
		/* checking for services. */
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		
		if(resultCode != ConnectionResult.SUCCESS)
		{
			/* checking if the device is supported. */
			if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
			{
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, CommonUtilities.PLAY_SERVICE_RESOLUTION_REQUEST).show();
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Device not supported.", Toast.LENGTH_LONG).show();
				finish();
			}
			return false;
		}
		return true;
	}
	
	public void updateLocation()
	{
		SharedPreferences prefs = getSharedPreferences("TRACKING_preferences", Context.MODE_PRIVATE);
		lattitude.setText(prefs.getString("CUR_TIME", "No time found."));
	}
}
