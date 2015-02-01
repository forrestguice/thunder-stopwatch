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

import com.forrestguice.thunderwatch.lib.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HistoryListAdapter extends ArrayAdapter<Object>
{
	public HistoryListAdapter(Context context, int resourceId)
	{
		super(context, resourceId);
	}

	@Override
	public long getItemId( int position )
	{
		HistoryListItem logItem = (HistoryListItem)this.getItem(position);
		return logItem.getRowId();
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		View v = convertView;
		if (v == null)
		{
			LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.listitem_history, null);
		}

		TextView startLabel = (TextView)v.findViewById(R.id.label_start);
		TextView elapsedLabel = (TextView)v.findViewById(R.id.label_elapsed);
		TextView distanceLabel = (TextView)v.findViewById(R.id.label_distance);

		HistoryListItem listItem = (HistoryListItem)this.getItem(position);
		startLabel.setText(listItem.getDateString());
		elapsedLabel.setText(listItem.getElapsedString());
		distanceLabel.setText(listItem.getDistanceString());

		return v;
	}
}
