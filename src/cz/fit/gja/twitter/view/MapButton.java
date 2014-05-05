package cz.fit.gja.twitter.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class MapButton extends Button {
	 double tweetBoundries[] = null;
	 double tweetCoords[] = null;

	public MapButton(Context context) {
		super(context);
	}

	public MapButton (Context context, AttributeSet attrs) {
	    super(context, attrs);
	}
	
	public MapButton (Context context, AttributeSet attrs, int defStyle) {
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
