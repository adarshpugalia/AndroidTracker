package adarsh.awesomeapps.androidtracker;

import android.content.Context;
import android.content.SharedPreferences;

public class CommonUtilities {
	
	/* for play services check request. */
	static final int PLAY_SERVICE_RESOLUTION_REQUEST = 9000;
	
	/* sender ID for registeration, project number for the app. */
	static final String SENDER_ID = "446797375642";
	
	/* TO-D0 To be removed. */
	/* API key for server, needed for authentication. */
	static final String SERVER_API_KEY = "AIzaSyAgnFFMa0aXX-riyOAdIvunLWzgYKqoWQA";
	
	/* This function returns the Phone number of the user. */
	public static String getPhoneNumber(Context context)
	{
		SharedPreferences prefs = context.getSharedPreferences("LOGIN_preferences", Context.MODE_PRIVATE);
		return prefs.getString("PHONE", "");
	}
}
