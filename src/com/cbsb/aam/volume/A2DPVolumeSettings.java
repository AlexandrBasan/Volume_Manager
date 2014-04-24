package com.cbsb.aam.volume;


import com.cbsb.aam.volume.R;
import com.cbsb.aam.volume.R.string;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class A2DPVolumeSettings extends PreferenceActivity {
	private static final int IDM_About = 101;
	
	
  

	final static String TAG = "A2DPVolumeSettings";
    
    private boolean startServicePrefState;
    
    private SharedPreferences prefs;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        addPreferencesFromResource(R.xml.preferences);
    }
    
    @Override
  	public boolean onCreateOptionsMenu(Menu menu) {
      	menu.add(Menu.NONE, IDM_About, Menu.NONE, string.about).setIcon(R.drawable.ic_menu_info_details);
  		return super.onCreateOptionsMenu(menu);
  	}

  	@Override
  	public boolean onOptionsItemSelected(MenuItem item) {
  		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://proalab.com/?page_id=517"));
  		startActivity(browserIntent);
  		return super.onOptionsItemSelected(item);
  	}
    
    private void startService() {
        ComponentName service = startService(
                new Intent(this, HeadsetVolumeService.class));
        if (service == null)
            Log.e(TAG, "Unable to start service");
    }

    private void stopService() {
        boolean stopped = stopService(
                new Intent(this, HeadsetVolumeService.class));
        if (!stopped)
            Log.e(TAG, "Unable to stop service");
    }
    
    @Override
    public void onResume() {
        super.onResume();
        boolean startService = prefs.getBoolean(
                getString(R.string.key_enable_headset_service_flag), true);
        
        if (startService)
            startService();
        startServicePrefState = startService;
    }

    @Override
    public void onPause() {
        super.onPause();
        boolean startService = prefs.getBoolean(
                getString(R.string.key_enable_headset_service_flag), true);

        if (startService != startServicePrefState) {
            enableReceiver(startService);
            if (startService) {
                startService();
            } else {
                stopService();
            }
        }
    }
    
    private void enableReceiver(boolean enable) {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(
                "com.hanhuy.android.a2dp.volume",
                ".UserPresentBroadcastReceiver"),
                enable ?  PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
    }
}