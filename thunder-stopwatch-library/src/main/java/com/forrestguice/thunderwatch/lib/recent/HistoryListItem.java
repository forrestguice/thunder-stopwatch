package com.forrestguice.thunderwatch.lib.recent;

import android.content.ContentValues;

public class HistoryListItem implements Comparable<Object>
{
	private long rowId;
	private String dateString;
	private String elapsedString;
	private String distanceString;

	public HistoryListItem()
	{
		rowId = -1L;
		dateString = "";
		elapsedString = "";
		distanceString = "";
	}

	public HistoryListItem(long _rowId, ContentValues v)
	{					
		rowId = _rowId;
		dateString = v.getAsString("dateString");
		elapsedString = v.getAsString("elapsedString");
		distanceString = v.getAsString("distanceString");
	}

	public HistoryListItem( HistoryListItem other )
	{
		rowId = other.rowId;
		dateString = other.dateString;
		elapsedString = other.elapsedString;
		distanceString = other.distanceString;
	}
	
	public long getRowId()
	{
		return rowId;
	}
	
	public String getDateString()
	{
		return dateString;
	}
	
	public String getElapsedString()
	{
		return elapsedString;
	}
	
	public String getDistanceString()
	{
		return distanceString;
	}

	public int compareTo( Object o )
	{
		return toString().compareTo(o.toString());
	}

	public String toString()
	{
		return dateString;
	}
}
