package com.reefangel.evolution;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;

public class ProgressView extends View {
	
	private static final String TAG = "ProgressView";

	private Drawable mProgressBackground;
	private Drawable[] mProgressDrawables;
	private int mDrawableindex;
	
	private Bitmap background; // holds the cached static part
	
	private Drawable indicator;

	private int currentP;
	private int targetP;
	private Paint mLabelPaint;
	private String mPercentageText;
	private String mLabelText;
	private int mMode;
	private Rect srcRect;
	private Rect destRect;
	
	private float IndicatorVelocity = 0.0f;
	private float IndicatorAcceleration = 0.0f;
	private long lastIndicatorMoveTime = -1L;



	public ProgressView(Context context) {
		super(context);
		initProgressView(context);
	}

	public ProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initProgressView(context);
	}

	public void setPercentage(int p) {
		targetP = p;
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

	public void setMode(int mode) {
		mMode = mode;
		invalidate();
	}	
	
	private void initProgressView(Context context) {
		currentP = 0;
		targetP = 0;
		mPercentageText="";
		mDrawableindex=0;
		mProgressDrawables=new Drawable[6];
		Resources r = context.getResources();
		mProgressDrawables[0]=r.getDrawable(R.drawable.daylight_bk);
		mProgressDrawables[1]=r.getDrawable(R.drawable.actinic_bk);
		mProgressDrawables[2]=r.getDrawable(R.drawable.waterlevel_bk);
		mProgressDrawables[3]=r.getDrawable(R.drawable.expchannel_bk);
		mProgressDrawables[4]=r.getDrawable(R.drawable.redchannel_bk);
		mProgressDrawables[5]=r.getDrawable(R.drawable.purplechannel_bk);
		mProgressBackground = r.getDrawable(R.drawable.blank_bk);
		indicator = r.getDrawable(R.drawable.indicator_red);
		int w = mProgressBackground.getIntrinsicWidth();
		int h = mProgressBackground.getIntrinsicHeight();
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
		srcRect = new Rect(0, 0, mProgressBackground.getIntrinsicWidth(), mProgressBackground.getIntrinsicHeight()); 
		destRect = new Rect(0, 0, mProgressBackground.getIntrinsicWidth()*2/3,mProgressBackground.getIntrinsicHeight());
		destRect.offset(mProgressBackground.getIntrinsicWidth()/3, 5); 
		setPercentage(0);
		setLabel("Channel");
		setMode(0);
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		Log.d(TAG, "Scaling Width: " + scalew);
//		Log.d(TAG, "Scaling Height: " + scaleh);
		if (mMode==0)
		{
			int w=mProgressBackground.getIntrinsicWidth();
			int h=mProgressBackground.getIntrinsicHeight();
			float scalew = (float) getWidth()/w;	
			float scaleh = (float) getHeight()/(h*2.5f);	
			canvas.save(); 
			canvas.scale(scalew, scaleh, 0, 0); 
			canvas.drawBitmap(background, 0, h*1.5f, null); 
			int x = 20 + (int)((currentP * (w-50) )/100);
			Utilities.centerAround(x,(int)(h*1.5f), indicator);
			indicator.draw(canvas);
			if (currentP<=50)
				canvas.drawText(mPercentageText, x + 12,h*1.5f + mLabelPaint.getTextSize() , mLabelPaint);
			else
				canvas.drawText(mPercentageText, x - 110,h*1.5f + mLabelPaint.getTextSize() , mLabelPaint);
			canvas.drawText(mLabelText, 0, h, mLabelPaint);
		}
		else
		{
			int w=mProgressBackground.getIntrinsicWidth();
			int h=mProgressBackground.getIntrinsicHeight();
			float scalew = (float) getWidth()/w;	
			float scaleh = (float) getHeight()/(h*1.2f);	
			canvas.save(); 
			canvas.scale(scalew, scaleh, 0, 0); 
			canvas.drawBitmap(background,srcRect,destRect, null); 
			int x = 20+(int)((currentP * (((mProgressBackground.getIntrinsicWidth()*2/3))-40) )/100);
			x+=mProgressBackground.getIntrinsicWidth()/3;
			Utilities.centerAround(x,5, indicator);
			indicator.draw(canvas);
			if (currentP<=50)
				canvas.drawText(mPercentageText, x + 12, 2+mLabelPaint.getTextSize() , mLabelPaint);
			else
				canvas.drawText(mPercentageText, x - 110, 2+mLabelPaint.getTextSize() , mLabelPaint);
			canvas.drawText(mLabelText, 0, h, mLabelPaint);
		}
		canvas.restore(); 
		if (IndicatorNeedsToMove()) {
			moveIndicator();
		}			
		mPercentageText = String.format("%d", currentP);
		mPercentageText += " %";		
			
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
	
	

	
}
