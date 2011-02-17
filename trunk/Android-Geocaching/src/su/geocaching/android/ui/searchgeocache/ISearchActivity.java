package su.geocaching.android.ui.searchgeocache;

import android.location.Location;

/**
 * Activity which search geocache
 * 
 * @author Grigory Kalabin. grigory.kalabin@gmail.com
 * @since Nov 18, 2010
 */
public interface ISearchActivity {
    public final static int STATUS_TYPE_GPS = 0;
    public final static int STATUS_TYPE_INTERNET = 1;

    /**
     * Init and run all search logic
     */
    public void runLogic();

    /**
     * @param status
     *            - show status to user
     * @param type
     *            of engine which status has been changed
     */
    public void updateStatus(String status, StatusType type);

    /**
     * @param location
     *            new user location called when user location is changer
     */
    public void updateLocation(Location location);

    /**
     * @param new user bearing called when user bearing is changer
     */
    public void updateBearing(float bearing);

    /**
     * @return last location obtained from GPS
     */
    public Location getLastKnownLocation();

    /**
     * Tell to user that best provider became unavailable
     */
    public void onBestProviderUnavailable();
}