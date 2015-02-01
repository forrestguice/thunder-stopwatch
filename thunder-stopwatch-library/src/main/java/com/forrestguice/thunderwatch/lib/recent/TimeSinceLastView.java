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

package com.forrestguice.thunderwatch.lib.recent;

import com.forrestguice.android.CollapsableLayout;
import com.forrestguice.android.TimeUtility;

import com.forrestguice.thunderwatch.lib.R;
import com.forrestguice.thunderwatch.lib.ThunderClockApp;
import com.forrestguice.thunderwatch.lib.ThunderClockDbAdapter;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class TimeSinceLastView extends CollapsableLayout
{	
	protected Handler displayHandler;
	public static final int UPDATE_TIME = 1000*15;
	protected TimeSinceLastView.DisplayUpdateTask updateDisplayTask;
    private java.text.DateFormat timeFormat;
	
	private long startTime = -1L;
	
	public TimeSinceLastView(Context context) 
	{
		super("sincelast", context);
		final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_timesincelast, this);
		displayHandler = new Handler();
		updateDisplayTask = new TimeSinceLastView.DisplayUpdateTask();
		setMainContent((ViewGroup)findViewById(R.id.layout_mainContent));
	}
	
	public TimeSinceLastView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_timesincelast, this);
		displayHandler = new Handler();
		updateDisplayTask = new DisplayUpdateTask();
		setMainContent((ViewGroup)findViewById(R.id.layout_mainContent));
	}
	
	@Override
	public void onRefreshDisplay()
	{
		new RefreshViewTask().execute();
	}
	
	@Override
	public void pauseWidget()
	{
		displayHandler.removeCallbacks(updateDisplayTask);
	}
	
	@Override
	public void restoreWidget()
	{
		super.restoreWidget();
		timeFormat = TimeUtility.getTimeFormat(myParent);
		displayHandler.removeCallbacks(updateDisplayTask);
		displayHandler.postDelayed(updateDisplayTask, UPDATE_TIME);
	}
			
	public TextView getTextView()
	{
		TextView txt_timeSinceLast = (TextView)findViewById(R.id.timesincelast_value);
		return txt_timeSinceLast;
	}
	
	public void setLastTime(long _startTime)
	{		
		startTime = _startTime;
		updateLastTime();
		
		if (startTime != -1L)
		{
			displayHandler.removeCallbacks(updateDisplayTask);
			displayHandler.postDelayed(updateDisplayTask, UPDATE_TIME);
		}
	}
	
	public void updateLastTime()
	{
		TextView txt_timeSinceLast = (TextView)findViewById(R.id.timesincelast_value);
		
		if (startTime != -1L)
		{ 
			if (landscape)
			{
				java.text.DateFormat dateFormat = TimeUtility.getDateFormat(myParent);
				txt_timeSinceLast.setText(shortDescription(startTime) + "\n" + timeFormat.format(startTime) + ", " + dateFormat.format(startTime));
			} else {
				txt_timeSinceLast.setText(shortDescription(startTime) + "\n" + timeFormat.format(startTime) + ", " + DateUtils.formatDateTime((Context)myParent, startTime, ThunderClockApp.DATEFORMAT_FULLABRV));
			}
			
		} else {
			txt_timeSinceLast.setText(" \n ");
		}
	}
	
	private String shortDescription(long startTime)
	{
		long span = System.currentTimeMillis() - startTime;
		GregorianCalendar c = new GregorianCalendar();
		c.setTimeInMillis(span);
				
		long timeInMillis = c.getTimeInMillis();
		long numberOfYears = timeInMillis / (1000*60*60*24*365);
		long numberOfWeeks = timeInMillis / (1000*60*60*24*7);
		long numberOfDays = timeInMillis / (1000*60*60*24);
		long numberOfHours = timeInMillis / (1000*60*60);
		long numberOfMinutes = timeInMillis / (1000*60);
		
		String r = "";
		if (numberOfYears >= 100) r += numberOfYears + ((numberOfYears >= 200) ?
				  " centuries ago" : " century ago"); // for the app that ends up on a device in a museum
		
		else if (numberOfYears >= 10) r += numberOfYears + ((numberOfYears >= 20) ?
					 				  " decades ago" : " decade ago"); // for the app gets installed on a device that is then put in a shoebox for 20 years.
		
		else if (numberOfYears > 0) r += numberOfYears + ((numberOfYears > 1) ?
									     " years ago" : " year ago");
		
		else if (numberOfWeeks > 0) r += numberOfWeeks + ((numberOfWeeks > 1) ?
										 " weeks ago" : " week ago");
		
		else if (numberOfDays > 0) r += numberOfDays + ((numberOfDays > 1) ?
										" days ago" : " day ago");
		
		else if (numberOfHours > 0) r += numberOfHours + ((numberOfHours > 1) ?
										 " hours ago" : " hour ago");
		
		else if (numberOfMinutes > 0) r += numberOfMinutes + ((numberOfMinutes > 1) ?
										   " minutes ago" : " minute ago");
				
		else r = "Moments ago";
		return r;
	}
	
	/**
	 * DisplayUpdateTask : Runnable
	 */
	protected class DisplayUpdateTask implements Runnable
	{
		public DisplayUpdateTask()	{}
		
		public void run()
		{
			updateLastTime();
			if (startTime != -1L)
			{
				displayHandler.removeCallbacks(updateDisplayTask);
				displayHandler.postDelayed(updateDisplayTask, UPDATE_TIME);
			}	
		}
	}

	/**
	 * RefreshViewTask   :   AsyncTask
	 */
	protected class RefreshViewTask extends AsyncTask<Void, ArrayList<Long>, Boolean> 
	{
		@Override
		protected Boolean doInBackground(Void... params)
		{	
			ThunderClockDbAdapter db = new ThunderClockDbAdapter(myParent.getApplicationContext());
			db.open();

			Cursor cursor = db.fetchLastEntry();
			if (cursor == null)
			{
				db.close();  	   // cursor request failed
				
				ArrayList<Long> item = new ArrayList<Long>();
				item.add(-1L);
	   		    publishProgress(item);
				return false;
			}
					
			cursor.moveToFirst();  // db connected - collect data
			long rawDate = Long.parseLong(cursor.getString(1));
			Long elapsed = Long.parseLong(cursor.getString(2));

			ArrayList<Long> item = new ArrayList<Long>();
			item.add(rawDate);
			item.add(elapsed);
   		    publishProgress(item);
			
   		    cursor.close();			
			db.close();
			return true;
		}

		@Override
		protected void onProgressUpdate(ArrayList<Long>... progress) 
		{
			setLastTime(progress[0].get(0));
		}

		@Override
		protected void onPreExecute()
		{
		}

		@Override
		protected void onPostExecute(Boolean result) 
		{
			if (result)
			{   // restart the display handler
				displayHandler.removeCallbacks(updateDisplayTask);
				displayHandler.postDelayed(updateDisplayTask, UPDATE_TIME);
			}
		}
	}
	
}
