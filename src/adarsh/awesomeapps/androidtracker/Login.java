package adarsh.awesomeapps.androidtracker;

import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
	
	/* This function starts the register activity. */
	public void register(View view)
	{
		Intent intent = new Intent(this, Register.class);
		startActivity(intent);
	}
	
	public void login(View view)
	{
		SharedPreferences prefs = getSharedPreferences("GCM_preferences", Context.MODE_PRIVATE);
		String gcmID = prefs.getString("REGISTRATION_ID", "");
		
		EditText editText = (EditText)findViewById(R.id.login_edit_phone);
		String phone = editText.getText().toString();
		
		editText = (EditText)findViewById(R.id.login_edit_password);
		String password = editText.getText().toString();
		
		/* checking for empty inputs. */
		if(phone.isEmpty() || password.isEmpty())
		{
			Toast.makeText(getApplicationContext(), "Please fill all the fields.", Toast.LENGTH_LONG).show();
			return;
		}
		
		if(gcmID.isEmpty())
		{
			Toast.makeText(getApplicationContext(), "GCM ID not found.", Toast.LENGTH_LONG).show();
			return;
		}
		
		/* sending the request to the server for login. */
		ServerRequest serverRequest = new ServerRequest(this);
		try 
		{
			serverRequest.execute("login.php", String.valueOf(3), "Phone", phone, "Password", password, "GCM_ID", gcmID).get();
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		} 
		catch (ExecutionException e) 
		{
			e.printStackTrace();
		}
		
		/* getting the server reply. */
		String reply = serverRequest.getReply();
		Toast.makeText(getApplicationContext(), reply, Toast.LENGTH_LONG).show();
		
		/* if login successful, saving login details and starting home activity. */
		/* TO - DO do we need to ask server for other details ? */
		if(reply.equals("Success!"))
		{
			
			prefs = getSharedPreferences("LOGIN_preferences", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("PHONE", phone);
			editor.putString("PASSWORD", password);
			editor.commit();
			
			Intent intent = new Intent(this, TrackingService.class);
			startService(intent);
			
			intent = new Intent(this, UpdateLocationService.class);
			startService(intent);
			
			intent = new Intent(this, Home.class);
			startActivity(intent);
		}
	}
}