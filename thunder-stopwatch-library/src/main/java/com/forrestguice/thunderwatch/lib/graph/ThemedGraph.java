/**
 Copyright (C) 2010 Forrest Guice
 This file is part of Thunder-Stopwatch.

 Thunder-Stopwatch is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Thunder-Stopwatch is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Thunder-Stopwatch.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.forrestguice.thunderwatch.lib.graph;

import com.forrestguice.android.CollapsableLayout;
import com.forrestguice.android.TimeUtility;
import com.forrestguice.thunderwatch.lib.*;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

import com.androidplot.Plot;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;

public class ThemedGraph extends CollapsableLayout implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener
{	
	public static final int DEFAULT_SETTING_MAX_ITEMS = 20;  // max 20 items
	public static final int DEFAULT_SETTING_MAX_TIME = 12;   // max 12 hours
	
	private PopulateViewTask task;
	
    private XYPlot xyPlot;
	private HistoryTimeSeries series1;
    private LineAndPointFormatter formatter;

    private java.text.DateFormat timeFormat;
	
	public ThemedGraph(Context context) 
	{
		super("graph", context);
		final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_graph, this);
		setMainContent((ViewGroup)findViewById(R.id.layout_mainContent));
		initGraph(context);
	}
	
	public ThemedGraph(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_graph, this);	
		setMainContent((ViewGroup)findViewById(R.id.layout_mainContent));
		initGraph(context);
	}
		
	public XYPlot getPlot()
	{
		return xyPlot;
	}
			
	private void initGraph(Context context)
	{
	    xyPlot = (XYPlot) findViewById(R.id.themedPlotWidget);
	    //xyPlot.disableAllMarkup();
	   	    
		int[] attrs = new int[] { R.attr.graphBackgroundColor,   // 0
				  				  R.attr.graphGridColor,         // 1
				  				  R.attr.graphDomainOriginColor, // 2
				  				  R.attr.graphRangeOriginColor,  // 3
				  				  R.attr.graphDomainLabelColor,  // 4
				  				  R.attr.graphRangeLabelColor,   // 5
				  				  R.attr.graphDomainTitleColor,  // 6
				  				  R.attr.graphRangeTitleColor,   // 7
				  				  R.attr.graphDomainOriginLabelColor, // 8 
				  				  R.attr.graphRangeOriginLabelColor,  // 9 
				  				  R.attr.graphLineColor,         // 10
				  				  R.attr.graphPointColor,        // 11
 				  				  R.attr.graphFillStartColor,    // 12
				  				  R.attr.graphFillEndColor };    // 13 
		TypedArray a = context.obtainStyledAttributes(attrs);
		int color_bg = a.getColor(0, Color.WHITE);
		int color_grid = a.getColor(1, Color.BLACK);
		int color_domain = a.getColor(2, Color.BLACK);
		int color_range = a.getColor(3, Color.BLACK);
		int color_domain_label = a.getColor(4, Color.BLACK);
		int color_range_label = a.getColor(5, Color.BLACK);
		int color_domain_title = a.getColor(6, Color.BLACK);
		int color_range_title = a.getColor(7, Color.BLACK);
		int color_domain_origin_label = a.getColor(8, Color.BLACK);
		int color_range_origin_label = a.getColor(9, Color.BLACK);
		int color_line = a.getColor(10, Color.BLACK);
		int color_point = a.getColor(11, Color.BLACK);
		int color_fill_start = a.getColor(12, Color.WHITE);
		int color_fill_end = a.getColor(13, Color.YELLOW);
		a.recycle();
         		
        // graph settings
	    xyPlot.getLegendWidget().setVisible(false);
	    xyPlot.getGraphWidget().setMarginTop(0);
	    xyPlot.getGraphWidget().setPaddingRight(2);
        xyPlot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
        xyPlot.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);

        // grid settings
        //xyPlot.getGraphWidget().getGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1,1}, 1));
        xyPlot.getGraphWidget().getDomainGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1,1}, 1));

        // color settings
        xyPlot.getGraphWidget().getGridBackgroundPaint().setColor(color_bg);

        //xyPlot.getGraphWidget().getGridLinePaint().setColor(color_grid);
        xyPlot.getGraphWidget().getDomainGridLinePaint().setColor(color_grid);

        xyPlot.getGraphWidget().getDomainOriginLinePaint().setColor(color_domain);
        xyPlot.getGraphWidget().getRangeOriginLinePaint().setColor(color_range);
        xyPlot.getGraphWidget().getDomainLabelPaint().setColor(color_domain_label);
        xyPlot.getGraphWidget().getRangeLabelPaint().setColor(color_range_label);
        xyPlot.getDomainLabelWidget().getLabelPaint().setColor(color_domain_title);
        xyPlot.getRangeLabelWidget().getLabelPaint().setColor(color_range_title);
        xyPlot.getGraphWidget().getDomainOriginLabelPaint().setColor(color_domain_origin_label);
        xyPlot.getGraphWidget().getRangeOriginLabelPaint().setColor(color_range_origin_label);
        
        // domain / range settings
	    xyPlot.setTicksPerRangeLabel(2);
        //xyPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 2);    
		xyPlot.setRangeBoundaries(0, 10, BoundaryMode.FIXED);
        xyPlot.setRangeValueFormat(new DecimalFormat("0.0"));
        xyPlot.setDomainLabel("");
        xyPlot.setRangeLabel("");
 
        // line settings
        Paint lineFill = new Paint();
        lineFill.setAlpha(200);  // setup our line fill paint to be a slightly transparent gradient:
        lineFill.setShader(new LinearGradient(0, 0, 0, 250, color_fill_start, color_fill_end, Shader.TileMode.MIRROR));

        PointLabelFormatter pointerLabelFormatter = new PointLabelFormatter(color_point);
        formatter = new LineAndPointFormatter(
                color_line,                   // line color
                color_point,                  // point color
                color_fill_start, pointerLabelFormatter);            // fill color
        formatter.setFillPaint(lineFill);
       
        xyPlot.setDomainValueFormat(new Format() 
        {        
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) 
            {
                // timestamps are seconds (multiply by 1000)
                long timestamp = ((Number)obj).longValue() * 1000;
                return timeFormat.format(timestamp, toAppendTo, pos);
            }
 
            @Override
            public Object parseObject(String source, ParsePosition pos) 
            {
                return null;
            }
        });
        
		xyPlot.setOnCreateContextMenuListener(this);
        xyPlot.setOnLongClickListener(new View.OnLongClickListener() 
        {			
			@Override
			public boolean onLongClick(View v) 
			{
				showContextMenu();
				return false;
			}
		});
	}
	
	@Override
	protected void onDialogPrepare(Dialog d)
	{	
		xyPlot.setVisibility(View.VISIBLE);
	}
	
	protected void onDialogDismiss(Dialog d) 
	{
	}
	
	protected void onDialogCancel(Dialog d) 
	{
		
	}
	
	@Override
	protected void onDialogInit(Dialog d)
	{
		xyPlot.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mainContent.removeView(xyPlot);
		d.setContentView(xyPlot);
	}
	
	@Override
	public void onRefreshDisplay()
	{
		if (task == null)
		{
			task = new PopulateViewTask();
			task.execute();	
			
		} else {
			task.restart = true;
		}
	}
	
	@Override
	public void restoreWidget()
	{
		timeFormat = TimeUtility.getTimeFormat(myParent, TimeUtility.STYLE_SHORT);
		super.restoreWidget();
	}
			
	/**
	 * PopulateViewTask   :   AsyncTask
	 */
	protected class PopulateViewTask extends AsyncTask<Void, ContentValues, Boolean> 
	{					
		public boolean restart = false;
		private HistoryXYDatasource data;
		private int maxItems = ThemedGraph.DEFAULT_SETTING_MAX_ITEMS;
		private long maxTime = ThemedGraph.DEFAULT_SETTING_MAX_TIME * 60 * 60; 
		private long lastTime;
		
		private boolean noItem = false;
		private boolean singleItem = false;
		private double singleValue = -1;
		
		@Override
		protected Boolean doInBackground(Void... params)
		{	
			ThunderClockApp app = (ThunderClockApp)myParent.getApplication();
			ThunderClockDbAdapter db = new ThunderClockDbAdapter(myParent.getApplicationContext());
			db.open();
			
			Cursor c = db.fetchAllEntries(maxItems);
			if (c == null)
			{
				db.close();
				return false;
			}
			
			int i = 0;
			long rawDate = -1L, rowId = -1L;
			double elapsed = 0, ei = 0, di = 0;  // ef = 0, df = 0;
			ContentValues v = null;
			
			c.moveToFirst();
			while (c.isAfterLast() == false)
			{				
				if (i >= maxItems) break;
				if (restart)
				{
					i = 0;
					c.moveToFirst();
					publishProgress(null);
					restart = false;
				}
												
				rowId = c.getLong(0);
				rawDate = Long.parseLong(c.getString(1));
				elapsed = Double.parseDouble(c.getString(2));
				
				if (lastTime == -1L) lastTime = rawDate;
				long deltaT = Math.abs(rawDate - lastTime);
				if (deltaT > maxTime) break;
				else lastTime = rawDate;
										
				ei = (double)elapsed/1000.0d;
				di = ei * app.speed_of_sound;
				//ef = (double)Math.round(ei * 10) / 10;
				//df = (double)Math.round(di * 10) / 10;
		
				v = new ContentValues();
				v.put("rowid", rowId);
				v.put("date", (double)(rawDate / 1000L));
				v.put("elapsed", elapsed);
				v.put("distance", di);
				publishProgress(v);
												
				i++;
				c.moveToNext();
			}
			
			if (i == 1)               // single item: duplicate entry
			{
				ContentValues v2 = new ContentValues();
				v2.put("rowid", rowId);
				v2.put("date", (double)(rawDate / 1000L)+60);
				v2.put("elapsed", elapsed);
				v2.put("distance", di+0.01);
				singleItem = true;
				singleValue = di;
				publishProgress(v2);
				
			} else if (i == 0) {      // no items
				noItem = true;
			}
			c.close();
			db.close();
			return true;
		}

		@Override
		protected void onProgressUpdate(ContentValues... progress) 
		{
			if (progress == null)
			{
				data.clearAll();

			} else {
				for (int i=0; i<progress.length; i++)
				{
					data.addItem(progress[i]);	
				}
			}
		}

		@Override
		protected void onPreExecute()
		{
			ThunderClockApp app = (ThunderClockApp)myParent.getApplication();
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(myParent);
			maxItems = settings.getInt("graph_dataset_size", DEFAULT_SETTING_MAX_ITEMS);
			maxTime = (settings.getInt("graph_dataset_bracket", DEFAULT_SETTING_MAX_TIME))*60*60*1000;
			lastTime = -1;
			
			String rangeLabel = app.distance_units;
			//rangeLabel = rangeLabel.substring(0, 1).toUpperCase() + rangeLabel.substring(1);
			
			data = new HistoryXYDatasource();
			xyPlot.setDomainStep(XYStepMode.SUBDIVIDE, 5);
			xyPlot.setRangeLabel(rangeLabel);
		}

		@Override
		protected void onPostExecute(Boolean result) 
		{
			if (series1 != null) xyPlot.removeSeries(series1);
			series1 = new HistoryTimeSeries(data, 0, "Distances");
			
			if (noItem)
			{
				xyPlot.setRangeBoundaries(0, 10, BoundaryMode.FIXED);
				xyPlot.setDomainBoundaries(System.currentTimeMillis() / 1000, (System.currentTimeMillis() / 1000) + 240, BoundaryMode.FIXED);
				
			} else if (singleItem) {
				xyPlot.setRangeBoundaries(singleValue/2, singleValue*2, BoundaryMode.FIXED);
				xyPlot.setDomainBoundaries(0, 1, BoundaryMode.AUTO);
								
			} else {
				xyPlot.setRangeBoundaries(0, 20, BoundaryMode.AUTO);
				xyPlot.setDomainBoundaries(0, 1, BoundaryMode.AUTO);
			}
			
	        xyPlot.addSeries(series1, formatter);
	        xyPlot.redraw();
			task = null;
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
	{
		MenuInflater inflater = myParent.getMenuInflater();
		inflater.inflate(R.menu.graph_menu, menu);

		for (int i=0; i<menu.size(); i++)
		{
			menu.getItem(i).setOnMenuItemClickListener(this);	
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item)
	{
		if (item.getItemId() == R.id.menu_item_graph_dataset)
		{			
			Intent i = ActivityUtil.createIntent(myParent, ThunderClockSettings.class);
			i.putExtra(Intent.EXTRA_TEXT, "graph_dataset_size");
			myParent.startActivity(i);
			
		} else if (item.getItemId() == R.id.menu_item_graph_bracket) {
			Intent i = ActivityUtil.createIntent(myParent, ThunderClockSettings.class);
			i.putExtra(Intent.EXTRA_TEXT, "graph_dataset_bracket");
			myParent.startActivity(i);
		}
			
		return false;
	}
		
}
