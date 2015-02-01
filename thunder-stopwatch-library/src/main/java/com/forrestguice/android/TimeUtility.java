package com.forrestguice.android;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import android.content.Context;
import android.text.format.DateUtils;

public class TimeUtility 
{		
	public static final int STYLE_SHORT = 0;
	public static final int STYLE_MEDIUM = 1;
	public static final int STYLE_LONG = 2;
	
	public static final String TIME_FORMAT_24HR = "HH:mm:ss";
	public static final String TIME_FORMAT_12HR = "h:mm:ss a";
	
	public static final String TIME_FORMAT_24HR_MEDIUM = "HH:mm";
	public static final String TIME_FORMAT_12HR_MEDIUM = "h:mm a";
	
	public static final String TIME_FORMAT_24HR_SHORT = "HH:mm";
	public static final String TIME_FORMAT_12HR_SHORT = "h:mm";
	
	/**
	   getTimeFormat(Context)
	 */
	public static java.text.DateFormat getTimeFormat(Context context)
	{
		return TimeUtility.getTimeFormat(context, TimeUtility.STYLE_LONG);
	}
	public static java.text.DateFormat getTimeFormat(Context context, int style)
	{
		java.text.DateFormat format;
		if (android.text.format.DateFormat.is24HourFormat(context))
		{
			switch (style)     // 24 hour time
			{
			case STYLE_SHORT:
				format = new java.text.SimpleDateFormat(TIME_FORMAT_24HR_SHORT);
				break;
				
			case STYLE_MEDIUM:
				format = new java.text.SimpleDateFormat(TIME_FORMAT_24HR_MEDIUM);
				break;
				
			case STYLE_LONG:
			default:
				format = new java.text.SimpleDateFormat(TIME_FORMAT_24HR);
				break;
			}

		} else {
			DateFormatSymbols symbols = new DateFormatSymbols();
			String[] s = { DateUtils.getAMPMString(Calendar.AM), DateUtils.getAMPMString(Calendar.PM) };
			symbols.setAmPmStrings(s);
			
			switch (style)    // 12 hour time
			{
			case STYLE_SHORT:
				format = new java.text.SimpleDateFormat(TIME_FORMAT_12HR_SHORT, symbols);
				break;
				
			case STYLE_MEDIUM:
				format = new java.text.SimpleDateFormat(TIME_FORMAT_12HR_MEDIUM, symbols);
				break;
				
			case STYLE_LONG:
			default:
				format = new java.text.SimpleDateFormat(TIME_FORMAT_12HR, symbols);
				break;
			}
		}
		return format;
	}

	/**
	   getDateFormat(Context)
	 */
	public static java.text.DateFormat getDateFormat(Context context)
	{
		java.text.DateFormat format;
		format = android.text.format.DateFormat.getDateFormat(context);
		return format;
	}

}
