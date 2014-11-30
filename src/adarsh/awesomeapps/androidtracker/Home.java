package adarsh.awesomeapps.androidtracker;

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends ActionBarActivity {

	public final static String EXTRA_MESSAGE = "adarsh.awesomeapps.androidtracker.MESSAGE";
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
		ContactDetails contactDetails = new ContactDetails(getContentResolver());
		
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		
		SharedPreferences prefs = getSharedPreferences("CONTACTS_preferences", Context.MODE_PRIVATE);
		int contacts = prefs.getInt("CONTACTS", 0);
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int height = metrics.heightPixels;
		
		for(int i=0; i<contacts; i++)
		{
			final String contactNum = prefs.getString("CONTACTS_"+String.valueOf(i), "");
			
			TextView textView = new TextView(this);
			textView.setText(contactDetails.getContactName(contactNum));
			textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int)height/9));
			textView.setTextSize(10 * getResources().getDisplayMetrics().density);
			textView.setGravity(Gravity.CENTER_VERTICAL);
			textView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(activity, User.class);
					intent.putExtra(EXTRA_MESSAGE, contactNum);
					startActivity(intent);
				}
			});
			linearLayout.addView(textView);
			
			TextView blank_view = new TextView(this);
			blank_view.setText("");
			blank_view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
			blank_view.setBackgroundColor(Color.GRAY);
			linearLayout.addView(blank_view);
		}
		
		//scrollView.addView(scrollView);
		this.setContentView(linearLayout);
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
		Vector<String> registeredContactsUnique = new Vector<String>();
		
		String[] registeredContacts = reply.split("\\$");
		for(int i=0; i<registeredContacts.length; i++)
		{
			if(!registeredContactsUnique.contains(registeredContacts[i]))
			{
				edit.putString("CONTACTS_"+String.valueOf(count), registeredContacts[i]);
				count++;
			}
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
