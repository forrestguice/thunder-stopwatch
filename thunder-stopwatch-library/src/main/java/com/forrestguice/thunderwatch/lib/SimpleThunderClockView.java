/**
 Copyright (C) 2012 Forrest Guice
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

import com.forrestguice.android.CollapsableLayout;
import com.forrestguice.thunderwatch.lib.timer.DigitalTimerView;
import com.forrestguice.thunderwatch.lib.timer.AnalogTimerView;
import com.forrestguice.thunderwatch.lib.timer.TimerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ToggleButton;
import android.view.View;

public class SimpleThunderClockView extends ThunderClockActivity
{
	public static final String DEFAULT_SETTING_TIMERVIEW = "analog";
			
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_view);
				
		AnalogTimerView aTimerView = (AnalogTimerView)findViewById(R.id.customAnalogClock);
		aTimerView.setTitle("Analog Mode");
		aTimerView.setMode(CollapsableLayout.MODE_OPEN);
		aTimerView.setVisibility(View.GONE);
		
		DigitalTimerView dTimerView = (DigitalTimerView)findViewById(R.id.digitaltimer);
		dTimerView.setTitle("Digital Mode");
		dTimerView.setMode(CollapsableLayout.MODE_OPEN);
		dTimerView.setVisibility(View.GONE);
				
		ToggleButton button = (ToggleButton)findViewById(R.id.button_trigger);
		button.setOnClickListener(this);
		button.setOnTouchListener(this);
	}
	
	public void setTimerView( TimerView v )
	{
		timerView = v;
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
		if (timerView != null) timerView.pauseWidget();
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
		//if (timerView != null) stopHandler();
		
		ThunderClockApp app = (ThunderClockApp)getApplication();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		ToggleButton triggerButton = (ToggleButton)findViewById(R.id.button_trigger);
		
		restoreTimer();		
								
		flag_quicktouch = settings.getBoolean("flag_quicktouch", DEFAULT_QUICKTOUCH);
		flag_camerabutton = settings.getBoolean("flag_camerabutton", DEFAULT_CAMERABUTTON);
						
		triggerButton.setChecked(app.isRunning());

		long elapsed = 0;
		if (app.isRunning())
		{
			elapsed = System.currentTimeMillis() - app.time_start;
			startHandler();

		} else {
			elapsed = app.time_stop - app.time_start;            // stop updater
			stopHandler();
		}

		updateLabels(elapsed);
		restoreComponents();
	}
	
	private void restoreTimer()
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		AnalogTimerView aTimerView = (AnalogTimerView)findViewById(R.id.customAnalogClock);
		DigitalTimerView dTimerView = (DigitalTimerView)findViewById(R.id.digitaltimer);

		String setting_timerview = settings.getString("timerview", DEFAULT_SETTING_TIMERVIEW);
		if (setting_timerview.equals("analog"))
		{			
			timerView = aTimerView;
			dTimerView.setVisibility(View.GONE);
			aTimerView.setVisibility(View.VISIBLE);
						
		} else {				
			timerView = dTimerView;	
			aTimerView.setVisibility(View.GONE);
			dTimerView.setVisibility(View.VISIBLE);			
		}
			
		displayHandler = timerView.getDisplayHandler();
		updateDisplayTask = timerView.getUpdateTask();
		timerView.restoreWidget();
	}
	
	public void startHandler()
	{
		timerView.startHandler();
	}
	
	public void stopHandler()
	{
		timerView.stopHandler();
	}
	
}
