package com.sublimis.glowmymind;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import com.sublimis.glowmymind.R;

public class MyPreference
{
	private static Context mContext = null;

	public synchronized static void setContext(Context context)
	{
		if (mContext == null)
			mContext = context.getApplicationContext();
	}
	
	private static SharedPreferences getPrefs()
	{
		if (mContext != null)
			return PreferenceManager.getDefaultSharedPreferences(mContext);
		else
			return null;
	}

	private static Resources getResources()
	{
		if (mContext != null)
			return mContext.getResources();
		else
			return null;
	}
	
	private static String getStringResource(int resId)
	{
		Resources res = getResources();
		
		if (res != null)
			return res.getString(resId);
		else
			return null;
	}
	
	private static boolean getBooleanPref(int prefKeyResId, boolean defaultValue)
	{
		String prefKey = getStringResource(prefKeyResId);
		SharedPreferences sharedPrefs = getPrefs();
		
		if (sharedPrefs != null)
			return sharedPrefs.getBoolean(prefKey, defaultValue);
		else
			return defaultValue;
	}
	
	public static String getStringPref(int prefKeyResId, String defaultValue)
	{
		String prefKey = getStringResource(prefKeyResId);
		SharedPreferences sharedPrefs = getPrefs();
		
		if (sharedPrefs != null)
			return sharedPrefs.getString(prefKey, defaultValue);
		else
			return defaultValue;
	}
	
	
	public static boolean isEnabled()
	{
		return getBooleanPref(R.string.pref_enabled_key, true);
	}

	public static int getGlowDuration()
	{
		int retVal = Config.glowDurationDefault;
		
		try
		{
			retVal = Integer.valueOf(getStringPref(R.string.pref_duration_key, Integer.toString(Config.glowDurationDefault)));
		}
		catch (RuntimeException e)
		{
		}
		
		return retVal;
	}
	
	public static boolean isCheckScreen()
	{
		return getBooleanPref(R.string.pref_screen_key, true);
	}
}
