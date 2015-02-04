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

import java.math.BigDecimal;

public class UnitsUtility
{
	public static final String UNITS_FT = "ft";
	public static final String UNITS_YD = "yd";
	public static final String UNITS_MI = "mi";
	public static final String UNITS_M = "m";
	public static final String UNITS_KM = "km";
	
	public static final double FT_IN_M = 3.28084;
	public static final double FT_IN_YD = 3;
	public static final double FT_IN_MI = 5280;
	public static final double FT_IN_KM = 3280.84;
	
	public static final double M_IN_FT = 1.0 / FT_IN_M;
	public static final double M_IN_YD = 0.9144;
	public static final double M_IN_MI = 1609.34;
	public static final double M_IN_KM = 1000;
	
	public static final double YD_IN_FT = 1.0 / FT_IN_YD;
	public static final double YD_IN_M = 1.0 / M_IN_YD;
	public static final double YD_IN_MI = 1760;
	public static final double YD_IN_KM = 1093.61;
	
	public static final double MI_IN_FT = 1.0 / FT_IN_MI;
	public static final double MI_IN_YD = 1.0 / YD_IN_MI;
	public static final double MI_IN_M = 1.0 / M_IN_MI;
	public static final double MI_IN_KM = 0.621371;
	
	public static final double KM_IN_FT = 1.0 / FT_IN_KM;
	public static final double KM_IN_YD = 1.0 / YD_IN_KM;
	public static final double KM_IN_MI = 1.0 / MI_IN_KM;
	public static final double KM_IN_M = 1.0 / M_IN_KM;
		
	public static final double convertUnits(double value, String oldUnits, String newUnits)
	{
		BigDecimal v = BigDecimal.valueOf(value);   // default; unsupported returns unchanged
		BigDecimal r = v;
		
		if (oldUnits.equals(UNITS_M) && newUnits.equals(UNITS_FT)) 
		{
			r = v.multiply(BigDecimal.valueOf(FT_IN_M));	
		} else if (oldUnits.equals(UNITS_M) && newUnits.equals(UNITS_YD)) {
			r = v.multiply(BigDecimal.valueOf(YD_IN_M));
		} else if (oldUnits.equals(UNITS_M) && newUnits.equals(UNITS_MI)) {
			r = v.multiply(BigDecimal.valueOf(MI_IN_M));
		} else if (oldUnits.equals(UNITS_M) && newUnits.equals(UNITS_KM)) {
			r = v.multiply(BigDecimal.valueOf(KM_IN_M));
					
		} else if (oldUnits.equals(UNITS_FT) && newUnits.equals(UNITS_M)) {
			r = v.multiply(BigDecimal.valueOf(M_IN_FT));
		} else if (oldUnits.equals(UNITS_FT) && newUnits.equals(UNITS_YD)) {
			r = v.multiply(BigDecimal.valueOf(YD_IN_FT));
		} else if (oldUnits.equals(UNITS_FT) && newUnits.equals(UNITS_MI)) {
			r = v.multiply(BigDecimal.valueOf(MI_IN_FT));
		} else if (oldUnits.equals(UNITS_FT) && newUnits.equals(UNITS_KM)) {
			r = v.multiply(BigDecimal.valueOf(KM_IN_FT));
			
		} else if (oldUnits.equals(UNITS_YD) && newUnits.equals(UNITS_FT)) {
			r = v.multiply(BigDecimal.valueOf(FT_IN_YD));
		} else if (oldUnits.equals(UNITS_YD) && newUnits.equals(UNITS_MI)) {
			r = v.multiply(BigDecimal.valueOf(MI_IN_YD));
		} else if (oldUnits.equals(UNITS_YD) && newUnits.equals(UNITS_M)) {
			r = v.multiply(BigDecimal.valueOf(M_IN_YD));
		} else if (oldUnits.equals(UNITS_YD) && newUnits.equals(UNITS_KM)) {
			r = v.multiply(BigDecimal.valueOf(KM_IN_YD));
			
		} else if (oldUnits.equals(UNITS_MI) && newUnits.equals(UNITS_FT)) {
			r = v.multiply(BigDecimal.valueOf(FT_IN_MI));
		} else if (oldUnits.equals(UNITS_MI) && newUnits.equals(UNITS_YD)) {
			r = v.multiply(BigDecimal.valueOf(YD_IN_MI));
		} else if (oldUnits.equals(UNITS_MI) && newUnits.equals(UNITS_M)) {
			r = v.multiply(BigDecimal.valueOf(M_IN_MI));
		} else if (oldUnits.equals(UNITS_MI) && newUnits.equals(UNITS_KM)) {
			r = v.multiply(BigDecimal.valueOf(KM_IN_MI));
			
		} else if (oldUnits.equals(UNITS_KM) && newUnits.equals(UNITS_FT)) {
			r = v.multiply(BigDecimal.valueOf(FT_IN_KM));
		} else if (oldUnits.equals(UNITS_KM) && newUnits.equals(UNITS_YD)) {
			r = v.multiply(BigDecimal.valueOf(YD_IN_KM));
		} else if (oldUnits.equals(UNITS_KM) && newUnits.equals(UNITS_MI)) {
			r = v.multiply(BigDecimal.valueOf(MI_IN_KM));
		} else if (oldUnits.equals(UNITS_KM) && newUnits.equals(UNITS_M)) {
			r = v.multiply(BigDecimal.valueOf(M_IN_KM));
		}
		return r.doubleValue(); 
	}

}
