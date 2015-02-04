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