package com.reefangel.evolution;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.reefangel.evolution.EvolutionActivity.UpdateData;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TableLayout;
import android.widget.TextView;

public class InputController extends AccessoryController  {
	private static final String TAG = "EvolutionActivity";
	
	private TextView mTemperature1;
	private TextView mTemperature2;
	private TextView mTemperature3;
	private TextView mpH;
	private TextView mSalinity;
	private TextView mOrp;
	private TextView mpHExp;
	private TextView mWaterLevel;
	private JoystickView mJoystickView;
	private ProgressView mDaylightView;
	private ProgressView mActinicView;
	private int Relay_R[];
	private int Relay_RON[];
	private int Relay_ROFF[];
	
	SharedPreferences prefs;
		
	ArrayList<SwitchDisplayer> mSwitchDisplayers;
	ArrayList<RelayButtonController> mRelayButtonControllers;
	private final DecimalFormat mTemperatureFormatter = new DecimalFormat("##.#" + (char)0x00B0);
	private final DecimalFormat mpHFormatter = new DecimalFormat("#.##");
	private final DecimalFormat mSalinityFormatter = new DecimalFormat("##.#");
	private final DecimalFormat mORPFormatter = new DecimalFormat("###");
	private final DecimalFormat mWLFormatter = new DecimalFormat("###" + "%");
	
	InputController(EvolutionActivity hostActivity) {
		super(hostActivity);
		mTemperature1 = (TextView) findViewById(R.id.temp1Value);
		mTemperature2 = (TextView) findViewById(R.id.temp2Value);
		mTemperature3 = (TextView) findViewById(R.id.temp3Value);
		mpH = (TextView) findViewById(R.id.pHValue);
		mSalinity = (TextView) findViewById(R.id.SalinityValue);
		mOrp = (TextView) findViewById(R.id.ORPValue);
		mpHExp = (TextView) findViewById(R.id.pHExpValue);
		mJoystickView = (JoystickView) findViewById(R.id.joystickView);
		mDaylightView = (ProgressView) findViewById(R.id.daylightdimming);
		mDaylightView.setLabel("Daylight");
		mActinicView = (ProgressView) findViewById(R.id.actinicdimming);
		mActinicView.setBarColor(1);
		mActinicView.setLabel("Actinic");
		mWaterLevel = (TextView) findViewById(R.id.WLValue);
		
		Relay_R = new int[9];
		Relay_RON = new int[9];
		Relay_ROFF = new int[9];

	    TabHost tabs = (TabHost) this.findViewById(R.id.tabhost);
		tabs.setup();

		TabSpec tspec1 = tabs.newTabSpec("Main Box");
		tspec1.setIndicator("Main Box");
		tspec1.setContent(R.id.mainbox);
		tabs.addTab(tspec1);
		tabs.setCurrentTabByTag("Main Box");
		
		TabHost tabsports = (TabHost) this.findViewById(R.id.tabhostports);
		tabsports.setup();

		TabSpec tspec1ports = tabsports.newTabSpec("Std Ports");
		tspec1ports.setIndicator("Std Ports");
		tspec1ports.setContent(R.id.standardports);
		tabsports.addTab(tspec1ports);
		tabsports.setCurrentTabByTag("Std Ports");

		prefs = mHostActivity.getSharedPreferences(Globals.PREFS_NAME, 0);
        Log.d(TAG,"Reef Angel ID: " + prefs.getString("MYREEFANGELID", ""));
        if (prefs.getString("MYREEFANGELID", "")!="")
        {
		    DownloadXMLLabels task = new DownloadXMLLabels();
		    task.execute();
        }
        UpdateLabels();
        
	}

	protected void onAccesssoryAttached() {

		mSwitchDisplayers = new ArrayList<SwitchDisplayer>();
		for (int i = 0; i < 2; ++i) {
			SwitchDisplayer sd = new SwitchDisplayer(i);
			mSwitchDisplayers.add(sd);
		}
		
		mRelayButtonControllers = new ArrayList<RelayButtonController>();
		int bt=1;
		for (int i=1;i<9;i++)
		{
			int resID = getResources().getIdentifier("Relay"+i, "id", "com.reefangel.evolution");
			setupRelayButtonController(i, bt, resID);
			bt= (bt==1)?0:1;
			resID = getResources().getIdentifier("expbox"+i, "id", "com.reefangel.evolution");
			ViewGroup v = (ViewGroup) this.findViewById(resID);
			v.setVisibility(4);
		}	
		
		mTemperature1.setText("0.0" + (char)0x00B0);
		mTemperature2.setText("0.0" + (char)0x00B0);
		mTemperature3.setText("0.0" + (char)0x00B0);
		mpH.setText("0.00");
//		mSalinity.setText("0.0");
//		mOrp.setText("0");
//		mpHExp.setText("0.00");
//		mWaterLevel.setText("0%");
		
		mDaylightView.setPercentage(0);
		mActinicView.setPercentage(0);
		
		for (int a=0;a<9;a++)
		{
			Relay_R[a]=0;
			Relay_RON[a] = 0;
			Relay_ROFF[a] = 255;
		}
	}

	public void setParam(byte param, int t) {
		switch (param){
		case Globals.T1_PROBE:
			mTemperature1.setText(mTemperatureFormatter.format((double)t/10));
			break;
		case Globals.T2_PROBE:
			mTemperature2.setText(mTemperatureFormatter.format((double)t/10));
			break;
		case Globals.T3_PROBE:
			mTemperature3.setText(mTemperatureFormatter.format((double)t/10));
			break;
		case Globals.PH:
			mpH.setText(mpHFormatter.format((double)t/100));
			break;
		case Globals.SALINITY:
			mSalinity.setText(mSalinityFormatter.format((double)t/10));
			break;
		case Globals.ORP:
			mOrp.setText(mORPFormatter.format((double)t));
			break;
		case Globals.PHEXP:
			mpHExp.setText(mpHFormatter.format((double)t/100));
			break;
		case Globals.DIMMING_WATERLEVEL:
			mWaterLevel.setText(mWLFormatter.format((double)t));
			break;			
		case Globals.EXPANSIONMODULES:
			if (mHostActivity.em!=t)
			{
				mHostActivity.em=t;
				Log.d("EvolutionActivity", "Expansion Modules: " + t);
				
				if (BigInteger.valueOf(t).testBit(0)) {
					
					TabHost tabsports = (TabHost) this.findViewById(R.id.tabhostports);
					tabsports.setup();
					
					TabSpec tspec1ports = tabsports.newTabSpec("Dim Exp Ports");
					tspec1ports.setIndicator("Dim Exp Ports");
					tspec1ports.setContent(R.id.dimmingexpports);
					tabsports.addTab(tspec1ports);
					tabsports.setCurrentTabByTag("Std Ports");
					
					ViewGroup v = (ViewGroup) this.findViewById(R.id.dimmingexpports);
					LinearLayout lDimmingExp = new LinearLayout(mHostActivity);
					lDimmingExp = (LinearLayout) View.inflate(mHostActivity, R.layout.dimmingexpcontainer, null);
					LinearLayout.LayoutParams lr = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT,
							(float) 1.0
							);	
					v.addView(lDimmingExp,lr);
					for (int i=0;i<6;i++)
					{
						int resID = getResources().getIdentifier("channel"+i+"dimming", "id", "com.reefangel.evolution");
					    ProgressView c = (ProgressView)findViewById(resID);
					    c.setBarColor(3);
					    c.setLabel("Channel "+ i);
					    c.setMode(1);
					    c.setPercentage(0);
					}
				}
				
				if (BigInteger.valueOf(t).testBit(1)) {
					
					TabHost tabsports = (TabHost) this.findViewById(R.id.tabhostports);
					tabsports.setup();
					
					TabSpec tspec1ports = tabsports.newTabSpec("RF Vortech");
					tspec1ports.setIndicator("RF Vortech");
					tspec1ports.setContent(R.id.rfvortechexpports);
					tabsports.addTab(tspec1ports);
					
					ViewGroup v = (ViewGroup) this.findViewById(R.id.rfvortechexpports);
					LinearLayout lRFVortech = new LinearLayout(mHostActivity);
					lRFVortech = (LinearLayout) View.inflate(mHostActivity, R.layout.rfvortechcontainer, null);
					LinearLayout.LayoutParams lr = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT,
							(float) 1.0
							);	
					v.addView(lRFVortech,lr);

					tspec1ports = tabsports.newTabSpec("RF Radion");
					tspec1ports.setIndicator("RF Radion");
					tspec1ports.setContent(R.id.rfradionexpports);
					tabsports.addTab(tspec1ports);
					tabsports.setCurrentTabByTag("Std Ports");
					
					v = (ViewGroup) this.findViewById(R.id.rfradionexpports);
					LinearLayout lRadionExp = new LinearLayout(mHostActivity);
					lRadionExp = (LinearLayout) View.inflate(mHostActivity, R.layout.rfradioncontainer, null);
					v.addView(lRadionExp,lr);
					int cColor[]={0,1,3,4,2,5};
					String cLabel[]={"White","Royal Blue","Red","Green","Blue","Intensity"};
					
					for (int i=0;i<6;i++)
					{
						int resID = getResources().getIdentifier("radion"+i+"dimming", "id", "com.reefangel.evolution");
					    ProgressView c = (ProgressView)findViewById(resID);
					    c.setBarColor(cColor[i]);
					    c.setLabel(cLabel[i]);
					    c.setMode(1);
					    c.setPercentage(0);
					}
				}
				if (BigInteger.valueOf(t).testBit(2)) {
					
					TabHost tabsports = (TabHost) this.findViewById(R.id.tabhostports);
					tabsports.setup();
					
					TabSpec tspec1ports = tabsports.newTabSpec("AI Ports");
					tspec1ports.setIndicator("AI Ports");
					tspec1ports.setContent(R.id.aiports);
					tabsports.addTab(tspec1ports);
					tabsports.setCurrentTabByTag("Std Ports");
					
					ViewGroup v = (ViewGroup) this.findViewById(R.id.aiports);
					LinearLayout lDimmingAI = new LinearLayout(mHostActivity);
					lDimmingAI = (LinearLayout) View.inflate(mHostActivity, R.layout.aidimmingcontainer, null);
					LinearLayout.LayoutParams lr = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT,
							(float) 1.0
							);	
					v.addView(lDimmingAI,lr);

				    ProgressView aiw = (ProgressView)findViewById(R.id.aiwhitedimming);
				    aiw.setLabel("White");
				    aiw.setPercentage(0);
				    ProgressView aib = (ProgressView)findViewById(R.id.aibluedimming);
				    aib.setLabel("Blue");
				    aib.setBarColor(2);
				    aib.setPercentage(0);
				    ProgressView airb = (ProgressView)findViewById(R.id.airoyalbluedimming);
				    airb.setLabel("Royal Blue");
				    airb.setBarColor(1);
				    airb.setPercentage(0);
				}

				if (BigInteger.valueOf(t).testBit(3)) {
					
					TextView sl=(TextView)findViewById(R.id.SalinityLabel);
					sl.setText(prefs.getString("SALN", "Salinity"));
					LinearLayout sc=(LinearLayout) findViewById(R.id.SalinityContainer);
					sc.setBackground(mHostActivity.getResources().getDrawable(R.drawable.sal_bk));
				}
				if (BigInteger.valueOf(t).testBit(4)) {
					
					TextView sl=(TextView)findViewById(R.id.ORPLabel);
					sl.setText(prefs.getString("ORPN", "ORP"));
					LinearLayout sc=(LinearLayout) findViewById(R.id.ORPContainer);
					sc.setBackground(mHostActivity.getResources().getDrawable(R.drawable.orp_bk));
				}				
				if (BigInteger.valueOf(t).testBit(5)) {
					
					TabHost tabsports = (TabHost) this.findViewById(R.id.tabhostports);
					tabsports.setup();
					
					TabSpec tspec1ports = tabsports.newTabSpec("I/O Exp Ports");
					tspec1ports.setIndicator("I/O Exp Ports");
					tspec1ports.setContent(R.id.ioexpports);
					tabsports.addTab(tspec1ports);
					tabsports.setCurrentTabByTag("Std Ports");
					
					ViewGroup v = (ViewGroup) this.findViewById(R.id.ioexpports);
					LinearLayout lIOExp = new LinearLayout(mHostActivity);
					lIOExp = (LinearLayout) View.inflate(mHostActivity, R.layout.ioexpcontainer, null);
					LinearLayout.LayoutParams lr = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT,
							(float) 1.0
							);	
					v.addView(lIOExp,lr);
				}			
				if (BigInteger.valueOf(t).testBit(6)) {
					
					TextView sl=(TextView)findViewById(R.id.pHExpLabel);
					sl.setText(prefs.getString("PHEN", "pH Exp"));
					LinearLayout sc=(LinearLayout) findViewById(R.id.pHExpContainer);
					sc.setBackground(mHostActivity.getResources().getDrawable(R.drawable.phexp_bk));
				}
				if (BigInteger.valueOf(t).testBit(7)) {
					
					TextView sl=(TextView)findViewById(R.id.WLLabel);
					sl.setText("WL");
					LinearLayout sc=(LinearLayout) findViewById(R.id.WaterLevelContainer);
					sc.setBackground(mHostActivity.getResources().getDrawable(R.drawable.waterlevel_bk));
				}				
			    
//				List<String> l = new ArrayList<String>();
//				
//				if ((t&(1<<0))!=0) l.add("Dimming");
//				if ((t&(1<<1))!=0) l.add("RF");
//				if ((t&(1<<2))!=0) l.add("Aqua Illumination");
//				if ((t&(1<<3))!=0) l.add("Salinity");
//				if ((t&(1<<4))!=0) l.add("ORP");
//				if ((t&(1<<5))!=0) l.add("I/O");
//				if ((t&(1<<6))!=0) l.add("pH Expansion");
//				if ((t&(1<<7))!=0) l.add("Water Level");
				

//			    final ActionBar actionBar = mHostActivity.getActionBar();
//
//			    // Specify that a dropdown list should be displayed in the action bar.
//			    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//
//			    actionBar.setListNavigationCallbacks(
//			            // Specify a SpinnerAdapter to populate the dropdown list.
//			            new ArrayAdapter<Object>(
//			                    actionBar.getThemedContext(),
//			                    android.R.layout.simple_list_item_1,
//			                    android.R.id.text1,
//			                    l.toArray()),
//
//			            // Provide a listener to be called when an item is selected.
//			            new ActionBar.OnNavigationListener() {
//			                public boolean onNavigationItemSelected(
//			                        int position, long id) {
//			                    // Take action here, e.g. switching to the
//			                    // corresponding fragment.
//			                    return true;
//			                }
//			            });				
			}
			break;
		case Globals.RELAYMODULES:
			if (mHostActivity.rem!=t)
			{
				Log.d("EvolutionActivity", "Relay Modules: " + t);
				mHostActivity.rem=t;
				TabHost tabs = (TabHost) this.findViewById(R.id.tabhost);
				for(int a=0;a<9;a++)
					if (BigInteger.valueOf(t).testBit(a)) {
						TabSpec tspec2 = tabs.newTabSpec("Exp Box " + (a+1));
						tspec2.setIndicator("Exp Box " + (a+1));
						int resID = getResources().getIdentifier("expbox"+(a+1), "id", "com.reefangel.evolution");
						tspec2.setContent(resID);
						tabs.addTab(tspec2);
						tabs.setCurrentTabByTag("Exp Box " + (a+1));

						ViewGroup v = (ViewGroup) this.findViewById(resID);
						v.setVisibility(0);
						TableLayout tLayout = new TableLayout(mHostActivity);
						tLayout = (TableLayout) View.inflate(mHostActivity, R.layout.relaybox, null);
//						tLayout.setBackgroundDrawable(mHostActivity.getResources().getDrawable(R.drawable.relay_box_bk));
//						tLayout.setMinimumHeight((int) (200 * scale + 0.5f));
//						tLayout.setPadding(padding_in_px, padding_in_px, padding_in_px, padding_in_px);
//						tLayout.setGravity(0x11);
						TableLayout.LayoutParams tp = new TableLayout.LayoutParams(
								TableLayout.LayoutParams.WRAP_CONTENT,
								TableLayout.LayoutParams.WRAP_CONTENT
								);
						v.addView(tLayout);
						LinearLayout tRow = new LinearLayout(mHostActivity);
						LinearLayout.LayoutParams tr = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT,
								LinearLayout.LayoutParams.MATCH_PARENT
								);
						tLayout.addView(tRow,tr);
						for (int i=1;i<9;i+=2)
						{
							LinearLayout lRelay = new LinearLayout(mHostActivity);
							lRelay = (LinearLayout) View.inflate(mHostActivity, R.layout.relaybuttontopcontainer, null);
							LinearLayout.LayoutParams lr = new LinearLayout.LayoutParams(
									LinearLayout.LayoutParams.MATCH_PARENT,
									LinearLayout.LayoutParams.WRAP_CONTENT,
									(float) 1.0
									);	
							lRelay.setId(((a+1)*10)+i);
							tRow.addView(lRelay,lr);
							setupRelayButtonController(((a+1)*10)+i, 0, ((a+1)*10)+i);
						}
						tRow = new LinearLayout(mHostActivity);
						tLayout.addView(tRow,tr);
						for (int i=2;i<9;i+=2)
						{
							LinearLayout lRelay = new LinearLayout(mHostActivity);
							lRelay = (LinearLayout) View.inflate(mHostActivity, R.layout.relaybuttonbottomcontainer, null);
							LinearLayout.LayoutParams lr = new LinearLayout.LayoutParams(
									LinearLayout.LayoutParams.MATCH_PARENT,
									LinearLayout.LayoutParams.WRAP_CONTENT,
									(float) 1.0
									);	
							lRelay.setId(((a+1)*10)+i);
							tRow.addView(lRelay,lr);
							setupRelayButtonController(((a+1)*10)+i, 1, ((a+1)*10)+i);
						}
						
					}
				tabs.setCurrentTabByTag("Main Box");				
			}
			
			break;
			
			
		}
	}
	
	public void setDimming(byte channel, int value) {
		switch (channel){
		case Globals.DIMMING_DAYLIGHT:
			mDaylightView.setPercentage(value);
			break;
		case Globals.DIMMING_ACTINIC:
			mActinicView.setPercentage(value);
			break;
		}
	}

	public void setRelay(byte attr, int value) {
		int box=attr/3;
		
		if (attr%3==0)
			Relay_R[box]=value;
		if (attr%3==1)
			Relay_RON[box]=value;
		if (attr%3==2)
			Relay_ROFF[box]=value;
		for (int a=0;a<8;a++)
		{
			RelayButtonController r = mRelayButtonControllers.get(a);
			r.SetState(Relay_R[box], Relay_RON[box], Relay_ROFF[box]);
		}
	}

	public void switchStateChanged(int switchIndex, boolean switchState) {
		if (switchIndex >= 0 && switchIndex < mSwitchDisplayers.size()) {
			SwitchDisplayer sd = mSwitchDisplayers.get(switchIndex);
			sd.onSwitchStateChange(switchState);
		}
	}

	public void joystickButtonSwitchStateChanged(boolean buttonState) {
		mJoystickView.setPressed(buttonState);
	}

	public void joystickMoved(int x, int y) {
		mJoystickView.setPosition(x, y);
	}

	public void onSwitchStateChange(int switchIndex, Boolean switchState) {
		switchStateChanged(switchIndex, switchState);
	}

	public void onButton(Boolean buttonState) {
		joystickButtonSwitchStateChanged(buttonState);
	}

	public void onStickMoved(int x, int y) {
		joystickMoved(x, y);
	}

	class SwitchDisplayer {
		private final ImageView mTargetView;
		private final Drawable mOnImage;
		private final Drawable mOffImage;

		SwitchDisplayer(int switchIndex) {
			int viewId, onImageId, offImageId;
			switch (switchIndex) {
			default:
				viewId = R.id.LowATO;
				onImageId = R.drawable.ato_on;
				offImageId = R.drawable.ato_off;
				break;
			case 1:
				viewId = R.id.HighATO;
				onImageId = R.drawable.ato_on;
				offImageId = R.drawable.ato_off;
				break;
			}
			mTargetView = (ImageView) findViewById(viewId);
			mOffImage = mHostActivity.getResources().getDrawable(offImageId);
			mOnImage = mHostActivity.getResources().getDrawable(onImageId);
		}

		public void onSwitchStateChange(Boolean switchState) {
			Drawable currentImage;
			if (!switchState) {
				currentImage = mOffImage;
			} else {
				currentImage = mOnImage;
			}
			mTargetView.setImageDrawable(currentImage);
		}

	}

	private void setupRelayButtonController(int index, int pos, int viewId) {
//		Log.d(TAG,"Relay Button index: " + index + ", pos: " + pos + ", viewID: " + viewId);
		RelayButtonController r = new RelayButtonController(mHostActivity, index, pos, getResources());
		r.attachToView((ViewGroup) findViewById(viewId));		
		mRelayButtonControllers.add(r);
	}	


	private class DownloadXMLLabels extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			XMLfunctions.GetLabels(mHostActivity);
			return null;
		}
	}
	
	private void UpdateLabels()
	{
		for (int a=1; a<4; a++)
		{
			TextView t=(TextView) findViewById(getResources().getIdentifier("t" + a + "n", "id", "com.reefangel.evolution"));
			t.setText(prefs.getString("T" + a + "N", "Temp "+a));
		}		
		TextView t=(TextView) findViewById(R.id.phn);
		t.setText(prefs.getString("PHN", "pH"));
	}
}
