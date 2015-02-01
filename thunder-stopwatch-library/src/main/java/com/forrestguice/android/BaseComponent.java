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

public interface BaseComponent 
{		
	//public void setMainContent( ViewGroup g );
	//public void setAnimateTransitions(boolean value);
	//public void setMode( int m );
	//public void setTitle(String t);
	//public String getTitle();
	//public void setCollapsed(boolean value);
	//public boolean isCollapsed();
	//public void touchAction();
	//public void initShowHide(Context context);
	
	public void pauseWidget();
	public void restoreWidget();
	
	//public void refreshShowHide();
	//public void onDialogAction();
	//public Dialog onCreateDialog(int id);
	//public void onPrepareDialog(int id, Dialog dialog);
	//public void onDialogDismiss(Dialog d);
	//public void onDialogCancel(Dialog d);
	//public void onDialogPrepare(Dialog d);
	//public void onDialogInit(Dialog d);
	//public void onShowAction();
	//public void onHideAction();
	//public void onRefreshDisplay();

}
