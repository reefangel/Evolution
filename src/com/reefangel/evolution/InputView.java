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

import com.reefangel.evolution.ParamsView.PortalUpdateLabelTask;
import com.reefangel.evolution.ParamsView.SendAlert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class InputView extends View {
	
	private static final String TAG = "EvolutionInputView";
	SharedPreferences prefs;

	private Context mContext;
	private Drawable[] mInputBackground;
	private int mInputID;
	private boolean mLoading;
	
	private Bitmap background; // holds the cached static part
	
	private Paint mInputPaint;
	private String mLabelText;
	private int mInput;
	private Date mLastAlert;
	
	public InputView(Context context) {
		super(context);
		initParamView(context);
		this.setOnLongClickListener(longlistener);
	}

	public InputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initParamView(context);
		this.setOnLongClickListener(longlistener);
	}

	public void setState(int p) {
		mInput = p;
		CheckInputAlerts();
		invalidate();
	}

	public int getState() {
		return mInput;
	}
	
	public void setLastAlert(Date d) {
		mLastAlert = d;
	}

	public Date getLastAlert() {
		return mLastAlert;
	}
	
	public void setInputID(int b) {
		mInputID=b;
		setLabel(prefs.getString(Globals.InputPortalID[mInputID],Globals.InputDefaultLabel[mInputID]));
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
		
		mInput=0;
		mLabelText="Input";
		mInputID=0;
		mLoading=false;		
		mLastAlert=new Date(new Date().getTime()-84600000);
		Resources r = context.getResources();
		mInputBackground = new Drawable[2];
		mInputBackground[0] = r.getDrawable(R.drawable.ato_off);
		mInputBackground[1] = r.getDrawable(R.drawable.ato_on);
		int w = mInputBackground[0].getIntrinsicWidth();
		int h = mInputBackground[0].getIntrinsicHeight();
		mInputBackground[0].setBounds(0, 0, w, h);
		mInputBackground[1].setBounds(0, 0, w, h);
		mInputPaint = new Paint();
		mInputPaint.setColor(Color.BLACK);
		mInputPaint.setTextSize(getResources().getDimension(R.dimen.InputValue)); 
		mInputPaint.setTextAlign(Paint.Align.LEFT);
		mInputPaint.setAntiAlias(true);
		mInputPaint.setShadowLayer(2, 2, 2, R.color.EvolutionPurple);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int w=mInputBackground[0].getIntrinsicWidth();

		canvas.drawBitmap(background, 0, 0, null); 
		if (mLoading)
			canvas.drawText("Saving...", w+10, mInputPaint.getTextSize()+3, mInputPaint);
		else
			canvas.drawText(mLabelText, w+10, mInputPaint.getTextSize()+3, mInputPaint);

	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int minheight = (MeasureSpec.getSize(widthMeasureSpec)/6);
		if (minheight<mInputBackground[0].getIntrinsicHeight()) minheight=mInputBackground[0].getIntrinsicHeight()+5;
		if (MeasureSpec.getSize(widthMeasureSpec)<MeasureSpec.getSize(heightMeasureSpec)*6)
			setMeasuredDimension(widthMeasureSpec, minheight);
		else
			setMeasuredDimension(MeasureSpec.getSize(heightMeasureSpec)*6, MeasureSpec.getSize(heightMeasureSpec));
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//		Log.d(TAG, "Size changed to " + w + "x" + h);
		regenerateBackground();
	}	

	private void regenerateBackground() {
		// free the old bitmap
		if (background != null) {
			background.recycle();
		}
		background = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas backgroundCanvas = new Canvas(background);
		mInputBackground[mInput].draw(backgroundCanvas);
	}	
	
	OnLongClickListener longlistener = new OnLongClickListener() {
		public boolean onLongClick(View v) {
			View view = LayoutInflater.from(mContext).inflate(R.layout.inputsettings, (ViewGroup) findViewById(R.id.inputContainer));
			final RadioButton rif = (RadioButton)view.findViewById(R.id.InputOff);
			final RadioButton rin = (RadioButton)view.findViewById(R.id.InputOn);
			final RadioButton rid = (RadioButton)view.findViewById(R.id.InputDisabled);
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			final int okid = Resources.getSystem().getIdentifier("ok", "string", "android");
			final int cancelid = Resources.getSystem().getIdentifier("cancel", "string", "android");
			final EditText trl = (EditText) view.findViewById(R.id.ParamsCurrentLabel);
			trl.setText(mLabelText);
			
			builder.setTitle(R.string.app_name);
			String s=getResources().getString(R.string.ParamSendEmailLabel);
			s=s.replace("xxx", mLabelText);
			TextView t = (TextView) view.findViewById(R.id.InputSendAlertWhenLabel);
			t.setText(s);
			builder.setView(view);
		    int i=prefs.getInt("INPUTIS"+mInputID, -1);
		    if (i==0) rif.setChecked(true);
		    if (i==1) rin.setChecked(true);
		    if (i==2) rid.setChecked(true);			
			builder.setNegativeButton(getResources().getString(cancelid), null);
			builder.setPositiveButton(getResources().getString(okid), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					SharedPreferences.Editor editor = prefs.edit();
					Log.d(TAG,trl.getText().toString());
					Log.d(TAG,mLabelText);
					if (!trl.getText().toString().equals(mLabelText))
					{
						editor.putString(Globals.InputPortalID[mInputID], trl.getText().toString());
						String params[] = new String[3];
						params[0]=prefs.getString("MYREEFANGELID", "");
						params[1]="&tag="+Globals.InputPortalID[mInputID]+"&value=";
						params[2]=trl.getText().toString();
						mLoading=true;
						invalidate();							
						PortalUpdateLabelTask p = new PortalUpdateLabelTask();
						p.execute(params);
					}
					if (rif.isChecked()) editor.putInt("INPUTIS"+mInputID, 0 );
					if (rin.isChecked()) editor.putInt("INPUTIS"+mInputID, 1 );
					if (rid.isChecked()) editor.putInt("INPUTIS"+mInputID, 2 );
					editor.commit();
				}
			});
			final AlertDialog alert = builder.create();
			
			TabHost tabsports = (TabHost) view.findViewById(R.id.tabhostinput);
			tabsports.setup();

			TabSpec tspecports = tabsports.newTabSpec("Alerts");
			tspecports.setIndicator("Override");
			tspecports.setContent(R.id.InputAlerts);
			tabsports.addTab(tspecports);
			tabsports.setCurrentTabByTag("Alerts");

			tspecports = tabsports.newTabSpec("Settings");
			tspecports.setIndicator("Settings");
			tspecports.setContent(R.id.InputSettings);
			tabsports.addTab(tspecports);
			tabsports.setCurrentTabByTag("Settings");

			tabsports.setCurrentTabByTag("Alerts");
			
			alert.show();
		
			return true;			
		}
		
	};
	
	private void CheckInputAlerts()
	{
//		float ParamCheck= (float)mParam/mDecimal;
//		float ParamLess=prefs.getFloat("PARAMSLESSTHAN"+mParamID,0f);
//		float ParamGreater=prefs.getFloat("PARAMSGREATERTHAN"+mParamID,0f);
		String[] OnOffState={"Off","On"};
		if (new Date().getTime()-mLastAlert.getTime()>Globals.AlertFrequency[(int) prefs.getLong("AlertFrequency", 2)] )
		{
			if (mInput == prefs.getInt("INPUTIS"+mInputID, -1))
			{
				SendAlert task = new SendAlert();
				task.execute(mLabelText + " Alert",mLabelText + " is "+OnOffState[mInput]);
				setLastAlert(new Date());
			}
		}
		else
		{
			Log.d(TAG,"Alert not sent. Last Alert was less than 1hr.");
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
			mLoading=false;
			invalidate();
			Log.d(TAG,"Portal Label Updated");
			Toast.makeText(mContext, values[0], Toast.LENGTH_SHORT).show();
		}

		protected void onPostExecute(Integer result) {
		}
	}	 	
}
