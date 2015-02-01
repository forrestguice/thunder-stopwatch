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
