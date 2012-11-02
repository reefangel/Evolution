/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reefangel.evolution;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.microbridge.server.AbstractServerListener;
import org.microbridge.server.Server;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;

public class EvolutionActivity extends Activity implements Runnable {
	private static final String TAG = "EvolutionActivity";

	private static final String ACTION_USB_PERMISSION = "com.reefangel.evolution.action.USB_PERMISSION";

	private UsbManager mUsbManager;
	private PendingIntent mPermissionIntent;
	private boolean mPermissionRequestPending;
	Timer DBUpdateTimer;

	UsbAccessory mAccessory;
	boolean MicrobridgeLink=false;
	ParcelFileDescriptor mFileDescriptor;
	FileInputStream mInputStream;
	FileOutputStream mOutputStream;
	
	int em=0;
	int rem=0;
	int[] c={0,0,0,0,0,0,0,0};
	
	byte[] buffer = new byte[16384];	

	Server server = null;

	private static final int MESSAGE_SWITCH = 1;
	private static final int MESSAGE_PARAMS = 2;
	private static final int MESSAGE_BYTE = 3;
	private static final int MESSAGE_RELAY = 4;
	private static final int MESSAGE_PORTAL = 5;



	protected class SwitchMsg {
		private byte sw;
		private byte state;

		public SwitchMsg(byte sw, byte state) {
			this.sw = sw;
			this.state = state;
		}

		public byte getSw() {
			return sw;
		}

		public byte getState() {
			return state;
		}
	}

	protected class ParamsMsg {
		private int paramvalue;
		private byte param;

		public ParamsMsg(byte param, int paramvalue) {
			this.paramvalue = paramvalue;
			this.param = param;
		}

		public int getParamValue() {
			return paramvalue;
		}
		public byte getParam() {
			return param;
		}
	}

	protected class ByteMsg {
		private int channelvalue;
		private byte channel;

		public ByteMsg(byte channel, int channelvalue) {
			this.channelvalue = channelvalue;
			this.channel = channel;
		}

		public int getChannelValue() {
			return channelvalue;
		}
		public byte getChannel() {
			return channel;
		}
	}

	protected class RelayMsg {
		private int relayvalue;
		private byte relayattr;

		public RelayMsg(byte relayattr, int relayvalue) {
			this.relayvalue = relayvalue;
			this.relayattr = relayattr;
		}

		public int getRelayValue() {
			return relayvalue;
		}
		public byte getAttr() {
			return relayattr;
		}
	}
	
	protected class PortalNameMsg {
		private String portalname;

		public PortalNameMsg(String p) {
			this.portalname = p;
		}

		public String getPortalNameValue() {
			return portalname;
		}
	}
		
	protected class LightMsg {
		private int light;

		public LightMsg(int light) {
			this.light = light;
		}

		public int getLight() {
			return light;
		}
	}

	protected class JoyMsg {
		private int x;
		private int y;

		public JoyMsg(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}

	@SuppressLint("NewApi")
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						openAccessory(accessory);
					} else {
						Log.d(TAG, "permission denied for accessory "
								+ accessory);
					}
					mPermissionRequestPending = false;
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
				if (accessory != null && accessory.equals(mAccessory)) {
					closeAccessory();
					finish();
				}
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(mUsbReceiver, filter);

		if (getLastNonConfigurationInstance() != null) {
			mAccessory = (UsbAccessory) getLastNonConfigurationInstance();
			openAccessory(mAccessory);
		}
		Log.d(TAG, "Created");

		setContentView(R.layout.main);

		// Create TCP server (based on  MicroBridge LightWeight Server)
		try
		{
			server = new Server(4567); //Use the same port number used in ADK Main Board firmware
			if (server !=null) server.start();			
		} catch (IOException e)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Unable to start TCP server");
			AlertDialog alert = builder.create();
			alert.show();
			Log.e(TAG, "Unable to start TCP server", e);
//			System.exit(-1);
		}

		server.addListener(new AbstractServerListener() {

			@Override
			public void onReceive(org.microbridge.server.Client client, byte[] data)
			{
				if (data.length<3) return;
				buffer=data;
				//Any update to UI can not be carried out in a non UI thread like the one used
				//for Server. Hence runOnUIThread is used.
				runOnUiThread(new Runnable() {
					public void run() {
						if (!MicrobridgeLink)
						{
							MicrobridgeLink=true;
							enableControls(true);
						}
						new UpdateData().execute();
					}
				});

			}

		});	 
		
		if (!MicrobridgeLink) enableControls(false);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		if (mAccessory != null) {
			return mAccessory;
		} else {
			return super.onRetainNonConfigurationInstance();
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onResume() {
		super.onResume();

		if (mInputStream != null && mOutputStream != null) {
			return;
		}
		if (MicrobridgeLink) return;

		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) {
			if (mUsbManager.hasPermission(accessory)) {
				openAccessory(accessory);
			} else {
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						mUsbManager.requestPermission(accessory, mPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}
		} else {
			Log.d(TAG, "mAccessory is null");
		}
	}

	@Override
	public void onPause() {
		super.onPause();
//		closeAccessory();
	}

	@Override
	public void onDestroy() {
		closeAccessory();
		server.stop();
		server=null;
		unregisterReceiver(mUsbReceiver);
		DBUpdateTimer.cancel();
		Log.d(TAG,"Destroyed");		
		super.onDestroy();
	}

	@SuppressLint("NewApi")
	private void openAccessory(UsbAccessory accessory) {
		mFileDescriptor = mUsbManager.openAccessory(accessory);
		if (mFileDescriptor != null) {
			mAccessory = accessory;
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			mInputStream = new FileInputStream(fd);
			mOutputStream = new FileOutputStream(fd);
			Thread thread = new Thread(null, this, "Evolution");
			thread.start();
			Log.d(TAG, "accessory opened");
			enableControls(true);
		} else {
			Log.d(TAG, "accessory open fail");
		}
	}

	private void closeAccessory() {
		if (!MicrobridgeLink) enableControls(false);

		try {
			if (mFileDescriptor != null) {
				mFileDescriptor.close();
			}
		} catch (IOException e) {
		} finally {
			mFileDescriptor = null;
			mAccessory = null;
		}
	}

	protected void enableControls(boolean enable) {
	}

	private int composeInt(byte hi, byte lo) {
		int val = (int) hi & 0xff;
		val *= 256;
		val += (int) lo & 0xff;
		return val;
	}

	public void run() {
		int ret = 0;

		while (ret >= 0) {
			try {
				ret = mInputStream.read(buffer);
			} catch (IOException e) {
				break;
			}
			ProcessData(buffer);
		}
	}

	
	public void ProcessData(byte[] buffer)
	{
		int i;
		int ret = buffer.length;
		
		i = 0;
		while (i < ret) {
			int len = ret - i;

			switch (buffer[i]) {
			case Globals.T1_PROBE: case Globals.T2_PROBE: case Globals.T3_PROBE: case Globals.PH: case Globals.SALINITY: case Globals.ORP: case Globals.PHEXP: case Globals.WL: case Globals.EXPANSIONMODULES: case Globals.RELAYMODULES:
				if (len >= 3) {
					Message m = Message.obtain(mHandler, MESSAGE_PARAMS);
					m.obj = new ParamsMsg(buffer[i], composeInt(buffer[i + 1], buffer[i + 2]));
					mHandler.sendMessage(m);
				}
				i += 3;
				break;
			case Globals.ATO:
				if (len >= 3) {
					Message m = Message.obtain(mHandler, MESSAGE_SWITCH);
					m.obj = new SwitchMsg(buffer[i + 1], buffer[i + 2]);
					mHandler.sendMessage(m);
				}
				i += 3;
				break;
			case Globals.BYTEMSG:
				if (len >= 3) {
					Message m = Message.obtain(mHandler, MESSAGE_BYTE);
					m.obj = new ByteMsg(buffer[i + 1], buffer[i + 2]);
					mHandler.sendMessage(m);
				}
				i += 3;
				break;

			case Globals.RELAY:
				if (len >= 3) {
					Message m = Message.obtain(mHandler, MESSAGE_RELAY);
					m.obj = new RelayMsg(buffer[i + 1], buffer[i + 2]);
					mHandler.sendMessage(m);
				}
				i += 3;
				break;

			case Globals.REEFANGELID:
				if (len >= 3) {
					String reefangelid=new String(buffer);
					reefangelid=reefangelid.substring(1,reefangelid.length());
					Log.d(TAG, "Received ReefAngelID: " + reefangelid );
					Message m = Message.obtain(mHandler, MESSAGE_PORTAL);
					m.obj = new PortalNameMsg(reefangelid);
					mHandler.sendMessage(m);
				}
				i += buffer.length;
				break;
				
			default:
				Log.d(TAG, "unknown msg: " + buffer[i]);
				i = len;
				break;
			}
		}
		
	}
	
	class UpdateData extends AsyncTask<Void, Void, Void> {
		// Called to initiate the background activity
		protected Void doInBackground(Void... params) {
			ProcessData(buffer);
			return null;
		}

		// Called when there's a status to be updated
		protected void onProgressUpdate() {
			super.onProgressUpdate();
			// Not used in this case
		}

		// Called once the background activity has completed
		protected void onPostExecute() {
		}
	}
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_SWITCH:
				SwitchMsg o = (SwitchMsg) msg.obj;
				handleSwitchMessage(o);
				break;

			case MESSAGE_PARAMS:
				ParamsMsg t = (ParamsMsg) msg.obj;
				handleParamsMessage(t);
				break;

			case MESSAGE_BYTE:
				ByteMsg d = (ByteMsg) msg.obj;
				handleByteMessage(d);
				break;
				
			case MESSAGE_RELAY:
				RelayMsg r = (RelayMsg) msg.obj;
				handleRelayMessage(r);
				break;
			case MESSAGE_PORTAL:
				PortalNameMsg p = (PortalNameMsg) msg.obj;
				handlePortalNameMessage(p);
				break;
			}			
		}
	};

	public void sendCommand(byte command, byte target, int value) {
		byte[] buffer = new byte[3];
		if (value > 255)
			value = 255;

		buffer[0] = command;
		buffer[1] = target;
		buffer[2] = (byte) value;
		if (mOutputStream != null && buffer[1] != -1) {
			try {
				mOutputStream.write(buffer);
			} catch (IOException e) {
				Log.e(TAG, "write failed", e);
			}
		}
	}

	protected void handleJoyMessage(JoyMsg j) {
	}

	protected void handleByteMessage(ByteMsg d) {
	}
	
	protected void handleParamsMessage(ParamsMsg t) {
	}

	protected void handleSwitchMessage(SwitchMsg o) {
	}

	protected void handleRelayMessage(RelayMsg o) {
	}

	protected void handlePortalNameMessage(PortalNameMsg o) {
	}
	
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Globals.PICK_RF_MODE) {
			Message m = Message.obtain(mHandler, MESSAGE_BYTE);
			m.obj = new ByteMsg(Globals.RF_MODE, resultCode);
			mHandler.sendMessage(m);
        }
    }	

}
