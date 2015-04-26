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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Build;
import android.text.format.DateUtils;

import com.forrestguice.android.CollapsableLayout;
import com.forrestguice.android.CollapsableLayoutContainer;
import com.forrestguice.android.TimeUtility;
import com.forrestguice.android.ViewUtilities;

import com.forrestguice.thunderwatch.lib.R;
import com.forrestguice.thunderwatch.lib.ThunderClockApp;
import com.forrestguice.thunderwatch.lib.ThunderClockDbAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryListView extends CollapsableLayout
{	
	public static final int DEFAULT_SETTING_MAX_ITEMS = 15;
	
	public static final int DEFAULT_SPACES_FT = 10;
	public static final int DEFAULT_SPACES_YD = 9;
	public static final int DEFAULT_SPACES_M = 8;
	public static final int DEFAULT_SPACES_KM = 6;
	public static final int DEFAULT_SPACES_MI = 6;
	
	private HistoryListViewTask task;
	private SelectedItemInfo selectedItem = null;
	
	private java.text.DateFormat timeFormat;
	private java.text.DateFormat dateFormat;
	private String dateTimeSeparator = ", ";
	private int maxPadding = 5;
	
	private HistoryListAdapter adapter;

	public HistoryListView(Context context) 
	{
		super("historylist", context);
		final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_historylist, this);
		setMainContent((ViewGroup)findViewById(R.id.layout_mainContent));
	}

	public HistoryListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_historylist, this);
		setMainContent((ViewGroup)findViewById(R.id.layout_mainContent));
	}

    @Override
    public void setActivity( Activity a )
    {
        super.setActivity(a);
        initList();
    }

	private void initList()
	{
        dateFormat = TimeUtility.getDateFormat(myParent);
        timeFormat = TimeUtility.getTimeFormat(myParent);
		
		ListView list = getListView();
		list.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() 
		{			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
			{
				MenuInflater inflater = myParent.getMenuInflater();
				inflater.inflate(R.menu.list_menu, menu);

				for (int i=0; i<menu.size(); i++)
				{
					menu.getItem(i).setOnMenuItemClickListener( new MenuItem.OnMenuItemClickListener()
					{	
						@Override
						public boolean onMenuItemClick(MenuItem item) 
						{
							if (item.getItemId() == R.id.menu_item_history_delete)
							{	
								removeHistoryItem(item);

							//} else if (item.getItemId() == R.id.menu_item_history_view) {
							//	viewHistoryItem(item);

							} else if (item.getItemId() == R.id.menu_item_history_copy) {
                                copyHistoryItemAsText(item);
                            }
							return false;
						}
					});	
				}
			}
		});
	}

	@Override
	public void onRefreshDisplay()
	{
		if (this.task == null) 
		{
			if (adapter != null && adapter.getCount() > 0)
			{
				HistoryListItem item = (HistoryListItem)adapter.getItem(adapter.getCount()-1);
				if (item != null)
				{
					new HistoryListViewTask(HistoryListViewTask.STATE_UPDATE).execute(item.getRowId());
					
				} else {
					new HistoryListViewTask(HistoryListViewTask.STATE_UPDATE).execute();
				}

			} else if (adapter != null && adapter.getCount() == 0) {
				new HistoryListViewTask(HistoryListViewTask.STATE_UPDATE).execute();
			}
		}
	}
		
	@Override
	public void pauseWidget()
	{
	}
		
	@Override
	public void restoreWidget()
	{
		if (task == null)
		{
			task = new HistoryListViewTask(HistoryListViewTask.STATE_POPULATE);
			task.execute();	
			
		} else {
			task.restart = true;
		}
		super.restoreWidget();
	}
			
	@Override
	protected void onShowAction()
	{
		getListView().setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void onHideAction()
	{
		getListView().setVisibility(View.GONE);
	}
	
	public ListView getListView()
	{
		ListView list = (ListView)findViewById(R.id.historylist);
		return list;
	}
	
	/**
	 * HistoryListViewTask   :   AsyncTask
	 */
	protected class HistoryListViewTask extends AsyncTask<Long, HistoryListItem, Boolean> 
	{	
		public final static int STATE_POPULATE = 0;
		public final static int STATE_UPDATE = 1;
		public final static int STATE_DELETE = 2;
		private int state = STATE_POPULATE;  // task acts like a state machine
		
		public boolean restart = false;
		private int displayMax;
				
		public HistoryListViewTask( int mode )
		{
			state = mode;
		}
		
		/**
		 * doInBackground
		 */
		@Override
		protected Boolean doInBackground(Long... params)
		{	
			switch(state)
			{
			case STATE_UPDATE:
				return updateDoInBackground(params);
				
			case STATE_DELETE:
				return deleteDoInBackground(params);
				
			case STATE_POPULATE:
			default:
				return populateDoInBackground(params);
			}
		}
		
		/**
		 * doInBackground - Populate Mode
		 */
		private boolean populateDoInBackground(Long... params)
		{					
			ThunderClockApp app = (ThunderClockApp)myParent.getApplication();
			ThunderClockDbAdapter db = new ThunderClockDbAdapter(myParent.getApplicationContext());
			db.open();
			
			Cursor c = db.fetchAllEntries(displayMax);
			if (c == null)
			{
				db.close();
				return false;
			}

			c.moveToFirst();
			while (c.isAfterLast() == false)
			{
				if (restart)
				{
					c.moveToFirst();
					publishProgress(null);
					restart = false;
				}
				
				long rowId = c.getLong(0);
				long rawDate = Long.parseLong(c.getString(1));
				double elapsed = Double.parseDouble(c.getString(2));
										
				double e = (double)elapsed/1000.0d;
				double ef = (double)Math.round(e * 10) / 10;
				
				double di = e * app.speed_of_sound;
				double df = (double)Math.round(di * 10) / 10;
				
				//if (distString.length() < 4) distString = " " + distString;
				
				//String dateString = DateUtils.formatDateTime((Context)myParent, rawDate, ThunderClockApp.DATEFORMAT_PARTABRV2);
				String dateString = dateFormat.format(rawDate);
				String timeString = timeFormat.format(rawDate);
				boolean isToday = DateUtils.isToday(rawDate);
				String tString = (isToday) ? timeString : timeString + dateTimeSeparator + dateString;			
								
				String dString = df + " " + app.distance_units;
				if (dString.length() <= maxPadding)
				{
					String padding = "";
					for (int i=dString.length(); i <= maxPadding; i++)
					{
						padding += " ";
					}
					dString = padding + dString;
				}
				
				ContentValues v = new ContentValues();
				v.put("dateString", tString);
				v.put("elapsedString", ef + "s");
				v.put("distanceString", dString);
				HistoryListItem item = new HistoryListItem(rowId, v);
			    publishProgress(item);
				
				c.moveToNext();
			}
			
			c.close();
			db.close();
			return true;
		}
		
		/**
		 * doInBackground - Update Mode
		 */
		private boolean updateDoInBackground(Long... params)
		{			
			long recentId = -1L;
			if (params != null && params.length > 0 && params[0] != null) recentId = params[0]; 
			
			ThunderClockApp app = (ThunderClockApp)myParent.getApplication();
			ThunderClockDbAdapter db = new ThunderClockDbAdapter(myParent.getApplicationContext());
			db.open();
			
			Cursor c = db.fetchLastEntry();
			if (c == null)
			{
				db.close();
				return false;
			}

			c.moveToFirst();
			long rowId = c.getLong(0);
			
			if (recentId == -1 || rowId != recentId)
			{
				long rawDate = Long.parseLong(c.getString(1));
				double elapsed = Double.parseDouble(c.getString(2));
												
				double e = (double)elapsed/1000.0d;
				double ef = (double)Math.round(e * 10) / 10;
				
				double di = e * app.speed_of_sound;
				double df = (double)Math.round(di * 10) / 10;
		
				//String dateString = DateUtils.formatDateTime((Context)myParent, rawDate, ThunderClockApp.DATEFORMAT_PARTABRV2);
				String dateString = dateFormat.format(rawDate);
				String timeString = timeFormat.format(rawDate);
				boolean isToday = DateUtils.isToday(rawDate);
				String tString = (isToday) ? timeString : timeString + dateTimeSeparator + dateString;			
				
				String dString = df + " " + app.distance_units;
				if (dString.length() <= maxPadding)
				{
					String padding = "";
					for (int i=dString.length(); i<=maxPadding; i++)
					{
						padding += " ";
					}
					dString = padding + dString;
				}				
				
				ContentValues v = new ContentValues();
				v.put("dateString", tString);
				v.put("elapsedString", ef + "s");
				v.put("distanceString", dString);
				
				HistoryListItem item = new HistoryListItem(rowId, v);
				publishProgress(item);
			}
			
			c.close();
			db.close();
			return true;
		}
		
		/**
		 * doInBackground - Delete Mode
		 */
		private boolean deleteDoInBackground(Long... params)
		{					
			ThunderClockDbAdapter db = new ThunderClockDbAdapter(myParent.getApplicationContext());
			db.open();
			
			boolean result = false;
			if (params[0] < 0)
			{
				// delete all entries
				db.clearEntries();
				result = true;
				
			} else {
				// delete single entry
				result = db.deleteEntry(params[0]);
			}
			db.close();
			return result;
		}

		/**
		 * onProgressUpdate
		 */
		@Override
		protected void onProgressUpdate(HistoryListItem... progress) 
		{
			switch (state)
			{
			case STATE_UPDATE:
				// insert at beginning of list
				adapter.insert(progress[0], 0);
				break;
				
			case STATE_DELETE:
				// do nothing
				break;
				
			case STATE_POPULATE:
			default:
				// clear on null, or add to end of list
				if (progress == null) adapter.clear();
				else adapter.add(progress[0]);
				break;
			}
		}

		/**
		 * onPreExecute
		 */
		@Override
		protected void onPreExecute()
		{
			switch(state)
			{
			case STATE_UPDATE:
				// prepare list for update
				initPaddingAndSeparators();
				break;
				
			case STATE_DELETE:
				// do nothing
				break;
				
			case STATE_POPULATE:
			default:
				// prepare list for populate
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(myParent);
		        displayMax = settings.getInt("history_dataset_size", DEFAULT_SETTING_MAX_ITEMS);
				
				ListView list = (ListView)findViewById(R.id.historylist);
		        adapter = new HistoryListAdapter(myParent, R.layout.listitem_history);
		        list.setAdapter(adapter);
		        initPaddingAndSeparators();
				break;
			}
		}
		
		/**
		 * onPostExecute
		 */
		@Override
		protected void onPostExecute(Boolean result) 
		{
			switch(state)
			{
			case STATE_UPDATE:
				// do nothing
				break;
				
			case STATE_DELETE:
				if (result)
				{
					// notify the parent container to refresh
					if (myParent instanceof CollapsableLayoutContainer)
					{
						CollapsableLayoutContainer container = (CollapsableLayoutContainer)myParent;
						container.refreshDisplay("clear");
					}
				}
				break;
				
			case STATE_POPULATE:
			default:
				task = null;
				break;
			}
		}
	}
		
	private void initPaddingAndSeparators()
	{
		ThunderClockApp app = (ThunderClockApp)myParent.getApplication();
		
		if (app.distance_units.equals("ft"))
		{
			maxPadding = HistoryListView.DEFAULT_SPACES_FT; 
	        dateTimeSeparator = "\n"; 
			
		} else if (app.distance_units.equals("yd")) {
			maxPadding = HistoryListView.DEFAULT_SPACES_YD;
        	dateTimeSeparator = "\n";
			
		} else if (app.distance_units.equals("m")) {
			maxPadding = HistoryListView.DEFAULT_SPACES_M;
        	dateTimeSeparator = "\n";
			
		} else if (app.distance_units.equals("km")) {
			maxPadding = HistoryListView.DEFAULT_SPACES_KM;
        	dateTimeSeparator = ", ";
			
		} else {  // mi
			maxPadding = HistoryListView.DEFAULT_SPACES_MI;
        	dateTimeSeparator = ", ";
		}
				
        /**switch (app.getUnits())
        {
        case ThunderClockApp.UNITS_FT:
        	maxPadding = HistoryListView.DEFAULT_SPACES_FT; 
	        dateTimeSeparator = "\n"; 
        	break;	        	
        case ThunderClockApp.UNITS_YD:        	
        	maxPadding = HistoryListView.DEFAULT_SPACES_YD;
        	dateTimeSeparator = "\n";
        	break;
        case ThunderClockApp.UNITS_M:
        	maxPadding = HistoryListView.DEFAULT_SPACES_M;
        	dateTimeSeparator = "\n";
        	break;
        case ThunderClockApp.UNITS_KM:
        	maxPadding = HistoryListView.DEFAULT_SPACES_KM;
        	dateTimeSeparator = ", ";
        	break;
        default:
        	maxPadding = HistoryListView.DEFAULT_SPACES_MI;
        	dateTimeSeparator = ", ";
        	break;
        }*/
        if (landscape)
        {
            dateTimeSeparator = ", ";
        }
	}
	
	private void removeHistoryItem( MenuItem item )
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();	
		View v = info.targetView;
		TextView tv = (TextView)v.findViewById(R.id.label_start);
		selectedItem = new SelectedItemInfo(tv.getText().toString(), info);
		myParent.showDialog(ThunderClockApp.DIALOG_CONFIRMDELETE_ID);
	}

    private void copyHistoryItemAsText( MenuItem item )
    {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
        View v = info.targetView;
        TextView text_startedOn = (TextView)v.findViewById(R.id.label_start);
        TextView text_distance = (TextView)v.findViewById(R.id.label_distance);
        TextView text_elapsed = (TextView)v.findViewById(R.id.label_elapsed);

        String clipLabel = text_startedOn.getText().toString();
        String clipText = text_startedOn.getText().toString() + ", " +
                          text_distance.getText().toString() + ", " +
                          text_elapsed.getText().toString();
        copyText(clipLabel, clipText);

        String notifyText = getContext().getResources().getString(R.string.msg_copied_success);
        Toast.makeText(getContext(), notifyText, Toast.LENGTH_SHORT).show();
    }

    private void copyText( String clipLabel, String clipText )
    {
        Context context = getContext();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
        {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(clipText);

        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText(clipLabel, clipText);
            clipboard.setPrimaryClip(clip);
        }
    }
	
	@Override
	public void onPrepareDialog(int id, Dialog d)
	{
		switch (id)
		{
		case ThunderClockApp.DIALOG_CLEAR_ID:
			break;
		
		case ThunderClockApp.DIALOG_CONFIRMDELETE_ID:
            if (selectedItem != null)
            {
                String msg = myParent.getString(R.string.msg_delete_confirm, selectedItem.getText());
                AlertDialog dialog = (AlertDialog) d;
                dialog.setMessage(msg);
            }
			break;
		}
	}
	
	@Override
	public Dialog onCreateDialog( int id )
	{
		Dialog dialog = null;
		String msg = "";
		
		switch(id)
		{
		case ThunderClockApp.DIALOG_CLEAR_ID:
			msg = myParent.getString(R.string.msg_clear_confirm);
			dialog = ViewUtilities.createConfirmationDialog(myParent, msg, new DialogInterface.OnClickListener() 
			{				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					new HistoryListViewTask(HistoryListViewTask.STATE_DELETE).execute(-1L);
				}
			});
			break;
	
		case ThunderClockApp.DIALOG_CONFIRMDELETE_ID:
            if (selectedItem == null)
            {
                dialog = null;
            } else {
                msg = myParent.getString(R.string.msg_delete_confirm, selectedItem.getText());
                dialog = ViewUtilities.createConfirmationDialog(myParent, msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new HistoryListViewTask(HistoryListViewTask.STATE_DELETE).execute(selectedItem.getID());
                    }
                });
            }
			break;
			
		default:
			dialog = null;
			break;	
		}
		
		return dialog;
	}
	
}
