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

package com.forrestguice.thunderwatch.lib;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import android.preference.PreferenceManager;
import android.app.Dialog;
import android.content.SharedPreferences;

import android.view.Menu;
import android.view.MenuInflater;

import android.widget.ToggleButton;
import android.widget.TextView;

import com.forrestguice.android.CollapsableLayout;
import com.forrestguice.android.TimeUtility;

import com.forrestguice.thunderwatch.lib.graph.ThemedGraph;
import com.forrestguice.thunderwatch.lib.recent.HistoryListView;
import com.forrestguice.thunderwatch.lib.recent.TimeSinceLastView;

public class LogThunderClockView extends ThunderClockActivity
{	
    private HistoryListView list;
    private ThemedGraph graph;
    private TimeSinceLastView timesince;
    
    private java.text.DateFormat timeFormat;
    private int maxPadding;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_view);
		
		displayHandler = new Handler();
		updateDisplayTask = new UpdateTask((ThunderClockApp)getApplication(), R.id.label_elapsed, R.id.label_distance);
			
		timesince = (TimeSinceLastView)findViewById(R.id.history_timesincelast);
        timesince.setActivity(this);
		timesince.setTitle(getString(R.string.timesincearea_title));
				
		graph = (ThemedGraph)findViewById(R.id.history_graph);
        graph.setActivity(this);
		graph.setTitle(getString(R.string.grapharea_title));
		if (graph.landscape && (graph.screensize < Configuration.SCREENLAYOUT_SIZE_LARGE))
        {
            // ... because screen size in landscape is probably too small to display the graph, launch it as a dialog
            graph.setMode(CollapsableLayout.MODE_DIALOG);
        }
		
		list = (HistoryListView)findViewById(R.id.history_listview);
        list.setActivity(this);
		list.setMode(HistoryListView.MODE_OPEN);
		
		components.add(timesince);
		components.add(graph);
		components.add(list);
		
		timeFormat = TimeUtility.getTimeFormat(this);
				
		ToggleButton toggle = (ToggleButton)findViewById(R.id.button_trigger);
		toggle.setOnClickListener(this);            // attach listener
		toggle.setOnTouchListener(this);
	}

	@Override
	protected void onSaveInstanceState( Bundle outState )
	{
		super.onSaveInstanceState(outState);
		displayHandler.removeCallbacks(updateDisplayTask);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		displayHandler.removeCallbacks(updateDisplayTask);
	}

	@Override
	protected void onResume()
	{
  	    super.onResume();
		restoreState();
	}
	
	@Override
	public void restoreState()
	{
		ThunderClockApp app = (ThunderClockApp)getApplication();
		boolean is_running = app.isRunning();
			
		if (app.distance_units.equals("ft"))
		{
        	maxPadding = HistoryListView.DEFAULT_SPACES_FT; 
        	
		} else if (app.distance_units.equals("yd")) {
        	maxPadding = HistoryListView.DEFAULT_SPACES_YD;
			
		} else if (app.distance_units.equals("m")) {
        	maxPadding = HistoryListView.DEFAULT_SPACES_M;
			
		} else if (app.distance_units.equals("km")) {
        	maxPadding = HistoryListView.DEFAULT_SPACES_KM;
			
		} else {  // mi
			maxPadding = HistoryListView.DEFAULT_SPACES_MI;
		}
		
       /** switch (app.distance_units)
        {
        case ThunderClockApp.UNITS_FT:
        	maxPadding = HistoryListView.DEFAULT_SPACES_FT; 
        	break;	        	
        case ThunderClockApp.UNITS_YD:        	
        	maxPadding = HistoryListView.DEFAULT_SPACES_YD;
        	break;
        case ThunderClockApp.UNITS_M:
        	maxPadding = HistoryListView.DEFAULT_SPACES_M;
        	break;
        case ThunderClockApp.UNITS_KM:
        	maxPadding = HistoryListView.DEFAULT_SPACES_KM;
        	break;
        default:
        	maxPadding = HistoryListView.DEFAULT_SPACES_MI;
        	break;
        }*/
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		flag_quicktouch = settings.getBoolean("flag_quicktouch", DEFAULT_QUICKTOUCH);
		flag_camerabutton = settings.getBoolean("flag_camerabutton", DEFAULT_CAMERABUTTON);
				
		// set button state
		ToggleButton button = (ToggleButton)findViewById(R.id.button_trigger);
		button.setChecked(is_running);
		
		long elapsed = 0;
		if (is_running)
		{
			elapsed = System.currentTimeMillis() - app.time_start;

			displayHandler.removeCallbacks(updateDisplayTask);   // start updater
			displayHandler.postDelayed(updateDisplayTask, UPDATE_TIME);

			TextView view = (TextView)findViewById(R.id.label_start);
			view.setText(timeFormat.format(app.time_start));
			updateLabels(elapsed);

		} else {
			elapsed = app.time_stop - app.time_start;            // stop updater
			displayHandler.removeCallbacks(updateDisplayTask);

			TextView view = (TextView)findViewById(R.id.label_start);
			view.setText(getString(R.string.log_empty_start));
			
			view = (TextView)findViewById(R.id.label_elapsed);
			view.setText(R.string.log_empty_elapsed);
			
			view = (TextView)findViewById(R.id.label_distance);
			view.setText(getEmptyDistanceString());
		}
		
		restoreComponents();
	}
	
	private String getEmptyDistanceString()
	{
		ThunderClockApp app = (ThunderClockApp)getApplication();
		String emptyDistance = getString(R.string.log_empty_distance) + " " + app.distance_units;
		if (emptyDistance.length() <= maxPadding)
		{
			String padding = "";
			for (int i=emptyDistance.length(); i<=maxPadding; i++)
			{
				padding += " ";
			}
			emptyDistance = padding + emptyDistance;
		}
		return emptyDistance;
	}
		
	@Override
	public void refreshDisplay(String caller)
	{
		if (caller.equals("clear"))
		{
			// arrived here by clear action; next step is to restore display
			list.restoreWidget();
			graph.onRefreshDisplay();
			timesince.onRefreshDisplay();
			return;
		}
				
		super.refreshDisplay(caller);
	}
	
	@Override
	public void onTimerStart()
	{
		ThunderClockApp app = (ThunderClockApp)getApplication();
		TextView view = (TextView)findViewById(R.id.label_start);
		view.setText(timeFormat.format(app.time_start));
	}
	
	@Override 
	public void onTimerStop()
	{
		TextView view = (TextView)findViewById(R.id.label_start);
		view.setText(getString(R.string.log_empty_start));

		view = (TextView)findViewById(R.id.label_elapsed);
		view.setText(getString(R.string.log_empty_elapsed));

		view = (TextView)findViewById(R.id.label_distance);
		view.setText(getEmptyDistanceString());
		
		refreshComponents();
	}
		
	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_log_menu, menu);
		return true;
	}
		
	@Override
	protected Dialog onCreateDialog( int id )
	{
		Dialog dialog = super.onCreateDialog(id);  // let superclass try first
	
		if (dialog == null)                        // .. nothing was set so
		{										   // now try each component.
			for (CollapsableLayout component : components)
			{
				dialog = component.onCreateDialog(id);
				if (dialog != null) break;		   // dialog created; break loop
			}
		}
		
		return dialog;
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog)
	{	
		super.onPrepareDialog(id, dialog);
		for (CollapsableLayout component : components)
		{
			component.onPrepareDialog(id, dialog);
		}
	}
	
	/**
    A Runnable that updates the labels in this activity when the timer is 
    running.
	 */	
	public class UpdateTask implements Runnable
	{
		private ThunderClockApp app;
		private TextView view1, view2;
		private StringBuilder builder;
		private StringBuilder builder1;
		
		public UpdateTask(ThunderClockApp _app, int idElapsed, int idDistance)
		{
			app = _app;
			view1 = (TextView)findViewById(idElapsed);
			view2 = (TextView)findViewById(idDistance);
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
			
			while (builder1.length() <= maxPadding)
			{
				builder1.insert(0, " ");
			}
			
			view1.setText(builder.toString());
			view2.setText(builder1.toString());
			builder.delete(0, builder.length());
			builder1.delete(0, builder1.length());
			displayHandler.postDelayed(this, UPDATE_TIME);
		}
	}
	
}
