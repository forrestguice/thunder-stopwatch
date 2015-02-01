/**
    Copyright (C) 2012 Forrest Guice
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

import com.forrestguice.thunderwatch.lib.ThunderClockSettings;
import android.os.Bundle;

public class ThunderClockSettingsExt extends ThunderClockSettings
{
	private AdStuff ads;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		ads = (AdStuff)findViewById(R.id.adview_settings);
		ads.setAdId(AdPlacements.mopub_banner1);
	}

	@Override
	public void onResume()
	{
		ads.onResume();
		super.onResume();
	}
	
	@Override
	public void onDestroy()
	{
		ads.onDestroy();
		super.onDestroy();
	}

}
