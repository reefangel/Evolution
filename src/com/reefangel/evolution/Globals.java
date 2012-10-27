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

	public static final byte DIMMING = 0x20;

	public static final byte RELAY = 0x30;

	public static final byte EXPANSIONMODULES = 0x70;

	public static final byte RELAYMODULES = 0x71;
	
	public static final byte REEFANGELID = 0x7f;


// Dimming Channels
	public static final byte DIMMING_CHANNEL0 = 0x0;
	public static final byte DIMMING_CHANNEL1 = 0x1;
	public static final byte DIMMING_CHANNEL2 = 0x2;
	public static final byte DIMMING_CHANNEL3 = 0x3;
	public static final byte DIMMING_CHANNEL4 = 0x4;
	public static final byte DIMMING_CHANNEL5 = 0x5;
	
	public static final byte DIMMING_DAYLIGHT = 0x6;
	public static final byte DIMMING_ACTINIC = 0x7;	

	public static final byte DIMMING_WATERLEVEL = 0x8;	

// Relay
	public static final byte RELAY_R = 0x0;
	public static final byte RELAY_RON = 0x1;
	public static final byte RELAY_ROFF = 0x2;

	public static final String PREFS_NAME = "EvolutionPrefs";
}
