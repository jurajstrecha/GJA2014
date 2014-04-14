package cz.fit.gja.twitter;

import static cz.fit.gja.twitter.BaseActivity.twitter;
import twitter4j.PagableResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

public class FollowingActivity extends RelatedUsersActivity {

    @Override
    protected PagableResponseList<User> getList(Long userId, long cursor) throws TwitterException {
        return twitter.getFriendsList(userId, cursor);
    }

    @Override
    protected Integer getTitleId() {
        return R.string.title_following;
    }

    @Override
    protected Integer getEmptyId() {
        return R.string.user_not_following;
    }

}
