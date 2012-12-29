package com.reefangel.evolution;

import java.io.IOException;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TabHost.TabSpec;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;
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
	private boolean mLoading;
	
	SharedPreferences prefs;

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
		mLoading=false;				
		mCommandTarget = (byte) (relayNumber);
		mTopOffBackground = res.getDrawable(R.drawable.relay_off1);
		mTopOnBackground = res.getDrawable(R.drawable.relay_on1);
		mBottomOffBackground = res.getDrawable(R.drawable.relay_off);
		mBottomOnBackground = res.getDrawable(R.drawable.relay_on);
		prefs = mActivity.getSharedPreferences(Globals.PREFS_NAME, 0);
	}

	public void attachToView(ViewGroup targetView) {
		mLabel = (TextView) targetView.getChildAt(0);
		mLabel.setText(prefs.getString("R" + mRelayNumber +"N","Port " + mRelayNumber));
		mOverride = (TextView) targetView.getChildAt(2);
		mOverride.setText("");
		mButton = (Button) targetView.getChildAt(1);
		mButton.setOnClickListener(this);
		 
		OnLongClickListener listener = new OnLongClickListener() {
			public boolean onLongClick(View v) {
//				final CharSequence[] items = {"Off (Override)", "On (Override)", "Auto"};
				View view = LayoutInflater.from(mActivity).inflate(R.layout.relaysettings, null);
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
				
				final Button bcf = (Button) view.findViewById(R.id.ChangeFunction);
				bcf.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						int l=R.layout.functionnone;
						switch ((int)srf.getSelectedItemId())
						{
						case 0:
							break;
						case 1:
							l=R.layout.functiontimedport;
							break;
						case 2: case 3: case 5: case 6:
							l=R.layout.functionnumbers;
							break;
						case 4:
							l=R.layout.functionato;
						default:
							break;
						}
						
						View view = LayoutInflater.from(mActivity).inflate(l, null);
						AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
						final int okid = Resources.getSystem().getIdentifier("ok", "string", "android");
						final int cancelid = Resources.getSystem().getIdentifier("cancel", "string", "android");
						builder.setTitle(R.string.app_name);
						builder.setView(view);
						builder.setNegativeButton(mActivity.getResources().getString(cancelid), null);
						builder.setPositiveButton(mActivity.getResources().getString(okid), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
							}
						});
						TextView fh = (TextView) view.findViewById(R.id.FunctionHeader);
						TextView fuon = (TextView) view.findViewById(R.id.FunctionUnitOn);
						TextView fuoff = (TextView) view.findViewById(R.id.FunctionUnitOff);
						NumberPicker pnon = (NumberPicker) view.findViewById(R.id.FunctionNumberOn);
						NumberPicker pdon = (NumberPicker) view.findViewById(R.id.FunctionDecimalOn);
						NumberPicker pnoff = (NumberPicker) view.findViewById(R.id.FunctionNumberOff);
						NumberPicker pdoff = (NumberPicker) view.findViewById(R.id.FunctionDecimalOff);
						switch ((int)srf.getSelectedItemId())
						{
						case 2:
							fuon.setText("�");
							fuoff.setText("�");
							fh.setText(mActivity.getResources().getString(R.string.HeaterSettingsLabel));
							pnon.setMaxValue(150);
							pnon.setMinValue(0);
							pdon.setMaxValue(9);
							pdon.setMinValue(0);
							pnoff.setMaxValue(150);
							pnoff.setMinValue(0);
							pdoff.setMaxValue(9);
							pdoff.setMinValue(0);
							break;
						case 3:
							fuon.setText("�");
							fuoff.setText("�");
							fh.setText(mActivity.getResources().getString(R.string.ChillerSettingsLabel));
							pnon.setMaxValue(150);
							pnon.setMinValue(0);
							pdon.setMaxValue(9);
							pdon.setMinValue(0);
							pnoff.setMaxValue(150);
							pnoff.setMinValue(0);
							pdoff.setMaxValue(9);
							pdoff.setMinValue(0);
							break;
						case 4:
							NumberPicker p = (NumberPicker) view.findViewById(R.id.ATOTimeout);
							p.setMaxValue(32000);
							p.setMinValue(0);
							break;
						case 5:
							fuon.setText("pH");
							fuoff.setText("pH");
							fh.setText(mActivity.getResources().getString(R.string.pHControlSettingsLabel));
							pnon.setMaxValue(13);
							pnon.setMinValue(1);
							pdon.setMaxValue(99);
							pdon.setMinValue(0);
							pnoff.setMaxValue(13);
							pnoff.setMinValue(1);
							pdoff.setMaxValue(99);
							pdoff.setMinValue(0);
							break;
						case 6:
							fuon.setText("pH");
							fuoff.setText("pH");
							fh.setText(mActivity.getResources().getString(R.string.CO2ControlSettingsLabel));
							pnon.setMaxValue(13);
							pnon.setMinValue(1);
							pdon.setMaxValue(99);
							pdon.setMinValue(0);
							pnoff.setMaxValue(13);
							pnoff.setMinValue(1);
							pdoff.setMaxValue(99);
							pdoff.setMinValue(0);
							break;
						default:
							break;
						}						
						final AlertDialog alert = builder.create();
						alert.show();
						
					}
				});
				
				builder.setTitle(R.string.app_name);
				builder.setView(view);
				builder.setNegativeButton(mActivity.getResources().getString(cancelid), null);
				builder.setPositiveButton(mActivity.getResources().getString(okid), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						SharedPreferences.Editor editor = prefs.edit();
						editor.putInt("RELAY_FUNCTION"+mCommandTarget, srf.getSelectedItemPosition());
						Log.d(TAG,trl.getText().toString());
						Log.d(TAG,mLabel.getText().toString());
						if (!trl.getText().toString().equals(mLabel.getText().toString()))
						{
							editor.putString("R"+mCommandTarget+"N", trl.getText().toString());
							String params[] = new String[3];
							params[0]=prefs.getString("MYREEFANGELID", "");
							params[1]="&tag=R"+mCommandTarget+"N&value=";
							params[2]=trl.getText().toString();
							mLabel.setText("Saving");
							PortalUpdateLabelTask p = new PortalUpdateLabelTask();
							p.execute(params);
						}
						editor.commit();
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
	
	public class PortalUpdateLabelTask extends AsyncTask<String, String, Integer> {

		protected Integer doInBackground(String... params) {
			String values[] = new String[2];
			values[0]=Globals.UpdateLabel(params[0], params[1], params[2]);
			values[1]=params[2];
			publishProgress(values); 
			return null;
		}

		protected void onProgressUpdate(String... values) {
			if (values[0].equals("Label Updated"))
				mLabel.setText(values[1]);
			else
				mLabel.setText(prefs.getString("R" + mRelayNumber +"N","Port " + mRelayNumber));
			Log.d(TAG,"Portal Label Updated");
			Toast.makeText(mActivity, values[0], Toast.LENGTH_SHORT).show();
		}

		protected void onPostExecute(Integer result) {
		}
	}	
}
