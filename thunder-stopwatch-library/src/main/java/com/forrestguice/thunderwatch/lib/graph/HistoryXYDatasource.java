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

package com.forrestguice.thunderwatch.lib.graph;

import java.util.ArrayList;

import android.content.ContentValues;
 
/**
 * A Datasource that can be added to DynamicTimeSeries. Create
 * and throw these away - they cannot be reused without crashing.
 */
public class HistoryXYDatasource
{
	private ArrayList<Double> domain = new ArrayList<Double>();
	private ArrayList<Double> range = new ArrayList<Double>(); 
	
	public void insertItemAtStart(double timestamp, double distance)
	{
		domain.add(0, timestamp);
		range.add(0, distance);
	}
	
    public void addItem(double timestamp, double distance)
    {
    	domain.add(timestamp);
    	range.add(distance);
    }
    
    public void addItem(ContentValues v)
    {
		domain.add(v.getAsDouble("date"));
		range.add(v.getAsDouble("distance"));
    }
    
    public void clearAll()
    {
    	domain.clear();
    	range.clear();
    }
        
    public int getItemCount(int series) 
    {
        return domain.size();
    }
        
    public Number getX(int series, int index) 
    {
    	Number value = 0;
    	try {
    		value = domain.get(index);
    		
    	} catch (Exception e) {
    		value = 0;
    		e.printStackTrace();
    	}
    	return value;
    	
    }
    
    public Number getY(int series, int index) 
    {
    	Number value = 0;
    	try {
    		value = range.get(index);
    		
    	} catch (Exception e) {
    		value = 0;
    		e.printStackTrace();
    	}
    	return value;
    }
 
}