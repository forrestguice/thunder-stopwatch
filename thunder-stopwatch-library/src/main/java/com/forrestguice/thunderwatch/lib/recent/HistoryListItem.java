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
