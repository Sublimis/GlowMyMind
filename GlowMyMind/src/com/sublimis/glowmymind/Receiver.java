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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Receiver extends BroadcastReceiver
{
	public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	public static final String WAP_PUSH_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED";

	@Override
	public void onReceive(Context context, Intent intent)
	{
		try
		{
			if (context != null && intent != null)
			{
				// We want action for both SMS and MMS messages
				if (SMS_RECEIVED.equals(intent.getAction()) || WAP_PUSH_RECEIVED.equals(intent.getAction()))
				{
					MagicService.startService(context, false);
				}
			}
		}
		catch (RuntimeException e)
		{}
	}
}
