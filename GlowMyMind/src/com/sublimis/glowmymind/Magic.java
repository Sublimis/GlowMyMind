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

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.widget.Toast;

public class Magic implements SensorEventListener
{
	private Context mContext = null;

	private float proximityDistanceThreshold = 3; // centimeters

	private SensorManager mSensorManager;
	private Sensor mSensor;

	private float mProximityDistance = -1;
	private boolean mIsTest = false;

	private ScheduledThreadPoolExecutor mScheduledExecutor = new ScheduledThreadPoolExecutor(1);

	private Runnable mRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			if (mProximityDistance >= proximityDistanceThreshold)
			{
				callFurtherActivity();
			}
			else
			{
				if (MyPreference.isObscuredFlashlight() && !MyPreference.isFlashlightEnabled())
				{
					flashlightGlow();
				}

				if (mIsTest)
				{
					outputError(R.string.toast_screen_obscured);
				}
			}

			finishMe();
		}
	};

	public Magic(Context context)
	{
		if (context != null)
		{
			mContext = context;

			MyPreference.setContext(context);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		// must be here
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		mProximityDistance = event.values[0];

		// isListening = false;
	}

	private void engageProximitySensor()
	{
		if (mContext != null) mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

		if (mSensorManager != null)
		{
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

			if (mSensor != null)
			{
				float maxRange = mSensor.getMaximumRange();

				if (maxRange < proximityDistanceThreshold) proximityDistanceThreshold = maxRange;

				// rate parameter really does nothing
				mSensorManager.registerListener(this, mSensor, 0);

				if (mScheduledExecutor != null && mRunnable != null)
				{
					// needed because sensor will sometimes fail to deliver the
					// (initial) event
					mScheduledExecutor.schedule(mRunnable, 500, TimeUnit.MILLISECONDS);
				}
			}
		}
	}

	private boolean isScreenOn(Context context)
	{
		boolean retVal = false;

		try
		{
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

			if (pm != null)
			{
				retVal = pm.isScreenOn();
			}
		}
		catch (RuntimeException e)
		{}

		return retVal;
	}

	private boolean isScreenStateOk(Context context, boolean isTest)
	{
		boolean retVal = false;

		if (MyPreference.isScreenEnabled())
		{
			if (MyPreference.isCheckScreen() && !isTest)
			{
				if (!isScreenOn(context)) retVal = true;
			}
			else
			{
				retVal = true;
			}
		}

		return retVal;
	}

	private void flashlightGlow()
	{
		new Thread()
		{
			public void run()
			{
				Camera camera = null;
				boolean flashOn = false;
				final int duration = MyPreference.getGlowDuration();

				if (duration > 0)
				{
					try
					{
						camera = Camera.open();

						Camera.Parameters param = camera.getParameters();
						param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
						camera.setParameters(param);

						camera.startPreview();

						flashOn = true;
					}
					catch (Exception e)
					{
						try
						{
							camera.release();
						}
						catch (Exception ex)
						{}
					}

					if (flashOn)
					{
						try
						{
							Thread.sleep(duration);
						}
						catch (InterruptedException e)
						{}
					}

					if (camera != null)
					{
						try
						{
							camera.release();
						}
						catch (Exception e)
						{}
					}
				}
			}
		}.start();
	}

	public void doTheMagic(boolean isTest)
	{
		if (mContext != null)
		{
			mIsTest = isTest;
			final Context context = mContext;

			if (MyPreference.isEnabled())
			{
				boolean waitForSensor = false;

				if (MyPreference.isScreenEnabled() && isScreenStateOk(context, isTest))
				{
					if (MyPreference.isCheckProximity())
					{
						engageProximitySensor();

						if (mSensorManager != null && mSensor != null)
						{
							waitForSensor = true;
						}
						else
						{
							finishMe();

							callFurtherActivity();
						}
					}
					else
					{
						callFurtherActivity();
					}
				}

				if (MyPreference.isFlashlightEnabled())
				{
					flashlightGlow();
				}

				if (isTest && !(MyPreference.isScreenEnabled() && isScreenStateOk(context, isTest)) && !MyPreference.isFlashlightEnabled())
				{
					outputError(R.string.toast_all_disabled);
				}

				if (waitForSensor)
				{
					try
					{
						Thread.sleep(600);
					}
					catch (InterruptedException e)
					{}
				}
			}
			else if (isTest)
			{
				outputError(R.string.toast_app_disabled);
			}
		}
	}

	private void callFurtherActivity()
	{
		if (mContext != null)
		{
			Context context = mContext;

			MyPreference.setContext(context);

			if (MyPreference.getGlowDuration() > 0)
			{
				Intent myIntent = new Intent(context, GlowingActivity.class);
				if (myIntent != null)
				{
					myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(myIntent);
				}
			}
			else
			{
				Intent myIntent = new Intent(context, WakingActivity.class);
				if (myIntent != null)
				{
					myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(myIntent);
				}
			}
		}
	}

	private synchronized void cleanupAllForFinish()
	{
		if (mScheduledExecutor != null)
		{
			mScheduledExecutor.shutdown();
			mScheduledExecutor = null;
		}

		if (mSensorManager != null)
		{
			mSensorManager.unregisterListener(this);
			mSensorManager = null;
		}
	}

	private synchronized void finishMe()
	{
		cleanupAllForFinish();
	}

	private void outputToast(final int textResId, final boolean shortDuration)
	{
		if (mContext instanceof Activity)
		{
			((Activity) mContext).runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						Toast.makeText(mContext, textResId, shortDuration ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
					}
					catch (Exception e)
					{}
				}
			});
		}
	}

	private void outputError(final int textResId)
	{
		outputToast(textResId, false);
	}
}
