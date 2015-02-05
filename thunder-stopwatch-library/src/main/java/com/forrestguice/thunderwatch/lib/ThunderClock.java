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

import android.app.Dialog;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.widget.TextView;
import android.view.WindowManager;
import android.view.Display;
import android.view.Surface;

import com.forrestguice.android.CollapsableLayout;

public class ThunderClock extends TabActivity
{	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
    	ActivityUtil.initActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getTabHost().setCurrentTab(0);
		restoreState();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		unregisterReceiver(updateReceiver);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		registerReceiver(updateReceiver, new IntentFilter("update_tabs"));
		restoreState();
	}

	private void restoreState()
	{	
		ThunderClockApp app = (ThunderClockApp)getApplication();
		final TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;
		
		int currentTab = tabHost.getCurrentTab();
		tabHost.setCurrentTab(0);
		tabHost.clearAllTabs();

		Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay(); 
		boolean smallTabs = true;
		int screen_orientation = display.getOrientation();
		if (screen_orientation == Surface.ROTATION_90 || screen_orientation == Surface.ROTATION_270) smallTabs = true;

		// Simple View
		intent = ActivityUtil.createIntent(this, SimpleThunderClockView.class);
		spec = tabHost.newTabSpec("simple");

		TextView indicator = ActivityUtil.createTabIndicator(this, getString(R.string.tab_simple), smallTabs);
		spec.setIndicator(indicator);

		spec.setContent(intent);
		tabHost.addTab(spec);

		if (app.isLogging())
		{   // Log View
			intent = ActivityUtil.createIntent(this, LogThunderClockView.class);
			spec = tabHost.newTabSpec("log");
			indicator = ActivityUtil.createTabIndicator(this, getString(R.string.tab_log), smallTabs);
			spec.setIndicator(indicator);
			spec.setContent(intent);
			tabHost.addTab(spec);
		}

        tabHost.setOnTabChangedListener( new TabHost.OnTabChangeListener()
        {
            @Override
            public void onTabChanged(String s)
            {
                invalidateOptionsMenu();
            }
        });
		
		tabHost.setCurrentTab(currentTab);
	}



    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        String currentActivityId = getLocalActivityManager().getCurrentId();
        ThunderClockActivity currentActivity = (ThunderClockActivity)getLocalActivityManager().getActivity(currentActivityId);
        return currentActivity.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu( Menu menu )
    {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        String currentActivityId = getLocalActivityManager().getCurrentId();
        ThunderClockActivity currentActivity = (ThunderClockActivity)getLocalActivityManager().getActivity(currentActivityId);
        return currentActivity.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog( int id )
    {
        String currentActivityId = getLocalActivityManager().getCurrentId();
        ThunderClockActivity currentActivity = (ThunderClockActivity)getLocalActivityManager().getActivity(currentActivityId);
        return currentActivity.onCreateDialog(id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog)
    {
        String currentActivityId = getLocalActivityManager().getCurrentId();
        ThunderClockActivity currentActivity = (ThunderClockActivity)getLocalActivityManager().getActivity(currentActivityId);
        currentActivity.onPrepareDialog(id, dialog);
    }



    protected BroadcastReceiver updateReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive( Context context, Intent intent )
		{
			restoreState();
		}
	};
		
}
