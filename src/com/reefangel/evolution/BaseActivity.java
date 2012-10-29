package com.reefangel.evolution;

import java.util.Random;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends EvolutionActivity {

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
			mInputController.setParam(Globals.T1_PROBE,775+new Random().nextInt(5));
			mInputController.setParam(Globals.T2_PROBE,978+new Random().nextInt(5));
			mInputController.setParam(Globals.T3_PROBE,871+new Random().nextInt(5));
			mInputController.setParam(Globals.PH,817+new Random().nextInt(5));
			mInputController.setParam(Globals.EXPANSIONMODULES,0);
			mInputController.setParam(Globals.RELAYMODULES,0);
			mInputController.setParam(Globals.EXPANSIONMODULES,255);
			mInputController.setParam(Globals.RELAYMODULES,255);
			mInputController.setParam(Globals.SALINITY,345+new Random().nextInt(5));
			mInputController.setParam(Globals.ORP,426+new Random().nextInt(5));
			mInputController.setParam(Globals.PHEXP,764+new Random().nextInt(5));
			mInputController.setParam(Globals.WL,61+new Random().nextInt(5));
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