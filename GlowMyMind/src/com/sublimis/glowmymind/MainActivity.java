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
import android.os.Bundle;
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

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.layout.preferences);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE, 0, 0, R.string.optmenu_about_text).setIcon(R.drawable.ic_menu_info_details);

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
}
