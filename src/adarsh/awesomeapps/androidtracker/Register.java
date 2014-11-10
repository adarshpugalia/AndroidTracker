package adarsh.awesomeapps.androidtracker;

import android.content.Intent;
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
	
	public void register(View view)
	{
		EditText editText = (EditText)findViewById(R.id.register_edit_name);
		String name = editText.getText().toString();
		
		editText = (EditText)findViewById(R.id.register_edit_phone);
		String phone = editText.getText().toString();
		
		editText = (EditText)findViewById(R.id.register_edit_password);
		String password = editText.getText().toString();
		
		if(name.isEmpty() || phone.isEmpty() || password.isEmpty())
		{
			Toast.makeText(getApplicationContext(), "Please fill all the fields.", Toast.LENGTH_LONG).show();
			return;
		}
		
		/* TO-DO fill the server send message code here. */
	}
}
