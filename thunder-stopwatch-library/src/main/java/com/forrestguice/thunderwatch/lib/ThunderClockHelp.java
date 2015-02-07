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

import android.os.Bundle;
import android.widget.TabHost;
import android.app.TabActivity;
import android.content.Intent;
import android.widget.TextView;

public class ThunderClockHelp extends TabActivity
{
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
   	  ActivityUtil.initActivity(this);
      super.onCreate(savedInstanceState);
      setContentView(R.layout.help_view);
      getTabHost().setCurrentTab(0);
   }

   @Override
   protected void onResume()
   {
      super.onResume();

      TabHost tabHost = getTabHost();
      TabHost.TabSpec spec;
      Intent intent;
      TextView indicator;

      // (re)populate tabs
      int currentTab = tabHost.getCurrentTab();
      tabHost.setCurrentTab(0);   
      tabHost.clearAllTabs();

      // About View
      intent = ActivityUtil.createIntent(this, HelpAboutThunderClockView.class);
      spec = tabHost.newTabSpec("about");
      indicator = ActivityUtil.createTabIndicator(this, getString(R.string.tab_about));
      spec.setIndicator(indicator);
      spec.setContent(intent);
      tabHost.addTab(spec);

      // Safety View
      intent = ActivityUtil.createIntent(this, HelpSafetyThunderClockView.class);
      spec = tabHost.newTabSpec("safety");
      indicator = ActivityUtil.createTabIndicator(this, getString(R.string.tab_safety));
      spec.setIndicator(indicator);
      spec.setContent(intent);
      tabHost.addTab(spec);

      // Howto View
      intent = ActivityUtil.createIntent(this, HelpHowtoThunderClockView.class);
      spec = tabHost.newTabSpec("howto");
      indicator = ActivityUtil.createTabIndicator(this, getString(R.string.tab_howto));
      spec.setIndicator(indicator);
      spec.setContent(intent);
      tabHost.addTab(spec);

      tabHost.setCurrentTab(currentTab);
   }
   
}
