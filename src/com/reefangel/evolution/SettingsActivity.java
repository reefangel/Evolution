package com.reefangel.evolution;

import java.util.ArrayList;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class SettingsActivity extends Activity {

	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.settings );
		final SharedPreferences sharedPreferences = getSharedPreferences(Globals.PREFS_NAME, 0);
        
        final EditText ea = (EditText)findViewById(R.id.AlertEmail);
        final Spinner el = (Spinner)findViewById(R.id.AlertFrequency);

        String[] alert_frequency = getResources().getStringArray(R.array.alert_frequency); 
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, R.layout.alert_frequency_item, R.id.label, alert_frequency);
        el.setAdapter(mAdapter);

        ea.setText(sharedPreferences.getString("AlertEmail", "email"));
        el.setSelection((int) sharedPreferences.getLong("AlertFrequency", 2));
		Button b = (Button)findViewById(R.id.SettingsOK);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
		        SharedPreferences.Editor editor = sharedPreferences.edit();
		        editor.putString("AlertEmail", ea.getText().toString());
		        editor.putLong("AlertFrequency", el.getSelectedItemId());
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
