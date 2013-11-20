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

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.sublimis.glowmymind.R;

public class MainActivity extends PreferenceActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		boolean isTest = false;
		
		Intent intent = getIntent();
		
		if (intent != null)
		{
			Bundle extras = intent.getExtras();
			
			if (extras != null)
			{
				isTest = extras.getBoolean("test", false);
				
				if (isTest)
				{
					Magic magic = new Magic(this);
					magic.doTheMagic();
					
					finish();
				}
			}
		}
		
		if (!isTest)
		{
			// Load the preferences from an XML resource
			addPreferencesFromResource(R.layout.preferences);
			
			MyPreference.setContext(this);

			updateGlowDurationPrefSummary(MyPreference.getGlowDuration());			

			MyPrefChangeListener myPrefChangeListener = new MyPrefChangeListener();

			Preference pref = null;
			
			pref = findPreference(getResources().getString(R.string.pref_duration_key));
			if (pref != null)
			{
				pref.setOnPreferenceChangeListener(myPrefChangeListener);
			}
		}
	}
	
	private class MyPrefChangeListener implements Preference.OnPreferenceChangeListener
	{
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue)
		{
			boolean retVal = true;
			
			if (getResources().getString(R.string.pref_duration_key).equals(preference.getKey()))
			{
				int newDuration = -1;
				
				try
				{
					newDuration = Integer.valueOf((String) newValue);
				}
				catch (RuntimeException e)
				{
				}
				
				updateGlowDurationPrefSummary(newDuration);			
			}

			return retVal;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.optmenu_share_text).setIcon(android.R.drawable.ic_menu_share);
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.optmenu_about_text).setIcon(R.drawable.ic_menu_info_details);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case 0:
			dialogAbout();
			return true;
		case 1:
			dialogShare();
			return true;
		}
		return false;
	}
	
	protected Dialog onCreateDialog(int id)
	{
		Dialog dialog = null;

		switch (id)
		{
		case 0:
			{
				dialog = new Dialog(this);
	
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.dialog_about);
	
				TextView textView = (TextView) dialog.findViewById(R.id.aboutText1);
				String text = getResources().getString(R.string.copyright_part1);
				textView.setText(Html.fromHtml(String.format(text, Config.version, Config.versionDate)));
	
				textView = (TextView) dialog.findViewById(R.id.aboutText2);
				textView.setText(R.string.copyright_part2);
				textView.setMovementMethod(LinkMovementMethod.getInstance());
	
				ImageView image = (ImageView) dialog.findViewById(R.id.logoIcon);
				image.setImageResource(R.drawable.icon);
			}
			break;
			
		default:
			break;
		}
		
		return dialog;
	}

	private void dialogAbout()
	{
		showDialog(0);
	}

	private void dialogShare()
	{
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		if (sharingIntent != null)
		{
			sharingIntent.setType("text/plain");
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getText(R.string.share_subject));
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getText(R.string.app_link));
			startActivity(Intent.createChooser(sharingIntent, getResources().getText(R.string.share_title)));
		}
	}

	private void updateGlowDurationPrefSummary(int duration)
	{
		Preference pref = findPreference(getResources().getString(R.string.pref_duration_key));
		
		if (pref != null)
		{
			String[] entries = getResources().getStringArray(R.array.pref_duration_entries);
			String[] values = getResources().getStringArray(R.array.pref_duration_values);
			String summary = "";

			for (int i=0; i < values.length; i++)
			{
				try
				{
					if (duration == Integer.valueOf(values[i]))
					{
						summary = entries[i];
						break;
					}
				}
				catch (RuntimeException e)
				{
				}
			}
			
			((Preference) pref).setSummary(summary);
		}
	}
}
