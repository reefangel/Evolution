package com.reefangel.evolution;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.util.Log;

import com.androidplot.Plot.BorderStyle;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYStepMode;
import com.androidplot.series.XYSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * The simplest possible example of using AndroidPlot to plot some data.
 */
public class GraphActivity extends Activity
{ 
	private static final String TAG = "EvolutionGraphActivity";
	
	EvolutionDB dh; 
	int paramid=0;
	String paramlabel="Param";
	int[] seriesColor = {
			Color.rgb( 236, 142, 142), 
			Color.rgb( 236, 173, 117),
			Color.rgb( 198, 149, 219),
			Color.rgb( 162, 224, 165),
			Color.rgb( 138, 150, 214),
			Color.rgb( 160, 125, 92),
			Color.rgb( 83, 125, 82),
			Color.rgb( 156, 191, 218)
	};
	int[] seriesDecimal = {10,10,10,100,10,1,100,1};
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            paramid = extras.getInt("PARAM_ID");
            paramlabel = extras.getString("PARAM_LABEL");
        }        
        Log.d(TAG,"Param ID: "+paramid);
        Log.d(TAG,"Param Label: "+paramlabel);
		MultitouchPlot multitouchPlot = (MultitouchPlot) findViewById(R.id.multitouchPlot);

        dh = new EvolutionDB(this);
        List<String[]> series =null ;
        
        series=dh.selectAll();
        dh.finalize();
        Log.d(TAG,"DB Dize: "+series.size());
		Number[] Logdate = new Number[series.size()];
		
		Double[] PARAM = new Double[series.size()];

		int x=0;
		Double maxy=0.0;
		Double miny=10000.0;
		for (String[] row:series)
		{
			Logdate[x]=Integer.parseInt(row[0]);
			PARAM[x]=Double.parseDouble(row[paramid+1])/seriesDecimal[paramid];
			if (PARAM[x]<miny) miny=PARAM[x];
			if (PARAM[x]>maxy) maxy=PARAM[x];
			
			x++;
		}
        multitouchPlot.addSeries(new SimpleXYSeries(Arrays.asList(Logdate),Arrays.asList(PARAM),paramlabel), new LineAndPointFormatter(seriesColor[paramid],null,null));

        //		XYSeries series1 = new SimpleXYSeries(Arrays.asList(Logdate),Arrays.asList(T1),paramlabel);
		
        // Turn the above arrays into XYSeries:
//        XYSeries series1 = new SimpleXYSeries(
//                Arrays.asList(Logdate),          // SimpleXYSeries takes a List so turn our array into a List
//                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
//                paramlabel);                             // Set the display title of the series

		// Same as above, for series2
//        XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
//                "Series2");

        // Create a formatter to use for drawing a series using LineAndPointRenderer:
//        LineAndPointFormatter series1Format = new LineAndPointFormatter(
//                Color.rgb(0, 200, 0),                   // line color
//                null,                   // point color
//                null);              // fill color (optional)

        multitouchPlot.setDomainValueFormat(new Format() {
        	 
            // create a simple date format that draws on the year portion of our timestamp.
            // see http://download.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html
            // for a full description of SimpleDateFormat.
            private SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
 
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
 
                // because our timestamps are in seconds and SimpleDateFormat expects milliseconds
                // we multiply our timestamp by 1000:
                long timestamp = ((Number) obj).longValue() * 1000;
                Date date = new Date(timestamp);
                return dateFormat.format(date, toAppendTo, pos);
            }
 
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
 
            }

        });

        // Same as above, with series2:
//        multitouchPlot.addSeries(series2, new LineAndPointFormatter(Color.rgb(0, 0, 200), Color.rgb(0, 0, 100),
//                null));

        // Reduce the number of range labels
        multitouchPlot.setTicksPerRangeLabel(1);

        // By default, AndroidPlot displays developer guides to aid in laying out your plot.
        // To get rid of them call disableAllMarkup():
        multitouchPlot.disableAllMarkup();

		multitouchPlot.getTitleWidget().getLabelPaint().setColor(Color.BLACK);
		multitouchPlot.getLegendWidget().getTextPaint().setColor(Color.BLACK);
        multitouchPlot.setRangeBoundaries(miny.intValue()-2, maxy.intValue()+2, BoundaryMode.FIXED);
        multitouchPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, .5);
		multitouchPlot.setDomainBoundaries((new Date().getTime()/1000)-(3600*24), new Date().getTime()/1000, BoundaryMode.FIXED);
		multitouchPlot.setBorderStyle(BorderStyle.NONE, null, null);
		multitouchPlot.getBackgroundPaint().setColor(Color.BLACK);
		multitouchPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.rgb(230, 230, 255));
		multitouchPlot.getGraphWidget().getGridLinePaint().setColor(Color.rgb(170, 170, 170));
		multitouchPlot.getGraphWidget().getGridLinePaint().setPathEffect(new DashPathEffect(new float[]{2,2}, 1));
		multitouchPlot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
		multitouchPlot.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);
		multitouchPlot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
		multitouchPlot.getGraphWidget().getRangeOriginLabelPaint().setColor(Color.BLACK);
		multitouchPlot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
		multitouchPlot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
		multitouchPlot.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
		multitouchPlot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
    }
}