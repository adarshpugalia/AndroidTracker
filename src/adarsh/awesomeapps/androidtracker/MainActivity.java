package adarsh.awesomeapps.androidtracker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

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
		else if(!checkPlayServices())
		{
			Toast.makeText(getApplicationContext(), "Play services not available. Try Again.", Toast.LENGTH_LONG).show();
		}
		else 
		{
			/* get the GCM registration ID. */
			getRegistrationID();
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
	    checkPlayServices();
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
	
	public void getRegistrationID()
	{
		/* creating the GCMRegistration object. */
		GCMRegistrationID gcmRegistrationId = new GCMRegistrationID(this);
		
		/* getting the current registration ID from preference file. */
		gcmRegistrationId.getRegistrationID(this);
		
		/* if not registered, registring with GCM. */
		if(CommonUtilities.REGISTRATION_ID.isEmpty())
		{
			gcmRegistrationId.register(this);
		}
	}
}
