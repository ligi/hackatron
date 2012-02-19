package org.c_base.hackatron.client;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

public class HackatronClientLogin extends Activity {
	
	private EditText userName;
	private EditText serverAddress;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		// get views
		userName = (EditText) findViewById(R.id.userName);
		serverAddress = (EditText) findViewById(R.id.serverAddress);
		
		if (getIntent().getData()!=null)
			serverAddress.setText(getIntent().getData().getEncodedQuery().replace("ip=", ""));
		else {
			SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
			serverAddress.setText(sp.getString("IP", ""));
			userName.setText(sp.getString("UNAME", ""));
		}
	}
	
	public void onClickStart(View v) {
		final Intent intent = new Intent(getApplicationContext(), HackatronClientMain.class);
		intent.putExtra(HackatronClientMain.INTENT_EXTRA_USERNAME, userName.getText().toString());
		intent.putExtra(HackatronClientMain.INTENT_EXTRA_SERVERADDRESS, serverAddress.getText().toString());
		
		Editor e=PreferenceManager.getDefaultSharedPreferences(this).edit();
		e.putString("IP", serverAddress.getText().toString());
		e.putString("UNAME", userName.getText().toString());
		e.commit();
		startActivity(intent);
	}
}
