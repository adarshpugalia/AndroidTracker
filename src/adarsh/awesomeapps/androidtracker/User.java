package adarsh.awesomeapps.androidtracker;

import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class User extends ActionBarActivity {

	public final static String EXTRA_MESSAGE = "adarsh.awesomeapps.androidtracker.MESSAGE";
	GPS gps;
	TextView lattitude;
	TextView longitude;
	Location location;
	Handler handler;
	Runnable runnableThread;
	String contact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		contact = intent.getStringExtra(Home.EXTRA_MESSAGE);
		getActionBar().setTitle((new ContactDetails(getContentResolver())).getContactName(contact));
		setContentView(R.layout.activity_user);
		
		TextView textView = (TextView)findViewById(R.id.user_text_map);
		
		checkPlayServices();
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
		//handler.removeCallbacks(runnableThread);
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
	
	public void drawMap(View view)
	{
		Toast.makeText(getApplicationContext(), "Map: "+contact+"latititude", Toast.LENGTH_SHORT).show();
		SharedPreferences prefs = getSharedPreferences("TRACKING_preferences", Context.MODE_PRIVATE);
		String latitude = prefs.getString(contact+"latitude", "");
		if(latitude.equals(""))
			Toast.makeText(getApplicationContext(), "No location found to be shown on map.", Toast.LENGTH_SHORT).show();
		else
		{
			Intent intent = new Intent(this, UserMapLocation.class);
			intent.putExtra(EXTRA_MESSAGE, contact);
			startActivity(intent);
		}
	}
	
	public void track(View view)
	{
		SharedPreferences login_preferences = getSharedPreferences("LOGIN_preferences", Context.MODE_PRIVATE);
		String phone = login_preferences.getString("PHONE", "");
		if(phone.isEmpty())
		{
			Toast.makeText(getApplicationContext(), "Login: Phone number not found.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		ServerRequest serverRequest = new ServerRequest(getApplicationContext());
		try 
		{
			Toast.makeText(getApplicationContext(), "Tracker: "+phone+" Trackee: "+contact, Toast.LENGTH_SHORT).show();
			serverRequest.execute("track_request.php", String.valueOf(2), "Tracker", phone, "Trackee", contact).get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String reply = serverRequest.getReply();
		if(reply.equals("Success!"))
			Toast.makeText(getApplicationContext(), "Tracking request sent!", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(getApplicationContext(), reply, Toast.LENGTH_SHORT).show();
	}
}
