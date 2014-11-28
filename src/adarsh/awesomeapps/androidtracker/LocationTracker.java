package adarsh.awesomeapps.androidtracker;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.internal.lo;
import com.google.android.gms.location.LocationListener;

public class LocationTracker implements android.location.LocationListener
{
	Context context;
	boolean isGPSEnabled;
	boolean isNetworkEnabled;
	Location location;
	LocationManager locationManager;
	
	public LocationTracker(Context ctx)
	{
		context = ctx;
		isGPSEnabled = false;
		isNetworkEnabled = false;
	}
	
	public Location getLocation()
	{
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if(!isGPSEnabled && !isNetworkEnabled)
			return null;
		
		if(isNetworkEnabled)
		{
			locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    (long)1000*60,
                    (float)10, this);
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		else
		{
			locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    (long)1000*60,
                    (float)10, this);
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		
		return location;
	}
	
	@Override
    public void onLocationChanged(Location location)
	{
		
    }
	
	@Override
	public void onProviderEnabled(String s)
	{
		
	}
	
	@Override
	public void onProviderDisabled(String s)
	{
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
