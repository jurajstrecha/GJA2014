/**
 * SetFavoriteTweet.java
 * 
 * Class implements Favorite button on-click functionality. Marks/unmarks tweet
 * denoted by the id carried by the button as favorite. 
 */

package cz.fit.gja.twitter.model;

import cz.fit.gja.twitter.R;
import twitter4j.TwitterException;
import twitter4j.Twitter;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;
import cz.fit.gja.twitter.view.IdButton;

public class SetFavoriteTweet extends AsyncTask<Void, Void, Void> {

    private Twitter twitter;
    // represents the Favorite button, instance of the IdButton class
    private View    view;

    public SetFavoriteTweet(Twitter twitter, View view) {
        this.twitter = twitter;
        this.view = view;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        IdButton idButton = (IdButton) this.view;

        // User requests to mark given tweet as favorite
        if (idButton.isChecked()) {
            try {
                twitter.createFavorite(idButton.getTweetId());
            } catch (TwitterException te) {
                Toast.makeText(this.view.getContext(), R.string.msg_favorite_failed, Toast.LENGTH_LONG).show();
            }

            // User requests to unmark given tweet as favorite
        } else {
            try {
                twitter.destroyFavorite(idButton.getTweetId());
            } catch (TwitterException te) {
                Toast.makeText(this.view.getContext(), R.string.msg_favorite_failed, Toast.LENGTH_LONG).show();
            }
        }
        return null;
    }
}
