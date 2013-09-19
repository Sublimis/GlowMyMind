package com.sublimis.glowmymind;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.sublimis.glowmymind.R;

public class WakingActivity extends Activity
{
    @Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		
		setContentView(R.layout.waking);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		finish();
	}

    @Override
    public void onDestroy()
    {
		super.onDestroy();
    }
}
