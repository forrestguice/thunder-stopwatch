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

import android.app.Activity;
import android.os.Handler;
import android.os.Bundle;

import android.widget.ToggleButton;
import java.util.ArrayList;

import com.forrestguice.android.CollapsableLayout;
import com.forrestguice.android.CollapsableLayoutContainer;
import com.forrestguice.thunderwatch.lib.timer.TimerView;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.app.Dialog;
import android.content.Intent;

public class ThunderClockActivity extends Activity implements View.OnClickListener, View.OnTouchListener, CollapsableLayoutContainer
{
	protected ArrayList<CollapsableLayout> components = new ArrayList<CollapsableLayout>();
		
	public static final boolean DEFAULT_QUICKTOUCH = true;
	public static final boolean DEFAULT_CAMERABUTTON = true;
	
	protected boolean flag_quicktouch = DEFAULT_QUICKTOUCH;
	protected boolean flag_camerabutton = DEFAULT_CAMERABUTTON;
	
	protected TimerView timerView;
	
	public static final int UPDATE_TIME = 99;
	protected Handler displayHandler;
	protected Runnable updateDisplayTask;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState( Bundle outState )
	{
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		pauseComponents();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_simple_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu( Menu menu )
	{
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) 
	{
		if (item.getItemId() == R.id.menu_item_clear) {
			showDialog(ThunderClockApp.DIALOG_CLEAR_ID);
			return true;
			
		} else if (item.getItemId() == R.id.menu_item_calc) {
			showDialog(ThunderClockApp.DIALOG_CALCULATE_ID);
			return true;
			
		} else if (item.getItemId() == R.id.menu_item_settings) {
			
			Intent i = ActivityUtil.createIntent(this, ThunderClockSettings.class);
			startActivity(i);
			return true;
			
		} else if (item.getItemId() == R.id.menu_item_help) {
			Intent helpIntent = ActivityUtil.createIntent(this, ThunderClockHelp.class);
			startActivity(helpIntent);
			return true;
			
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	protected void updateLabels(long elapsed)
	{
		if (timerView != null) timerView.setElapsed(elapsed);  // new way // todo: remove null check - implement timerView for logview	
	}
	
	public void refreshDisplay(String caller)
	{
		refreshComponents();
	}
	
	public void refreshDisplay()
	{	
		refreshComponents();
	}
		
	public void restoreState()
	{
	}

	protected void refreshDisplay(long timestamp, double _elapsed)
	{
	}
	
	public void pauseComponents()
	{
		for (CollapsableLayout component : components)
		{
			component.pauseWidget();
		}
	}
	
	public void restoreComponents()
	{
		for (CollapsableLayout component : components)
		{
			component.restoreWidget();
		}
	}
	
	public void refreshComponents()
	{
		for (CollapsableLayout component : components)
		{
			component.onRefreshDisplay();
		}
	}	
	
	public ArrayList<CollapsableLayout> getComponents()
	{
		return components;
	}

	@Override
	protected Dialog onCreateDialog( int id )
	{
		Dialog dialog = null;
		switch(id) 
		{
		case ThunderClockApp.DIALOG_CALCULATE_ID:
			dialog = new DistanceDialog(this);
			break;
			
		default:
			dialog = null;
			break;
		}
		return dialog;
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog)
	{
		switch (id)
		{
		case ThunderClockApp.DIALOG_CALCULATE_ID:
			DistanceDialog distanceDialog = (DistanceDialog)dialog;
			distanceDialog.onPrepareDialog();
			break;
		}
	}
		
	/** Override to execute actions when the timer is started */
	public void onTimerStart()
	{	
	}
	
	/** Override to execute actions when the timer is stopped */
	public void onTimerStop()
	{
		ThunderClockApp app = (ThunderClockApp)getApplication();
		updateLabels(app.time_stop - app.time_start);
		refreshComponents();
	}
	
	@Override
	public void onClick( View v )
	{
		
		if (flag_quicktouch) return;   // use onTouch approach instead;
									   // otherwise action on regular click
		ThunderClockApp app = (ThunderClockApp)getApplication();
		app.toggleTimer();
		if (app.isRunning())
		{
			onTimerStart();
			startHandler();
			
		} else {
			stopHandler();
			onTimerStop();
		}
	}
	
	@Override
	public boolean onTouch( View v, MotionEvent e )
	{
		if (!flag_quicktouch) return false; // use onClick approach instead
									        // otherwise action on first touch
		
		ThunderClockApp app = (ThunderClockApp)getApplication();
		ToggleButton button = (ToggleButton)findViewById(R.id.button_trigger);
		
		switch (e.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			button.setPressed(true);
			button.setChecked(!app.isRunning());
			app.toggleTimer();
			if (app.isRunning())
			{
				onTimerStart();
				startHandler();
				
			} else {
				stopHandler();
				onTimerStop();
			}
			break;
			
		case MotionEvent.ACTION_UP:
			button.setPressed(false);
			break;
		}
			
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if(event.getAction() == KeyEvent.ACTION_DOWN)
		{
			switch(keyCode)
			{
			case KeyEvent.KEYCODE_CAMERA:
				if (!flag_camerabutton) return super.onKeyDown(keyCode, event);
				
				ThunderClockApp app = (ThunderClockApp)getApplication();
				ToggleButton button = (ToggleButton)findViewById(R.id.button_trigger);
				
				button.setChecked(!app.isRunning());
				app.toggleTimer();
				if (app.isRunning())
				{
					onTimerStart();
					startHandler();

				} else {
					stopHandler();
					onTimerStop();
				}
				return true;  // consumed?
			}
		}

		return super.onKeyDown(keyCode, event);
	}
	
	public void startHandler()
	{
		displayHandler.removeCallbacks(updateDisplayTask);   // update display
		displayHandler.postDelayed(updateDisplayTask, UPDATE_TIME);
	}
	
	public void stopHandler()
	{
		displayHandler.removeCallbacks(updateDisplayTask);   // update display
	}

}
