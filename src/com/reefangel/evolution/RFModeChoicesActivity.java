package com.reefangel.evolution;

import com.reefangel.evolution.EvolutionActivity.ByteMsg;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class RFModeChoicesActivity extends Activity {
	
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.rfmodes );

		for (int i=0;i<7;i++)
		{
			final int mode = i;
			int resID = getResources().getIdentifier("RFMode"+i, "id", "com.reefangel.evolution");
			TextView c = (TextView) findViewById(resID);
			c.setOnClickListener( new OnClickListener() {
				public void onClick ( View v ) {
					setResult(mode);
					finish();
				}
			} );
		}
	}
}
