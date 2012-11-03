package com.reefangel.evolution;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.reefangel.evolution.InputController.SwitchDisplayer;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

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
		menu.add("Settings");
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
			mInputController.setByteMsg(Globals.DIMMING_ACTINIC1,41+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.DIMMING_DAYLIGHT1,84+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.DIMMING_ACTINIC2,95+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.DIMMING_DAYLIGHT2,37+new Random().nextInt(5));
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
			mInputController.setByteMsg(Globals.CUSTOM0,23+new Random().nextInt(5));
			mInputController.setByteMsg(Globals.CUSTOM1,79+new Random().nextInt(5));

		} else if (item.getTitle() == "Settings") {
			this.startActivityForResult( new Intent( this, SettingsActivity.class ),Globals.PICK_RF_MODE );
			return true;

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
				
				EvolutionDB dh; 
				dh = new EvolutionDB(getBaseContext());
				dh.insert(
						Integer.toString(mInputController.mTemperature1.getParam()),
						Integer.toString(mInputController.mTemperature2.getParam()),
						Integer.toString(mInputController.mTemperature3.getParam()),
						Integer.toString(mInputController.mpH.getParam()),
						Integer.toString(mInputController.mSalinity.getParam()),
						Integer.toString(mInputController.mOrp.getParam()),
						Integer.toString(mInputController.mpHExp.getParam()),
						Integer.toString(mInputController.mWaterLevel.getParam())
						);
				dh.finalize();
				Log.d(TAG,"Saved Data");
				
		        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences(Globals.PREFS_NAME, 0);
				String line = null;
				
				String url=Globals.PORTAL_SUBMIT+"?id="+sharedPreferences.getString("MYREEFANGELID", "test")+
						"&t1="+Integer.toString(mInputController.mTemperature1.getParam())+
						"&t2="+Integer.toString(mInputController.mTemperature2.getParam())+
						"&t3="+Integer.toString(mInputController.mTemperature3.getParam())+
						"&ph="+Integer.toString(mInputController.mpH.getParam())+
						"&sal="+Integer.toString(mInputController.mSalinity.getParam())+
						"&orp="+Integer.toString(mInputController.mOrp.getParam())+
						"&phe="+Integer.toString(mInputController.mpHExp.getParam())+
						"&wl="+Integer.toString(mInputController.mWaterLevel.getParam())+
						"&atolow="+Integer.toString(mInputController.getswitchState(0)==true?1:0) +
						"&atohigh="+Integer.toString(mInputController.getswitchState(1)==true?1:0) +
						"&pwma="+Integer.toString(mInputController.mActinicView1.getPercentage())+
						"&pwmd="+Integer.toString(mInputController.mDaylightView1.getPercentage())+
						"&pwma2="+Integer.toString(mInputController.mActinicView2.getPercentage())+
						"&pwmd2="+Integer.toString(mInputController.mDaylightView2.getPercentage())+
						"&em="+em+
						"&rem="+rem;
				for (int a=0;a<8;a++)
				{
					url+="&r" + (a+1) + "="+mInputController.Relay_R[a]+
							"&ron" + (a+1) + "="+mInputController.Relay_RON[a]+
							"&roff" + (a+1) + "="+mInputController.Relay_ROFF[a];
				}
				if (BigInteger.valueOf(em).testBit(0)) {
					for (int a=0;a<6;a++)
					{
						int resID = getResources().getIdentifier("channel"+a+"dimming", "id", "com.reefangel.evolution");
						ProgressView c = (ProgressView)findViewById(resID);
						url+="&pwme" + a + "="+c.getPercentage();
					}							
				}
				if (BigInteger.valueOf(em).testBit(1)) {
					String[] r={"rfw","rfrb","rfr","rfg","rfb","rfi"};
					for (int a=0;a<6;a++)
					{
						int resID = getResources().getIdentifier("radion"+a+"dimming", "id", "com.reefangel.evolution");
						ProgressView c = (ProgressView)findViewById(resID);
						url+="&"+ r[a] + "="+c.getPercentage();
					}		
					TextView t=(TextView)findViewById(R.id.RFModeValue);
					url+="&rfm="+t.getTag();
					t=(TextView)findViewById(R.id.RFSpeedValue);
					url+="&rfs="+t.getTag();
					t=(TextView)findViewById(R.id.RFDurationValue);
					url+="&rfd="+t.getTag();							
				}
				if (BigInteger.valueOf(em).testBit(2)) {
					ProgressView aiw = (ProgressView)findViewById(R.id.aiwhitedimming);
					url+="&aiw="+aiw.getPercentage();
					ProgressView aib = (ProgressView)findViewById(R.id.aibluedimming);
					url+="&aib="+aib.getPercentage();
					ProgressView airb = (ProgressView)findViewById(R.id.airoyalbluedimming);					
					url+="&airb="+airb.getPercentage();
				}
				if (BigInteger.valueOf(em).testBit(5)) {
					int mio=0;
					for (int a=Globals.IO_CHANNEL0;a<=Globals.IO_CHANNEL5;a++)
						if (mInputController.getswitchState(a))
							mio+=1<<(a-Globals.IO_CHANNEL0);
					url+="&io="+mio;
				}
				boolean checkcustom=false;
				for (int a=0;a<8;a++)
					if (c[a]!=0) checkcustom=true;
				if (checkcustom)
					for (int a=0;a<8;a++)
						url+="&c" + a + "="+c[a];
						
				try {
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(url);
					Log.d(TAG,"Portal URL: "+url);
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