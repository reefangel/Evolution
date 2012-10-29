package com.reefangel.evolution;

import android.view.ViewGroup;

public class OutputController extends AccessoryController {

	private boolean mVertical;

	OutputController(EvolutionActivity hostActivity, boolean vertical) {
		super(hostActivity);
		mVertical = vertical;
	}

	protected void onAccesssoryAttached() {
//		setupServoController(1, R.id.servo1);
//		setupServoController(2, R.id.servo2);
//		setupServoController(3, R.id.servo3);
//
//		setupLedController(1, R.id.leds1);
//		setupLedController(2, R.id.leds2);
//		setupLedController(3, R.id.leds3);
//
//		setupRelayController(1, R.id.relay1);
//		setupRelayController(2, R.id.relay2);
		
//		setupRelayButtonController(1, 1, R.id.Relay1);
//		setupRelayButtonController(2, 0, R.id.Relay2);
//		setupRelayButtonController(3, 1, R.id.Relay3);
//		setupRelayButtonController(4, 0, R.id.Relay4);
//		setupRelayButtonController(5, 1, R.id.Relay5);
//		setupRelayButtonController(6, 0, R.id.Relay6);
//		setupRelayButtonController(7, 1, R.id.Relay7);
//		setupRelayButtonController(8, 0, R.id.Relay8);
	}

//	private void setupServoController(int servoIndex, int viewId) {
//		ServoController sc = new ServoController(mHostActivity, servoIndex);
//		sc.attachToView((ViewGroup) findViewById(viewId));
//	}
//
//	private void setupLedController(int index, int viewId) {
//		ColorLEDController ledC = new ColorLEDController(mHostActivity, index,
//				getResources(), mVertical);
//		ledC.attachToView((ViewGroup) findViewById(viewId));
//	}
//
//	private void setupRelayController(int index, int viewId) {
//		RelayController r = new RelayController(mHostActivity, index,
//				getResources());
//		r.attachToView((ViewGroup) findViewById(viewId));
//	}
	
//	private void setupRelayButtonController(int index, int pos, int viewId) {
//		RelayButtonController r = new RelayButtonController(mHostActivity, index, pos, getResources());
//		r.attachToView((ViewGroup) findViewById(viewId));		
//	}
}
