package com.reefangel.evolution;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class Globals {
	
// Message IDs
	public static final byte T1_PROBE = 0x1;
	public static final byte T2_PROBE = 0x2;
	public static final byte T3_PROBE = 0x3;
	public static final byte PH = 0x4;
	public static final byte SALINITY = 0x5;
	public static final byte ORP = 0x6;
	public static final byte PHEXP = 0x7;
	public static final byte WL = 0x8;
	
	public static final byte ATO = 0x10;

	public static final byte BYTEMSG = 0x20;

	public static final byte RELAY = 0x30;

	public static final byte EXPANSIONMODULES = 0x70;

	public static final byte RELAYMODULES = 0x71;
	
	public static final byte REEFANGELID = 0x7f;

// ATO 
	public static final byte LOW_ATO = 0x0;
	public static final byte HIGH_ATO = 0x1;
	
	public static final byte IO_CHANNEL0 = 0x2;
	public static final byte IO_CHANNEL1 = 0x3;
	public static final byte IO_CHANNEL2 = 0x4;
	public static final byte IO_CHANNEL3 = 0x5;
	public static final byte IO_CHANNEL4 = 0x6;
	public static final byte IO_CHANNEL5 = 0x7;

// Byte Messages
	public static final byte DIMMING_CHANNEL0 = 0x0;
	public static final byte DIMMING_CHANNEL1 = 0x1;
	public static final byte DIMMING_CHANNEL2 = 0x2;
	public static final byte DIMMING_CHANNEL3 = 0x3;
	public static final byte DIMMING_CHANNEL4 = 0x4;
	public static final byte DIMMING_CHANNEL5 = 0x5;
	
	public static final byte DIMMING_DAYLIGHT1 = 0x6;
	public static final byte DIMMING_ACTINIC1 = 0x7;	
	public static final byte DIMMING_DAYLIGHT2 = 0x8;
	public static final byte DIMMING_ACTINIC2 = 0x9;	

	public static final byte RF_MODE = 0xa;	
	public static final byte RF_SPEED = 0xb;	
	public static final byte RF_DURATION = 0xc;	

	public static final byte RF_RADION_WHITE = 0x10;	
	public static final byte RF_RADION_ROYAL_BLUE = 0x11;	
	public static final byte RF_RADION_RED = 0x12;	
	public static final byte RF_RADION_GREEN = 0x13;	
	public static final byte RF_RADION_BLUE = 0x14;	
	public static final byte RF_RADION_INTENSITY = 0x15;	
	
	public static final byte AI_WHITE = 0x20;	
	public static final byte AI_BLUE = 0x21;	
	public static final byte AI_ROYAL_BLUE = 0x22;
	
	public static final byte CUSTOM0 = 0x30;
	public static final byte CUSTOM1 = 0x31;
	public static final byte CUSTOM2 = 0x32;
	public static final byte CUSTOM3 = 0x33;
	public static final byte CUSTOM4 = 0x34;
	public static final byte CUSTOM5 = 0x35;
	public static final byte CUSTOM6 = 0x36;
	public static final byte CUSTOM7 = 0x37;	
	
// Relay
	public static final byte RELAY_R = 0x0;
	public static final byte RELAY_RON = 0x1;
	public static final byte RELAY_ROFF = 0x2;

// Commands
	public static final byte RELAY_COMMAND = 0x3;
	public static final byte DIMMING_COMMAND_OVERRIDE_STATE = 0x4;
	public static final byte RF_COMMAND_MODE = 0x4;
	public static final byte RF_COMMAND_SPEED = 0x4;
	public static final byte RF_COMMAND_DURATION = 0x4;
	public static final byte PORTAL_REQUEST_COMMAND = 0x7f;
	
// Activity Requests
	public static final int PICK_RF_MODE = 0;
	

// Strings	
	public static final String PREFS_NAME = "EvolutionPrefs";
	public static final String PORTAL_LABELS = "http://forum.reefangel.com/status/labels.aspx";
	public static final String PORTAL_SUBMIT = "http://forum.reefangel.com/status/submitp.aspx";
	public static final String PORTAL_ALERT = "http://www.reefangel.com/status/sendalert.asp";
	public static final String PORTAL_UPDATE_LABELS = "http://forum.reefangel.com/status/updatelabels.aspx";
	
	public static final long[] AlertFrequency={600000, 1800000, 3600000, 21600000, 86400000};
	
	public static final String[] DimmingPortalID={"PWME0N","PWME1N","PWME2N","PWME3N","PWME4N","PWME5N","PWMD1N","PWMA1N","PWMD2N","PWMA2N","RFWN","RFRBN","RFRN","RFGN","RFBN","RFIN","AIWN","AIBN","AIRBN"}; 
	public static final String[] DimmingDefaultLabel={"Channel 0","Channel 1","Channel 2","Channel 3","Channel 4","Channel 5","Daylight 1","Actinic 1","Daylight 2","Actinic 2","White","Royal Blue","Red","Green","Blue","Intensity","White","Blue","Royal Blue"}; 
	public static final String[] ParamsPortalID={"T1N","T2N","T3N","PHN","SALN","ORPN","PHEN","WLN"}; 
	public static final String[] ParamsDefaultLabel={"Temp 1","Temp 2","Temp 3","pH","Salinity","ORP","pH Exp","WL"}; 
	public static final String[] InputPortalID={"ATOLOWN","ATOHIGHN","IO0N","IO1N","IO2N","IO3N","IO4N","IO5N"}; 
	public static final String[] InputDefaultLabel={"Low ATO","High ATO","I/O Channel 0","I/O Channel 1","I/O Channel 2","I/O Channel 3","I/O Channel 4","I/O Channel 5"}; 
	
	public static String UpdateLabel(String ReefAngelID, String LabelID, String LabelText)
	{
		Log.d("EvolutionUpdateLabel","Trying to update");
		String updateurl=Globals.PORTAL_UPDATE_LABELS+"?id="+ReefAngelID;
		try {
			updateurl+=LabelID+URLEncoder.encode(LabelText, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String line = null;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(updateurl);
			Log.d("EvolutionUpdateLabel","Update Label URL: "+updateurl);
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
		Log.d("EvolutionUpdateLabel",line);
		return line;
	}
}
