package com.reefangel.evolution;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;

public class RelayButtonController implements android.view.View.OnClickListener {
	private final int mRelayNumber;
	private final byte mCommandTarget;
	private EvolutionActivity mActivity;
	private TextView mLabel;
	private TextView mOverride;
	private Button mButton;
	private Drawable mTopOffBackground;
	private Drawable mTopOnBackground;
	private Drawable mBottomOffBackground;
	private Drawable mBottomOnBackground;
	private int mPos;
	private int mState;
	private int mR;
	private int mRON;
	private int mROFF;
	

	public RelayButtonController(EvolutionActivity activity, int relayNumber, int pos,
			Resources res) {
		mActivity = activity;
		mRelayNumber = relayNumber;
		mPos=pos;
		mState=2;
		mR=0;
		mRON=0;
		mROFF=1;
		mCommandTarget = (byte) (relayNumber - 1);
		mTopOffBackground = res.getDrawable(R.drawable.relay_off1);
		mTopOnBackground = res.getDrawable(R.drawable.relay_on1);
		mBottomOffBackground = res.getDrawable(R.drawable.relay_off);
		mBottomOnBackground = res.getDrawable(R.drawable.relay_on);
	}

	public void attachToView(ViewGroup targetView) {
//		SpannableStringBuilder ssb = new SpannableStringBuilder("Relay");
//		ssb.append(String.valueOf(mRelayNumber));
//		ssb.setSpan(new SubscriptSpan(), 5, 6, 0);
//		ssb.setSpan(new RelativeSizeSpan(0.7f), 5, 6, 0);
		mLabel = (TextView) targetView.getChildAt(0);
		mLabel.setText("Relay " + mRelayNumber);
		mOverride = (TextView) targetView.getChildAt(2);
		mOverride.setText("");
		mButton = (Button) targetView.getChildAt(1);
		mButton.setOnClickListener(this);
//		mButton.setOnClickListener((android.view.View.OnClickListener) this);
//		mButton.setOnCheckedChangeListener(this);
		 
		OnLongClickListener listener = new OnLongClickListener() {
			public boolean onLongClick(View v) {
//				Dialog dialog = new Dialog(mActivity);
//				dialog.setContentView(R.layout.overridecontainer);
//				dialog.setTitle("Choose Mode:");
//				dialog.show();
				final CharSequence[] items = {"Off (Override)", "On (Override)", "Auto"};
				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
				builder.setTitle("Choose Mode:");
	        	 builder.setItems(items, new DialogInterface.OnClickListener() {
	        		 public void onClick(DialogInterface dialog, int item) {
	        			 mState= item;
	        			 updateView();
	        		 }
	        	 });
	        	 AlertDialog alert = builder.create();
	        	 alert.show();
				return true;
			}
		};
		mButton.setOnLongClickListener(listener);
		 
		 
		 
		}

//	public void setOnClickListener(CompoundButton arg0, boolean isChecked) {
//		mOverride.setText("Overridden");
//
//		if (mPos==0)
//		{
//			if (isChecked) {
//				mButton.setBackgroundDrawable(mBottomOnBackground);
//			} else {
//				mButton.setBackgroundDrawable(mBottomOffBackground);
//			}
//
//		}
//		else
//		{
//			if (isChecked) {
//				mButton.setBackgroundDrawable(mTopOnBackground);
//			} else {
//				mButton.setBackgroundDrawable(mTopOffBackground);
//			}
//
//		}
//		if (mActivity != null) {
//			mActivity.sendCommand(EvolutionActivity.RELAY_COMMAND,
//					mCommandTarget, isChecked ? 1 : 0);
//		}
//		
//	}

	public void onClick(View v) {
		switch (mState)
		{
		case 0:
			mState=1;
			break;
		case 1:
			mState=0;
			break;
		case 2:
			if (mR==0)
				mState=1;
			else
				mState=0;
			break;
		}
		updateView();
	}

	public int GetState() {
		return mState;
	}
	
	public void SetState(int R, int RON, int ROFF) {
		mState=2;
		mR= (((R&(1<<(mRelayNumber-1)))!=0) ?  1:0);
		mRON=(((RON&(1<<(mRelayNumber-1)))!=0) ?  1:0);
		mROFF=(((ROFF&(1<<(mRelayNumber-1)))!=0) ? 1:0);
		if (mRON==1) mState=1;
		if (mROFF==0) mState=0;
		updateView();
	}
	
	public void updateView(){
		switch (mState)
		{
		case 0:
			mOverride.setText("Overridden");
			if (mPos==0)
				mButton.setBackgroundDrawable(mBottomOffBackground);
			else
				mButton.setBackgroundDrawable(mTopOffBackground);
			break;
		case 1:
			mOverride.setText("Overridden");
			if (mPos==0)
				mButton.setBackgroundDrawable(mBottomOnBackground);
			else
				mButton.setBackgroundDrawable(mTopOnBackground);
			break;
		case 2:
			mOverride.setText("");
			if (mR==0)
			{
				if (mPos==0)
					mButton.setBackgroundDrawable(mBottomOffBackground);
				else
					mButton.setBackgroundDrawable(mTopOffBackground);			
			}
			else
			{
				if (mPos==0)
					mButton.setBackgroundDrawable(mBottomOnBackground);
				else
					mButton.setBackgroundDrawable(mTopOnBackground);				
			}
			break;
		}
	}
}
