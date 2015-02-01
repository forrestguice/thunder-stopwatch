package com.forrestguice.android;

import java.lang.reflect.Method;
import java.util.ArrayList;

import com.forrestguice.thunderwatch.lib.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class CollapsableLayout extends LinearLayout
{	
	private static final String androidns="http://schemas.android.com/apk/res/android";
	
	/** action is to expand / collapse the child views */
	public static final int MODE_EXPAND = 0;
	
	/** action is to launch a dialog (always collapsed) */
	public static final int MODE_DIALOG = 1;
	
	/** no action (always expanded, no title / close bar) */
	public static final int MODE_OPEN = 2;
	
	public static final boolean DEFAULT_SETTING_ANIMATE = true;
	
	protected ImageButton btn_showHide;
	protected TextView txt_title;
	
	protected Activity myParent;	
	protected String myName;
	protected ViewGroup mainContent;
	public boolean landscape; 
	protected int mode = MODE_EXPAND;
	
	private CollapsableLayoutDialog dialog;	
	private boolean animate = DEFAULT_SETTING_ANIMATE;
	
	public ArrayList<CollapsableLayout> companions = new ArrayList<CollapsableLayout>();
	
	public CollapsableLayout(String _name, Context context)
	{
		super(context);
		myParent = (Activity)context;
		myName = _name;
		this.setPadding(0, 0, 0, 0);
		this.setOrientation(VERTICAL);
				
		final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_showhide, this);	
		initShowHide(context);
	}
	
	public CollapsableLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		myParent = (Activity)context;
		this.setPadding(0, 0, 0, 0);
		this.setOrientation(VERTICAL);
		
		String stringId = attrs.getAttributeValue(androidns, "text").trim();
		myName = (!stringId.equals("")) ? stringId : "missingId" + System.currentTimeMillis();
		
		final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_showhide, this);
		initShowHide(context);
	}
		
	public void setMainContent( ViewGroup g )
	{
		mainContent = g;
	}
	
	public void setAnimateTransitions(boolean value)
	{
		animate = value;
	}
	
	public void setMode( int m )
	{
		mode = m;
	}
	
	public void setTitle(String t)
	{
		int[] attrs = new int[] { R.attr.panelTitleColor };   // 0 
		TypedArray a = myParent.obtainStyledAttributes(attrs);
		ColorStateList colors = a.getColorStateList(0);
		a.recycle();
		
		txt_title.setTextColor(colors);
		txt_title.setText(t);
	}
	
	public String getTitle()
	{
		return txt_title.getText().toString();
	}
	
	public void setCollapsed(boolean value)
	{
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(myParent);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(myName + "_collapse_widget", value);
        editor.commit();
        refreshShowHide();
	}
	
	public boolean isCollapsed()
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(myParent);
		return settings.getBoolean(myName + "_collapse_widget", true);
	}
	
	private void touchAction()
	{
		switch (mode)
		{
		case MODE_OPEN:
			break;
			
		case MODE_DIALOG:
			onDialogAction();
			break;
			
		case MODE_EXPAND:
		default:
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(myParent);
	        SharedPreferences.Editor editor = settings.edit();
    		        
	        boolean hidden = settings.getBoolean(myName + "_collapse_widget", true);
	        if (hidden)
	        {
	           editor.putBoolean(myName + "_collapse_widget", false);

	        } else {
	           editor.putBoolean(myName + "_collapse_widget", true);
	           
	        }
	        editor.commit();
	        refreshShowHide();
			break;
		}
	}
	
	protected void initShowHide(Context context)
	{				
		txt_title = (TextView)findViewById(R.id.boxTitle);
		
		btn_showHide = (ImageButton)findViewById(R.id.btn_showhide);
		btn_showHide.setOnTouchListener( new View.OnTouchListener()
		{			
			@Override
			public boolean onTouch(View v, MotionEvent e) 
			{
				switch (e.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					btn_showHide.setPressed(true);
					txt_title.setPressed(true);
					break;
					
				case MotionEvent.ACTION_UP:
					btn_showHide.setPressed(false);
					txt_title.setPressed(false);
					touchAction();
					break;
				}
				
				return true;
			}
		});
		
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout_showhide);
		layout.setOnTouchListener( new View.OnTouchListener() {

			@Override
			public boolean onTouch( View v, MotionEvent e )
			{
				switch (e.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					btn_showHide.setPressed(true);
					txt_title.setPressed(true);
					break;

				case MotionEvent.ACTION_UP:
					btn_showHide.setPressed(false);
					txt_title.setPressed(false);
					touchAction();
					break;
				}
				return true;
			}
		});
		
		Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		landscape = false;
		int screen_orientation = display.getOrientation();
		if (screen_orientation == Surface.ROTATION_90 || screen_orientation == Surface.ROTATION_270) landscape = true;	
	}
		
	public void pauseWidget()
	{
		if (dialog != null) dialog.dismiss();
	}
	
	public void restoreWidget()
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(myParent);
		animate = settings.getBoolean("animate_transitions", DEFAULT_SETTING_ANIMATE);
		refreshShowHide();
		onRefreshDisplay();
	}

	protected void refreshShowHide()
	{
		TextView txt = (TextView)findViewById(R.id.boxTitle);
		
		switch (mode)
		{
		case MODE_OPEN:
			txt.setVisibility(View.GONE);
			btn_showHide.setVisibility(View.GONE);
			if (mainContent != null) mainContent.setVisibility(View.VISIBLE);
			break;
			
		case MODE_DIALOG:
			txt.setVisibility(View.VISIBLE);
			btn_showHide.setVisibility(View.VISIBLE);
			btn_showHide.setImageResource(R.drawable.showhide_button_hiding);
			if (mainContent != null) mainContent.setVisibility(View.GONE);
			break;
			
		case MODE_EXPAND:
		default:
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(myParent);
			boolean hidden = settings.getBoolean(myName + "_collapse_widget", true);
			
			if (hidden)
			{
				btn_showHide.setImageResource(R.drawable.showhide_button_hiding);
				onHideAction();
				
			} else {
				btn_showHide.setImageResource(R.drawable.showhide_button_showing);
				onShowAction();
			}
			txt.setVisibility(View.VISIBLE);
			btn_showHide.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	protected void onDialogAction()
	{
		if (dialog == null) dialog = new CollapsableLayoutDialog(myParent);
		dialog.onPrepareDialog();
		dialog.show();
	}
	
	public Dialog onCreateDialog(int id)
	{
		return null;
	}
	public void onPrepareDialog(int id, Dialog dialog) 
	{
	}
	
	protected void onDialogDismiss(Dialog d) {}
	protected void onDialogCancel(Dialog d) {}
	protected void onDialogPrepare(Dialog d) {}
	protected void onDialogInit(Dialog d) {}
		
	protected void onShowAction()
	{
		if (mainContent == null) return;
		if (mainContent.getVisibility() == View.VISIBLE) return;

		if (animate)
		{
			Animation a = expandAnimation(mainContent, 500, true);
			mainContent.startAnimation(a);
			
		} else {
			mainContent.getLayoutParams().height = getInitialHeight(mainContent);
			mainContent.setVisibility(View.VISIBLE);
		}
	}
	
	protected void onHideAction()
	{
		if (mainContent == null) return;
		if (mainContent.getVisibility() != View.VISIBLE) return;
		
		if (animate)
		{
			Animation a = expandAnimation(mainContent, 500, false);
			mainContent.startAnimation(a);
			
		} else {
			mainContent.getLayoutParams().height = getInitialHeight(mainContent);
			mainContent.setVisibility(View.GONE);	
		}
	}
	
	public void onRefreshDisplay()
	{
		
	}
			
	public class CollapsableLayoutDialog extends Dialog 
	{
		protected Context myContext;

		public CollapsableLayoutDialog( Context c )
		{
			super(c);
			myContext = c;
			onDialogInit(this);
		}
		
		public void onPrepareDialog()
		{
			onDialogPrepare(this);
		}
		
		@Override
		public void dismiss()
		{
			onDialogDismiss(this);
			super.dismiss();
		}
		
		@Override
		public void cancel()
		{
			onDialogCancel(this);
			super.cancel();
		}
	}
	
	public static int getInitialHeight(final View v)
	{
	    try {
	        Method m = v.getClass().getDeclaredMethod("onMeasure", int.class, int.class);
	        m.setAccessible(true);
	        m.invoke(
	            v,
	            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
	            MeasureSpec.makeMeasureSpec(((View)v.getParent()).getMeasuredWidth(), MeasureSpec.AT_MOST)
	        );
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
	    return v.getMeasuredHeight();
	}
		
	public static Animation expandAnimation(final View v, final int animationSpeed, final boolean expand) 
	{
	    final int initialHeight = getInitialHeight(v);
	    
	    if (expand) v.getLayoutParams().height = 0;
	    else v.getLayoutParams().height = initialHeight;
	    v.setVisibility(View.VISIBLE);
	    
	    Animation a = new Animation() {
	        @Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) {
	            int newHeight = 0;
	            if (expand) {
	            	newHeight = (int) (initialHeight * interpolatedTime);
	            } else {
	            	newHeight = (int) (initialHeight * (1 - interpolatedTime));
	            }
	            v.getLayoutParams().height = newHeight;	            
	            v.requestLayout();
	            
	            if (interpolatedTime == 1 && !expand)
	            	v.setVisibility(View.GONE);
	        }

	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };
	    a.setDuration(animationSpeed);
	    return a;
	}
}
