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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import com.forrestguice.android.EditTextUtility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.app.Dialog;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.view.MenuItem;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Window;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextWatcher;
import android.text.Editable;

public class DistanceDialog extends Dialog 
{
	public static final String DEFAULTS_SETTING_DISTANCE_DIALOG_UNITS = "mi";
	
	private static final String FIELD_DIST = "distancedialog_dist";
	private static final String FIELD_TIME = "distancedialog_time"; 
	
	private Activity myParent;
	public DecimalFormat formatter1 = new DecimalFormat("0.00");
	public DecimalFormat formatter2 = new DecimalFormat("0.0000");
	
	private String units;
	private BigDecimal dist, time;
	private boolean toggle = true;
	private boolean forward = false;
	
	public DistanceDialog( Activity c )
	{	
		super(c);
		myParent = c;
		setContentView(R.layout.dialog_distance);
		setTitle(myParent.getString(R.string.menu_item_calc)); 
		setCancelable(true);
		
		EditText txtTime = (EditText)findViewById(R.id.txt_distance_time);
		EditTextUtility.addClearButtonToField(myParent, txtTime);
		txtTime.addTextChangedListener(new TextWatcher() 
		{
			public void afterTextChanged(Editable s)
			{
				forward = true;
				if (toggle) refreshDisplay();
			}	
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
		});
		
		EditText txtDist = (EditText)findViewById(R.id.txt_distance_dist);
		EditTextUtility.addClearButtonToField(myParent, txtDist);
		txtDist.addTextChangedListener(new TextWatcher() 
		{
			public void afterTextChanged(Editable s)
			{
				forward = false;
				if (toggle) refreshDisplay();
			}	
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
		});
		
		final Button unitsButton = (Button)findViewById(R.id.btn_distance_units);
		registerForContextMenu(unitsButton);
		unitsButton.setOnClickListener( new View.OnClickListener()
		{
			public void onClick(View v)
			{
				unitsButton.showContextMenu();
			}
		});
							
		Button closeButton = (Button)findViewById(R.id.btn_distance_close);
		closeButton.setOnClickListener( new View.OnClickListener()
		{
			public void onClick(View v)
			{
				dismiss();
			}
		});
							
		restoreDialogFields();
		refreshDisplay();
	}
	
	@Override
	public Bundle onSaveInstanceState ()
	{
		saveDialogFields();
		return super.onSaveInstanceState();
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		restoreDialogFields();
		refreshDisplay();
	}
	
	
	public void onPrepareDialog()
	{
		restoreDialogFields();
		refreshDisplay();
	}
			
	private void saveDialogFields()
	{
		SharedPreferences settings = myParent.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor settings_editor = settings.edit();
		EditText txtTime = (EditText)findViewById(R.id.txt_distance_time);		
		EditText txtDist = (EditText)findViewById(R.id.txt_distance_dist);
		
		String sTime = txtTime.getText().toString().trim();
		String sDist = txtDist.getText().toString().trim();
		settings_editor.putString(FIELD_TIME, sTime);
        settings_editor.putString(FIELD_DIST, sDist);
        settings_editor.commit();
	}
	
	private void restoreDialogFields()
	{
		SharedPreferences settings = myParent.getPreferences(Context.MODE_PRIVATE);
		
		EditText txtTime = (EditText)findViewById(R.id.txt_distance_time);
		String sTxtTime = settings.getString(FIELD_TIME, "").trim();
		if (sTxtTime.equals("0")) txtTime.setText("");
		else txtTime.setText(sTxtTime);
		
		EditText txtDist = (EditText)findViewById(R.id.txt_distance_dist);
		String sTxtDist = settings.getString(FIELD_DIST, "").trim();
		if (sTxtDist.equals("0")) txtDist.setText("");
		else txtDist.setText(sTxtDist);	
		
		restoreUnitsButton();
	}
	
	private void restoreUnitsButton()
	{
		SharedPreferences settings2 = PreferenceManager.getDefaultSharedPreferences(myParent);
		units = settings2.getString("distance_dialog_units", DEFAULTS_SETTING_DISTANCE_DIALOG_UNITS);
		Button unitsButton = (Button)findViewById(R.id.btn_distance_units);
		unitsButton.setText(units);
	}
	
	private void refreshDisplay()
	{			
		if (units == null) return;
		
		NumberFormat nf = NumberFormat.getInstance();
		
		EditText txtTime = (EditText)findViewById(R.id.txt_distance_time);
		String inputTime = txtTime.getText().toString().trim();
		time = BigDecimal.valueOf(0);
		if (!inputTime.equals(""))
		{
			try {
				time = BigDecimal.valueOf(nf.parse(inputTime).doubleValue());
				
			} catch (ParseException e) {
				time = BigDecimal.valueOf(0);
				e.printStackTrace();
			}
		}
				
		EditText txtDist = (EditText)findViewById(R.id.txt_distance_dist);
		String inputDist = txtDist.getText().toString();
		dist = BigDecimal.valueOf(0);
		if (!inputDist.equals(""))
		{
			try {
				dist = BigDecimal.valueOf(nf.parse(inputDist).doubleValue());
			} catch (ParseException e) {
				dist = BigDecimal.valueOf(0);
				e.printStackTrace();
			}
		}
				
		ThunderClockApp app = (ThunderClockApp)myParent.getApplication();
		double temp = ThunderClockApp.DEFAULT_SETTING_TEMP;
		double speedOfSound = app.determineSpeedOfSound(units, temp);

		toggle = false;
		if (forward)
		{
			if (time.doubleValue() != 0)
			{
				// determine distance from time and speed
				dist = time.multiply(BigDecimal.valueOf(speedOfSound));
				txtDist.setText(formatter2.format(dist));
				
			} else {
				txtDist.setText("");
			}
			
		} else {	
			if (dist.doubleValue() != 0)
			{
				// determine time from distance and speed
				time = dist.divide(BigDecimal.valueOf(speedOfSound), BigDecimal.ROUND_HALF_UP);
				txtTime.setText(formatter1.format(time));
				
			} else {
				txtTime.setText("");
			}
		}
		toggle = true;
		
	}
	
	@Override
	public void show()
	{
		super.show();
		restoreUnitsButton();
	}
		
	@Override
	public void dismiss()
	{
		saveDialogFields();
		super.dismiss();
	}
	
	@Override
	public void cancel()
	{
		saveDialogFields();
		super.cancel();
	}	
			
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = myParent.getMenuInflater();
		inflater.inflate(R.menu.units_menu, menu);

		SharedPreferences settings2 = PreferenceManager.getDefaultSharedPreferences(myParent);
		String dU = settings2.getString("distance_dialog_units", DEFAULTS_SETTING_DISTANCE_DIALOG_UNITS);
		
		Resources resources = myParent.getResources();
		int id1 = resources.getIdentifier("units", "array", myParent.getPackageName());
		int id2 = resources.getIdentifier("units_values", "array", myParent.getPackageName());
		String entry[] = resources.getStringArray(id1);
		String entryV[] = resources.getStringArray(id2);

		MenuItem selected = null;
		for (int i=0; i<menu.size(); i++)
		{
			MenuItem item = menu.getItem(i);
			item.setTitle(entry[i]);
			item.setChecked(false);		
			if (dU.equals(entryV[i])) selected = item;
		}
		selected.setChecked(true);
	}
	
	@Override
	public boolean onContextItemSelected( android.view.MenuItem item ) 
	{
		boolean retValue = false;
		int itemId = item.getItemId();
				
		Button unitsButton = (Button)findViewById(R.id.btn_distance_units);
		SharedPreferences settings2 = PreferenceManager.getDefaultSharedPreferences(myParent);
				
		String oldUnits = settings2.getString("distance_dialog_units", DEFAULTS_SETTING_DISTANCE_DIALOG_UNITS);
		String newUnits = "";
		
		boolean changed = false;
		if (itemId == R.id.menu_item_units_ft)
		{
			newUnits = "ft";
			changed = true;
			
		} else if (itemId == R.id.menu_item_units_yd) {
			newUnits = "yd";
			changed = true;
			
		} else if (itemId == R.id.menu_item_units_km) {
			newUnits = "km";
			changed = true;
			
		} else if (itemId == R.id.menu_item_units_m) {
			newUnits = "m";
			changed = true;
			
		} else if (itemId == R.id.menu_item_units_mi) {
			newUnits = "mi";
			changed = true;
			
		} else {
			retValue = super.onContextItemSelected(item);
			changed = false;
		}
				
		if (changed)
		{
			SharedPreferences.Editor settings_editor = settings2.edit();
			settings_editor.putString("distance_dialog_units", newUnits);
			settings_editor.commit();
			unitsButton.setText(newUnits);
			units = newUnits;

			if (item.isChecked()) item.setChecked(false);
			else item.setChecked(true);

			if (!oldUnits.equals(newUnits) && !newUnits.equals(""))
			{
				EditText txtDist = (EditText)findViewById(R.id.txt_distance_dist);
		    	NumberFormat nf = NumberFormat.getInstance();
		    	
				double oldVal = 0;  // get previous value
				String sOldVal = txtDist.getText().toString().trim();
				if (!sOldVal.equals(""))
				{
					try {
						oldVal = nf.parse(sOldVal).doubleValue();
					} catch (ParseException e) {
						oldVal = 0;
						e.printStackTrace();
					}
				}
				
				double newVal = UnitsUtility.convertUnits(oldVal, oldUnits, newUnits);		
				if (newVal != 0) txtDist.setText(formatter2.format(newVal) + "");
				else txtDist.setText("");
			}
		}
		
		return retValue;
	}

	/**
		This override is required to get the dialog to trigger onContextItemSelected.
	*/
	@Override
	public boolean onMenuItemSelected(int aFeatureId, MenuItem aMenuItem) 
	{
	    if (aFeatureId==Window.FEATURE_CONTEXT_MENU)
	        return onContextItemSelected(aMenuItem);
	    else
	        return super.onMenuItemSelected(aFeatureId, aMenuItem);
	}
	
}