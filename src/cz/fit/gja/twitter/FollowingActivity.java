package cz.fit.gja.twitter;

import twitter4j.PagableResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * Activity for listing users that the account follows
 * 
 * @see RelatedUsersActivity
 * @see UserAdapter
 */
public class FollowingActivity extends RelatedUsersActivity {

    /**
     * Returns list of users
     * 
     * @param userId
     * @param cursor
     * @return
     * @throws TwitterException
     */
    @Override
    protected PagableResponseList<User> getList(Long userId, long cursor) throws TwitterException {
        return twitter.getFriendsList(userId, cursor);
    }

    /**
     * Returns id of a string for menu bar title
     * 
     * @return
     */
    @Override
    protected Integer getTitleId() {
        return R.string.title_following;
    }

    /**
     * Returns id of a string for "no results"
     * 
     * @return
     */
    @Override
    protected Integer getEmptyId() {
        return R.string.user_not_following;
    }

}
