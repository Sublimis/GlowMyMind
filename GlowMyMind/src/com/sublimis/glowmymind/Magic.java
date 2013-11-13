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

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.PowerManager;

public class Magic implements SensorEventListener
{
	private Context mContext = null;
	
	private final int proximityDistanceThreshold = 3;	// centimeters
	private final int proximityDistanceCount = 1;		// how many times to poll the sensor
														// must be 1, otherwise will block

	private SensorManager mSensorManager;
	private Sensor mSensor;

	private float mProximityDistance[] = new float[proximityDistanceCount];
	private int mSensorEventCounter = 0;
	private volatile boolean isListening = false;
	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			if (isListening)
			{
				finishMe();
				
				if (MyPreference.isObscuredFlashlight() && !MyPreference.isFlashlightEnabled())
				{
					flashlightGlow();
				}
			}
		}
	};

	public Magic(Context context)
	{
		if (context != null)
		{
			mContext = context.getApplicationContext();

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
		if (isListening)
		{
			if (mSensorEventCounter < proximityDistanceCount)
				mProximityDistance[mSensorEventCounter++] = event.values[0];
			
			if (mSensorEventCounter >= proximityDistanceCount)
			{
				isListening = false;
				
				boolean okToProceed = true;
				
				for (float value : mProximityDistance)
				{
					if (value < proximityDistanceThreshold)
					{
						okToProceed = false;
						break;
					}
				}
				
				finishMe();
				
				if (okToProceed)
				{
					callFurtherActivity();
				}
				else if (MyPreference.isObscuredFlashlight() && !MyPreference.isFlashlightEnabled())
				{
					flashlightGlow();
				}
			}
		}
	}
	
	private void engageProximitySensor()
	{
		if (mContext != null)
			mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		
		if (mSensorManager != null)
		{
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
			
			if (mSensor != null)
			{
				isListening = true;

				// rate parameter really does nothing
				mSensorManager.registerListener(this, mSensor, 0);

				if (mHandler != null && mRunnable != null)
				{
					// needed because sensor will sometimes fail to deliver the initial event
					mHandler.postDelayed(mRunnable, 500);
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
				retVal =  pm.isScreenOn();
			}
		}
		catch (RuntimeException e)
		{
		}
		
		return retVal;
	}
	
	private boolean isScreenStateOk(Context context)
	{
		boolean retVal = false;
		
		if (MyPreference.isScreenEnabled())
		{
			if (MyPreference.isCheckScreen())
			{
				if (!isScreenOn(context))
					retVal = true;
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
						{
						}
					}
					
					if (flashOn)
					{
						try
						{
							Thread.sleep(duration);
						}
						catch (InterruptedException e)
						{
						}
					}
					
					if (camera != null)
					{
						try
						{
							camera.release();
						}
						catch (Exception e)
						{
						}
					}
				}
			}
		}.start();
	}
	
	public void doTheMagic()
	{
		if (mContext != null)
		{
			final Context context = mContext;
			
			if (MyPreference.isEnabled())
			{
				if (MyPreference.isScreenEnabled() && isScreenStateOk(context))
				{
					if (MyPreference.isCheckProximity())
					{
						mSensorEventCounter = 0;
						for (int i = 0; i < proximityDistanceCount; i++)
						{
							mProximityDistance[i] = -1;
						}

						engageProximitySensor();
						
						if (mSensorManager == null || mSensor == null)
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
		isListening = false;
		
		if (mHandler != null)
		{
			mHandler.removeCallbacks(mRunnable);
			mHandler = null;
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
}
