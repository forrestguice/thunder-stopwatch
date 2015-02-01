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
