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

package com.forrestguice.thunderwatch.lib;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.text.method.LinkMovementMethod;

public class HelpSafetyThunderClockView extends Activity
{
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.help_safety_view);      
   }
   
   @Override
   protected void onResume()
   {
      super.onResume();
      TextView t1 = (TextView)findViewById(R.id.label_safety1);
      t1.setMovementMethod(LinkMovementMethod.getInstance());
   }
}
