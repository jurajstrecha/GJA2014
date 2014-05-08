/**
 * Retweet.java
 * 
 * Class implements Retweet button on-click functionality by calling Twitter API
 * on status denoted by the ID contained within IdButton class instance.
 */

package cz.fit.gja.twitter.model;

import cz.fit.gja.twitter.R;
import cz.fit.gja.twitter.view.IdButton;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

public class Retweet extends AsyncTask<Void, Void, Void> {

    // represents the Favorite button, instance of the IdButton class
    private View    view;
    private Twitter twitter;

    public Retweet(View view, Twitter twitter) {
        this.view = view;
        this.twitter = twitter;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        IdButton idButton = (IdButton) this.view;
        try {
            twitter.retweetStatus(idButton.getTweetId());
        } catch (TwitterException te) {
            Toast.makeText(this.view.getContext(), R.string.msg_retweet_failed, Toast.LENGTH_LONG).show();
        }
        return null;
    }
}
