package com.sublimis.glowmymind;

import com.sublimis.glowmymind.MyPreference;
import com.sublimis.glowmymind.GlowingActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class Receiver extends BroadcastReceiver
{
	public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	public static final String WAP_PUSH_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED";

	private boolean isOkToFire(Context context)
	{
		boolean retVal = false;
		
		try
		{
			if (context != null)
			{
				MyPreference.setContext(context);
				
				if (MyPreference.isEnabled())
				{
					if (MyPreference.isCheckScreen())
					{
						PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
						
						if (pm != null && !pm.isScreenOn())
						{
							retVal = true;
						}
					}
					else
					{
						retVal = true;
					}
				}
			}
		}
		catch (RuntimeException e)
		{
		}
		
		return retVal;
	}
	
	@Override
    public void onReceive(Context context, Intent intent)
    {
		try
		{
			if (context != null && intent != null)
			// We want action for both SMS and MMS messages
			if (SMS_RECEIVED.equals(intent.getAction()) || WAP_PUSH_RECEIVED.equals(intent.getAction()))
			if (isOkToFire(context))
			{
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
		catch (RuntimeException e)
		{
		}
    }
}
