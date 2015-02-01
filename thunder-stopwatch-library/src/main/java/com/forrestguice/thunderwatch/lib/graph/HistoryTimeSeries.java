package com.forrestguice.thunderwatch.lib.graph;

import com.androidplot.xy.XYSeries;
//import com.androidplot.series.XYSeries;

public class HistoryTimeSeries implements XYSeries 
{
    private HistoryXYDatasource datasource;
    private int seriesIndex;
    private String title;
 
    public HistoryTimeSeries(HistoryXYDatasource datasource, int seriesIndex, String title) 
    {
        this.datasource = datasource;
        this.seriesIndex = seriesIndex;
        this.title = title;
    }
    @Override
    public String getTitle() 
    {
        return title;
    }
 
    @Override
    public int size() 
    {
        return datasource.getItemCount(seriesIndex);
    }
 
    @Override
    public Number getX(int index) 
    {
        return datasource.getX(seriesIndex, index);
    }
 
    @Override
    public Number getY(int index) 
    {
        return datasource.getY(seriesIndex, index);
    }
}