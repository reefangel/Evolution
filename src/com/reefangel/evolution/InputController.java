package com.reefangel.evolution;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.reefangel.evolution.EvolutionActivity.UpdateData;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TableLayout;
import android.widget.TextView;

public class InputController extends AccessoryController  {
	private static final String TAG = "EvolutionActivity";
	
	private String RfMode[] = {"Constant","Lagoon","Reef Crest","Short Pulse","Long Pulse","Nutrient Transport","Tidal Swell","Feeding","Feeding","Night","Storm","Custom","Custom","Custom","Custom"};
	private ParamsView mTemperature1;
	private ParamsView mTemperature2;
	private ParamsView mTemperature3;
	private ParamsView mpH;
	private ParamsView mSalinity;
	private ParamsView mOrp;
	private ParamsView mpHExp;
	private ParamsView mWaterLevel;
	private ProgressView mDaylightView;
	private ProgressView mActinicView;
	private int Relay_R[];
	private int Relay_RON[];
	private int Relay_ROFF[];
	
	String rf_unit=" s";
	
	SharedPreferences prefs;
		
	ArrayList<SwitchDisplayer> mSwitchDisplayers;
	ArrayList<RelayButtonController> mRelayButtonControllers;
	private final DecimalFormat mTemperatureFormatter = new DecimalFormat("##.#" + (char)0x00B0);
	private final DecimalFormat mpHFormatter = new DecimalFormat("#.##");
	private final DecimalFormat mSalinityFormatter = new DecimalFormat("##.#");
	private final DecimalFormat mORPFormatter = new DecimalFormat("###");
	
	InputController(EvolutionActivity hostActivity) {
		super(hostActivity);
		mTemperature1 = (ParamsView) findViewById(R.id.T1);
		mTemperature1.setParam("0.0");
		mTemperature2 = (ParamsView) findViewById(R.id.T2);
		mTemperature2.setParam("0.0");
		mTemperature3 = (ParamsView) findViewById(R.id.T3);
		mTemperature3.setParam("0.0");
		mpH = (ParamsView) findViewById(R.id.PH);
		mpH.setParam("0.0");
		mSalinity = (ParamsView) findViewById(R.id.SAL);
		mOrp = (ParamsView) findViewById(R.id.ORP);
		mpHExp = (ParamsView) findViewById(R.id.PHE);
		mWaterLevel = (ParamsView) findViewById(R.id.WL);
		mDaylightView = (ProgressView) findViewById(R.id.daylightdimming);
		mDaylightView.setLabel("Daylight");
		mActinicView = (ProgressView) findViewById(R.id.actinicdimming);
		mActinicView.setBarColor(1);
		mActinicView.setLabel("Actinic");
		
		mRelayButtonControllers = new ArrayList<RelayButtonController>();
		mSwitchDisplayers = new ArrayList<SwitchDisplayer>();

		Relay_R = new int[8];
		Relay_RON = new int[8];
		Relay_ROFF = new int[8];

	    TabHost tabs = (TabHost) this.findViewById(R.id.tabhost);
		tabs.setup();
//
//		TabSpec tspec1 = tabs.newTabSpec("Main Box");
//		tspec1.setIndicator("Main Box");
//		tspec1.setContent(R.id.mainbox);
//		tabs.addTab(tspec1);
//		tabs.setCurrentTabByTag("Main Box");
		
		TabHost tabsports = (TabHost) this.findViewById(R.id.tabhostports);
		tabsports.setup();

		TabSpec tspec1ports = tabsports.newTabSpec("Std Ports");
		tspec1ports.setIndicator("Std Ports");
		tspec1ports.setContent(R.id.standardports);
		tabsports.addTab(tspec1ports);
		tabsports.setCurrentTabByTag("Std Ports");

		prefs = mHostActivity.getSharedPreferences(Globals.PREFS_NAME, 0);
//        Log.d(TAG,"Reef Angel ID: " + prefs.getString("MYREEFANGELID", ""));
//        if (prefs.getString("MYREEFANGELID", "")!="")
//        {
//		    DownloadXMLLabels task = new DownloadXMLLabels();
//		    task.execute();
//        }
//        else
//        {
    		try {
    			mHostActivity.server.send(new byte[] {Globals.PORTAL_REQUEST_COMMAND});
    			 Log.d(TAG,"Request Portal name");
    		} catch (IOException e) {
    			Log.e(TAG,e.getMessage());
    			e.printStackTrace();
    		}	
//        }
    	Log.d(TAG,"Set Default Labels");
        UpdateLabels();
        
        
	}

	protected void onAccesssoryAttached() {

		for (int i = 0; i < 2; i++) {
			SwitchDisplayer sd = new SwitchDisplayer(i);
			mSwitchDisplayers.add(sd);
		}
		
//		int bt=1;
		for (int i=1;i<9;i++)
		{
//			int resID = getResources().getIdentifier("Relay"+i, "id", "com.reefangel.evolution");
//			setupRelayButtonController(i, bt, resID);
//			bt= (bt==1)?0:1;
			int resID = getResources().getIdentifier("expbox"+i, "id", "com.reefangel.evolution");
			ViewGroup v = (ViewGroup) this.findViewById(resID);
			v.setVisibility(4);
		}	
		
		mDaylightView.setPercentage(0);
		mActinicView.setPercentage(0);
		
		for (int a=0;a<8;a++)
		{
			Relay_R[a]=0;
			Relay_RON[a] = 0;
			Relay_ROFF[a] = 255;
		}
	}

	public void setParam(byte param, int t) {
		switch (param){
		case Globals.T1_PROBE:
			mTemperature1.setParam(mTemperatureFormatter.format((double)t/10));
			break;
		case Globals.T2_PROBE:
			mTemperature2.setParam(mTemperatureFormatter.format((double)t/10));
			break;
		case Globals.T3_PROBE:
			mTemperature3.setParam(mTemperatureFormatter.format((double)t/10));
			break;
		case Globals.PH:
			mpH.setParam(mpHFormatter.format((double)t/100));
			break;
		case Globals.SALINITY:
			mSalinity.setParam(mSalinityFormatter.format((double)t/10));
			break;
		case Globals.ORP:
			mOrp.setParam(mORPFormatter.format(t));
			break;
		case Globals.PHEXP:
			mpHExp.setParam(mpHFormatter.format((double)t/100));
			break;
		case Globals.WL:
			mWaterLevel.setParam(t+"%");
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
					TextView trm = (TextView) this.findViewById(R.id.RFModeValue);

					OnLongClickListener listenermode = new OnLongClickListener() {
						public boolean onLongClick(View v) {
							mHostActivity.startActivityForResult( new Intent( mHostActivity, RFModeChoicesActivity.class ),Globals.PICK_RF_MODE );
							return true;
						}
					};
					trm.setOnLongClickListener(listenermode);

					final TextView trs = (TextView) this.findViewById(R.id.RFSpeedValue);
					OnLongClickListener listenerspeed = new OnLongClickListener() {
						public boolean onLongClick(View v) {
							AlertDialog.Builder builder = new AlertDialog.Builder(mHostActivity);
							final ProgressView p = new ProgressView(mHostActivity);
							builder.setTitle("Reef Angel Evolution");
							builder.setMessage("Select Speed %:");
							builder.setNegativeButton("Cancel", null);
							builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int item) {
									trs.setText(p.getPercentage()+"%");
									SpeedometerView s = (SpeedometerView) findViewById(R.id.RFSpeedometer);
									s.setHandTarget(p.getPercentage());
									SendCommand(Globals.RF_COMMAND_MODE,(byte)item);
								}
							});
							TextView t=(TextView)v;
							String cp =t.getText().toString();
							cp=cp.replace("%",""); 
							p.setOverrideMode(1);
							p.setPercentage(Integer.parseInt(cp));
							p.setCurrentPercentage(Integer.parseInt(cp));
							p.setPercentageText(Integer.parseInt(cp)+"%");
							p.setLabel("Speed");
							p.setScaleX(.9f);
							builder.setView(p);
							AlertDialog alert = builder.create();
							alert.show();
							return true;
						}
					};
					trs.setOnLongClickListener(listenerspeed);					
					
					
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
					
					ParamsView sl=(ParamsView)findViewById(R.id.SAL);
					sl.setLabel(prefs.getString("SALN", "Salinity"));
					sl.setBackgroundDrawable(mHostActivity.getResources().getDrawable(R.drawable.sal_bk));
				}
				if (BigInteger.valueOf(t).testBit(4)) {
					
					ParamsView sl=(ParamsView)findViewById(R.id.ORP);
					sl.setLabel(prefs.getString("ORPN", "ORP"));
					sl.setBackgroundDrawable(mHostActivity.getResources().getDrawable(R.drawable.orp_bk));
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
					for (int i = 2; i < 8; i++) {
						SwitchDisplayer sd = new SwitchDisplayer(i);
						mSwitchDisplayers.add(sd);
					}
					
				}			
				if (BigInteger.valueOf(t).testBit(6)) {
					
					ParamsView sl=(ParamsView)findViewById(R.id.PHE);
					sl.setLabel(prefs.getString("PHEN", "pH Exp"));
					sl.setBackgroundDrawable(mHostActivity.getResources().getDrawable(R.drawable.phexp_bk));
				}
				if (BigInteger.valueOf(t).testBit(7)) {
					
					ParamsView sl=(ParamsView)findViewById(R.id.WL);
					sl.setLabel("WL");
					sl.setBackgroundDrawable(mHostActivity.getResources().getDrawable(R.drawable.wl_bk));
				}				

			}
			break;
		case Globals.RELAYMODULES:
			if (mHostActivity.rem!=t)
			{
				int firstbox=-1;
				Log.d("EvolutionActivity", "Relay Modules: " + t);
				mHostActivity.rem=t;
				TabHost tabs = (TabHost) this.findViewById(R.id.tabhost);
				for(int a=0;a<8;a++)
					if (BigInteger.valueOf(t).testBit(a)) {
						if (firstbox==-1) firstbox=a;
						Log.d(TAG,"Adding Box "+(a+1)+ " tab");
						TabSpec tspec2 = tabs.newTabSpec("Box " + (a+1));
						tspec2.setIndicator("Box " + (a+1));
						int resID = getResources().getIdentifier("expbox"+(a+1), "id", "com.reefangel.evolution");
						Log.d(TAG,"ID: " + resID);
						tspec2.setContent(resID);
						tabs.addTab(tspec2);
						tabs.setCurrentTabByTag("Box " + (a+1));

						ViewGroup v = (ViewGroup) this.findViewById(resID);
						v.setVisibility(0);
						TableLayout tLayout = new TableLayout(mHostActivity);
						tLayout = (TableLayout) View.inflate(mHostActivity, R.layout.relaybox, null);
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
				if (firstbox!=-1) tabs.setCurrentTabByTag("Box " + (firstbox+1));
				
			}
			
			break;
			
			
		}
	}
	
	public void setByteMsg(byte channel, int value) {
		TextView c;
		SpeedometerView s;
		
		switch (channel){
		case Globals.DIMMING_DAYLIGHT:
			mDaylightView.setPercentage(value);
			break;
		case Globals.DIMMING_ACTINIC:
			mActinicView.setPercentage(value);
			break;
		case Globals.DIMMING_CHANNEL0: case Globals.DIMMING_CHANNEL1: case Globals.DIMMING_CHANNEL2: case Globals.DIMMING_CHANNEL3: case Globals.DIMMING_CHANNEL4: case Globals.DIMMING_CHANNEL5:
			if (BigInteger.valueOf(mHostActivity.em).testBit(0)) {
				int resID = getResources().getIdentifier("channel"+channel+"dimming", "id", "com.reefangel.evolution");
			    ProgressView p = (ProgressView)findViewById(resID);
			    p.setPercentage(value);
			}			
			break;
		case Globals.RF_RADION_WHITE: case Globals.RF_RADION_ROYAL_BLUE: case Globals.RF_RADION_RED: case Globals.RF_RADION_GREEN: case Globals.RF_RADION_BLUE: case Globals.RF_RADION_INTENSITY:
			if (BigInteger.valueOf(mHostActivity.em).testBit(1)) {
				int resID = getResources().getIdentifier("radion"+(channel-0x10)+"dimming", "id", "com.reefangel.evolution");
			    ProgressView p = (ProgressView)findViewById(resID);
			    p.setPercentage(value);				
			}
			break;
		case Globals.AI_WHITE:
			if (BigInteger.valueOf(mHostActivity.em).testBit(2)) {
			    ProgressView p = (ProgressView)findViewById(R.id.aiwhitedimming);
			    p.setPercentage(value);				
			}
			break;			
		case Globals.AI_BLUE:
			if (BigInteger.valueOf(mHostActivity.em).testBit(2)) {
			    ProgressView p = (ProgressView)findViewById(R.id.aibluedimming);
			    p.setPercentage(value);				
			}
			break;			
		case Globals.AI_ROYAL_BLUE:
			if (BigInteger.valueOf(mHostActivity.em).testBit(2)) {
			    ProgressView p = (ProgressView)findViewById(R.id.airoyalbluedimming);
			    p.setPercentage(value);				
			}
			break;			
		case Globals.RF_MODE:
			if (BigInteger.valueOf(mHostActivity.em).testBit(1)) {
				c = (TextView) findViewById(R.id.RFModeValue);
				c.setText(RfMode[value]);
			    s = (SpeedometerView)findViewById(R.id.RFSpeedometer);
				switch (value)
				{
				case 0:
					rf_unit=" s";
					s.setScaleColor(0x9F00FF00);
					break;
				case 1: case 2:
					rf_unit=" s";
					s.setScaleColor(0x9FFFFF00);
					break;
				case 3:
					rf_unit=" ms";
					s.setScaleColor(0x9F0000FF);
					break;
				case 4:
					rf_unit=" s";
					s.setScaleColor(0x9FFF99FF);
					break;
				case 5:
					rf_unit=" ms";
					s.setScaleColor(0x9FBB00FF);
					break;
				case 6:
					rf_unit=" s";
					s.setScaleColor(0x9FBB00FF);
					break;
				case 7: case 8: case 9:
					rf_unit=" s";
					s.setScaleColor(0x9FFFFFFF);
					break;
				case 10:
					rf_unit=" ms";
					s.setScaleColor(0x9F00DDFF);
					break;
				default:
					rf_unit=" s";
					s.setScaleColor(0x9FFF0000);
					break;
				}
				c = (TextView) findViewById(R.id.RFDurationValue);
				String d=c.getText().toString();
				d=d.replace(" ms", "").replace(" s", "");
				c.setText(d+rf_unit);
			}
			break;
		case Globals.RF_SPEED:
			c = (TextView) findViewById(R.id.RFSpeedValue);
			c.setText(value+"%");
		    s = (SpeedometerView)findViewById(R.id.RFSpeedometer);
		    s.setHandTarget(value);
			break;
		case Globals.RF_DURATION:
			c = (TextView) findViewById(R.id.RFDurationValue);
			c.setText(value+rf_unit);
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

	public void setPortalName(String portalname) {
		Log.d(TAG,"Downloading Labels");
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("MYREEFANGELID", portalname);
		editor.commit();
		DownloadXMLLabels task = new DownloadXMLLabels();
		task.execute();
	}
	
	public void switchStateChanged(int switchIndex, boolean switchState) {
		if (switchIndex >= 0 && switchIndex < mSwitchDisplayers.size()) {
			SwitchDisplayer sd = mSwitchDisplayers.get(switchIndex);
			sd.onSwitchStateChange(switchState);
		}
	}

	public void onSwitchStateChange(int switchIndex, Boolean switchState) {
		switchStateChanged(switchIndex, switchState);
	}

	class SwitchDisplayer {
		private final ImageView mTargetView;
		private final Drawable mOnImage;
		private final Drawable mOffImage;

		SwitchDisplayer(int switchIndex) {
			int viewId= R.id.LowATO;
			switch (switchIndex) {
			case Globals.LOW_ATO:
				viewId = R.id.LowATO;
				break;
			case Globals.HIGH_ATO:
				viewId = R.id.HighATO;
				break;
			case Globals.IO_CHANNEL0:
				viewId = R.id.iochannel0;
				break;
			case Globals.IO_CHANNEL1:
				viewId = R.id.iochannel1;
				break;
			case Globals.IO_CHANNEL2:
				viewId = R.id.iochannel2;
				break;
			case Globals.IO_CHANNEL3:
				viewId = R.id.iochannel3;
				break;
			case Globals.IO_CHANNEL4:
				viewId = R.id.iochannel4;
				break;
			case Globals.IO_CHANNEL5:
				viewId = R.id.iochannel5;
				break;
				
//			case Globals.IO_CHANNEL0: case Globals.IO_CHANNEL1: case Globals.IO_CHANNEL2: case Globals.IO_CHANNEL3: case Globals.IO_CHANNEL4: case Globals.IO_CHANNEL5:
//				viewId = getResources().getIdentifier("iochannel"+(switchIndex-0x2), "id", "com.reefangel.evolution");
//				break;
			}
			mTargetView = (ImageView) findViewById(viewId);
			mOffImage = mHostActivity.getResources().getDrawable(R.drawable.ato_off);
			mOnImage = mHostActivity.getResources().getDrawable(R.drawable.ato_on);
			
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


	public class DownloadXMLLabels extends AsyncTask<String, Integer, Integer> {
		
		protected Integer doInBackground(String... params) {
			XMLfunctions.GetLabels(mHostActivity);
			publishProgress(0); 
			return null;
		}
		
		protected void onProgressUpdate(Integer... values) {
			Log.d(TAG,"Updating Labels");
			UpdateLabels();
		}

		protected void onPostExecute(Integer result) {

	    }

		
	}
	
	public void UpdateLabels()
	{
		for (int a=1; a<4; a++)
		{
			ParamsView t=(ParamsView) findViewById(getResources().getIdentifier("T" + a, "id", "com.reefangel.evolution"));
			t.setLabel (prefs.getString("T" + a + "N", "Temp "+a));
			Log.d(TAG,"Updated Label T" + a + ": " + prefs.getString("T" + a + "N", "Temp "+a));
		}		
		ParamsView t=(ParamsView) findViewById(R.id.PH);
		t.setLabel(prefs.getString("PHN", "pH"));
		Log.d(TAG,"Updated Label PH: " + prefs.getString("PHN", "pH"));
		if (BigInteger.valueOf(mHostActivity.em).testBit(3)) {
			t=(ParamsView) findViewById(R.id.SAL);
			t.setLabel(prefs.getString("SALN", "Salinity"));
			Log.d(TAG,"Updated Label SAL: " + prefs.getString("SALN", "Salinity"));
		}
		if (BigInteger.valueOf(mHostActivity.em).testBit(4)) {
			t=(ParamsView) findViewById(R.id.ORP);
			t.setLabel(prefs.getString("ORPN", "ORP"));
			Log.d(TAG,"Updated Label ORP: " + prefs.getString("ORPN", "ORP"));
		}
		if (BigInteger.valueOf(mHostActivity.em).testBit(6)) {
			t=(ParamsView) findViewById(R.id.PHE);
			t.setLabel(prefs.getString("PHEN", "PH Exp"));
			Log.d(TAG,"Updated Label PHE: " + prefs.getString("PHEN", "PH Exp"));
		}
		if (mRelayButtonControllers.size()>0)
		{
			int indexr=0;
			for (int a=1;a<9;a++)
			{
				RelayButtonController r = mRelayButtonControllers.get(indexr);
				r.SetLabel(prefs.getString("R"+a+"N", "Port "+a));
				Log.d(TAG,"Updated Label R"+a+"N: " + prefs.getString("R"+a+"N", "Relay "+a));
				indexr++;
			}
			int x=1;
			int y=1;
			while (indexr<mRelayButtonControllers.size())
			{
				RelayButtonController r = mRelayButtonControllers.get(indexr);
				r.SetLabel(prefs.getString("R"+x+""+y+"N", "Relay "+x+""+y));
				Log.d(TAG,"Updated Label R"+x+""+y+"N: " + prefs.getString("R"+x+""+y+"N", "Relay "+x+""+y));
				indexr++;
				y++;
				if (y==9)
				{
					y=1;
					x++;
				}
			}
		}
	}
	
	public void SendCommand (byte cmd, byte state)
	{
		Log.d(TAG,"Command Sent: " + state);
		mHostActivity.sendCommand(cmd, (byte)0, state);
		try {
			mHostActivity.server.send(new byte[] {cmd, (byte) 0, state});
		} catch (IOException e) {
			Log.e(TAG,e.getMessage());
			e.printStackTrace();
		}		
	}		
	
}
