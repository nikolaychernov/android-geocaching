package su.geocaching.android.ui.selectmap;

import android.graphics.Point;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import su.geocaching.android.model.GeoCache;
import su.geocaching.android.model.GeoCacheType;
import su.geocaching.android.ui.map.GeocacheMarkerTapListener;
import su.geocaching.android.ui.map.GoogleMapWrapper;
import su.geocaching.android.ui.map.GoogleMarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SelectGoogleMapWrapper extends GoogleMapWrapper implements ISelectMapWrapper {

    private HashMap<String, GeoCache> markers = new HashMap<String, GeoCache>();
    private HashMap<Integer, Marker> geocacheMarkers = new HashMap<Integer, Marker>();
    private List<Marker> groupMarkers = new ArrayList<Marker>();
    private GeocacheMarkerTapListener geocacheMarkerTapListener;

    public SelectGoogleMapWrapper(final GoogleMap mMap) {
        super(mMap);
    }

    @Override
    protected boolean onMarkerTap(Marker marker) {
        GeoCache geoCache = markers.get(marker.getId());
        if (geoCache == null) return false;

        if (geoCache.getType() == GeoCacheType.GROUP) {
            LatLng latLng = getCacheLocation(geoCache);
            Point point = googleMap.getProjection().toScreenLocation(latLng);
            googleMap.animateCamera(CameraUpdateFactory.zoomBy(1, point));
        } else {
            if (geocacheMarkerTapListener != null) {
                geocacheMarkerTapListener.OnMarkerTapped(geoCache);
            }
        }
        return true;
    }

    @Override
    public void clearGeocacheMarkers() {
        // delete geocache markers
        for (Marker marker : geocacheMarkers.values()) {
            removeGeoCacheMarker(marker);
        }
        geocacheMarkers.clear();

        // delete group markers
        for (Marker marker : groupMarkers) {
            removeGeoCacheMarker(marker);
        }
        groupMarkers.clear();
    }

    @Override
    public void updateGeoCacheMarkers(List<GeoCache> geoCacheList) {
        //TODO Optimize. Reuse existing group markers
        for (Marker marker : groupMarkers) {
            removeGeoCacheMarker(marker);
        }
        groupMarkers.clear();

        HashSet<Integer> cacheIds = new HashSet<Integer>();

        for (GeoCache geoCache : geoCacheList) {
            if (geoCache.getType() == GeoCacheType.GROUP) {
                Marker marker = addGeoCacheMarker(geoCache);
                groupMarkers.add(marker);
            } else {
                if (!geocacheMarkers.containsKey(geoCache.getId())) {
                    Marker marker = addGeoCacheMarker(geoCache);
                    geocacheMarkers.put(geoCache.getId(), marker);
                }
                cacheIds.add(geoCache.getId());
            }
        }

        // remove retired cache markers
        for (Integer cacheId : geocacheMarkers.keySet().toArray(new Integer[geocacheMarkers.size()])) {
            if (!cacheIds.contains(cacheId)) {
                removeGeoCacheMarker(geocacheMarkers.get(cacheId));
                geocacheMarkers.remove(cacheId);
            }
        }
    }

    private Marker addGeoCacheMarker(GeoCache geoCache) {
        Marker marker = googleMap.addMarker(GoogleMarkerOptions.fromGeocache(geoCache));
        markers.put(marker.getId(), geoCache);
        return marker;
    }

    private void removeGeoCacheMarker(Marker marker) {
        markers.remove(marker.getId());
        marker.remove();
    }

    @Override
    public void setGeocacheTapListener(GeocacheMarkerTapListener listener) {
        geocacheMarkerTapListener = listener;
    }
}