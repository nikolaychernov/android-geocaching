package su.geocaching.android.ui.preferences;

import android.os.Bundle;
import android.support.v4.app.SherlockPreferenceActivity;
import android.support.v4.view.MenuItem;
import su.geocaching.android.controller.Controller;
import su.geocaching.android.controller.managers.NavigationManager;
import su.geocaching.android.ui.R;

/**
 * @author: Yuri Denison
 * @since: 23.02.11
 */
public class MapPreferenceActivity extends SherlockPreferenceActivity {

    private static final String MAP_PREFERENCE_ACTIVITY_NAME= "/preferences/Map";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Controller.getInstance().getGoogleAnalyticsManager().trackActivityLaunch(MAP_PREFERENCE_ACTIVITY_NAME);
        addPreferencesFromResource(R.xml.map_preference);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavigationManager.startDashboardActivity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }    
}