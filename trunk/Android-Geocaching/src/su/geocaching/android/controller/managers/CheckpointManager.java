package su.geocaching.android.controller.managers;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import su.geocaching.android.controller.Controller;
import su.geocaching.android.controller.CoordinateHelper;
import su.geocaching.android.model.GeoCache;
import su.geocaching.android.model.GeoCacheStatus;
import su.geocaching.android.model.GeoCacheType;
import su.geocaching.android.ui.R;
import su.geocaching.android.ui.geocachemap.GeoCacheOverlayItem;

import com.google.android.maps.GeoPoint;

public class CheckpointManager {

    private List<GeoCache> checkpoints;
   // private List<GeoCache> infoCheckpoints;
    private GeoPoint lastInputGP;
    private int checkpointNumber = 0;
    private int cacheId;

    private DbManager dbm;
    private Controller controller;

    public CheckpointManager() {
        controller = Controller.getInstance();
        dbm = controller.getDbManager();
        checkpoints = new LinkedList<GeoCache>();
    }

    public CheckpointManager(int id) {
        this();
        cacheId = id;
        checkpoints = dbm.getCheckpointsArrayById(id);
        for (GeoCache checkpoint : checkpoints) {
            if (checkpoint.getId() > checkpointNumber) {
                checkpointNumber = checkpoint.getId();
            }
        }
    }

    /**
     * @return the cacheId
     */
    public int getCacheId() {
        return cacheId;
    }

    /**
     * @param latitudeE6
     * @param longitudeE6
     * @return
     */
    public GeoCacheOverlayItem addCheckpoint(int latitudeE6, int longitudeE6, int cacheId) {
        deactivateCheckpoints();
        checkpointNumber++;
        GeoCache gc = new GeoCache();
        gc.setName(String.format("%s %d", controller.getResourceManager().getString(R.string.checkpoint_dialog_title), checkpointNumber));
        gc.setLocationGeoPoint(new GeoPoint(latitudeE6, longitudeE6));
        gc.setType(GeoCacheType.CHECKPOINT);
        gc.setStatus(GeoCacheStatus.ACTIVE_CHECKPOINT);
        gc.setId(checkpointNumber);
        checkpoints.add(gc);

        dbm.addCheckpointGeoCache(gc, cacheId);
        controller.setSearchingGeoCache(gc);
        return new GeoCacheOverlayItem(gc, "", "");
    }

    public void deactivateCheckpoints() {
        for (GeoCache checkpoint : checkpoints) {
            if (checkpoint.getStatus() == GeoCacheStatus.ACTIVE_CHECKPOINT) {
                checkpoint.setStatus(GeoCacheStatus.NOT_ACTIVE_CHECKPOINT);
                dbm.ubdateCheckpointCacheStatus(controller.getPreferencesManager().getLastSearchedGeoCache().getId(), checkpoint.getId(), GeoCacheStatus.NOT_ACTIVE_CHECKPOINT);
            }
        }
    }

    private void removeCheckpointByIndex(int index) {
        GeoCache gc = checkpoints.get(index);
        if (gc.getStatus() == GeoCacheStatus.ACTIVE_CHECKPOINT) {
            controller.setSearchingGeoCache(controller.getPreferencesManager().getLastSearchedGeoCache());
        }
        dbm.deleteCheckpointCache(controller.getPreferencesManager().getLastSearchedGeoCache().getId(), gc.getId());
        checkpoints.remove(index);
    }

    public int removeCheckpoint(int id) {
        int index = findItemById(id);
        if (id != -1) {
            removeCheckpointByIndex(index);
        }
        return index;
    }

    public GeoCache getGeoCache(int id) {
        return checkpoints.get(findItemById(id));
    }

    /**
     * @return the activeItem
     */
    public int getActiveItem() {
        int i = 0;
        for (GeoCache checkpoint : checkpoints) {
            if (checkpoint.getStatus() == GeoCacheStatus.ACTIVE_CHECKPOINT) {
                break;
            }
            i++;
        }
        return i >= checkpoints.size() ? -1 : i;
    }

    /**
     * @param activeItemS
     *            the activeItem to set
     */
    public void setActiveItem(int id) {
        int activeItem = findItemById(id);
        deactivateCheckpoints();
        checkpoints.get(activeItem).setStatus(GeoCacheStatus.ACTIVE_CHECKPOINT);
        controller.setSearchingGeoCache(checkpoints.get(activeItem));
        dbm.ubdateCheckpointCacheStatus(controller.getPreferencesManager().getLastSearchedGeoCache().getId(), checkpoints.get(activeItem).getId(), GeoCacheStatus.ACTIVE_CHECKPOINT);
    }

    /**
     * @param id
     *            the Id of active item
     */
    public void setActiveItemById(int id) {
        int index = findItemById(id);
        if (id != -1) {
            setActiveItem(index);
        }
    }

    private int findItemById(int id) {
        for (GeoCache item : checkpoints) {
            if (item.getId() == id) {
                return checkpoints.indexOf(item);
            }
        }
        return -1;
    }

    /**
     * @return the checkpoints
     */
    public List<GeoCache> getCheckpoints() {
        return checkpoints;
    }

    public void clear() {
        dbm.deleteCheckpointCache(cacheId);
        checkpoints.clear();
    }

    public void setLastInputGeoPoint(GeoPoint geoPoint) {
        lastInputGP = geoPoint;
    }

    public GeoPoint getLastInputGeoPoint() {
        return lastInputGP;
    }



    public static String insertCheckpointsLinkAndSaveInDB(String text, int cacheId){
        Pattern geoPattern = Pattern.compile("[N|S]\\s*(\\d+)\\s*(<sup>&#9702;</sup>|&rsquo;)\\s*(\\d+)\\s*.\\s*(\\d+)\\s*/?\\s*[E|W]\\s*(\\d+)\\s*(<sup>&#9702;</sup>|&rsquo;)\\s*(\\d+)\\s*.\\s*(\\d+)");   //<a href="geo:0,0?q="><b>N 59<sup>&#9702;</sup>52.513 E 029<sup>&#9702;</sup>56.664</b></a>
        Matcher pageMatcher = geoPattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        List<GeoCache> checkpoints = new LinkedList<GeoCache>();
        DbManager dbManager = Controller.getInstance().getDbManager();

        int checkpointId = 0;
        while (pageMatcher.find()) {
            int latitude = 0;
            int lngitude = 0;
            try {
                 latitude =  CoordinateHelper.sexagesimalToCoordinateE6(Integer.parseInt(pageMatcher.group(1)), Integer.parseInt(pageMatcher.group(3)), Integer.parseInt(pageMatcher.group(4)));
                 lngitude =  CoordinateHelper.sexagesimalToCoordinateE6(Integer.parseInt(pageMatcher.group(5)), Integer.parseInt(pageMatcher.group(7)),Integer.parseInt(pageMatcher.group(8)));
            } catch (Exception e) {
                break;
            }

            pageMatcher.appendReplacement(sb, String.format("<a href=\"checkpoint_id=%d\"><b>%s</b></a>", checkpointId++, pageMatcher.group(0)));
            dbManager.addInfoCheckpointGeoCache(cacheId, latitude, lngitude);
        }

        pageMatcher.appendTail(sb);
        return sb.toString();
    }
}