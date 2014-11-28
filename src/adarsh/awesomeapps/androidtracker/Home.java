package adarsh.awesomeapps.androidtracker;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends ActionBarActivity {

	Activity activity = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		if(!checkPrefFile()) 
		{
			ProgressDialog dialog;
			
			/* creating a dialog until the user sets the contact list. */
			dialog = new ProgressDialog(this);
			dialog.setMessage("Building contacts list");
			dialog.show();
			
			/* query for the registered users. */
			queryRegisteredContacts();
			dialog.dismiss();
		}
		
		/* setting the contacts on display screen. */
		populateContacts();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
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
	
	public void track_user(View view)
	{
		Toast.makeText(getApplicationContext(), "Hi", Toast.LENGTH_LONG).show();
		Intent intent = new Intent(this, User.class);
		startActivity(intent);
	}
	
	/* This function displays on the screen the list of registered users. */
	public void populateContacts()
	{	
		/* Obtaining the text view of the layout. */
		TextView textView = (TextView)findViewById(R.id.home_text_contacts);
		String contact = "";
		
		/* Obtaining data from shared preference file. */
		SharedPreferences prefs = getSharedPreferences("CONTACTS_preferences", Context.MODE_PRIVATE);
		int contacts = prefs.getInt("CONTACTS", 0);
		
		for(int i=0; i<contacts; i++)
			contact += prefs.getString("CONTACTS_"+String.valueOf(i), "") + "\n";
		
		textView.setText(contact);
		textView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, User.class);
				startActivity(intent);
			}
		});
	}
	
	/* This function checks if there are any registered users in the preference file. */
	Boolean checkPrefFile()
	{
		SharedPreferences prefs = getSharedPreferences("CONTACTS_preferences", Context.MODE_PRIVATE);
		int contacts = prefs.getInt("CONTACTS", 0);
		
		if(contacts == 0)
			return false;
		
		return true;
	}
	
	/* This function queries the server for the list of registered contacts. */
	void queryRegisteredContacts()
	{
		/* building the request. */
		String[] contacts = (new ContactDetails(this.getContentResolver())).getAllContactNumbers();
		
		SharedPreferences prefs = getSharedPreferences("CONTACTS_preferences", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = prefs.edit();
		int count = 0;
		
		String concatenatedContacts = "";
		for(int i=0; i<contacts.length; i++)
		{
			if(!getFormattedContact(contacts[i]).equals(""))
			{
				concatenatedContacts += getFormattedContact(contacts[i]) + "$";
			}
		}
		
		/* sending the request to the server. */
		ServerRequest serverRequest = new ServerRequest(this);
		try 
		{
			serverRequest.execute("query_contacts.php", String.valueOf(1), "Contacts", concatenatedContacts).get();
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
		
		String[] registeredContacts = reply.split("\\$");
		for(int i=0; i<registeredContacts.length; i++)
		{
			edit.putString("CONTACTS_"+String.valueOf(count), registeredContacts[i]);
			count++;
		}
		
		edit.putInt("CONTACTS", count);
		edit.commit();
	}
	
	/* This function builds a formatted 10 digit contact number from the contact string provided. */
	String getFormattedContact(String contact)
	{
		/* if the number is less than 10 digits, not valid. */
		if(contact.length()<10)
			return "";
		
		String formattedContact = "";
		for(int i=contact.length()-1, j=0; i>=0 && j<10; i--)
		{
			if(contact.charAt(i)>='0' && contact.charAt(i)<='9')
			{
				formattedContact += contact.charAt(i);
				j++;
			}
		}
		
		if(formattedContact.length()<10)
			return "";
		
		/* The number should begin with a number other than 0. */
		if(formattedContact.charAt(9)=='0')
			return "";
		
		return new StringBuilder(formattedContact).reverse().toString();
	}
}
