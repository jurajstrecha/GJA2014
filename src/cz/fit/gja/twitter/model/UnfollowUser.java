package cz.fit.gja.twitter.model;

import android.os.Handler;
import cz.fit.gja.twitter.adapters.UserAdapter;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class UnfollowUser extends ChangeRelationship {

	public UnfollowUser(Twitter twitter, String screenName, UserAdapter adapter, Handler refresh) {
		super(twitter, screenName, adapter, refresh);
	}

	@Override
	protected void issueChange(final Long id) throws TwitterException {
		twitter.destroyFriendship(screenName);
		refresh.post(new Runnable() {
			public void run() {
				adapter.unfollowed(id, screenName);
				adapter.notifyDataSetChanged();
			}
		});
	}
	
}
