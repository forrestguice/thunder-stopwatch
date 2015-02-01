package com.forrestguice.android;

import com.forrestguice.thunderwatch.lib.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class ViewUtilities 
{	
	public static Dialog createConfirmationDialog( Context context, String msg, DialogInterface.OnClickListener l )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(msg)
		.setCancelable(false)
		.setPositiveButton(context.getString(R.string.yes), l)
		.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int id) 
			{
				dialog.cancel();
			}
		});
		return builder.create();
	}

}
