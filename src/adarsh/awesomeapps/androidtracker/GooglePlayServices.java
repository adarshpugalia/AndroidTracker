package adarsh.awesomeapps.androidtracker;

import android.app.Activity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class GooglePlayServices
{
	private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	Activity activity;
	
	public GooglePlayServices(Activity act)
	{
		activity = act;
	}
	
	public Boolean servicesConnected()
	{
		/* checking for services. */
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
		
		if(resultCode != ConnectionResult.SUCCESS)
		{
			/* checking if the device is supported. */
			if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
			{
				GooglePlayServicesUtil.getErrorDialog(resultCode, activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
				return true;
			}
			
			return false;
		}
		return true;
	}
}
