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