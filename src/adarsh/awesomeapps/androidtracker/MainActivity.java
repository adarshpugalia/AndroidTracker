package adarsh.awesomeapps.androidtracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends ActionBarActivity {

	private Boolean isChecked = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/* checking for internet connection. */
		if(!checkInternetConnection())
		{
			Toast.makeText(getApplicationContext(), "No Internet Connection.", Toast.LENGTH_LONG).show();
		}
		/* checking for play services. */
		/*else if(!checkPlayServices())
		{
			Toast.makeText(getApplicationContext(), "Play services not available. Try Again.", Toast.LENGTH_LONG).show();
		}*/
		else 
		{	
			/* get the GCM registration ID. */
			//getRegistrationID();
			isChecked = true;
			
			Intent intent = new Intent(this, TrackingService.class);
			startService(intent);
			
			Toast.makeText(getApplicationContext(), "Tap to continue.", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	
	@Override
	protected void onResume()
	{
	    super.onResume();
	    //checkPlayServices();
	}
	
	/* onclicklistener for the activity. */
	public void nextActivity(View view)
	{
		if(isChecked)
		{
			Intent intent;
			
			/* If no login data found start Login activity, else start home activity*/
			if(CommonUtilities.getPhoneNumber(this).isEmpty())
				intent = new Intent(this, Login.class);
			else
				intent = new Intent(this, Home.class);
			
			startActivity(intent);
		}
	}
	
	/*
	 * This function checks whether the device is connected to a network.
	 */
	public boolean checkInternetConnection()
	{
		InternetConnection internetConnection = new InternetConnection(getApplicationContext());
		return internetConnection.isConnected();
	}
	
	/*
	 * This function checks if the device supports play services.
	 * If the devices does not have play services, it shows a dialog box to
	 * update the services.
	 * If the device is not supported, it exits the application.
	 */
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
	
	/* This function registers the device for GCM. */
	public void getRegistrationID()
	{
		/* creating the GCMRegistration object. */
		GCMRegistrationID gcmRegistrationId = new GCMRegistrationID(this);
		
		/* getting the current registration ID from preference file. */
		String registrationID = gcmRegistrationId.getRegistrationID();
		
		/* if not registered, registring with GCM. */
		if(registrationID.isEmpty())
		{
			gcmRegistrationId.register();
			registrationID = gcmRegistrationId.getRegistrationID();
			
			if(registrationID.isEmpty())
			{
				/* TO-DO remove finish() and use timer. */
				Toast.makeText(getApplicationContext(), "Could not register to GCM. Try again.", Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}
}