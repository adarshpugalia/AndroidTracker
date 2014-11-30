package adarsh.awesomeapps.androidtracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class UserMapLocation extends ActionBarActivity
{

	private GoogleMap mMap;
    private double lat;
    private double lon;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_map_location);
		
		Intent intent = getIntent();
		String contact = intent.getStringExtra(User.EXTRA_MESSAGE);
		getActionBar().setTitle((new ContactDetails(getContentResolver())).getContactName(contact));
		
		/*mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        
        LocationTracker locationTracker = new LocationTracker(getApplicationContext());
        Location location = locationTracker.getLocation();

        SharedPreferences prefs = getSharedPreferences("TRACKING_preferences", Context.MODE_PRIVATE);
        double latitude = Double.parseDouble(prefs.getString(contact+"latitude", "0"));
        double longitude = Double.parseDouble(prefs.getString(contact+"longitude", "0"));
        
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("contact"));
        
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15);
        mMap.animateCamera(cameraUpdate);*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_map_location, menu);
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
}
