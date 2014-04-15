/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fit.gja.twitter.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.fit.gja.twitter.R;
import cz.fit.gja.twitter.model.PortraitLoader;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cz.fit.gja.twitter.TweetActivity;

public class TweetAdapter extends BaseAdapter {

    protected Context                context;
    protected Twitter                twitter;
    protected LayoutInflater         layoutInflater;
    protected List<twitter4j.Status> statuses = new ArrayList<twitter4j.Status>();

    public TweetAdapter(Context context, Twitter twitter) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.twitter = twitter;

        new LoadTimeline().execute();
    }

    @Override
    public int getCount() {
        return this.statuses.size();
    }

    @Override
    public twitter4j.Status getItem(int i) {
        return this.statuses.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final TweetAdapter tweetAdapter = this;
        TextView textView;
        ImageView imageView;
        Button button;

        final twitter4j.Status status = getItem(i);
        final User user = status.getUser();
        view = this.layoutInflater.inflate(R.layout.item_tweet, null);

        textView = (TextView) view.findViewById(R.id.tweet_fullName);
        if (textView != null) {
            textView.setText(user.getName());
        }

        textView = (TextView) view.findViewById(R.id.tweet_time);
        if (textView != null) {
            SimpleDateFormat format = new SimpleDateFormat("hh:mm a '-' d.M", Locale.getDefault());
            textView.setText(format.format(status.getCreatedAt()));
        }
        
        textView = (TextView) view.findViewById(R.id.tweet_twitterName);
        if (textView != null) {
            textView.setText("@" + user.getScreenName());
        }
        
        textView = (TextView) view.findViewById(R.id.tweet_text);
        if (textView != null) {
            textView.setText(status.getText());
        }
        Log.d(user.getName(), status.getText());

        imageView = (ImageView) view.findViewById(R.id.tweet_portrait);
        if (imageView != null) {
            if (PortraitLoader.hasPortrait(user.getScreenName())) {
                imageView.setImageBitmap(PortraitLoader.getPortrait(user.getScreenName()));
            } else {
                final Handler refresh = new Handler(Looper.getMainLooper());
                (new PortraitLoader(user, (ImageView) view.findViewById(R.id.tweet_portrait), refresh)).execute();
            }
        }

        button = (Button) view.findViewById(R.id.tweet_reply);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TweetActivity.class);
				intent.putExtra("tweet", "@" + user.getScreenName() + " ");
				intent.putExtra("replyToId", status.getId());
				intent.putExtra("replyToText", status.getText());
                context.startActivity(intent);
            }
        });

        button = (Button) view.findViewById(R.id.tweet_retweet);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //final Handler refresh = new Handler(Looper.getMainLooper());
                //(new UnfollowUser(twitter, user.getScreenName(), adapter, refresh)).execute();
            }
        });  
        
        button = (Button) view.findViewById(R.id.tweet_favorite);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //final Handler refresh = new Handler(Looper.getMainLooper());
                //(new UnfollowUser(twitter, user.getScreenName(), adapter, refresh)).execute();
            }
        });
        
        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * AsyncTask to load Timeline
     * */
    private class LoadTimeline extends AsyncTask<Void, Void, List<twitter4j.Status>> {

        /**
         * Getting Timeline
         */
        protected List<twitter4j.Status> doInBackground(Void... args) {
            List<twitter4j.Status> statuses = null;
            try {
                statuses = twitter.getHomeTimeline();
            } catch (TwitterException e) {
                Log.d("Twitter getHomeTimeline Error", e.getMessage());
            }
            return statuses;
        }

        /**
         * After completing background task.
         */
        protected void onPostExecute(List<twitter4j.Status> _statuses) {
            statuses = _statuses;
            notifyDataSetChanged();
            super.onPostExecute(_statuses);
        }
    }
}
