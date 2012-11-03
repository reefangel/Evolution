package com.reefangel.evolution;

import com.reefangel.evolution.EvolutionActivity.ByteMsg;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class SettingsActivity extends Activity {
	
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.settings );
		final SharedPreferences sharedPreferences = getSharedPreferences(Globals.PREFS_NAME, 0);
        
        final EditText ea = (EditText)findViewById(R.id.AlertEmail);
        ea.setText(sharedPreferences.getString("AlertEmail", "email"));
		Button b = (Button)findViewById(R.id.SettingsOK);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
		        SharedPreferences.Editor editor = sharedPreferences.edit();
		        editor.putString("AlertEmail", ea.getText().toString());
		        editor.commit();
				finish();
			}			
		});
		b = (Button)findViewById(R.id.SettingsCancel);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}			
		});
		
	}
}
