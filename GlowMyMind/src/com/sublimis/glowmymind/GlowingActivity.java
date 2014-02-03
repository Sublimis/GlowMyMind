/*
    Copyright 2014. Sublimis Solutions

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

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.sublimis.glowmymind.R;

public class GlowingActivity extends Activity
{
	public static int DURATION = Config.glowDurationDefault;

	private Handler mHandler = new Handler();

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

		if (hasFocus)
		{
			setScreenBrightness(LayoutParams.BRIGHTNESS_OVERRIDE_FULL);

			new Thread()
			{
				@Override
				public void run()
				{
					if (mHandler != null)
					{
						mHandler.postDelayed(new Runnable()
						{
							public void run()
							{
								finishMe();
							}
						}, DURATION);
					}
					else
					{
						try
						{
							Thread.sleep(DURATION);
						}
						catch (InterruptedException e)
						{}

						finishMe();
					}
				}
			}.start();
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	private void finishMe()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				setScreenBrightness(LayoutParams.BRIGHTNESS_OVERRIDE_NONE);

				finish();
			}
		});
	}

	private void setScreenBrightness(final float screenBrightness)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				LayoutParams params = getWindow().getAttributes();
				params.screenBrightness = screenBrightness;
				getWindow().setAttributes(params);
			}
		});
	}
}
