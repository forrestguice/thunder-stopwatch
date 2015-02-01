package com.forrestguice.android;

import java.util.ArrayList;

public interface CollapsableLayoutContainer
{
	public ArrayList<CollapsableLayout> getComponents();
	
	public void restoreState();
	public void refreshDisplay();	
	public void refreshDisplay(String caller);	

	public void pauseComponents();
	public void restoreComponents();
	public void refreshComponents();

}
