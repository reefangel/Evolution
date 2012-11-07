package com.reefangel.evolution;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TabHost.TabSpec;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;
//import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;

public class RelayButtonController implements android.view.View.OnClickListener {
	private static final String TAG = "EvolutionActivity";
	
	private final int mRelayNumber;
	private final byte mCommandTarget;
	private EvolutionActivity mActivity;
	private TextView mLabel;
	private TextView mOverride;
	private Button mButton;
	private Drawable mTopOffBackground;
	private Drawable mTopOnBackground;
	private Drawable mBottomOffBackground;
	private Drawable mBottomOnBackground;
	private int mPos;
	private int mState;
	private int mR;
	private int mRON;
	private int mROFF;
	private int mFunction;
	

	public RelayButtonController(EvolutionActivity activity, int relayNumber, int pos,
			Resources res) {
		mActivity = activity;
		mRelayNumber = relayNumber;
		mPos=pos;
		mState=2;
		mR=0;
		mRON=0;
		mROFF=1;
		mFunction=-1;
		mCommandTarget = (byte) (relayNumber);
		mTopOffBackground = res.getDrawable(R.drawable.relay_off1);
		mTopOnBackground = res.getDrawable(R.drawable.relay_on1);
		mBottomOffBackground = res.getDrawable(R.drawable.relay_off);
		mBottomOnBackground = res.getDrawable(R.drawable.relay_on);
	}

	public void attachToView(ViewGroup targetView) {
		final SharedPreferences prefs = mActivity.getSharedPreferences(Globals.PREFS_NAME, 0);
		mLabel = (TextView) targetView.getChildAt(0);
		mLabel.setText(prefs.getString("Relay " + mRelayNumber +"N","Relay " + mRelayNumber));
		mOverride = (TextView) targetView.getChildAt(2);
		mOverride.setText("");
		mButton = (Button) targetView.getChildAt(1);
		mButton.setOnClickListener(this);
		 
		OnLongClickListener listener = new OnLongClickListener() {
			public boolean onLongClick(View v) {
//				final CharSequence[] items = {"Off (Override)", "On (Override)", "Auto"};
				View view = LayoutInflater.from(mActivity).inflate(R.layout.relaysettings, (ViewGroup) mActivity.findViewById(R.id.RelayContainer_ref));
				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
				final int okid = Resources.getSystem().getIdentifier("ok", "string", "android");
				final int cancelid = Resources.getSystem().getIdentifier("cancel", "string", "android");
		        String[] relay_modes = mActivity.getResources().getStringArray(R.array.relay_mode);
				ArrayAdapter<String> ra = new ArrayAdapter<String>(mActivity, R.layout.relay_mode_item, R.id.label, relay_modes); 
		        String[] relay_functions = mActivity.getResources().getStringArray(R.array.relay_function);
				ArrayAdapter<String> fa = new ArrayAdapter<String>(mActivity, R.layout.relay_function_item, R.id.label, relay_functions); 
				final EditText trl = (EditText) view.findViewById(R.id.RelayCurrentLabel);
				trl.setText(mLabel.getText());
				
				final Spinner srf = (Spinner) view.findViewById(R.id.RelayCurrentFunction);
				srf.setAdapter(fa);
				srf.setSelection(prefs.getInt("RELAY_FUNCTION"+mCommandTarget, 0));
				builder.setTitle(R.string.app_name);
				builder.setView(view);
				builder.setNegativeButton(mActivity.getResources().getString(cancelid), null);
				builder.setPositiveButton(mActivity.getResources().getString(okid), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						SharedPreferences.Editor editor = prefs.edit();
						editor.putInt("RELAY_FUNCTION"+mCommandTarget, srf.getSelectedItemPosition());
						editor.commit();
						Log.d(TAG,trl.getText().toString());
						Log.d(TAG,mLabel.getText().toString());
						if (!trl.getText().toString().equals(mLabel.getText().toString()))
						{
							String url=Globals.PORTAL_UPDATE_LABELS+"?id="+prefs.getString("MYREEFANGELID", "");
							try {
								url+="&R"+mCommandTarget+"N="+URLEncoder.encode(trl.getText().toString(), "utf-8");
							} catch (UnsupportedEncodingException e1) {
								e1.printStackTrace();
							}
							String line = null;
							try {
								DefaultHttpClient httpClient = new DefaultHttpClient();
								HttpPost httpPost = new HttpPost(url);
								Log.d(TAG,"Update Label URL: "+url);
								HttpResponse httpResponse = httpClient.execute(httpPost);
								HttpEntity httpEntity = httpResponse.getEntity();
								line = EntityUtils.toString(httpEntity);
								
							} catch (UnsupportedEncodingException e) {
								line = "Can't connect to server";
							} catch (MalformedURLException e) {
								line = "Can't connect to server";
							} catch (IOException e) {
								line = "Can't connect to server";
							}		
							Toast.makeText(mActivity, line, Toast.LENGTH_SHORT).show();
							Log.d(TAG,line);							
						}
					}
				});
				final AlertDialog alert = builder.create();
				ListView lrm = (ListView) view.findViewById(R.id.RelayMode);
				lrm.setAdapter(ra);
				lrm.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					    mState=position;
					    SendCommand((byte) mState);
					    updateView();
					    alert.dismiss();
					}
				});
				
				TabHost tabsports = (TabHost) view.findViewById(R.id.tabhostrelaycontroller);
				tabsports.setup();

				TabSpec tspecports = tabsports.newTabSpec("Override");
				tspecports.setIndicator("Override");
				tspecports.setContent(R.id.RelayOverride);
				tabsports.addTab(tspecports);
				tabsports.setCurrentTabByTag("Override");

				tspecports = tabsports.newTabSpec("Settings");
				tspecports.setIndicator("Settings");
				tspecports.setContent(R.id.RelaySettings);
				tabsports.addTab(tspecports);
				tabsports.setCurrentTabByTag("Settings");

				tabsports.setCurrentTabByTag("Override");
				
				alert.show();
//		        String[] relay_functions = mActivity.getResources().getStringArray(R.array.relay_function); 
//				ArrayAdapter<String> sa = new ArrayAdapter<String>(mActivity, R.layout.relay_function_item, R.id.label, relay_functions); 
//				Spinner srf = (Spinner) view.findViewById(R.id.RelayFunction);
//				srf.setAdapter(sa);
				return true;
			}
		};
		mButton.setOnLongClickListener(listener);
		 
		}

	public void onClick(View v) {
		switch (mState)
		{
		case 0:
			mState=1;
			break;
		case 1:
			mState=0;
			break;
		case 2:
			if (mR==0)
				mState=1;
			else
				mState=0;
			break;
		}
		updateView();
		SendCommand((byte)mState);
	}

	
	public void SendCommand (byte state)
	{
		Log.d(TAG,"Relay " + mCommandTarget + " Command Sent: " + state);
		mActivity.sendCommand(Globals.RELAY_COMMAND, mCommandTarget, state);
		try {
			mActivity.server.send(new byte[] {Globals.RELAY_COMMAND, mCommandTarget, state});
		} catch (IOException e) {
			Log.e(TAG,e.getMessage());
			e.printStackTrace();
		}		
	}
	
	public int GetState() {
		return mState;
	}
	
	public void SetState(int R, int RON, int ROFF) {
		mState=2;
		mR= (((R&(1<<(mRelayNumber-1)))!=0) ?  1:0);
		mRON=(((RON&(1<<(mRelayNumber-1)))!=0) ?  1:0);
		mROFF=(((ROFF&(1<<(mRelayNumber-1)))!=0) ? 1:0);
		if (mRON==1) mState=1;
		if (mROFF==0) mState=0;
		updateView();
	}
	
	public void updateView(){
		switch (mState)
		{
		case 0:
			mOverride.setText("Overridden");
			if (mPos==0)
				mButton.setBackgroundDrawable(mTopOffBackground);
			else
				mButton.setBackgroundDrawable(mBottomOffBackground);
			break;
		case 1:
			mOverride.setText("Overridden");
			if (mPos==0)
				mButton.setBackgroundDrawable(mTopOnBackground);
			else
				mButton.setBackgroundDrawable(mBottomOnBackground);
			break;
		case 2:
			mOverride.setText("");
			if (mR==0)
			{
				if (mPos==0)
					mButton.setBackgroundDrawable(mTopOffBackground);
				else
					mButton.setBackgroundDrawable(mBottomOffBackground);			
			}
			else
			{
				if (mPos==0)
					mButton.setBackgroundDrawable(mTopOnBackground);
				else
					mButton.setBackgroundDrawable(mBottomOnBackground);				
			}
			break;
		}
	}
	
	public void SetLabel(String label)
	{
		mLabel.setText(label);
	}
}
