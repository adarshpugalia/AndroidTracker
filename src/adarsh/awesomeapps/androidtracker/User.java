package adarsh.awesomeapps.androidtracker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class User extends ActionBarActivity {

	public final static String EXTRA_MESSAGE = "adarsh.awesomeapps.androidtracker.MESSAGE";
	String contact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		contact = intent.getStringExtra(Home.EXTRA_MESSAGE);
		getActionBar().setTitle((new ContactDetails(getContentResolver())).getContactName(contact));
		setContentView(R.layout.activity_user);
		
		setLayout();
		checkPlayServices();
	}
	
	public void setLayout()
	{
		// getting screen width
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int width = metrics.widthPixels;	
		int height = metrics.heightPixels;
		
		// setting width for first name field
		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)findViewById(R.id.user_text_track).getLayoutParams();
		param.height = (int)height/10;
		findViewById(R.id.user_text_track).setLayoutParams(param);
		
		TextView textView = (TextView)findViewById(R.id.user_text_track);
		textView.setTextSize(10 * getResources().getDisplayMetrics().density);
		
		param = (RelativeLayout.LayoutParams)findViewById(R.id.user_text_toggle).getLayoutParams();
		param.height = (int)height/10;
		findViewById(R.id.user_text_toggle).setLayoutParams(param);
		
		textView = (TextView)findViewById(R.id.user_text_toggle);
		textView.setTextSize(10 * getResources().getDisplayMetrics().density);
		
		SharedPreferences prefs = getSharedPreferences("TRACKING_preferences", Context.MODE_PRIVATE);
		textView.setText(prefs.getString(contact+"tracking", "Off"));
		
		textView = (TextView)findViewById(R.id.user_text_track_space);
		textView.setBackgroundColor(Color.GRAY);
		
		param = (RelativeLayout.LayoutParams)findViewById(R.id.user_text_map).getLayoutParams();
		param.height = (int)height/10;
		findViewById(R.id.user_text_map).setLayoutParams(param);
		
		textView = (TextView)findViewById(R.id.user_text_map);
		textView.setTextSize(10 * getResources().getDisplayMetrics().density);
		
		textView = (TextView)findViewById(R.id.user_text_map_space);
		textView.setBackgroundColor(Color.GRAY);
		
		param = (RelativeLayout.LayoutParams)findViewById(R.id.user_text_geo).getLayoutParams();
		param.height = (int)height/10;
		findViewById(R.id.user_text_geo).setLayoutParams(param);
		
		textView = (TextView)findViewById(R.id.user_text_geo);
		textView.setTextSize(10 * getResources().getDisplayMetrics().density);
		
		param = (RelativeLayout.LayoutParams)findViewById(R.id.user_text_geo_radius).getLayoutParams();
		param.height = (int)height/10;
		findViewById(R.id.user_text_geo_radius).setLayoutParams(param);
		
		textView = (TextView)findViewById(R.id.user_text_geo_radius);
		textView.setTextSize(10 * getResources().getDisplayMetrics().density);
		textView.setText(String.valueOf(prefs.getInt(contact+"radius", 1000)));
		
		textView = (TextView)findViewById(R.id.user_text_geo_space);
		textView.setBackgroundColor(Color.GRAY);
		
		param = (RelativeLayout.LayoutParams)findViewById(R.id.user_text_address_text).getLayoutParams();
		param.height = (int)height/10;
		findViewById(R.id.user_text_address_text).setLayoutParams(param);
		
		textView = (TextView)findViewById(R.id.user_text_address_text);
		textView.setTextSize(10 * getResources().getDisplayMetrics().density);
		
		textView = (TextView)findViewById(R.id.user_text_address_space);
		textView.setTextSize(9 * getResources().getDisplayMetrics().density);
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
	
	public void setRadius(View view)
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		
		alertDialog.setTitle("Geofencing radius");
		
		alertDialog.setMessage("Set radius in metres:");
        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
          input.setLayoutParams(lp);
          input.setInputType(InputType.TYPE_CLASS_NUMBER);
          alertDialog.setView(input);
          
          alertDialog.setPositiveButton("Yes",
                  new DialogInterface.OnClickListener()
          		  {
                      public void onClick(DialogInterface dialog,int which)
                      {
                          String radius = input.getText().toString();
                          SharedPreferences prefs = getSharedPreferences("TRACKING_preferences", Context.MODE_PRIVATE);
                          SharedPreferences.Editor edit = prefs.edit();
                          edit.putInt(contact+"radius", Integer.parseInt(radius));
                          edit.commit();
                          
                          TextView textView = (TextView) findViewById(R.id.user_text_geo_radius);
                          textView.setText(radius);
                      }
                  }
          );
          
          alertDialog.setNegativeButton("No",
                  new DialogInterface.OnClickListener()
          		  {
                      public void onClick(DialogInterface dialog,int which)
                      {
                          dialog.cancel();
                      }
                  }
          );
          
          alertDialog.show();
	}
	
	public void drawMap(View view)
	{
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
		TextView textView = (TextView) findViewById(R.id.user_text_toggle);
		
		Intent intent = new Intent(this, TrackingService.class);
		intent.putExtra(EXTRA_MESSAGE, contact);
		startService(intent);
		
		String toggleString = textView.getText().toString();
		if(toggleString.equals("Off"))
		{
			SharedPreferences prefs = getSharedPreferences("TRACKING_preferences", Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putString(contact+"tracking", "On");
			edit.commit();
			textView.setText("On");
		}
		else
		{
			SharedPreferences prefs = getSharedPreferences("TRACKING_preferences", Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putString(contact+"tracking", "Off");
			edit.commit();
			textView.setText("Off");
		}
	}
	
	public void addressToggle(View view)
	{
		TextView textView = (TextView)findViewById(R.id.user_text_address_text);
		String test = textView.getText().toString();
		if(test.equals("Show Address"))
		{
			SharedPreferences prefs = getSharedPreferences("TRACKING_preferences", Context.MODE_PRIVATE);
			
			String latitude = prefs.getString(contact+"latitude", "");
			if(latitude.equals(""))
			{
				TextView textView2 = (TextView)findViewById(R.id.user_text_address_space);
				textView2.setText("No address found!");
			}
			else
			{
				String longitude = prefs.getString(contact+"longitude", "");
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Geocoder.isPresent())
				{
                    /*
                     * Reverse geocoding is long-running and synchronous.
                     * Run it on a background thread.
                     * Pass the current location to the background task.
                     * When the task finishes,
                     * onPostExecute() displays the address.
                     */
                    Location locA = new Location("Source");
                                locA.setLatitude(Double.parseDouble(latitude));
                                locA.setLongitude(Double.parseDouble(longitude));
                    (new GetAddressTask(getApplicationContext())).execute(locA);
                }
				else
				{
					TextView textView2 = (TextView)findViewById(R.id.user_text_address_space);
					textView2.setText("Version Not supported!");
				}
			}
			
			textView.setText("Clear Address");
		}
		else
		{
			TextView textView2 = (TextView)findViewById(R.id.user_text_address_space);
			textView2.setText("");
			textView.setText("Show Address");
		}
	}
	
	private class GetAddressTask extends AsyncTask<Location, Void, String>
	{
	    Context mContext;
	    
	    public GetAddressTask(Context context)
	    {
	        super();
	        mContext = context;
	    }
	    
	    /**
	     * Get a Geocoder instance, get the latitude and longitude
	     * look up the address, and return it
	     *
	     * @params params One or more Location objects
	     * @return A string containing the address of the current
	     * location, or an empty string if no address can be found,
	     * or an error message
	     */
	    @Override
	    protected String doInBackground(Location... params)
	    {
	        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
	        // Get the current location from the input parameter list
	        Location loc = params[0];
	        // Create a list to contain the result address
	        List<Address> addresses = null;
	        try {
	            /*
	             * Return 1 address.
	             */
	            addresses = geocoder.getFromLocation(loc.getLatitude(),
	                    loc.getLongitude(), 1);
	        } catch (IOException e1) {
	        Log.e("LocationSampleActivity",
	                "IO Exception in getFromLocation()");
	        e1.printStackTrace();
	        return ("IO Exception trying to get address");
	        } catch (IllegalArgumentException e2) {
	        // Error message to post in the log
	        String errorString = "Illegal arguments " +
	                Double.toString(loc.getLatitude()) +
	                " , " +
	                Double.toString(loc.getLongitude()) +
	                " passed to address service";
	        Log.e("LocationSampleActivity", errorString);
	        e2.printStackTrace();
	        return errorString;
	        }
	        // If the reverse geocode returned an address
	        if (addresses != null && addresses.size() > 0) {
	            // Get the first address
	            Address address = addresses.get(0);
	            /*
	             * Format the first line of address (if available),
	             * city, and country name.
	             */
	            String addressText = String.format(
	                    "%s, %s, %s",
	                    // If there's a street address, add it
	                    address.getMaxAddressLineIndex() > 0 ?
	                            address.getAddressLine(0) : "",
	                    // Locality is usually a city
	                    address.getLocality(),
	                    // The country of the address
	                    address.getCountryName());
	            // Return the text
	            return addressText;
	        } else {
	            return "No address found";
	        }
	    }
	    
		/**
		* A method that's called once doInBackground() completes. Turn
		* off the indeterminate activity indicator and set
		* the text of the UI element that shows the address. If the
		* lookup failed, display the error message.
		*/
		@Override
		protected void onPostExecute(String address)
		{
			// Display the results of the lookup.
			TextView mAddress = (TextView)findViewById(R.id.user_text_address_space);
			mAddress.setText(address);
		}
	}

}

