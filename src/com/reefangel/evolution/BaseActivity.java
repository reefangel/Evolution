package com.reefangel.evolution;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends EvolutionActivity {
	private static final String TAG = "EvolutionBaseActivity";
	private InputController mInputController;

	public BaseActivity() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mAccessory != null || MicrobridgeLink) {
			showControls();
		} else {
			hideControls();
		}
		Log.d(TAG,"Created");		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Simulate");
		menu.add("Quit");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle() == "Simulate") {
			showControls();
			EvolutionDB dh; 
			int t1=775+new Random().nextInt(5);
			int t2=978+new Random().nextInt(5);
			int t3=871+new Random().nextInt(5);
			int ph=817+new Random().nextInt(5);
			int sal=345+new Random().nextInt(5);
			int orp=426+new Random().nextInt(5);
			int phe=764+new Random().nextInt(5);
			int wl=61+new Random().nextInt(5);

			dh = new EvolutionDB(this);
	        dh.insert(Integer.toString(t1),Integer.toString(t2),Integer.toString(t3),Integer.toString(ph),Integer.toString(sal),Integer.toString(orp),Integer.toString(phe),Integer.toString(wl));
	        dh.finalize();
			mInputController.setParam(Globals.T1_PROBE,t1);
			mInputController.setParam(Globals.T2_PROBE,t2);
			mInputController.setParam(Globals.T3_PROBE,t3);
			mInputController.setParam(Globals.PH,ph);
			mInputController.setParam(Globals.EXPANSIONMODULES,0);
			mInputController.setParam(Globals.RELAYMODULES,0);
			mInputController.setParam(Globals.EXPANSIONMODULES,255);
			mInputController.setParam(Globals.RELAYMODULES,255);
			mInputController.setParam(Globals.SALINITY,sal);
			mInputController.setParam(Globals.ORP,orp);
			mInputController.setParam(Globals.PHEXP,phe);
			mInputController.setParam(Globals.WL,wl);
			mInputController.setByteMsg(Globals.DIMMING_ACTINIC,41+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.DIMMING_DAYLIGHT,84+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.DIMMING_CHANNEL0,61+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.DIMMING_CHANNEL1,48+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.DIMMING_CHANNEL2,12+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.DIMMING_CHANNEL3,89+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.DIMMING_CHANNEL4,2+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.DIMMING_CHANNEL5,34+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.RF_MODE,new Random().nextInt(6));
			mInputController.setByteMsg(Globals.RF_SPEED,89+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.RF_DURATION,14+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.RF_RADION_WHITE,61+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.RF_RADION_ROYAL_BLUE,95+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.RF_RADION_RED,10+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.RF_RADION_GREEN,16+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.RF_RADION_BLUE,83+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.RF_RADION_INTENSITY,79+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.AI_WHITE,57+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.AI_BLUE,83+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.AI_ROYAL_BLUE,91+new Random().nextInt(5));
			mInputController.switchStateChanged(Globals.LOW_ATO, new Random().nextInt(10)<5);
			mInputController.switchStateChanged(Globals.HIGH_ATO, new Random().nextInt(10)<5);
			mInputController.switchStateChanged(Globals.IO_CHANNEL0, new Random().nextInt(10)<5);
			mInputController.switchStateChanged(Globals.IO_CHANNEL1, new Random().nextInt(10)<5);
			mInputController.switchStateChanged(Globals.IO_CHANNEL2, new Random().nextInt(10)<5);
			mInputController.switchStateChanged(Globals.IO_CHANNEL3, new Random().nextInt(10)<5);
			mInputController.switchStateChanged(Globals.IO_CHANNEL4, new Random().nextInt(10)<5);
			mInputController.switchStateChanged(Globals.IO_CHANNEL5, new Random().nextInt(10)<5);
			
		} else if (item.getTitle() == "Quit") {
			finish();
			System.exit(0);
		}
		return true;
	}

	protected void enableControls(boolean enable) {
		if (enable) {
			showControls();
		} else {
			hideControls();
		}
	}

	protected void hideControls() {
		setContentView(R.layout.no_device);
		mInputController = null;
	}

	protected void showControls() {
		setContentView(R.layout.main);
		mInputController = new InputController(this);
		mInputController.accessoryAttached();
		DBUpdateTimer = new Timer();
		DBUpdateTimer.scheduleAtFixedRate(new TimerTask() {

			public void run() {
				
				ParamsView mTemperature1 = (ParamsView) findViewById(R.id.T1);
				ParamsView mTemperature2 = (ParamsView) findViewById(R.id.T2);
				ParamsView mTemperature3 = (ParamsView) findViewById(R.id.T3);
				ParamsView mpH = (ParamsView) findViewById(R.id.PH);
				ParamsView mSalinity = (ParamsView) findViewById(R.id.SAL);
				ParamsView mOrp = (ParamsView) findViewById(R.id.ORP);
				ParamsView mpHExp = (ParamsView) findViewById(R.id.PHE);
				ParamsView mWaterLevel = (ParamsView) findViewById(R.id.WL);
				EvolutionDB dh; 
				dh = new EvolutionDB(getBaseContext());
				dh.insert(
						Integer.toString(mTemperature1.getParam()),
						Integer.toString(mTemperature2.getParam()),
						Integer.toString(mTemperature3.getParam()),
						Integer.toString(mpH.getParam()),
						Integer.toString(mSalinity.getParam()),
						Integer.toString(mOrp.getParam()),
						Integer.toString(mpHExp.getParam()),
						Integer.toString(mWaterLevel.getParam())
						);
				dh.finalize();
				Log.d(TAG,"Saved Data");
				Log.d(TAG,"T1: "+ mTemperature1.getParam());
			}

		}, 5000, 300000);		
	}
	
	protected void handleParamsMessage(ParamsMsg t) {
		if (mInputController != null) {
			mInputController.setParam(t.getParam(), t.getParamValue());
		}
	}

	protected void handleByteMessage(ByteMsg t) {
		if (mInputController != null) {
			mInputController.setByteMsg(t.getChannel(), t.getChannelValue());
		}
	}
	
	protected void handleRelayMessage(RelayMsg r) {
		if (mInputController != null) {
			mInputController.setRelay(r.getAttr(), r.getRelayValue());
		}
	}
	
	protected void handlePortalNameMessage(PortalNameMsg p) {
		if (mInputController != null) {
			mInputController.setPortalName(p.getPortalNameValue());
		}
	}	
	protected void handleSwitchMessage(SwitchMsg o) {
		if (mInputController != null) {
			mInputController.switchStateChanged(o.getSw(), o.getState() != 0);
		}
	}

}