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

public class Register extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
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
	
	/* This function starts the login activity. */
	public void login(View view)
	{
		Intent intent = new Intent(this, Login.class);
		startActivity(intent);
	}
	
	/* This function registers the user with the servers, and starts home activity if successful. */
	public void register(View view)
	{
		/* extracting user details. */
		EditText editText = (EditText)findViewById(R.id.register_edit_name);
		String name = editText.getText().toString();
		
		editText = (EditText)findViewById(R.id.register_edit_phone);
		String phone = editText.getText().toString();
		
		editText = (EditText)findViewById(R.id.register_edit_password);
		String password = editText.getText().toString();
		
		/* checking for empty inputs. */
		if(name.isEmpty() || phone.isEmpty() || password.isEmpty())
		{
			Toast.makeText(getApplicationContext(), "Please fill all the fields.", Toast.LENGTH_LONG).show();
			return;
		}
		
		/* sending the request to the server. */
		ServerRequest serverRequest = new ServerRequest(this);
		try 
		{
			serverRequest.execute("register.php", String.valueOf(4), "Phone", phone, "GCM_ID", "abc", "Name", name, "Password", password).get();
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
		
		/* if registeration successful, saving login details and starting home activity. */
		if(reply.equals("Success!"))
		{
			SharedPreferences prefs = getSharedPreferences("LOGIN_preferences", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("PHONE", phone);
			editor.putString("NAME", name);
			editor.putString("PASSWORD", password);
			editor.commit();
			
			Intent intent = new Intent(this, UpdateLocationService.class);
			startService(intent);
			
			/*intent = new Intent(this, TrackingService.class);
			startService(intent);*/
			
			intent = new Intent(this, Home.class);
			startActivity(intent);
		}
	}
}
