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

package com.forrestguice.thunderwatch.lib.timer;

import com.forrestguice.android.CollapsableLayout;

import com.forrestguice.thunderwatch.lib.R;
import com.forrestguice.thunderwatch.lib.ThunderClockActivity;
import com.forrestguice.thunderwatch.lib.ThunderClockApp;

import android.content.Context;

import android.os.Handler;
import android.app.Activity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class DigitalTimerView extends CollapsableLayout implements TimerView
{	
	private Handler displayHandler;
	private Runnable updateDisplayTask;

	public DigitalTimerView(Context context) 
	{
		super("digitaltimer", context);
		final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_digitaltimer, this);
		setMainContent((ViewGroup)findViewById(R.id.layout_mainContent));
	}
	
	public DigitalTimerView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_digitaltimer, this);
		setMainContent((ViewGroup)findViewById(R.id.layout_mainContent));
	}

	public Handler getDisplayHandler()
	{
		return displayHandler;
	}

    @Override
    public void setActivity( Activity a )
    {
        super.setActivity(a);
        initTimerView();
    }

	private void initTimerView()
	{	
		displayHandler = new Handler();
		updateDisplayTask = new UpdateTask((ThunderClockApp)myParent.getApplication(), R.id.label_elapsedD, R.id.label_distanceD);

		/**TextView unitsLabel = (TextView)findViewById(R.id.label_unitsD);
		unitsLabel.setOnLongClickListener(new View.OnLongClickListener() 
		{		
			@Override
			public boolean onLongClick(View v) 
			{
				Intent i = ActivityUtil.createIntent(myParent, ThunderClockSettings.class);
				i.putExtra(Intent.EXTRA_TEXT, "units");
				myParent.startActivity(i);
				return false;
			}
		});*/
	}
	
	@Override
	public void onRefreshDisplay()
	{
	}
	
	//@Override
	//public void pauseWidget()
	//{
	//	super.pauseWidget();
	//}
	
	//@Override
	//public void restoreWidget()
	//{
	//	super.restoreWidget();
	//	
	//	ThunderClockApp app = (ThunderClockApp)myParent.getApplication();
	//	//TextView unitsView = (TextView)findViewById(R.id.label_unitsD);
	//	//unitsView.setText(" " + app.distance_units_string);
	//}

	@Override
	public void setElapsed(long elapsed) 
	{
		double e = (double)elapsed / 1000.0;
		double d = e * ((ThunderClockApp)myParent.getApplication()).speed_of_sound;
		double ef = (double)Math.round(e * 10) / 10;
		double df =  (double)Math.round(d * 10) / 10;
				
		TextView view = (TextView)findViewById(R.id.label_elapsedD);
		view.setText(ef + "s");

		view = (TextView)findViewById(R.id.label_distanceD);
		view.setText(df + " " + ((ThunderClockApp)myParent.getApplication()).distance_units);
	}
	
	protected class UpdateTask implements Runnable
	{
		private ThunderClockApp app;
		private TextView view1, view2;
		private StringBuilder builder, builder1;
				
		public UpdateTask(ThunderClockApp _app, int idElapsed, int idDistance)
		{
			app = _app;
			view1 = (TextView)myParent.findViewById(idElapsed);
			view2 = (TextView)myParent.findViewById(idDistance);
			builder = new StringBuilder(80);
			builder1 = new StringBuilder(80);
		}
		
		public void run()
		{
			double elapsed = System.currentTimeMillis() - app.time_start;
			double e = (double)elapsed/1000.0d;
			double d = e * app.speed_of_sound;
			double ef = (double)Math.round(e * 10) / 10; 
			double df = (double)Math.round(d * 10) / 10;
		   
			builder.append(ef);
			builder.append("s");
			builder1.append(df);
			builder1.append(" ");
			builder1.append(app.distance_units);
			view1.setText(builder.toString());
			view2.setText(builder1.toString());
			builder.delete(0, builder.length());
			builder1.delete(0, builder1.length());
			displayHandler.postDelayed(this, ThunderClockActivity.UPDATE_TIME);
		}
	}

	@Override
	public Runnable getUpdateTask() 
	{	
		if (updateDisplayTask != null)
        {
            stopHandler();
        }
		updateDisplayTask = new UpdateTask((ThunderClockApp)myParent.getApplication(), R.id.label_elapsedD, R.id.label_distanceD);
		return updateDisplayTask;
	}

	@Override
	public void startHandler() 
	{
		displayHandler.removeCallbacks(updateDisplayTask);   // update display
		displayHandler.postDelayed(updateDisplayTask, ThunderClockActivity.UPDATE_TIME);		
	}

	@Override
	public void stopHandler() 
	{
		displayHandler.removeCallbacks(updateDisplayTask);   // update display
	}
		
}
