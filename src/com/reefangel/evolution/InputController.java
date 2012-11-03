package com.reefangel.evolution;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TableLayout;
import android.widget.TextView;

public class InputController extends AccessoryController  {
	private static final String TAG = "EvolutionInputController";

	private String RfMode[] = {"Constant","Lagoon","Reef Crest","Short Pulse","Long Pulse","Nutrient Transport","Tidal Swell","Feeding","Feeding","Night","Storm","Custom","Custom","Custom","Custom"};
	public ParamsView mTemperature1;
	public ParamsView mTemperature2;
	public ParamsView mTemperature3;
	public ParamsView mpH;
	public ParamsView mSalinity;
	public ParamsView mOrp;
	public ParamsView mpHExp;
	public ParamsView mWaterLevel;
	public ProgressView mDaylightView1;
	public ProgressView mActinicView1;
	public ProgressView mDaylightView2;
	public ProgressView mActinicView2;
	public int Relay_R[];
	public int Relay_RON[];
	public int Relay_ROFF[];
	Runnable TimerRunnable;

	String rf_unit=" s";

	SharedPreferences prefs;

	ArrayList<SwitchDisplayer> mSwitchDisplayers;
	ArrayList<RelayButtonController> mRelayButtonControllers;
	private final DecimalFormat mNoFormatter = new DecimalFormat("###");
	private final DecimalFormat mTemperatureFormatter = new DecimalFormat("##.#" + (char)0x00B0);
	private final DecimalFormat mpHFormatter = new DecimalFormat("#.##");
	private final DecimalFormat mSalinityFormatter = new DecimalFormat("##.#");
	private final DecimalFormat mWLFormatter = new DecimalFormat("###" + (char)0x0025);

	
	InputController(EvolutionActivity hostActivity) {
		super(hostActivity);
		mTemperature1 = (ParamsView) findViewById(R.id.T1);
		mTemperature1.setDecimal(10);
		mTemperature1.setParamID(0);
		mTemperature1.setFormat(mTemperatureFormatter);
		mTemperature2 = (ParamsView) findViewById(R.id.T2);
		mTemperature2.setDecimal(10);
		mTemperature2.setParamID(1);
		mTemperature2.setFormat(mTemperatureFormatter);
		mTemperature3 = (ParamsView) findViewById(R.id.T3);
		mTemperature3.setDecimal(10);
		mTemperature3.setParamID(2);
		mTemperature3.setFormat(mTemperatureFormatter);
		mpH = (ParamsView) findViewById(R.id.PH);
		mpH.setDecimal(100);
		mpH.setParamID(3);
		mpH.setFormat(mpHFormatter);
		mSalinity = (ParamsView) findViewById(R.id.SAL);
		mSalinity.setDecimal(10);
		mSalinity.setParamID(4);
		mSalinity.setFormat(mSalinityFormatter);
		mOrp = (ParamsView) findViewById(R.id.ORP);
		mOrp.setDecimal(1);
		mOrp.setParamID(5);
		mOrp.setFormat(mNoFormatter);
		mpHExp = (ParamsView) findViewById(R.id.PHE);
		mpHExp.setDecimal(100);
		mpHExp.setParamID(6);
		mpHExp.setFormat(mpHFormatter);
		mWaterLevel = (ParamsView) findViewById(R.id.WL);
		mWaterLevel.setDecimal(100);
		mWaterLevel.setParamID(7);
		mWaterLevel.setFormat(mWLFormatter);
		
		mDaylightView1 = (ProgressView) findViewById(R.id.daylightdimming1);
		mDaylightView1.setLabel(R.string.Daylight1Label);
		mDaylightView1.setMode(1);
		mActinicView1 = (ProgressView) findViewById(R.id.actinicdimming1);
		mActinicView1.setBarColor(1);
		mActinicView1.setLabel(R.string.Actinic1Label);
		mActinicView1.setMode(1);
		mDaylightView2 = (ProgressView) findViewById(R.id.daylightdimming2);
		mDaylightView2.setLabel(R.string.Daylight2Label);
		mDaylightView2.setMode(1);
		mActinicView2 = (ProgressView) findViewById(R.id.actinicdimming2);
		mActinicView2.setBarColor(1);
		mActinicView2.setLabel(R.string.Actinic2Label);
		mActinicView2.setMode(1);

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

		String s=getResources().getString(R.string.TabStdPortsLabel);
		TabSpec tspec1ports = tabsports.newTabSpec(s);
		tspec1ports.setIndicator(s);
		tspec1ports.setContent(R.id.standardports);
		tabsports.addTab(tspec1ports);
		tabsports.setCurrentTabByTag(s);

		prefs = mHostActivity.getSharedPreferences(Globals.PREFS_NAME, 0);

		try {
			mHostActivity.server.send(new byte[] {Globals.PORTAL_REQUEST_COMMAND});
			Log.d(TAG,"Request Portal name");
		} catch (IOException e) {
			Log.e(TAG,e.getMessage());
			e.printStackTrace();
		}	
		Log.d(TAG,"Set Default Labels");
		UpdateLabels();


	}

	public void onDestroy() {
		Log.d(TAG,"Destroyed");
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

		mDaylightView1.setPercentage(0);
		mActinicView1.setPercentage(0);
		mDaylightView2.setPercentage(0);
		mActinicView2.setPercentage(0);

		for (int a=0;a<8;a++)
		{
			Relay_R[a]=0;
			Relay_RON[a] = 0;
			Relay_ROFF[a] = 255;
		}
	}
	
	public void setParam(byte param, int t) {
		getResources();
		final int okid = Resources.getSystem().getIdentifier("ok", "string", "android");
		final int cancelid = Resources.getSystem().getIdentifier("cancel", "string", "android");
		
		switch (param){
		case Globals.T1_PROBE:
			mTemperature1.setParam(t);
			CheckAlerts(t, mTemperature1, prefs.getFloat("PARAMSLESSTHAN0",0f),prefs.getFloat("PARAMSGREATERTHAN0",0f));
			break;
		case Globals.T2_PROBE:
			mTemperature2.setParam(t);
			CheckAlerts(t, mTemperature2, prefs.getFloat("PARAMSLESSTHAN1",0f),prefs.getFloat("PARAMSGREATERTHAN1",0f));
			break;
		case Globals.T3_PROBE:
			mTemperature3.setParam(t);
			CheckAlerts(t, mTemperature3, prefs.getFloat("PARAMSLESSTHAN2",0f),prefs.getFloat("PARAMSGREATERTHAN2",0f));
			break;
		case Globals.PH:
			mpH.setParam(t);
			CheckAlerts(t, mpH, prefs.getFloat("PARAMSLESSTHAN3",0f),prefs.getFloat("PARAMSGREATERTHAN3",0f));
			break;
		case Globals.SALINITY:
			mSalinity.setParam(t);
			CheckAlerts(t, mSalinity, prefs.getFloat("PARAMSLESSTHAN4",0f),prefs.getFloat("PARAMSGREATERTHAN4",0f));
			break;
		case Globals.ORP:
			mOrp.setParam(t);
			CheckAlerts(t, mOrp, prefs.getFloat("PARAMSLESSTHAN5",0f),prefs.getFloat("PARAMSGREATERTHAN5",0f));
			break;
		case Globals.PHEXP:
			mpHExp.setParam(t);
			CheckAlerts(t, mpHExp, prefs.getFloat("PARAMSLESSTHAN6",0f),prefs.getFloat("PARAMSGREATERTHAN6",0f));
			break;
		case Globals.WL:
			mWaterLevel.setParam(t);
			CheckAlerts(t, mWaterLevel, prefs.getFloat("PARAMSLESSTHAN7",0f),prefs.getFloat("PARAMSGREATERTHAN7",0f));
			break;	
		case Globals.EXPANSIONMODULES:
			if (mHostActivity.em!=t)
			{
				mHostActivity.em=t;
				Log.d(TAG, "Expansion Modules: " + t);

				if (BigInteger.valueOf(t).testBit(0)) {

					TabHost tabsports = (TabHost) this.findViewById(R.id.tabhostports);
					tabsports.setup();

					String s=getResources().getString(R.string.TabDimPortsLabel);
					TabSpec tspec1ports = tabsports.newTabSpec(s);
					tspec1ports.setIndicator(s);
					tspec1ports.setContent(R.id.dimmingexpports);
					tabsports.addTab(tspec1ports);
					tabsports.setCurrentTabByTag(getResources().getString(R.string.TabStdPortsLabel));

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

					String s=getResources().getString(R.string.TabRFVortechLabel);
					TabSpec tspec1ports = tabsports.newTabSpec(s);
					tspec1ports.setIndicator(s);
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
							builder.setTitle(R.string.app_name);
							builder.setMessage(R.string.SelectSpeedLabel);
							builder.setNegativeButton(getResources().getString(cancelid), null);
							builder.setPositiveButton(getResources().getString(okid), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int item) {
									trs.setText(p.getPercentage()+"%");
									SpeedometerView s = (SpeedometerView) findViewById(R.id.RFSpeedometer);
									s.setHandTarget(p.getPercentage());
									trs.setTag(p.getPercentage());
									SendCommand(Globals.RF_COMMAND_MODE,(byte)item);
								}
							});
//							TextView t=(TextView)v;
//							String cp =t.getText().toString();
//							cp=cp.replace("%",""); 
							p.setOverrideMode(1);
							p.setPercentage((Integer) trs.getTag());
							p.setCurrentPercentage((Integer) trs.getTag());
							p.setPercentageText((Integer) trs.getTag()+"%");
							p.setLabel(R.string.SpeedLabel);
							p.setScaleX(.9f);
							builder.setView(p);
							AlertDialog alert = builder.create();
							alert.show();
							return true;
						}
					};
					trs.setOnLongClickListener(listenerspeed);					

					final TextView trd = (TextView) this.findViewById(R.id.RFDurationValue);
					OnLongClickListener listenerduration = new OnLongClickListener() {
						@SuppressLint("NewApi")
						public boolean onLongClick(View v) {
							AlertDialog.Builder builder = new AlertDialog.Builder(mHostActivity);
							final NumberPicker p = new NumberPicker(mHostActivity);
							builder.setTitle(R.string.app_name);
							builder.setMessage(R.string.SelectDurationLabel);
							builder.setNegativeButton(getResources().getString(cancelid), null);
							builder.setPositiveButton(getResources().getString(okid), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int item) {
									trd.setText(p.getValue()+rf_unit);
									trd.setTag(p.getValue());
									SendCommand(Globals.RF_COMMAND_DURATION,(byte)item);
								}
							});
//							String cp =trd.getText().toString();
//							cp=cp.replace(" ms","").replace(" s", "");
							p.setMaxValue(100);
							p.setMinValue(0);
//							p.setValue(Integer.parseInt(cp));
							p.setValue((Integer) trd.getTag());
							builder.setView(p);
							AlertDialog alert = builder.create();
							alert.show();
							return true;
						}
					};
					trd.setOnLongClickListener(listenerduration);		
					
					String sr=getResources().getString(R.string.TabRFRadionLabel);
					tspec1ports = tabsports.newTabSpec(sr);
					tspec1ports.setIndicator(sr);
					tspec1ports.setContent(R.id.rfradionexpports);
					tabsports.addTab(tspec1ports);
					tabsports.setCurrentTabByTag(getResources().getString(R.string.TabStdPortsLabel));

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
					
					String s=getResources().getString(R.string.TabAiPortsLabel);
					TabSpec tspec1ports = tabsports.newTabSpec(s);
					tspec1ports.setIndicator(s);
					tspec1ports.setContent(R.id.aiports);
					tabsports.addTab(tspec1ports);
					tabsports.setCurrentTabByTag(getResources().getString(R.string.TabStdPortsLabel));

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

					String s=getResources().getString(R.string.TabIOPortsLabel);
					TabSpec tspec1ports = tabsports.newTabSpec(s);
					tspec1ports.setIndicator(s);
					tspec1ports.setContent(R.id.ioexpports);
					tabsports.addTab(tspec1ports);
					tabsports.setCurrentTabByTag(getResources().getString(R.string.TabStdPortsLabel));

					ViewGroup v = (ViewGroup) this.findViewById(R.id.ioexpports);
					LinearLayout lIOExp = new LinearLayout(mHostActivity);
					lIOExp = (LinearLayout) View.inflate(mHostActivity, R.layout.ioexpcontainer, null);
					LinearLayout.LayoutParams lr = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT,
							(float) 1.0
							);	
					v.addView(lIOExp,lr);
					for (int i = Globals.IO_CHANNEL0; i <= Globals.IO_CHANNEL5; i++) {
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
				Log.d(TAG, "Relay Modules: " + t);
				mHostActivity.rem=t;
				TabHost tabs = (TabHost) this.findViewById(R.id.tabhost);
				String s=getResources().getString(R.string.TabBoxLabel);
				for(int a=0;a<8;a++)
					if (BigInteger.valueOf(t).testBit(a)) {
						if (firstbox==-1) firstbox=a;
						Log.d(TAG,"Adding Box "+(a+1)+ " tab");
						TabSpec tspec2 = tabs.newTabSpec(s + (a+1));
						tspec2.setIndicator(s + (a+1));
						int resID = getResources().getIdentifier("expbox"+(a+1), "id", "com.reefangel.evolution");
						Log.d(TAG,"ID: " + resID);
						tspec2.setContent(resID);
						tabs.addTab(tspec2);
						tabs.setCurrentTabByTag(s + (a+1));

						ViewGroup v = (ViewGroup) this.findViewById(resID);
						v.setVisibility(0);
						TableLayout tLayout = new TableLayout(mHostActivity);
						tLayout = (TableLayout) View.inflate(mHostActivity, R.layout.relaybox, null);
						new TableLayout.LayoutParams(
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
				if (firstbox!=-1) tabs.setCurrentTabByTag(s + (firstbox+1));

			}

			break;


		}
	}

	public void setByteMsg(byte channel, int value) {
		TextView c;
		SpeedometerView s;

		switch (channel){
		case Globals.DIMMING_DAYLIGHT1:
			mDaylightView1.setPercentage(value);
			break;
		case Globals.DIMMING_ACTINIC1:
			mActinicView1.setPercentage(value);
			break;
		case Globals.DIMMING_DAYLIGHT2:
			mDaylightView2.setPercentage(value);
			break;
		case Globals.DIMMING_ACTINIC2:
			mActinicView2.setPercentage(value);
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
				c.setTag(value);
				s = (SpeedometerView)findViewById(R.id.RFSpeedometer);
				TextView rdv = (TextView) findViewById(R.id.RFDurationValue);
				TextView rdl = (TextView) findViewById(R.id.RFDurationLabel);
				switch (value)
				{
				case 0:
					rf_unit=" s";
					s.setScaleColor(0x9F00FF00);
					rdv.setVisibility(4);
					rdl.setVisibility(4);
					rdv.setEnabled(false);
					break;
				case 1: case 2:
					rf_unit=" s";
					s.setScaleColor(0x9FFFFF00);
					rdv.setVisibility(4);
					rdl.setVisibility(4);
					rdv.setEnabled(false);
					break;
				case 3:
					rf_unit=" ms";
					s.setScaleColor(0x9F0000FF);
					rdv.setVisibility(0);
					rdl.setVisibility(0);
					rdv.setEnabled(true);
					break;
				case 4:
					rf_unit=" s";
					s.setScaleColor(0x9FFF99FF);
					rdv.setVisibility(0);
					rdl.setVisibility(0);
					rdv.setEnabled(true);
					break;
				case 5:
					rf_unit=" ms";
					s.setScaleColor(0x9FBB00FF);
					rdv.setVisibility(0);
					rdl.setVisibility(0);
					rdv.setEnabled(true);
					break;
				case 6:
					rf_unit=" s";
					s.setScaleColor(0x9FBB00FF);
					rdv.setVisibility(4);
					rdl.setVisibility(4);
					rdv.setEnabled(false);
					break;
				case 7: case 8: case 9:
					rf_unit=" s";
					s.setScaleColor(0x9FFFFFFF);
					rdv.setVisibility(4);
					rdl.setVisibility(4);
					rdv.setEnabled(false);
					break;
				case 10:
					rf_unit=" ms";
					s.setScaleColor(0x9F00DDFF);
					rdv.setVisibility(0);
					rdl.setVisibility(0);
					rdv.setEnabled(true);
					break;
				default:
					rf_unit=" s";
					s.setScaleColor(0x9FFF0000);
					rdv.setVisibility(4);
					rdl.setVisibility(4);
					rdv.setEnabled(false);
					break;
				}
				c = (TextView) findViewById(R.id.RFDurationValue);
//				String d=c.getText().toString();
//				d=d.replace(" ms", "").replace(" s", "");
				c.setText(c.getTag()+rf_unit);
			}
			break;
		case Globals.RF_SPEED:
			c = (TextView) findViewById(R.id.RFSpeedValue);
			c.setText(value+"%");
			s = (SpeedometerView)findViewById(R.id.RFSpeedometer);
			s.setHandTarget(value);
			c.setTag(value);
			break;
		case Globals.RF_DURATION:
			c = (TextView) findViewById(R.id.RFDurationValue);
			c.setText(value+rf_unit);
			c.setTag(value);
			break;
		case Globals.CUSTOM0: case Globals.CUSTOM1: case Globals.CUSTOM2: case Globals.CUSTOM3: case Globals.CUSTOM4: case Globals.CUSTOM5: case Globals.CUSTOM6: case Globals.CUSTOM7:
			mHostActivity.c[channel-0x30]=value;
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

	public boolean getswitchState(int switchIndex)
	{
		SwitchDisplayer sd = mSwitchDisplayers.get(switchIndex);
		return sd.GetState();
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
		private boolean state;

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
			state=false;
		}

		void onSwitchStateChange(Boolean switchState) {
			Drawable currentImage;
			state=switchState;
			if (!switchState) {
				currentImage = mOffImage;
			} else {
				currentImage = mOnImage;
			}
			mTargetView.setImageDrawable(currentImage);
		}
		boolean GetState()
		{
			return state;
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
	
	public class SendAlert extends AsyncTask<String, Integer, Integer> {

		protected Integer doInBackground(String... params) {
			Log.d(TAG,"Sending Alert: " + params[0]);
			Log.d(TAG,"Sending Alert: " + params[1]);
			String line = null;
			String url="";
			try {
				url="e="+URLEncoder.encode(prefs.getString("AlertEmail", ""), "utf-8");
				url+="&s="+URLEncoder.encode(params[0], "utf-8");
				url+="&b="+URLEncoder.encode(params[1], "utf-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			url=Globals.PORTAL_ALERT+"?"+url;

			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				Log.d(TAG,"Alert URL: "+url);
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

			Log.d(TAG,line);					
			return null;
		}
		protected void onProgressUpdate(Integer... values) {
		}

		protected void onPostExecute(Integer result) {
		}
	}
	
	private void CheckAlerts(int CurrentValue, ParamsView Param, float ParamLess, float ParamGreater )
	{
		String ParamLabel=Param.getLabel();
		float ParamCheck= (float)CurrentValue/Param.getDecimal();
		Date LastAlert=Param.getLastAlert();
		if (new Date().getTime()-LastAlert.getTime()>3600000 )
		{
			if (ParamCheck < ParamLess)
			{
				SendAlert task = new SendAlert();
				task.execute(ParamLabel + " Alert",ParamCheck + " is less than "+ParamLess);
				Param.setLastAlert(new Date());
			}
			if (ParamCheck > ParamGreater && ParamGreater>0)
			{
				SendAlert task = new SendAlert();
				task.execute(ParamLabel + " Alert",ParamCheck + " is greater than "+ParamGreater);
				Param.setLastAlert(new Date());
			}
		}
		else
		{
			Log.d(TAG,"Alert not sent. Last Alert was less than 1hr.");
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
