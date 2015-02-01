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

import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

//import com.mopub.mobileads.MoPubView;

public class AdStuff extends LinearLayout //implements MoPubView.OnAdLoadedListener, MoPubView.OnAdFailedListener
{
	private Activity myParent;
	//private MoPubView mpv;   // mopub view
	private ImageView vpm;   // custom view
	private boolean adLoaded;
	private String ad_id = null;
	
	private HashMap<Integer, String> customAds = new HashMap<Integer, String>();
	private String customAdUrl = "";
	
	private Random random;
			
	public AdStuff(Context context, String id)
	{
		super(context);
		myParent = (Activity)context;
		ad_id = id;
		setOrientation(VERTICAL);
		
		final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_adview, this);
		
		initAdStuff();
	}
	
	public AdStuff(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		myParent = (Activity)context;
		setOrientation(VERTICAL);
		
		final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_adview, this);
		initAdStuff();
	}
	
	private void initAdStuff()
	{
		adLoaded = false;
		
		random = new Random();
		customAds.clear();
		customAds.put(R.drawable.thunderclock_bannerad, "https://play.google.com/store/apps/details?id=com.forrestguice.thunderwatch.free");
		
		vpm = (ImageView)findViewById(R.id.adview_noNet);
		vpm.setVisibility(View.GONE);
		vpm.setOnClickListener(new View.OnClickListener()
		{			
			@Override
			public void onClick(View v) 
			{
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(customAdUrl));
				myParent.startActivity(browserIntent);
			}
		});
		
		//mpv = (MoPubView)findViewById(R.id.adview);
		//mpv.setOnAdLoadedListener(this);
		//mpv.setOnAdFailedListener(this);
		//if (ad_id != null) mpv.setAdUnitId(ad_id);
	}
	
	//public MoPubView getAdView()
	//{
	//	return mpv;
	//}
	
	public void setAdId(String id)
	{
		ad_id = id;
		//if (ad_id != null) mpv.setAdUnitId(ad_id);
	}
	
	public String getAdId()
	{
		return ad_id;
	}
	
	public boolean isAdLoaded()
	{
		return adLoaded;
	}
		
	public void onResume()
	{
		if (!adLoaded && ad_id != null)
		{
			vpm.setVisibility(View.GONE);
			//mpv.setVisibility(View.VISIBLE);
			//mpv.loadAd();
		}
	}
	
	public void onDestroy()
	{
		//if (mpv != null)
		//{
		//	mpv.destroy();
		//}
	}

	//@Override
	//public void OnAdLoaded(MoPubView m)
	{
		adLoaded = true;
	}

	//@Override
	//public void OnAdFailed(MoPubView m)
	//{
	//	adLoaded = false;
	//	mpv.setVisibility(View.GONE);
	//
	//	refreshCustomAd();
	//	vpm.setVisibility(View.VISIBLE);
	//}
	
	public void refreshCustomAd()
	{
		Set<Integer> keys = customAds.keySet();
		
		int c = 0;    // randomly select an ad
		int i = random.nextInt(customAds.size());
		Integer drawable = (Integer)keys.toArray()[0];
		for (Integer id : keys)
		{
			if (c == i)
			{
				drawable = id;
			}
			c++;
		}
		
		customAdUrl = customAds.get(drawable);
		vpm.setImageResource(drawable);
	}

}
