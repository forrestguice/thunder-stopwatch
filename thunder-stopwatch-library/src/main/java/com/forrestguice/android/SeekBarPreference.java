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

package com.forrestguice.android;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.preference.DialogPreference;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.LinearLayout;

public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener
{
	private static final String androidns="http://schemas.android.com/apk/res/android";
	
	private Context context;
	private SeekBar bar;
	private TextView txtSplash, txtValue;
	
	private String msg, suffixS, suffixP;
	private int defaultValue, currentValue, maxValue, minValue = 0;

	public SeekBarPreference(Context _context, AttributeSet attrs) 
	{ 
		super(_context,attrs); 
		context = _context;
		
		msg = context.getString(attrs.getAttributeResourceValue(androidns, "dialogMessage", 0));
		
		String suffix = context.getString(attrs.getAttributeResourceValue(androidns, "text", 0));
		if (suffix.contains("~"))
		{
			String[] parts = suffix.split("~");
			suffixS = parts[0];
			suffixP = parts[1];
			
		} else {
			suffixS = suffix;
			suffixP = suffix;
		}
		if (!suffixS.startsWith(" ")) suffixS = " " + suffixS;
		if (!suffixP.startsWith(" ")) suffixP = " " + suffixP;
				
		defaultValue = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
		currentValue = defaultValue;
		minValue = attrs.getAttributeIntValue(androidns, "minLines", 0);
		maxValue = attrs.getAttributeIntValue(androidns, "maxLines", 100);
	}

	@Override 
	protected View onCreateDialogView() 
	{
		if (shouldPersist()) currentValue = getPersistedInt(currentValue) - minValue;
		
		txtSplash = new TextView(context);
		txtSplash.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		txtSplash.setTextColor(Color.WHITE);
		if (msg != null) txtSplash.setText(msg);
		
		txtValue = new TextView(context);
		txtValue.setTextAppearance(context, android.R.style.TextAppearance);
		txtValue.setTextColor(Color.WHITE);
		txtValue.setGravity(Gravity.CENTER_HORIZONTAL);
		
		bar = new SeekBar(context);
		bar.setOnSeekBarChangeListener(this);
		bar.setMax(maxValue - minValue);
		bar.setProgress(currentValue);
		onProgressChanged(bar, currentValue, false);
	
		LinearLayout.LayoutParams params;
		params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 
											   LinearLayout.LayoutParams.WRAP_CONTENT);
		
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(5, 5, 5, 5);		
		layout.addView(txtSplash);
		layout.addView(txtValue, params);
		layout.addView(bar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		return layout;
	}
	
	@Override 
	protected void onBindDialogView(View v) 
	{
		super.onBindDialogView(v);
		bar.setMax(maxValue - minValue);
		bar.setProgress(currentValue);
		onProgressChanged(bar, currentValue, false);
	}
	
	@Override
	protected void onSetInitialValue(boolean restore, Object _defaultValue)  
	{
		super.onSetInitialValue(restore, _defaultValue);
		if (restore) 
		{
			currentValue = shouldPersist() ? getPersistedInt(currentValue) - minValue : defaultValue;
			
		} else { 
			currentValue = defaultValue;
		}	
	}

	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch)
	{
		int displayValue = value + minValue;
		String t = String.valueOf(displayValue);
		String suffix = (displayValue == 1) ? suffixS : suffixP;
		txtValue.setText(suffix == null ? t : t.concat(suffix));
		if (shouldPersist()) persistInt(displayValue);  // potentially move to dialog dismiss
		callChangeListener(Integer.valueOf(displayValue));
	}

	public void onStartTrackingTouch(SeekBar seek) {}
	public void onStopTrackingTouch(SeekBar seek) {}

	public void setMax(int _max) 
	{ 
		maxValue = _max; 
	}
	public int getMax() 
	{ 
		return maxValue; 
	}

	public void setMin(int min) 
	{ 
		minValue = min; 
	}
	public int getMin() 
	{ 
		return minValue; 
	}

	public void setProgress(int progress) 
	{ 
		if (bar != null) bar.setProgress(progress);
	}
	public int getProgress() 
	{ 
		return bar.getProgress(); 
	}

}