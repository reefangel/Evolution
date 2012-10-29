package com.reefangel.evolution;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;

public class ParamsView extends View {
	
	private static final String TAG = "EvolutionParamsView";

	private Drawable mParamBackground;
	private Drawable[] mParamDrawables;
	private int mDrawableindex;
	
	private Bitmap background; // holds the cached static part
	
	private Paint mLabelPaint;
	private Paint mParamPaint;
	private String mLabelText;
	private String mParamText;

	public ParamsView(Context context) {
		super(context);
		initParamView(context);
	}

	public ParamsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initParamView(context);
	}

	public void setParam(String p) {
		mParamText = p;
		invalidate();
	}
	
	public void setBoxColor(int b) {
		mDrawableindex=b;
		invalidate();
	}

	public void setLabel(String label) {
		mLabelText = label;
		invalidate();
	}	
	
	private void initParamView(Context context) {
		mParamText="";
		mLabelText="";
		mDrawableindex=0;
		mParamDrawables=new Drawable[8];
		Resources r = context.getResources();
		mParamDrawables[0]=r.getDrawable(R.drawable.temp1_bk);
		mParamDrawables[1]=r.getDrawable(R.drawable.temp2_bk);
		mParamDrawables[2]=r.getDrawable(R.drawable.temp3_bk);
		mParamDrawables[3]=r.getDrawable(R.drawable.ph_bk);
		mParamDrawables[4]=r.getDrawable(R.drawable.sal_bk);
		mParamDrawables[5]=r.getDrawable(R.drawable.orp_bk);
		mParamDrawables[6]=r.getDrawable(R.drawable.phexp_bk);
		mParamDrawables[7]=r.getDrawable(R.drawable.wl_bk);
		mParamBackground = r.getDrawable(R.drawable.none_bk);
		int w = mParamBackground.getIntrinsicWidth();
		int h = mParamBackground.getIntrinsicHeight();
		mParamBackground.setBounds(0, 0, w, h);
		mParamDrawables[0].setBounds(0, 0, w, h);
		mParamDrawables[1].setBounds(0, 0, w, h);
		mParamDrawables[2].setBounds(0, 0, w, h);
		mParamDrawables[3].setBounds(0, 0, w, h);
		mParamDrawables[4].setBounds(0, 0, w, h);
		mParamDrawables[5].setBounds(0, 0, w, h);
		mParamDrawables[6].setBounds(0, 0, w, h);
		mParamDrawables[7].setBounds(0, 0, w, h);
		mLabelPaint = new Paint();
		mLabelPaint.setColor(Color.WHITE);
		mLabelPaint.setTextSize(getResources().getDimension(R.dimen.ParamsLabel)); 
		mLabelPaint.setTextAlign(Paint.Align.CENTER);
		mLabelPaint.setAntiAlias(true);
		mLabelPaint.setShadowLayer(2, 2, 2, Color.BLACK);
		mParamPaint = new Paint();
		mParamPaint.setColor(Color.BLACK);
		mParamPaint.setTextSize(getResources().getDimension(R.dimen.ParamsValue)); 
		mParamPaint.setTextAlign(Paint.Align.CENTER);
		mParamPaint.setAntiAlias(true);
		mParamPaint.setShadowLayer(2, 2, 2, R.color.EvolutionPurple);
		
		setParam("");
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int w=mParamBackground.getIntrinsicWidth();
		int h=mParamBackground.getIntrinsicHeight();
		float scalew = (float) getWidth()/w;	
		canvas.save(); 
		canvas.scale(scalew, scalew, 0, 0); 
//		canvas.drawBitmap(background, 0, 0, null); 
		int x =(w/2);
		canvas.drawText(mLabelText, x, h/2.5f, mLabelPaint);
		canvas.drawText(mParamText, x ,h*1.15f  , mParamPaint);
		canvas.restore(); 
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		float scalew = (float)MeasureSpec.getSize(widthMeasureSpec)/mParamBackground.getIntrinsicWidth();
		setMeasuredDimension(widthMeasureSpec, (int)(mParamBackground.getIntrinsicHeight()*scalew*1.5));
	}	
	
//	@Override
//	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//		Log.d(TAG, "Size changed to " + w + "x" + h);
//		regenerateBackground();
//	}	
//
//	private void regenerateBackground() {
//		// free the old bitmap
//		if (background != null) {
//			background.recycle();
//		}
//		background = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
//		Canvas backgroundCanvas = new Canvas(background);
//		mParamDrawables[mDrawableindex].draw(backgroundCanvas);
//		
//	}
}
