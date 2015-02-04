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

package com.forrestguice.thunderwatch.lib.timer;

import android.os.Handler;
import com.forrestguice.android.BaseComponent;

public interface TimerView extends BaseComponent
{
	public void setElapsed(long timeframe);
	public Runnable getUpdateTask();     // TimerView's supply a runnable
	public Handler getDisplayHandler();  // and handler responsible for
	public void startHandler();          // managing the update cycle
	public void stopHandler();
}
