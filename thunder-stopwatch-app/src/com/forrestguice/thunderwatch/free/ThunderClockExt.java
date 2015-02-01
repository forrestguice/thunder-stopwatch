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

package com.forrestguice.thunderwatch.free;

import com.forrestguice.thunderwatch.lib.ThunderClock;
import android.os.Bundle;
import android.widget.TabHost;

public class ThunderClockExt extends ThunderClock
{
	//private AdStuff ads;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//ads = (AdStuff)findViewById(R.id.adview_main);
		//ads.setAdId(AdPlacements.mopub_banner1);
		
		//TabHost tabHost = getTabHost();
		//tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener()
		//{
		//	@Override
		//	public void onTabChanged(String tabId)
		//	{
		//		ads.onResume();
		//	}
		//});
	}
	
	@Override
	public void onResume()
	{
		//ads.onResume();
		super.onResume();
	}
	
	@Override
	public void onDestroy()
	{
		//ads.onDestroy();
		super.onDestroy();
	}
}
