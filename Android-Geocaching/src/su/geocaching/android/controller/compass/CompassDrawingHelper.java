package su.geocaching.android.controller.compass;

import android.graphics.Canvas;
import android.graphics.Color;
import su.geocaching.android.controller.Controller;
import su.geocaching.android.ui.R;

/**
 * Abstract class implements a strategy of drawing compass
 * 
 * @author Nikita Bumakov
 */
public abstract class CompassDrawingHelper {

    protected static final int DEFAULT_NEEDLE_WIDTH = 8;

    protected int center;
    protected int bgColor; // background color, taken from the xml
    protected int needleWidth = DEFAULT_NEEDLE_WIDTH;
    protected float distance;

    /**
     * @param distance
     *            the distance to set
     */
    public void setDistance(float distance) {
        this.distance = distance;
    }

    public CompassDrawingHelper() {
        bgColor = Color.parseColor(Controller.getInstance().getResourceManager().getString(R.color.menu_background));
    }

    /**
     * Draw a compass with a given direction of the compass's needle
     * 
     * @param canvas
     *            - drawing canvas
     * @param northDirection
     *            - direction to the North relative to 0 degrees
     */
    public abstract void draw(Canvas canvas, float northDirection);

    /**
     * called when resize the view
     * 
     * @param width
     *            - new width
     * @param height
     *            - new height
     */
    public abstract void onSizeChanged(int width, int height);

    /**
     * Draw cache arrow
     * 
     * @param canvas
     *            - drawing canvas
     * @param cacheDirection
     *            - direction to the cache relative to 0 degrees
     */
    public abstract void drawCacheArrow(Canvas canvas, float cacheDirection);
}