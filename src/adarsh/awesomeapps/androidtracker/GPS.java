package adarsh.awesomeapps.androidtracker;

import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

public class GPS implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener
{
	User activity;
	LocationClient locationClient;
	Location currentLocation;
	
	GPS(User act)
	{
		activity = act;
		locationClient = new LocationClient(activity, this, this);
	}
	
	/*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle)
    {
        // Display the connection status
        Toast.makeText(activity.getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
        activity.updateLocation();
    }
    
    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(activity.getApplicationContext(), "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }
    
    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution())
        {
            try 
            {
                /* Start an Activity that tries to resolve the error. */
                connectionResult.startResolutionForResult(activity, CommonUtilities.PLAY_SERVICE_RESOLUTION_REQUEST);
                
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } 
            catch (IntentSender.SendIntentException e)
            {
                // Log the error
                e.printStackTrace();
            }
        } 
        else
        {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Toast.makeText(activity.getApplicationContext(), "Error in play services. Try Again.", Context.MODE_PRIVATE).show();
        }
    }
    
    @Override
    public void onLocationChanged(Location location)
    {
    	Toast.makeText(activity.getApplicationContext(), "Location changed.", Toast.LENGTH_SHORT).show();
        activity.updateLocation();
    }
    
    void connect()
    {
    	locationClient.connect();
    }
    
    void disconnect()
    {
    	locationClient.disconnect();
    }
    
    Location getLastLocation()
    {
    	currentLocation = locationClient.getLastLocation();
    	return currentLocation;
    }
    
    Boolean isConnected()
    {
    	return locationClient.isConnected();
    }
}
