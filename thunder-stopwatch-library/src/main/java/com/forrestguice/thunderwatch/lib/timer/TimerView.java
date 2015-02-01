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
