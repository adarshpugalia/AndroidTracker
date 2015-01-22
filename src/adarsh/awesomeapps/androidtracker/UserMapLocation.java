package adarsh.awesomeapps.androidtracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class UserMapLocation extends Activity
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
		
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        
        LocationTracker locationTracker = new LocationTracker(getApplicationContext());
        Location location = locationTracker.getLocation();

        SharedPreferences prefs = getSharedPreferences("TRACKING_preferences", Context.MODE_PRIVATE);
        double latitude = Double.parseDouble(prefs.getString(contact+"latitude", "0"));
        double longitude = Double.parseDouble(prefs.getString(contact+"longitude", "0"));
        double radius = prefs.getInt(contact+"radius", 1000);
        
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(new ContactDetails(getContentResolver()).getContactName(contact)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("My Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12);
        mMap.animateCamera(cameraUpdate);
        
        mMap.addCircle(new CircleOptions().center(new LatLng(location.getLatitude(), location.getLongitude())).radius(radius).strokeColor(Color.BLUE).fillColor(Color.CYAN));
       
        String url = getDirectionsUrl(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(latitude, longitude));
        new DownloadTask().execute(url);
	}
	
	private String getDirectionsUrl(LatLng source, LatLng dest)
	{
        String str_origin = "origin="+source.latitude+","+source.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
	
		// Sensor enabled
		String sensor = "sensor=false";
		String parameters = str_origin+"&"+str_dest+"&"+sensor;
		
		// Output format
		String output = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
		return url;
	}
	
	private String downloadUrl(String strUrl) throws IOException
	{
		String data = "";
	    InputStream iStream = null;
	    HttpURLConnection urlConnection = null;
	    try{
	            URL url = new URL(strUrl);
	
	            // Creating an http connection to communicate with url
	            urlConnection = (HttpURLConnection) url.openConnection();
	
	            // Connecting to url
	            urlConnection.connect();
	
	            // Reading data from url
	            iStream = urlConnection.getInputStream();
	
	            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
	
	            StringBuffer sb  = new StringBuffer();
	
	            String line = "";
	            while( ( line = br.readLine())  != null){
	                sb.append(line);
	            }
	
	            data = sb.toString();
	
	            br.close();
	
	        }catch(Exception e){
	            Log.d("Exception while downloading url", e.toString());
	        }finally{
	            iStream.close();
	            urlConnection.disconnect();
	        }
	        return data;
	    }
	private class DownloadTask extends AsyncTask<String, Void, String>{
	
	// Downloading data in non-ui thread
	@Override
	protected String doInBackground(String... url) {
	
	    // For storing data from web service
	
	    String data = "";
	
	    try{
	        // Fetching the data from web service
	         data = downloadUrl(url[0]);
	    }catch(Exception e){
	        Log.d("Background Task",e.toString());
	    }
	    return data;
	}
	
	
	        // Executes in UI thread, after the execution of
	// doInBackground()
	@Override
	protected void onPostExecute(String result) {
	    super.onPostExecute(result);
	
	    ParserTask parserTask = new ParserTask();
	
	    // Invokes the thread for parsing the JSON data
	    parserTask.execute(result);
	}
	}
	private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
	
	// Parsing the data in non-ui thread
	@Override
	protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
	
	JSONObject jObject;
	List<List<HashMap<String, String>>> routes = null;
	
	    try{
	        jObject = new JSONObject(jsonData[0]);
	        DirectionsJSONParser parser = new DirectionsJSONParser();
	
	        // Starts parsing data
	        routes = parser.parse(jObject);
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    return routes;
	}
	
	// Executes in UI thread, after the parsing process
	@Override
	protected void onPostExecute(List<List<HashMap<String, String>>> result) {
	
	    ArrayList<LatLng> points = null;
	    PolylineOptions lineOptions = null;
	
	    // Traversing through all the routes
	    for(int i=0;i<result.size();i++){
	        points = new ArrayList<LatLng>();
	        lineOptions = new PolylineOptions();
	
	        // Fetching i-th route
	        List<HashMap<String, String>> path = result.get(i);
	
	        // Fetching all the points in i-th route
	        for(int j=0;j<path.size();j++){
	            HashMap<String,String> point = path.get(j);
	
	            double lat = Double.parseDouble(point.get("lat"));
	            double lng = Double.parseDouble(point.get("lng"));
	            LatLng position = new LatLng(lat, lng);
	
	            points.add(position);
	        }
	
	        // Adding all the points in the route to LineOptions
	        lineOptions.addAll(points);
	        lineOptions.width(2);
	        lineOptions.color(Color.RED);
	    }
	
	     // Drawing polyline in the Google Map for the i-th route
	     mMap.addPolyline(lineOptions);
	 }
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
