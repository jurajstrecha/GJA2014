package cz.fit.gja.twitter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cz.fit.gja.twitter.adapters.UserAdapter;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.PagableResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * Handles fetching and displaying of users somehow related to account
 */
abstract public class RelatedUsersActivity extends LoggedActivity {

    protected ListView    userList;
    protected UserAdapter userAdapter;
    protected long        cursor = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users);
        setTitle(getTitleId());

        View currentView = this.findViewById(android.R.id.content);

        // loading animation
        final ProgressBar pb = (ProgressBar) currentView.findViewById(R.id.progressBar);

        // empty results message
        final TextView empty = (TextView) currentView.findViewById(R.id.usersEmpty);
        empty.setText(getEmptyId());
        empty.setVisibility(View.GONE);

        final RelatedUsersActivity activity = this;
        userList = (ListView) currentView.findViewById(R.id.users);
        userList.setScrollContainer(false);

        userAdapter = new UserAdapter(activity, twitter);
        userList.setAdapter(userAdapter);

        // fetches more users
        // users Runnable and Handler to access this thread's owned objects from
        // other one
        final Handler refresh = new Handler(Looper.getMainLooper());
        final Runnable loadUsers = new Runnable() {

            public void run() {
                if (cursor == 0) {
                    return;
                }

                try {
                    PagableResponseList<User> list = getList(userId, cursor);
                    if (cursor == -1 && list.isEmpty()) {
                        refresh.post(new Runnable() {

                            public void run() {
                                userList.setVisibility(View.GONE);
                                pb.setVisibility(View.GONE);
                                empty.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        cursor = list.getNextCursor();
                        userAdapter.addUsers(list);
                        refresh.post(new Runnable() {

                            public void run() {
                                userAdapter.notifyDataSetChanged();
                                pb.setVisibility(View.GONE);
                            }
                        });
                    }
                } catch (TwitterException ex) {
                    Logger.getLogger(FollowingActivity.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        // issue loading of next batch of users
        userList.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userList == null || userAdapter == null || userList.getChildAt(userList.getChildCount() - 1) == null) {
                    return;
                }

                // scrolled to the bottom
                if (userList.getLastVisiblePosition() == userAdapter.getCount() - 1 &&
                    userList.getChildAt(userList.getChildCount() - 1).getBottom() <= userList.getHeight()) {
                    (new Thread(loadUsers)).start();
                }
            }
        });

        (new Thread(loadUsers)).start();
    }

    /**
     * Returns list of users
     * 
     * @param userId
     * @param cursor
     * @return
     * @throws TwitterException
     */
    abstract protected PagableResponseList<User> getList(Long userId, long cursor) throws TwitterException;

    /**
     * Returns id of a string for menu bar title
     * 
     * @return
     */
    abstract protected Integer getTitleId();

    /**
     * Returns id of a string for "no results"
     * 
     * @return
     */
    abstract protected Integer getEmptyId();

}
