package su.geocaching.android.ui.searchgeocache.stepbystep;

import su.geocaching.android.controller.CheckpointManager;
import su.geocaching.android.controller.Controller;
import su.geocaching.android.ui.R;
import su.geocaching.android.ui.geocachemap.CheckpointCacheOverlay;
import su.geocaching.android.ui.searchgeocache.DistanceToGeoCacheOverlay;
import su.geocaching.android.utils.GpsHelper;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.MapView;

public class CheckpointDialog extends Dialog {

    private Button active, delete;
    private TextView coordinates;

    private CheckpointCacheOverlay checkpointOverlay;
    private DistanceToGeoCacheOverlay distanceOverlay;
    private CheckpointManager checkpointManager;
    private MapView map;
    private int index;

    public CheckpointDialog(Context context, int index, CheckpointManager checkpointManager, CheckpointCacheOverlay checkpointCacheOverlay, DistanceToGeoCacheOverlay distanceOverlay, MapView map) {
        super(context);

        this.index = index;
        this.checkpointManager = checkpointManager;
        this.checkpointOverlay = checkpointCacheOverlay;
        this.distanceOverlay = distanceOverlay;
        this.map = map;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkpoint_dialog);      

        coordinates = (TextView) findViewById(R.id.checkpointCoordinate);       
        active = (Button) findViewById(R.id.checkpointActiveButton);
        delete = (Button) findViewById(R.id.checkpointDeleteButton);
        ButtonClickListener clickListener = new ButtonClickListener();
        active.setOnClickListener(clickListener);
        delete.setOnClickListener(clickListener);
    }

    @Override
    public void show() {
        setTitle(checkpointManager.getGeoCache(index).getName());
        coordinates.setText(GpsHelper.coordinateToString(checkpointManager.getGeoCache(index).getLocationGeoPoint()));
        super.show();
    }

    private class ButtonClickListener implements android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.equals(active)) {
                checkpointManager.setActiveItem(index);
            } else if (v.equals(delete)) {
                checkpointManager.removeCheckpointByIndex(index);
                checkpointOverlay.removeOverlayItem(index);
            }
            if (distanceOverlay != null) {
                distanceOverlay.setCachePoint(Controller.getInstance().getSearchingGeoCache().getLocationGeoPoint());
            }
            map.invalidate();
            dismiss();
        }
    }
}
