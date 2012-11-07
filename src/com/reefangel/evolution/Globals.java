package com.reefangel.evolution;


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
	
}
