package su.geocaching.android.controller.managers;

import su.geocaching.android.controller.Controller;
import su.geocaching.android.ui.R;
import android.content.Context;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class GoogleAnalyticsManager {
    private static final boolean DEBUG = Controller.DEBUG; // it is constant really need, because compiler can remove code blocks which cannot be execute
    private GoogleAnalyticsTracker tracker;

    public GoogleAnalyticsManager(Context context) {
        if (!DEBUG) {
            tracker = GoogleAnalyticsTracker.getInstance();
            tracker.start(context.getString(R.string.id_Google_Analytics), context);
        }
    }

    public void trackPageView(String activityName) {
        if (!DEBUG) {
            tracker.trackPageView(activityName);
            tracker.dispatch();
        }
    }

    public void trackEvent(String category, String action, String opt_label, int opt_value) {
        if (!DEBUG) {
            tracker.trackEvent(category, action, opt_label, opt_value);
            tracker.dispatch();
        }
    }
}