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

import android.widget.AdapterView.AdapterContextMenuInfo;

public class SelectedItemInfo
{	
	private AdapterContextMenuInfo menuInfo;
	private String text;
	private long id;

	public SelectedItemInfo( String txt, long _id)
	{
		text = txt;
		menuInfo = null;
		id = _id;
	}

	public SelectedItemInfo( String txt, AdapterContextMenuInfo menu_info )
	{
		text = txt;
		menuInfo = menu_info;
		id = menu_info.id;
	}

	public String getText()
	{
		return text;
	}

	public long getID()
	{
		return id;
	}

	public AdapterContextMenuInfo getMenuInfo()
	{
		return menuInfo;
	}
}
