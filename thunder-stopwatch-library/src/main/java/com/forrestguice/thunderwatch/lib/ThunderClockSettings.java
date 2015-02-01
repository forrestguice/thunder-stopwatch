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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceActivity;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

public class ThunderClockSettings extends PreferenceActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		ActivityUtil.initActivity(this);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		setContentView(R.layout.settings_view);

		Preference pref_units = (Preference)findPreference("units");
		pref_units.setOnPreferenceChangeListener( new OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(Preference preference, Object value)
			{
				ThunderClockApp app = (ThunderClockApp)getApplication();
				app.setUnits((String)value, false);
				return true;
			}
		});

		Preference pref_logging = (Preference)findPreference("use_log");
		pref_logging.setOnPreferenceChangeListener( new OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(Preference preference, Object value)
			{
				ThunderClockApp app = (ThunderClockApp)getApplication();
				app.setIsLogging((Boolean)value, false);
				return true;
			}
		});

		// check for extras
		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			String autolaunch_pref_key = extras.getString(Intent.EXTRA_TEXT);
			if (autolaunch_pref_key != null) 
			{
				launchPreference(autolaunch_pref_key);
			}
		}
	}

	private void launchPreference(String key)
	{	   	     
		int pos = getPreferencePosition(key);
		getPreferenceScreen().onItemClick( null, null, pos, 0);

		try {
			final DialogPreference p = (DialogPreference)findPreference(key);
			final Preference.OnPreferenceChangeListener l = p.getOnPreferenceChangeListener();
			p.setOnPreferenceChangeListener( new Preference.OnPreferenceChangeListener() 
			{
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) 
				{
					if (l != null) l.onPreferenceChange(preference, newValue);
					finish();
					return true;
				}				
			});
			
			p.getDialog().setOnCancelListener(new DialogInterface.OnCancelListener()
			{				
				@Override
				public void onCancel(DialogInterface dialog) 
				{
					finish();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int getPreferencePosition(String key)
	{
		int counter = 0;
		PreferenceScreen screen = getPreferenceScreen();

		// loop over categories
		for (int i = 0; i < screen.getPreferenceCount(); i++)
		{
			PreferenceCategory cat = (PreferenceCategory) screen.getPreference(i);
			counter++;

			// loop over category items
			for (int j = 0; j < cat.getPreferenceCount(); j++)
			{ 
				if (cat.getPreference(j).getKey().contentEquals(key))
				{
					return counter;
				}
				counter++;
			}
		}
		return 0; // not found
	}

}
