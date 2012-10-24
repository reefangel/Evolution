package com.reefangel.evolution;

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

	protected void handleJoyMessage(JoyMsg j) {
		if (mInputController != null) {
			mInputController.joystickMoved(j.getX(), j.getY());
		}
	}
	
	protected void handleParamsMessage(ParamsMsg t) {
		if (mInputController != null) {
			mInputController.setParam(t.getParam(), t.getParamValue());
		}
	}

	protected void handleDimmingMessage(DimmingMsg t) {
		if (mInputController != null) {
			mInputController.setDimming(t.getChannel(), t.getChannelValue());
		}
	}
	
	protected void handleRelayMessage(RelayMsg r) {
		if (mInputController != null) {
			mInputController.setRelay(r.getAttr(), r.getRelayValue());
		}
	}
	protected void handleSwitchMessage(SwitchMsg o) {
		if (mInputController != null) {
			byte sw = o.getSw();
			if (sw >= 0 && sw < 2) {
				mInputController.switchStateChanged(sw, o.getState() != 0);
			}
		}
	}

}