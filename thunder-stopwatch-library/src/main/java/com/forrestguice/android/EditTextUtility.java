package com.forrestguice.android;

import com.forrestguice.thunderwatch.lib.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

public class EditTextUtility 
{	
	public static void addClearButtonToField(Context context, final EditText field)
	{
		final Drawable x = context.getResources().getDrawable(R.drawable.ic_input_delete);
		x.setBounds(0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight());
		field.setCompoundDrawables(null, null, null, null);

		field.setOnFocusChangeListener(new ClearButtonFocusChangeListener(x));

		field.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (field.getError() == null)
				{
					Drawable d = field.getText().toString().equals("") ||
							!field.hasFocus() ? null : x;
					field.setCompoundDrawables(null, null, d, null);
				}
			}
			@Override
			public void afterTextChanged(Editable arg0) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		});

		field.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (field.getCompoundDrawables()[2] == null) {
					return false;
				}
				if (event.getAction() != MotionEvent.ACTION_UP) {
					return false;
				}
				if (event.getX() > field.getWidth() - field.getPaddingRight() - x.getIntrinsicWidth()) {
					field.setText("");
					field.setCompoundDrawables(null, null, null, null);
				}
				return false;
			}
		});
	}

	/**
	 * An OnFocusChangedListener that toggles the display of a drawable.
	 */
	public static class ClearButtonFocusChangeListener implements View.OnFocusChangeListener
	{
		private Drawable x;

		public ClearButtonFocusChangeListener(Drawable d)
		{
			x = d;
		}

		@Override
		public void onFocusChange(View v, boolean hasFocus) 
		{
			EditText field = (EditText)v;
			if (field.getError() == null)
			{
				Drawable d = field.getText().toString().trim().equals("") ? null : x;
				if (hasFocus) field.setCompoundDrawables(null, null, d, null);
				else field.setCompoundDrawables(null, null, null, null);
			}
		}
	}
}
