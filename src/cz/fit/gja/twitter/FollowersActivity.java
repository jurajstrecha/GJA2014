package cz.fit.gja.twitter;

import static cz.fit.gja.twitter.BaseActivity.twitter;
import twitter4j.PagableResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

public class FollowersActivity extends RelatedUsersActivity {

	@Override
	protected PagableResponseList<User> getList(Long userId, long cursor) throws TwitterException {
		return twitter.getFollowersList(userId, cursor);
	}
	
	@Override
	protected Integer getTitleId() {
		return R.string.title_followers;
	}
		
}
