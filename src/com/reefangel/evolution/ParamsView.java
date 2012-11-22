package com.reefangel.evolution;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.reefangel.evolution.ProgressView.PortalUpdateLabelTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class ParamsView extends View implements OnClickListener {
	
	private static final String TAG = "EvolutionParamsView";
	SharedPreferences prefs;
	
	private Context mContext;
	private Drawable mParamBackground;
	private int mParamID;
	private int mDecimal;
	
	private Paint mLabelPaint;
	private Paint mParamPaint;
	private String mLabelText;
	private int mParam;
	private DecimalFormat mFormat;
	private Date mLastAlert;
	
	public ParamsView(Context context) {
		super(context);
		initParamView(context);
		this.setOnClickListener(this);
		this.setOnLongClickListener(longlistener);
	}

	public ParamsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initParamView(context);
		this.setOnClickListener(this);
		this.setOnLongClickListener(longlistener);
	}

	public void setParam(int p) {
		mParam = p;
		CheckParamsAlerts();
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
	
	public void setLastAlert(Date d) {
		mLastAlert = d;
	}

	public Date getLastAlert() {
		return mLastAlert;
	}
	
	public void setDecimal(int d) {
		mDecimal = d;
	}

	public int getDecimal() {
		return mDecimal;
	}
	
	public void setParamID(int b) {
		mParamID=b;
		setLabel(prefs.getString(Globals.ParamsPortalID[mParamID],Globals.ParamsDefaultLabel[mParamID]));
		invalidate();
	}

	public void setLabel(String label) {
		mLabelText = label;
		invalidate();
	}	
	
	public String getLabel() {
		return mLabelText;
	}		
	private void initParamView(Context context) {
		mContext=context;
		prefs = mContext.getSharedPreferences(Globals.PREFS_NAME, 0);
		
		mParam=0;
		mLabelText="";
		mParamID=0;
		mDecimal=1;
		mFormat=new DecimalFormat();
		mLastAlert=new Date(new Date().getTime()-Globals.AlertFrequency[(int) prefs.getLong("AlertFrequency", 2)]);
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
	
	OnLongClickListener longlistener = new OnLongClickListener() {
		public boolean onLongClick(View v) {
//			final SharedPreferences sharedPreferences = mContext.getSharedPreferences(Globals.PREFS_NAME, 0);
//		    View view = LayoutInflater.from(mContext).inflate(R.layout.paramssettings, (ViewGroup) findViewById(R.id.inputContainer));
//			final EditText elt = (EditText)view.findViewById(R.id.ParamsLessThan);
//			final EditText egt = (EditText)view.findViewById(R.id.ParamsGreaterThan);
//			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//			getResources();
//			final int okid = Resources.getSystem().getIdentifier("ok", "string", "android");
//			final int cancelid = Resources.getSystem().getIdentifier("cancel", "string", "android");
//			builder.setTitle(R.string.app_name);
//			String s=getResources().getString(R.string.ParamSendEmailLabel);
//			s=s.replace("xxx", mLabelText);
//			builder.setMessage(s);
//			builder.setNegativeButton(getResources().getString(cancelid), null);
//			builder.setPositiveButton(getResources().getString(okid), new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int item) {
//					SharedPreferences.Editor editor = sharedPreferences.edit();
//					editor.putFloat("PARAMSLESSTHAN"+mParamID, Float.parseFloat(elt.getText().toString()));
//					editor.putFloat("PARAMSGREATERTHAN"+mParamID,Float.parseFloat(egt.getText().toString()));
//					editor.commit();
//				}
//			});
//		    builder.setView(view);
//		    elt.setText(Float.toString(sharedPreferences.getFloat("PARAMSLESSTHAN"+mParamID, 0)));
//		    egt.setText(Float.toString(sharedPreferences.getFloat("PARAMSGREATERTHAN"+mParamID,0)));	
//			AlertDialog alert = builder.create();
//			alert.show();
//			return true;
			View view = LayoutInflater.from(mContext).inflate(R.layout.paramssettings, (ViewGroup) findViewById(R.id.ParamsContainer));
			final EditText elt = (EditText)view.findViewById(R.id.ParamsLessThan);
			final EditText egt = (EditText)view.findViewById(R.id.ParamsGreaterThan);
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			final int okid = Resources.getSystem().getIdentifier("ok", "string", "android");
			final int cancelid = Resources.getSystem().getIdentifier("cancel", "string", "android");
			final EditText trl = (EditText) view.findViewById(R.id.ParamsCurrentLabel);
			trl.setText(mLabelText);
			
			builder.setTitle(R.string.app_name);
			String s=getResources().getString(R.string.ParamSendEmailLabel);
			s=s.replace("xxx", mLabelText);
			TextView t = (TextView) view.findViewById(R.id.ParamsSendAlertWhenLabel);
			t.setText(s);
			builder.setView(view);
		    elt.setText(Float.toString(prefs.getFloat("PARAMSLESSTHAN"+mParamID, 0)));
		    egt.setText(Float.toString(prefs.getFloat("PARAMSGREATERTHAN"+mParamID,0)));	
			
			builder.setNegativeButton(getResources().getString(cancelid), null);
			builder.setPositiveButton(getResources().getString(okid), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					SharedPreferences.Editor editor = prefs.edit();
					Log.d(TAG,trl.getText().toString());
					Log.d(TAG,mLabelText);
					if (!trl.getText().toString().equals(mLabelText))
					{
						editor.putString(Globals.ParamsPortalID[mParamID], trl.getText().toString());
						String params[] = new String[3];
						params[0]=prefs.getString("MYREEFANGELID", "");
						params[1]="&tag="+Globals.ParamsPortalID[mParamID]+"&value=";
						params[2]=trl.getText().toString();
						PortalUpdateLabelTask p = new PortalUpdateLabelTask();
						p.execute(params);
					}
					editor.putFloat("PARAMSLESSTHAN"+mParamID, Float.parseFloat(elt.getText().toString()));
					editor.putFloat("PARAMSGREATERTHAN"+mParamID,Float.parseFloat(egt.getText().toString()));
					editor.commit();
					
				editor.commit();
				}
			});
			final AlertDialog alert = builder.create();
			
			TabHost tabsports = (TabHost) view.findViewById(R.id.tabhostparams);
			tabsports.setup();

			TabSpec tspecports = tabsports.newTabSpec("Alerts");
			tspecports.setIndicator("Override");
			tspecports.setContent(R.id.ParamsAlerts);
			tabsports.addTab(tspecports);
			tabsports.setCurrentTabByTag("Alerts");

			tspecports = tabsports.newTabSpec("Settings");
			tspecports.setIndicator("Settings");
			tspecports.setContent(R.id.ParamsSettings);
			tabsports.addTab(tspecports);
			tabsports.setCurrentTabByTag("Settings");

			tabsports.setCurrentTabByTag("Alerts");
			
			alert.show();
		
			return true;			
		}
		
	};
	
	private void CheckParamsAlerts()
	{
		float ParamCheck= (float)mParam/mDecimal;
		float ParamLess=prefs.getFloat("PARAMSLESSTHAN"+mParamID,0f);
		float ParamGreater=prefs.getFloat("PARAMSGREATERTHAN"+mParamID,0f);
		if (new Date().getTime()-mLastAlert.getTime()>Globals.AlertFrequency[(int) prefs.getLong("AlertFrequency", 2)] )
		{
			if (ParamCheck < ParamLess)
			{
				SendAlert task = new SendAlert();
				task.execute(mLabelText + " Alert",ParamCheck + " is less than "+ParamLess);
				setLastAlert(new Date());
			}
			if (ParamCheck > ParamGreater && ParamGreater>0)
			{
				SendAlert task = new SendAlert();
				task.execute(mLabelText + " Alert",ParamCheck + " is greater than "+ParamGreater);
				setLastAlert(new Date());
			}
		}
		else
		{
			Log.d(TAG,"Alert not sent. Last Alert was less than " + Globals.AlertFrequency[(int) prefs.getLong("AlertFrequency", 2)]);
		}
	}	
	
	
	public class SendAlert extends AsyncTask<String, Integer, Integer> {

		protected Integer doInBackground(String... params) {
			Log.d(TAG,"Sending Alert: " + params[0]);
			Log.d(TAG,"Sending Alert: " + params[1]);
			String line = null;
			String url="";
			try {
				url="e="+URLEncoder.encode(prefs.getString("AlertEmail", ""), "utf-8");
				url+="&s="+URLEncoder.encode(params[0], "utf-8");
				url+="&b="+URLEncoder.encode(params[1], "utf-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			url=Globals.PORTAL_ALERT+"?"+url;

			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				Log.d(TAG,"Alert URL: "+url);
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
			return null;
		}
		protected void onProgressUpdate(Integer... values) {
		}

		protected void onPostExecute(Integer result) {
		}
	}		
	public class PortalUpdateLabelTask extends AsyncTask<String, String, Integer> {

		protected Integer doInBackground(String... params) {
			String values[] = new String[2];
			values[0]=Globals.UpdateLabel(params[0], params[1], params[2]);
			values[1]=params[2];
			publishProgress(values); 
			return null;
		}

		protected void onProgressUpdate(String... values) {
			if (values[0].equals("Label Updated"))
				mLabelText=values[1];
			invalidate();
			Log.d(TAG,"Portal Label Updated");
			Toast.makeText(mContext, values[0], Toast.LENGTH_SHORT).show();
		}

		protected void onPostExecute(Integer result) {
		}
	}	  	
}
