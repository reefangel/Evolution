package com.reefangel.evolution;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnLongClickListener;

public class ProgressView extends View {
	
	private static final String TAG = "EvolutionProgressView";

	private EvolutionActivity mActivity;
	private Drawable mProgressBackground;
	private Drawable[] mProgressDrawables;
	private int mDrawableindex;
	private int mState;
	private int mOverrideMode;
	private int mChannel;
	
	private Bitmap background; // holds the cached static part
	
	private Drawable indicator;

	private int currentP;
	private int targetP;
	private Paint mLabelPaint;
	private Paint mOverriddenPaint;
	private String mPercentageText;
	private String mLabelText;
	private int mMode;
	private Rect srcRect;
	private Rect destRect;
	
	private float IndicatorVelocity = 0.0f;
	private float IndicatorAcceleration = 0.0f;
	private long lastIndicatorMoveTime = -1L;

	int w;
	int h;
	float scalew;	


	public ProgressView(Context context) {
		super(context);
		initProgressView(context);
	}

	public ProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initProgressView(context);
	}

	public int getPercentage() {
		return targetP;
	}
	
	public void setPercentage(int p) {
		targetP = p;
		invalidate();
	}

	public void setCurrentPercentage(int p) {
		currentP = p;
		invalidate();
	}

	public void setBarColor(int b) {
		mDrawableindex=b;
		invalidate();
	}

	public void setLabel(String label) {
		mLabelText = label;
		invalidate();
	}	

	public void setLabel(int label) {
		mLabelText = getResources().getString(label);
		invalidate();
	}	
	
	public void setPercentageText(String label) {
		mPercentageText = label;
		invalidate();
	}

	public void setMode(int mode) {
		mMode = mode;
		invalidate();
	}	

	public void setChannel(int channel) {
		mChannel = channel;
		invalidate();
	}

	public void setState(int state) {
		mState = state;
		invalidate();
	}	
	
	public void setOverrideMode(int mode) {
		mOverrideMode = mode;
		invalidate();
	}	
	
	private void initProgressView(Context context) {
		mActivity = (EvolutionActivity) context;
		OnLongClickListener listener = new OnLongClickListener() {
			public boolean onLongClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
				final ProgressView p = new ProgressView(mActivity);
				builder.setTitle("Override Channel?");
				builder.setMessage("Select Override %:");
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						mState= 1;
						setPercentage(p.getPercentage());
						SendCommand(Globals.DIMMING_COMMAND_OVERRIDE_STATE,(byte)item);
						invalidate();
					}
				});
				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						mState= 0;
						SendCommand(Globals.DIMMING_COMMAND_OVERRIDE_STATE,(byte)item);
						invalidate();
					}
				});
				p.setOverrideMode(1);
				p.setPercentage(currentP);
				p.setCurrentPercentage(currentP);
				p.setPercentageText(currentP+"%");
				p.setBarColor(mDrawableindex);
				p.setLabel(mLabelText);
				p.setScaleX(.9f);
				builder.setView(p);
				AlertDialog alert = builder.create();
				alert.show();
				invalidate();
				return true;
			}
		};
		this.setOnLongClickListener(listener);
		
//		OnClickListener clicklistener = new OnClickListener() {
//			public void onClick(View v) {
//				if (mState==1)
//				{
//					setPercentage(100);
//					invalidate();
//				}
//			}
//		};
//		this.setOnClickListener(clicklistener);
		
		mState=0;
		mChannel=0;
		mOverrideMode=0;
		currentP = 0;
		targetP = 0;
		mPercentageText="";
		mDrawableindex=0;
		mProgressDrawables=new Drawable[6];
		Resources r = context.getResources();
		mProgressDrawables[0]=r.getDrawable(R.drawable.daylight_bk);
		mProgressDrawables[1]=r.getDrawable(R.drawable.actinic_bk);
		mProgressDrawables[2]=r.getDrawable(R.drawable.bluechannel_bk);
		mProgressDrawables[3]=r.getDrawable(R.drawable.expchannel_bk);
		mProgressDrawables[4]=r.getDrawable(R.drawable.redchannel_bk);
		mProgressDrawables[5]=r.getDrawable(R.drawable.purplechannel_bk);
		mProgressBackground = r.getDrawable(R.drawable.blank_bk);
		indicator = r.getDrawable(R.drawable.indicator_red);
		w = mProgressBackground.getIntrinsicWidth();
		h = mProgressBackground.getIntrinsicHeight();
		mProgressBackground.setBounds(0, 0, w, h);
		mProgressDrawables[0].setBounds(0, 0, w, h);
		mProgressDrawables[1].setBounds(0, 0, w, h);
		mProgressDrawables[2].setBounds(0, 0, w, h);
		mProgressDrawables[3].setBounds(0, 0, w, h);
		mProgressDrawables[4].setBounds(0, 0, w, h);
		mProgressDrawables[5].setBounds(0, 0, w, h);
		mLabelPaint = new Paint();
		mLabelPaint.setColor(Color.BLACK);
		mLabelPaint.setTextSize(getResources().getDimension(R.dimen.dimmingpercentage)); 
		mLabelPaint.setAntiAlias(true);
		mLabelPaint.setShadowLayer(1, 1, 1, Color.LTGRAY);
		mOverriddenPaint = new Paint();
		mOverriddenPaint.setColor(Color.RED);
		mOverriddenPaint.setTextSize(getResources().getDimension(R.dimen.dimmingoverride)); 
		mOverriddenPaint.setAntiAlias(true);
		mOverriddenPaint.setShadowLayer(1, 1, 1, Color.BLACK);	
		mOverriddenPaint.setAlpha(80);
		srcRect = new Rect(0, 0, mProgressBackground.getIntrinsicWidth(), mProgressBackground.getIntrinsicHeight()); 
		destRect = new Rect(0, 0, mProgressBackground.getIntrinsicWidth()*2/3,mProgressBackground.getIntrinsicHeight());
		destRect.offset(mProgressBackground.getIntrinsicWidth()/3, 0); 
		setPercentage(0);
		setLabel("Channel");
		setMode(0);
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		Log.d(TAG, "Scaling Width: " + scalew);
//		Log.d(TAG, "Scaling Height: " + scaleh);
		scalew = (float) getWidth()/w;			
//		Log.d(TAG,"0%: "+ w/26);
//		Log.d(TAG,"100%: "+ (w-(w/26)));
//		Log.d(TAG,"0% Scaled: "+ (w/26)*scalew);
//		Log.d(TAG,"100% Scaled: "+ (w-(w/26))*scalew);
		
		if (mMode==0)
		{
			canvas.save(); 
			canvas.scale(scalew, scalew, 0, 0); 
			canvas.drawBitmap(background, 0, h*1.2f, null); 
			if (mState==1) canvas.drawText("Overridden", w*2/5, h*1.07f + mLabelPaint.getTextSize(), mOverriddenPaint);
			int x =(w/26) + (int)((currentP * (w-(w/12)) )/100);
			Utilities.centerAround(x,(int)(h*1.2f), indicator);
			indicator.draw(canvas);
			if (currentP<=50)
				canvas.drawText(mPercentageText, x + (w/26),h*1.15f + mLabelPaint.getTextSize() , mLabelPaint);
			else
				canvas.drawText(mPercentageText, x - (w/5.2f),h*1.15f + mLabelPaint.getTextSize() , mLabelPaint);
			canvas.drawText(mLabelText, 0, h, mLabelPaint);
		}
		else
		{
			canvas.save(); 
			canvas.scale(scalew, scalew, 0, 0); 
			canvas.drawBitmap(background,srcRect,destRect, null); 
			if (mState==1) canvas.drawText("Overridden", w*5/9, h*0.67f, mOverriddenPaint);
			int x = (int)(w/2.7f) + (int)((currentP * (w-(w/2.45f)) )/100);
			Utilities.centerAround(x,0, indicator);
			indicator.draw(canvas);
			if (currentP<=50)
				canvas.drawText(mPercentageText, x + (w/26), (h*.75f) , mLabelPaint);
			else
				canvas.drawText(mPercentageText, x - (w/5.2f), (h*.75f) , mLabelPaint);
			canvas.drawText(mLabelText, 0, (h*.75f), mLabelPaint);
		}
		canvas.restore(); 
		if (IndicatorNeedsToMove()) {
			moveIndicator();
		}			
		mPercentageText = String.format("%d", currentP);
		mPercentageText += " %";		
			
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		float scalew = (float)MeasureSpec.getSize(widthMeasureSpec)/mProgressBackground.getIntrinsicWidth();
		if (mMode==0)
		{		
			setMeasuredDimension(widthMeasureSpec, (int)(mProgressBackground.getIntrinsicHeight()*scalew*2.3));
		}
		else
		{
			setMeasuredDimension(widthMeasureSpec, (int)(mProgressBackground.getIntrinsicHeight()*scalew*1.1));
		}
	}	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.d(TAG, "Size changed to " + w + "x" + h);
		regenerateBackground();
	}	

	private void regenerateBackground() {
		// free the old bitmap
		if (background != null) {
			background.recycle();
		}
		background = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas backgroundCanvas = new Canvas(background);
		mProgressDrawables[mDrawableindex].draw(backgroundCanvas);
		
	}
	
	private boolean IndicatorNeedsToMove() {
		return Math.abs(currentP - targetP) > 0.01f;
	}	
	
	private void moveIndicator() {
		if (! IndicatorNeedsToMove()) {
			return;
		}
		
		if (lastIndicatorMoveTime != -1L) {
			long currentTime = System.currentTimeMillis();
			float delta = (currentTime - lastIndicatorMoveTime) / 1000.0f;

			float direction = Math.signum(IndicatorVelocity);
			if (Math.abs(IndicatorVelocity) < 90.0f) {
				IndicatorAcceleration = 3.0f * (targetP - currentP);
			} else {
				IndicatorAcceleration = 0.0f;
			}
			currentP += IndicatorVelocity * delta;
			IndicatorVelocity += IndicatorAcceleration * delta;
			if ((targetP - currentP) * direction < 0.01f * direction) {
				currentP = targetP;
				IndicatorVelocity = 0.0f;
				IndicatorAcceleration = 0.0f;
				lastIndicatorMoveTime = -1L;
			} else {
				lastIndicatorMoveTime = System.currentTimeMillis();				
			}
			Log.d(TAG, "Current %: " + currentP );
			invalidate();
		} else {
			lastIndicatorMoveTime = System.currentTimeMillis();
			moveIndicator();
		}
	}	
	
	public void SendCommand (byte cmd, byte state)
	{
		Log.d(TAG,"Dimming Command Sent: " + state);
		mActivity.sendCommand(cmd, (byte) mChannel, state);
		try {
			mActivity.server.send(new byte[] {cmd, (byte) mChannel, state});
		} catch (IOException e) {
			Log.e(TAG,e.getMessage());
			e.printStackTrace();
		}		
	}	

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if (mOverrideMode==1)
    	{
//	    	Log.d(TAG,"Override %: "+p);
	    	int p =(int)((event.getX()-((w/26)*scalew))*100/((w-(w/13))*scalew));
	    	p=p<0?0:p;
	    	p=p>100?100:p;
	    	currentP=p;
	    	targetP=p;
	    	invalidate();
	    	return true;
    	}
    	else
    		return super.onTouchEvent(event);    		

    }
}
