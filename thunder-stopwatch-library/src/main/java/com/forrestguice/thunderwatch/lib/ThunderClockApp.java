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

import android.app.Application;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.content.SharedPreferences;

/**
   This class matches the Application tag defined in AndroidManifext.xml
   Contains global variables / application state shared between activities.
 */
public class ThunderClockApp extends Application
{	
	public static final int DIALOG_CLEAR_ID = 0;
	public static final int DIALOG_CALCULATE_ID = 2;
	public static final int DIALOG_CONFIRMDELETE_ID = 3;

	public static final String DEFAULT_SETTING_UNITS = "mi";
	public static final double DEFAULT_SETTING_TEMP = 20;
	
  	public static final int DATEFORMAT_FULLABRV = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_ABBREV_WEEKDAY;

	public static final double SPEED_SOUND_M = 331.5d;  // m/s at 0C
	
	public String distance_units;		// units stuff
	public double speed_of_sound;
	
	private boolean is_running = false; // timer stuff
	public long time_start, time_stop;
	
	private boolean is_logging;			// logging stuff
	private ThunderClockDbAdapter databaseHelper = null;

	@Override
	public void onCreate()
	{
		SharedPreferences config = PreferenceManager.getDefaultSharedPreferences(this);

		is_running = config.getBoolean("is_running", false);
		time_start = config.getLong("time_start", 0L);
		time_stop = config.getLong("time_stop", 0L);

		setUnits(config.getString("units", DEFAULT_SETTING_UNITS), false);
		setIsLogging(config.getBoolean("use_log", true), false);
	}
	
	//@Override
	//public void onLowMemory()
	//{
	//	super.onLowMemory();
	//}
	
	public void setUnits( String units, boolean save_change )
	{
		distance_units = units;
		speed_of_sound = determineSpeedOfSound(units, ThunderClockApp.DEFAULT_SETTING_TEMP);
		
		if (save_change)
		{
			SharedPreferences config = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = config.edit();
			editor.putString("units", distance_units);
			editor.commit();
		}
	}
		
	/**
	 * @param units the units String id (ft, yd, mi, m, km, etc)
	 * @param temp temperature in celsius
	 * @return a speed of sound at temperature and in the given units
	 */
	public double determineSpeedOfSound(String units, double temp)
	{		
		double speedAtTemp = SPEED_SOUND_M + (0.60 * temp);
		double r = speedAtTemp;
				
		if (units.equals("ft"))
		{
			r = speedAtTemp * 3.28084;      // convert m to ft
			
		} else if (units.equals("yd")) {
			r = speedAtTemp * 1.09361;      // convert m to yd
			
		} else if (units.equals("m")) {
			r = speedAtTemp;                // no conversion (already m)
			
		} else if (units.equals("km")) {
			r = speedAtTemp / 1000d;        // convert m to km
			
		} else {  // mi
			r = speedAtTemp * 0.000621371;  // convert m to mi
		}
		
		return r;
	}

	/**
      @return true, timer is running; false, timer is not running
	 */
	public boolean isRunning()
	{
		return is_running;
	}

	/**
      Toggle the timer on/off.
	 */
	public void toggleTimer()
	{
		SharedPreferences.Editor config_editor;

		if (is_running)
		{   // turn timer off
			is_running = false;
			time_stop = System.currentTimeMillis();
			config_editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
			config_editor.putBoolean("is_running", false);
			config_editor.putLong("time_stop", time_stop);
			if (is_logging) addLogEntry(time_start, time_stop);
		
		} else {
			// turn timer on
			time_start = System.currentTimeMillis();
			is_running = true;
			config_editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
			config_editor.putBoolean("is_running", true);
			config_editor.putLong("time_start", time_start);
		}

		config_editor.commit();
	}

	//////////////////////////////////////////////////
	//////////////////////////////////////////////////
	//// LOGGING
	//////////////////////////////////////////////////
	//////////////////////////////////////////////////

	public boolean isLogging()
	{
		return is_logging;
	}

	public void setIsLogging( boolean value, boolean saveChanges )
	{
		is_logging = value;

		if (is_logging)
		{
			// open the log database if it does not exist
			if (databaseHelper == null)
			{
				databaseHelper = new ThunderClockDbAdapter(this);
				databaseHelper.open();
			}
		} else {
			// close the log database
			if (databaseHelper != null)
			{
				databaseHelper.close();
				databaseHelper = null;
			}
		}

		if (saveChanges)
		{
			SharedPreferences config = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = config.edit();
			editor.putBoolean("use_log", is_logging);
			editor.commit();
		}
	}
	
	public void addLogEntry(long startTime, long stopTime)
	{
		databaseHelper.createEntry("" + time_start, "" + (time_stop - time_start));
	}
	
}
