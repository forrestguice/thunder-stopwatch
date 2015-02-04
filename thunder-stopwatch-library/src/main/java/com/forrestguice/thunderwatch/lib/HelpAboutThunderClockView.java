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

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class HelpAboutThunderClockView extends Activity
{
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.help_about_view);
      restoreState();
   }

   @Override
   protected void onSaveInstanceState( Bundle outState )
   {
      super.onSaveInstanceState(outState);
   }

   @Override
   protected void onPause()
   {
      super.onPause();
   }

   @Override
   protected void onResume()
   {
      super.onResume();
      restoreState();
   }

   private void restoreState()
   {
	   TextView t1 = (TextView)findViewById(R.id.label_support_notice);
       t1.setMovementMethod(LinkMovementMethod.getInstance());
       
       TextView t2 = (TextView)findViewById(R.id.label_legal);
       t2.setMovementMethod(LinkMovementMethod.getInstance());
	   //Linkify.addLinks(t2, Linkify.WEB_URLS);
   }
   
}
