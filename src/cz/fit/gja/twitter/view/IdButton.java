/**
 * IdButton.java
 * 
 * Class extends Android ToggleButton capabilities by adding ID of the tweet
 * that the button belongs to. Set and Get methods are provided for ID access
 * and modification purpose. On-click method uses ID for request calls.
 */

package cz.fit.gja.twitter.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

public class IdButton extends ToggleButton {

    private long tweetID;

    public IdButton(Context context) {
        super(context);
    }

    public IdButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IdButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setTweetId(long newTweetId) {
        this.tweetID = newTweetId;
    }

    public long getTweetId() {
        return this.tweetID;
    }
}
