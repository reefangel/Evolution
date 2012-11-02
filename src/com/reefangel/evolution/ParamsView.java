package com.reefangel.evolution;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

public class ParamsView extends View implements OnClickListener {
	
	private static final String TAG = "EvolutionParamsView";

	private Context mContext;
	private Drawable mParamBackground;
	private int mParamID;
	private int mDecimal;
	
	private Paint mLabelPaint;
	private Paint mParamPaint;
	private String mLabelText;
	private int mParam;
	private DecimalFormat mFormat;

	public ParamsView(Context context) {
		super(context);
		initParamView(context);
		this.setOnClickListener(this);
	}

	public ParamsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initParamView(context);
		this.setOnClickListener(this);
	}

	public void setParam(int p) {
		mParam = p;
		invalidate();
	}

	public int getParam() {
		return mParam;
	}

	public void setFormat(DecimalFormat d) {
		mFormat = d;
	}

	public DecimalFormat getFormat() {
		return mFormat;
	}
	
	public void setDecimal(int d) {
		mDecimal = d;
	}

	public int getDecimal() {
		return mDecimal;
	}
	
	public void setParamID(int b) {
		mParamID=b;
		invalidate();
	}

	public void setLabel(String label) {
		mLabelText = label;
		invalidate();
	}	
	
	private void initParamView(Context context) {
		mContext=context;
		mParam=0;
		mLabelText="";
		mParamID=0;
		Resources r = context.getResources();
		mParamBackground = r.getDrawable(R.drawable.none_bk);
		int w = mParamBackground.getIntrinsicWidth();
		int h = mParamBackground.getIntrinsicHeight();
		mParamBackground.setBounds(0, 0, w, h);
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
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int w=mParamBackground.getIntrinsicWidth();
		int h=mParamBackground.getIntrinsicHeight();
		float scalew = (float) getWidth()/w;	
		canvas.save(); 
		canvas.scale(scalew, scalew, 0, 0); 
		int x =(w/2);
		canvas.drawText(mLabelText, x, h/2.5f, mLabelPaint);
		canvas.drawText(mFormat.format((double)mParam/mDecimal), x ,h*1.15f  , mParamPaint);
		canvas.restore(); 
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		float scalew = (float)MeasureSpec.getSize(widthMeasureSpec)/mParamBackground.getIntrinsicWidth();
		setMeasuredDimension(widthMeasureSpec, (int)(mParamBackground.getIntrinsicHeight()*scalew*1.5));
	}
	
	public void onClick(View v) {
		Intent intent = new Intent(mContext, GraphActivity.class);
		intent.putExtra("PARAM_ID", mParamID);
		intent.putExtra("PARAM_LABEL", mLabelText);
		mContext.startActivity(intent);
	}
}
