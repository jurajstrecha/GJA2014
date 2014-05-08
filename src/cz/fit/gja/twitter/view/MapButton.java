package cz.fit.gja.twitter.view;

/**
 * File: MapButton.java
 * 
 *  Class represents button used to store one of two types of location data.
 *  Boundries if user didn't provide exact geo position or exact coordinates
 *  that belong to the tweet. When the button is pressed, data stored within
 *  are passed to the underlying methods that creates map view according
 *  to given coordinates or geo position points.  
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class MapButton extends Button {

    double tweetBoundries[] = null;
    double tweetCoords[]    = null;

    public MapButton(Context context) {
        super(context);
    }

    public MapButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setCoords(double coords[]) {
        this.tweetCoords = coords;
    }

    public double[] getCoords() {
        return this.tweetCoords;
    }

    public void setBounds(double bounds[]) {
        this.tweetBoundries = bounds;
    }

    public double[] getBounds() {
        return this.tweetBoundries;
    }
}
