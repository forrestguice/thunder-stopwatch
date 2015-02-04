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
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.TextView;

public class ActivityUtil 
{
	public static final String DEFAULT_THEME = "light";
	
	private static Class<?> getActivityClass(Context context, Class<?> classObj) 
	{
		StringBuilder b = new StringBuilder(255);
		b.append(context.getPackageName());
		b.append(".");
		b.append(classObj.getSimpleName());
		b.append("Ext");
		
		String extClassName = b.toString();
	    try {
	        Class<?> extClass = Class.forName(extClassName);
	        return extClass;
	        
	    } catch (ClassNotFoundException e) {
	        //e.printStackTrace();   // DEBUG: uncomment to debug
	        return classObj;
	    }
	}

	public static Intent createIntent(Context context, Class<?> classObj) 
	{
	    Class<?> activityClass = getActivityClass(context, classObj);
	    return new Intent(context, activityClass);
	}
	
	public static void initActivity(Activity a)
	{
		String theme = PreferenceManager.getDefaultSharedPreferences(a).getString("theme", DEFAULT_THEME);
		if (theme.equals("dark"))
		{
			a.setTheme(R.style.dark);
			
		} else if (theme.equals("light")) {
			a.setTheme(R.style.light);
		}
	}
	
	public static TextView createTabIndicator(Context context, String title)
	{
		return createTabIndicator(context, title, false);
	}
	public static TextView createTabIndicator(Context context, String title, boolean compact)
	{
		int[] attrs = new int[] { R.attr.tabBackground,   // 0
				  				  R.attr.tabTextColor };  // 1
		TypedArray a = context.obtainStyledAttributes(attrs);
		
		TextView indicator = new TextView(context);
		indicator.setTextAppearance(context, R.style.tabTextAppearance);
		indicator.setText(title);
		indicator.setBackgroundDrawable(a.getDrawable(0));
		indicator.setTextColor(a.getColorStateList(1));
		indicator.setGravity(Gravity.CENTER);
		
		if (compact)
		{
			indicator.setPadding(5, 5, 5, 5);
		} else {
			indicator.setPadding(0, 0, 0, 0);
		}
		
		a.recycle();
		return indicator;
	}
}
