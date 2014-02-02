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

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class MagicService extends IntentService
{
	public static final String INTENT_ACTION = "com.sublimis.glowmymind.MagicService.ACTION";
	public static final String ACTION_COMMAND = "com.sublimis.glowmymind.COMMAND";
	public static final String TEST_MODE_EXTRA = "com.sublimis.glowmymind.TestMode";
	public static final int COMMAND_DO_MAGIC = 1;

	public MagicService()
	{
		super(MagicService.class.getName());
	}
	
	public static void startService(Context context, boolean isTest)
	{
        Intent intent = new Intent(INTENT_ACTION);
        intent.putExtra(ACTION_COMMAND, COMMAND_DO_MAGIC);
        intent.putExtra(TEST_MODE_EXTRA, isTest);
        
		intent.setClassName(context, MagicService.class.getName());
		context.startService(intent);
	}
	
	@Override
	public final void onHandleIntent(Intent intent)
	{
		try
		{
			if (intent != null && INTENT_ACTION.equals(intent.getAction()))
			{
				MyPreference.setContext(this);

				int command = intent.getIntExtra(ACTION_COMMAND, -1);
				
				switch (command)
				{
				case COMMAND_DO_MAGIC:
					boolean isTest = intent.getBooleanExtra(TEST_MODE_EXTRA, false);

					Magic magic = new Magic(this);
					magic.doTheMagic(isTest);
					
					break;
				
				default:
		        	break;
				}
			}
		}
		catch(Exception e)
		{
		}
	}
}
