package adarsh.awesomeapps.androidtracker;

import java.util.Vector;

import com.google.android.gms.internal.cu;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

/*
 * This class provides the contact details.
 */
public class ContactDetails
{
	/* content resolver for the contacts contact provider. */
	private ContentResolver resolver;
	
	/* cursor object to query the contacts table. */
	private Cursor cursor;
	
	/* constructor for the objects. */
	public ContactDetails(ContentResolver contentResolver) 
	{
		resolver = contentResolver;
		cursor = null;
	}
	
	/* This function returns the entire contact list from the device. */
	Cursor getAllContacts()
	{
		/* if the cursor is initialized before, closing the cursor. */
		if(cursor!=null && !cursor.isClosed())
			cursor.close();
		
		/* getting the contact list. */
		cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
		return cursor;
	}
	
	/* This function returns the contact numbers as an array of strings. */
	String[] getAllContactNumbers()
	{
		/* getting the entire contact list. */
		getAllContacts();
		
		/* iterating over the list and getting the contact numbers. */
		String[] contacts = new String[cursor.getCount()];
		int index = 0;
		while(cursor.moveToNext())
		{
			contacts[index] = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))+"\n";
			index++;
		}
		
		if(cursor!=null && !cursor.isClosed())
			cursor.close();
		return contacts;
	}
}
