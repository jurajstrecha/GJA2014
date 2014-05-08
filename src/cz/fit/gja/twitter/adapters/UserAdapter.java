/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fit.gja.twitter.adapters;

import cz.fit.gja.twitter.model.PortraitLoader;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cz.fit.gja.twitter.R;
import cz.fit.gja.twitter.model.FollowUser;
import cz.fit.gja.twitter.model.UnfollowUser;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.IDs;
import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * Handles displaying of lists of users
 */
public class UserAdapter extends BaseAdapter {

    protected Context           context;
    protected Twitter           twitter;
    protected LayoutInflater    inflater;
    protected List<User>        list     = new ArrayList<User>();
    protected PortraitLoader    portraitLoader;

    // as long as we only allow to see lists of current user
    protected static List<Long> followed = new ArrayList<Long>();

    public UserAdapter(Context context, Twitter twitter) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.twitter = twitter;

        if (followed.isEmpty()) {
            (new LoadFriends(twitter)).execute();
        }
    }

    public static void addFriendsIds(List<Long> ids) {
        for (Long id : ids) {
            followed.add(id);
        }
    }

    public void addUsers(PagableResponseList<User> response) {
        for (User user : response) {
            this.list.add(user);
        }
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public View getView(int position, View itemView, ViewGroup parent) {
        final UserAdapter adapter = this;
        TextView tv;
        ImageView iv;
        Button b;

        final User user = getItem(position);
        itemView = this.inflater.inflate(R.layout.item_user, null);

        if (user == null) {
            return itemView;
        }

        tv = (TextView) itemView.findViewById(R.id.fullName);
        if (tv != null) {
            tv.setText(user.getName());
        }

        tv = (TextView) itemView.findViewById(R.id.twitterName);
        if (tv != null) {
            tv.setText("@" + user.getScreenName());
        }

        tv = (TextView) itemView.findViewById(R.id.bio);
        if (tv != null) {
            tv.setText(user.getDescription());
        }

        iv = (ImageView) itemView.findViewById(R.id.portrait);
        if (iv != null) {
            if (PortraitLoader.hasPortrait(user.getScreenName())) {
                iv.setImageBitmap(PortraitLoader.getPortrait(user.getScreenName()));
            } else {
                final Handler refresh = new Handler(Looper.getMainLooper());
                (new PortraitLoader(user, (ImageView) itemView.findViewById(R.id.portrait), refresh)).execute();
            }
        }

        if (followed.contains(user.getId())) {
            b = (Button) itemView.findViewById(R.id.follow);
            b.setVisibility(View.GONE);
            b = (Button) itemView.findViewById(R.id.unfollow);
            b.setVisibility(View.VISIBLE);
        } else {
            b = (Button) itemView.findViewById(R.id.unfollow);
            b.setVisibility(View.GONE);
            b = (Button) itemView.findViewById(R.id.follow);
            b.setVisibility(View.VISIBLE);
        }

        b = (Button) itemView.findViewById(R.id.follow);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Handler refresh = new Handler(Looper.getMainLooper());
                (new FollowUser(twitter, user.getScreenName(), adapter, refresh)).execute();
            }
        });

        b = (Button) itemView.findViewById(R.id.unfollow);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Handler refresh = new Handler(Looper.getMainLooper());
                (new UnfollowUser(twitter, user.getScreenName(), adapter, refresh)).execute();
            }
        });

        return itemView;
    }

    public void followed(Long id, String screenName) {
        if (followed.contains(id) == false) {
            Toast.makeText(context, String.format(context.getString(R.string.user_msg_followed), "@" + screenName), Toast.LENGTH_SHORT).show();
            followed.add(id);
        }
    }

    public void unfollowed(Long id, String screenName) {
        if (followed.contains(id)) {
            Toast.makeText(context, String.format(context.getString(R.string.user_msg_unfollowed), "@" + screenName), Toast.LENGTH_SHORT).show();
            followed.remove(id);
        }
    }

    @Override
    public User getItem(int i) {
        return this.list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // bypassing a bug, keep it here
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }

    private class LoadFriends extends AsyncTask<Void, Void, List<Long>> {

        final private Twitter twitter;

        public LoadFriends(Twitter twitter) {
            this.twitter = twitter;
        }

        @Override
        protected List<Long> doInBackground(Void... params) {
            try {
                List<Long> ids = new ArrayList<Long>();
                long cursor = -1;
                IDs result;

                do {
                    result = twitter.getFriendsIDs(cursor);
                    for (Long id : result.getIDs()) {
                        ids.add(id);
                    }
                } while ((cursor = result.getNextCursor()) != 0);

                return ids;
            } catch (TwitterException ex) {
                Logger.getLogger(UserAdapter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalStateException ex) {
                Logger.getLogger(UserAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Long> result) {
            addFriendsIds(result);
            notifyDataSetChanged();
            super.onPostExecute(result);
        }

    }

}
