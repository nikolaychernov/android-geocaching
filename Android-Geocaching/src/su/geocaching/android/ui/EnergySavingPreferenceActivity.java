package su.geocaching.android.ui;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import su.geocaching.android.controller.LogManager;

public class EnergySavingPreferenceActivity extends PreferenceActivity {
    
    private static final String TAG = EnergySavingPreferenceActivity.class.getCanonicalName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogManager.d(TAG, "onCreate");
        addPreferencesFromResource(R.xml.energy_saving_preference);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
