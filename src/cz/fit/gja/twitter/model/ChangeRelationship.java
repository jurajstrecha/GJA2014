package cz.fit.gja.twitter.model;

import android.os.AsyncTask;
import android.os.Handler;
import cz.fit.gja.twitter.adapters.UserAdapter;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.Twitter;
import twitter4j.TwitterException;

abstract public class ChangeRelationship extends AsyncTask<Void, Void, Void> {

    protected final twitter4j.Twitter twitter;
    protected final String            screenName;
    protected final UserAdapter       adapter;
    protected final Handler           refresh;

    public ChangeRelationship(Twitter twitter, String screenName, UserAdapter adapter, Handler refresh) {
        this.twitter = twitter;
        this.screenName = screenName;
        this.adapter = adapter;
        this.refresh = refresh;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            long id = twitter.getId();
            issueChange(id);
        } catch (TwitterException ex) {
            Logger.getLogger(ChangeRelationship.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    abstract protected void issueChange(Long id) throws TwitterException;

}
