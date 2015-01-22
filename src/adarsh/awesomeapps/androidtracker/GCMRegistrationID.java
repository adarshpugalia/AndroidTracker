package adarsh.awesomeapps.androidtracker;

import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMRegistrationID {
	
	GoogleCloudMessaging gcm;
	Context context;
	
	/* constructor for the object. */
	public GCMRegistrationID(Context ctx) {
		gcm = GoogleCloudMessaging.getInstance(ctx);
		context = ctx;
	}
	
	/* This function gets the Registration ID of the application. */
	public String getRegistrationID()
	{
		/* getting the registration ID. */
		final SharedPreferences prefs = context.getSharedPreferences("GCM_preferences", Context.MODE_PRIVATE);
		String registration_ID = prefs.getString("REGISTRATION_ID", "");
		
		/* checking if the version matches. */
		int appVersion = prefs.getInt("VERSION", 0);
		if(appVersion != getAppVersion())
			return "";
		
		return registration_ID;
	}
	
	/* This function returns the version number of the application. */
	public int getAppVersion()
	{
		try
		{
			PackageInfo packageInfo = context.getApplicationContext().getPackageManager().getPackageInfo(context.getApplicationContext().getPackageName(), 0);
			return packageInfo.versionCode;
		}
		catch(NameNotFoundException e)
		{
			throw new RuntimeException("Could not get package name: "+e);
		}
	}
	
	/* This function registers the application with GCM. */
	public void register()
	{
		/* creating a dialog box. */
		/*final ProgressDialog dialog = new ProgressDialog(context);
		dialog.setMessage("Registering with Google Cloud Servers!");
		dialog.show();*/
		
		/* registering to GCM. */
		new AsyncTask<Void, Void, String>() 
		{
			@Override
			protected String doInBackground(Void... params)
			{
				String message = "";
				try
				{	

					/* getting the ID. */
					String registrationID = gcm.register(CommonUtilities.SENDER_ID);
					
					/* saving the ID in preference file. */
					SharedPreferences prefs = context.getSharedPreferences("GCM_preferences", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString("REGISTRATION_ID", registrationID);
					editor.putInt("VERSION", getAppVersion());
					editor.commit();
				}
				catch (IOException e)
				{	
					message = e.getMessage();
				}
				
				return message;
			}
			
			@Override
			protected void onPostExecute(String message)
			{
				/* dismissing the dialog box. */
				//dialog.dismiss();
				
				if(!message.isEmpty())
				{
					Toast.makeText(context, message, Toast.LENGTH_LONG);
					Log.e("Error_log", message);
				}
			}			
		}.execute(null, null, null);
	}
}