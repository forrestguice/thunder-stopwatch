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
import com.forrestguice.thunderwatch.lib.recent.HistoryListView;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import android.view.ViewGroup;

import android.widget.TextView;

public class AnalogTimerView extends CollapsableLayout implements TimerView
{	
	private Handler displayHandler;
	private Runnable updateDisplayTask;
	
	private com.forrestguice.thunderwatch.lib.timer.AnalogClock clock;
	private float valueModifier = 1;
	
	public AnalogTimerView(Context context) 
	{
		super("analogtimer", context);
		final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_analogtimer, this);
		setMainContent((ViewGroup)findViewById(R.id.layout_mainContent));
	}
	
	public AnalogTimerView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_analogtimer, this);
		setMainContent((ViewGroup)findViewById(R.id.layout_mainContent));
	}
	
	private void initTimerView()
	{	
		displayHandler = new Handler();
		
		clock = (com.forrestguice.thunderwatch.lib.timer.AnalogClock)findViewById(R.id.analogClockWidget);
						
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
		
		//SingleLineTextView elapsedLabel = (SingleLineTextView)findViewById(R.id.label_distanceD);
	}

    @Override
    public void setActivity( Activity a )
    {
        super.setActivity(a);
        initTimerView();
    }
	
	@Override
	public void onRefreshDisplay()
	{
		ThunderClockApp app = (ThunderClockApp)myParent.getApplication();
		valueModifier = 1;
			
		if (app.distance_units.equals("ft"))
		{
			valueModifier = 5280;
			
		} else if (app.distance_units.equals("yd")) {
			valueModifier = 5280 / 3;
			
		} else if (app.distance_units.equals("m")) {
			valueModifier = 1000;
			
		} else if (app.distance_units.equals("km")) {
			valueModifier = 1;
			
		} else {  // mi
			valueModifier = 1;
		}
		
		/**switch (app.getUnits())
		{
		case ThunderClockApp.UNITS_FT:
			valueModifier = 5280;
			break;
			
		case ThunderClockApp.UNITS_YD:
			valueModifier = 5280 / 3;
			break;
			
		case ThunderClockApp.UNITS_M:
			valueModifier = 1000;
			break;
			
		default:
			valueModifier = 1;
			break;
		}*/
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
	//	//ThunderClockApp app = (ThunderClockApp)myParent.getApplication();
	//	//TextView unitsView = (TextView)findViewById(R.id.label_unitsD);
	//	//unitsView.setText(" " + app.distance_units_string);
	//}

	@Override
	public void setElapsed(long elapsed) 
	{
		ThunderClockApp app = (ThunderClockApp)myParent.getApplication();
				
		float decaseconds = elapsed / 100;
		long e = elapsed/1000L;
		double d = (e * app.speed_of_sound) / valueModifier;
		float d2 = (float)d * 25;
		
		clock.value_large = 0.6f * decaseconds;
		clock.value_small = -0.6f * d2;
				
		clock.changed = true;
		clock.invalidate();
		
		double elapsed2 = elapsed;
		double e2 = (double)elapsed2/1000.0d;
		double d3 = e2 * app.speed_of_sound;
		double ef = (double)Math.round(e2 * 10) / 10; 
		double df = (double)Math.round(d3 * 10) / 10;

		TextView view = (TextView)findViewById(R.id.label_elapsedA);
		view.setText(ef + "s");

		view = (TextView)findViewById(R.id.label_distanceA);
		view.setText(df + " " + app.distance_units);	
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
			long elapsed = (System.currentTimeMillis() - app.time_start);
			float decaseconds = elapsed / 100;
					
			double e0 = elapsed/1000L;
			double d0 = e0 * app.speed_of_sound / valueModifier;	
			float d2 = (float)d0 * 25;
			
			clock.value_large = 0.6f * decaseconds;
			clock.value_small = -0.6f * d2;
					
			clock.changed = true;
			clock.invalidate();
						
			double elapsed2 = System.currentTimeMillis() - app.time_start;
			double e = (double)elapsed2/1000.0d;
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

	public Handler getDisplayHandler()
	{
		return displayHandler;
	}
	
	@Override
	public Runnable getUpdateTask() 
	{
		if (updateDisplayTask != null) stopHandler();
		updateDisplayTask = new UpdateTask((ThunderClockApp)myParent.getApplication(), R.id.label_elapsedA, R.id.label_distanceA);
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
