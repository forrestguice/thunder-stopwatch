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

import com.forrestguice.thunderwatch.lib.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class AnalogClock extends View
{
	private Context context;
	public boolean changed;
	
	private int dial_width, dial_height;
	private float scale = 1f;
	
	private Drawable hand_small;
    private Drawable hand_large;
    private Drawable dial_small;

    public float value_large;
    public float value_small;

	public AnalogClock(Context _context)
	{
		super(_context);
		context = _context;
	}

	public AnalogClock(Context _context, AttributeSet attrs) 
	{
		this(_context, attrs, 0);
	}	

	public AnalogClock(Context _context, AttributeSet attrs, int defStyle)
	{
		super(_context, attrs, defStyle);
		context = _context;
		initClock(attrs);
	}

	private void initClock(AttributeSet attrs)
	{
	    Resources r = context.getResources();
	    
	    hand_small = r.getDrawable(R.drawable.clock_hand_hour);
	    hand_large = r.getDrawable(R.drawable.clock_hand_minute);
	    dial_small = r.getDrawable(R.drawable.clockface);   
	    	
		dial_width = dial_small.getIntrinsicWidth();
		dial_height = dial_small.getIntrinsicHeight();
	}

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize =  MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize =  MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f * scale;
        float vScale = 1.0f * scale;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < dial_width) 
        {
            hScale = (float)widthSize/(float)dial_width;
        }

        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < dial_height) 
        {
            vScale = (float)heightSize/(float)dial_height;
        }

        float scale = Math.min(hScale, vScale);

        setMeasuredDimension(resolveSize((int)(dial_width * scale), widthMeasureSpec),
                resolveSize((int)(dial_height * scale), heightMeasureSpec));
	}
	
	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) 
	{
        super.onSizeChanged(w, h, oldw, oldh);
        changed = true;
    }
	
    @Override
    protected void onDraw(Canvas canvas) 
    {
        boolean changed = this.changed;
        if (changed) this.changed = false;
        
        final Drawable dial = dial_small;
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();
        
        int x = getWidth() / 2;   // center of view
        int y = getHeight() / 2;
        
        canvas.scale(scale, scale, x, y);
                
        if (changed) 
        {
            dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        dial.draw(canvas);
        
        canvas.save();    // draw the large hand
        canvas.rotate(value_large, x, y);
        final Drawable largeHand = hand_large;
        if (changed)
        {
            w = largeHand.getIntrinsicWidth();
            h = largeHand.getIntrinsicHeight();
            largeHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        largeHand.draw(canvas);
        canvas.restore();
        
        canvas.save();    // draw the small hand
        canvas.rotate(value_small, x, y);
        final Drawable smallHand = hand_small;
        if (changed) 
        {
            w = smallHand.getIntrinsicWidth();
            h = smallHand.getIntrinsicHeight();
            smallHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        smallHand.draw(canvas);
        canvas.restore();  
    }
  
}
