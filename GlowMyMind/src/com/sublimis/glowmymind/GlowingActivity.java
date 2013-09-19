/*
    Copyright 2013. Sublimis Solutions

    This file is part of GlowMyMind.

    GlowMyMind is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The software is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this software. If not, see <http://www.gnu.org/licenses/>.
*/

package com.sublimis.glowmymind;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.sublimis.glowmymind.R;

public class GlowingActivity extends Activity
{
	public static final int TIMER_PERIOD = 25;

	public static int DURATION = Config.glowDurationDefault;
	
    private Timer mTimer = null;
    private TimerRefreshJob mTimerTask = null;
    private boolean mTimerEngaged = false;
    
    @Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		
		setContentView(R.layout.glowing);
		
		MyPreference.setContext(this);
		DURATION = MyPreference.getGlowDuration();
		
		if (DURATION <= 0)
		{
			finishMe();
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		setScreenBrightness(LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		
		if (hasFocus && !mTimerEngaged)
		{
			setScreenBrightness(LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
			
			mTimer = new Timer();
			mTimerTask = new TimerRefreshJob();
	
			try
			{
				if (mTimer != null)
					mTimer.schedule(mTimerTask, 0, TIMER_PERIOD);
				
				mTimerEngaged = true;
			}
			catch (IllegalArgumentException e)
			{
			}
			catch (IllegalStateException e)
			{
			}
		}
	}

    @Override
    public void onDestroy()
    {
		if (mTimerTask != null)
			mTimerTask.cancel();
		
		mTimerTask = null;

		if (mTimer != null)
			mTimer.cancel();
		
		mTimer = null;
		
		super.onDestroy();
    }
    
	private final class TimerRefreshJob extends TimerTask
	{
	    private long mTimeElapse = 0;
		
		@Override
		public void run()
		{
		    if (mTimeElapse >= DURATION)
			{
				if (mTimerTask != null)
					mTimerTask.cancel();

				Runnable runnable = new Runnable()
				{
					public void run()
					{
						finishMe();
					}
				};
				
				runOnUiThread(runnable);
			}
			
			mTimeElapse += TIMER_PERIOD;
		}
	}
	
	private void finishMe()
	{
		setScreenBrightness(LayoutParams.BRIGHTNESS_OVERRIDE_NONE);

		finish();
	}
	
	private void setScreenBrightness(float screenBrightness)
	{
		LayoutParams params = getWindow().getAttributes();
		params.screenBrightness = screenBrightness;
		getWindow().setAttributes(params);
	}
}
