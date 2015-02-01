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
