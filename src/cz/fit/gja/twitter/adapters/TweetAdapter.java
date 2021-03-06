/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fit.gja.twitter.adapters;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import cz.fit.gja.twitter.R;
import cz.fit.gja.twitter.model.ImageLoader;
import cz.fit.gja.twitter.view.IdButton;
import cz.fit.gja.twitter.view.MapButton;
import twitter4j.GeoLocation;
import twitter4j.MediaEntity;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cz.fit.gja.twitter.TweetActivity;

public class TweetAdapter extends BaseAdapter {

    protected Context              context;
    protected Twitter              twitter;
    protected LayoutInflater       layoutInflater;
    private List<twitter4j.Status> statuses        = new ArrayList<twitter4j.Status>();
    // flag represents that there is a loading of next page of tweets in
    // progress
    private boolean                loadingTimeline = false;
    // number of tweet pages already loaded to the timeline
    private int                    pageCounter     = 1;

    public TweetAdapter(Context context, Twitter twitter) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.twitter = twitter;

        new LoadTimeline().execute();
    }

    /**
     * Return obtained twitter instance. Method is used for example by the
     * Favorite button.
     * 
     * @return twitter instance
     */
    public Twitter getTwitter() {
        return this.twitter;
    }

    /**
     * If loading is in progress right now, flag is set.
     * 
     * @return True if loading is in progress, false otherwise
     */
    public boolean isTimelineLoading() {
        return loadingTimeline;
    }

    /**
     * Asynchronously loads next page of tweets to the tweets list
     * 
     */
    public void loadMoreTimelineTweets() {
        new LoadMoreTweets().execute();
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
        // final TweetAdapter tweetAdapter = this;
        TextView textView;
        ImageView imageView;
        ProgressBar progressBar;
        Button button;
        IdButton idButton;
        MapButton mapButton;

        final twitter4j.Status status = getItem(i);
        final User user = status.getUser();
        view = this.layoutInflater.inflate(R.layout.item_tweet, null);

        textView = (TextView) view.findViewById(R.id.tweet_fullName);
        if (textView != null) {
            textView.setText(user.getName());
        }

        textView = (TextView) view.findViewById(R.id.tweet_time);
        if (textView != null) {
            SimpleDateFormat format = new SimpleDateFormat("hh:mm d.M.y", Locale.getDefault());
            textView.setText(format.format(status.getCreatedAt()));
        }

        textView = (TextView) view.findViewById(R.id.tweet_twitterName);
        if (textView != null) {
            textView.setText("@" + user.getScreenName());
        }

        textView = (TextView) view.findViewById(R.id.tweet_text);
        if (textView != null) {
            // if the tweet text contains URL addresses, make them clickable and
            // open them in a web browser
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            String text = status.getText();
            if (text.contains("http://") || text.contains("https://")) {
                // replace plaintext URL with hyperlink surrounded by HTML marks
                // for link
                text = text.replaceAll("\\b((http|https)://[^\\s]+)\\b", "<a href=\"$1\">$1</a>");
            }

            // transform HTML formating to clickable text in text view
            textView.setText(Html.fromHtml(text));

        }

        imageView = (ImageView) view.findViewById(R.id.tweet_portrait);
        if (imageView != null) {
            try {
                URL url = new URL(user.getBiggerProfileImageURL());
                if (ImageLoader.hasImage(url)) {
                    imageView.setImageBitmap(ImageLoader.getImage(url));
                } else {
                    new ImageLoader(imageView, null, url).execute();
                }
            } catch (MalformedURLException e) {
                // e.printStackTrace();
                Log.e("Wrong image url", e.getMessage());
            }
        }

        final MediaEntity[] mediaEntities = status.getMediaEntities();

        imageView = (ImageView) view.findViewById(R.id.tweet_image);
        progressBar = (ProgressBar) view.findViewById(R.id.tweet_image_progress);
        if (imageView != null && progressBar != null) {
            if (mediaEntities.length >= 1) {
                try {
                    URL url = new URL(mediaEntities[0].getMediaURL());
                    if (mediaEntities[0].getType().equals("photo")) {
                        if (ImageLoader.hasImage(url)) {
                            imageView.setImageBitmap(ImageLoader.getImage(url));
                        } else {
                            progressBar.setVisibility(View.VISIBLE);
                            new ImageLoader(imageView, progressBar, url).execute();
                        }
                        imageView.setOnClickListener(new View.OnClickListener() {

                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                if (mediaEntities[0].getExpandedURL() != null)
                                    intent.setData(Uri.parse(mediaEntities[0].getExpandedURL()));
                                else
                                    intent.setData(Uri.parse(mediaEntities[0].getURL()));
                                context.startActivity(intent);
                            }
                        });

                    }
                } catch (MalformedURLException e) {
                    Log.e("Wrong image url", e.getMessage());
                }
            }
        }

        // if the tweet contains location data, map button is displayed
        // under the tweet on the timeline and coordinates are stored within
        // the button
        mapButton = (MapButton) view.findViewById(R.id.tweet_map);

        // exact location data avalible, center the map camera to its position
        if (status.getGeoLocation() != null) {
            GeoLocation location = status.getGeoLocation();
            mapButton.setBounds(null);
            mapButton.setCoords(new double[] { location.getLatitude(), location.getLongitude() });
            mapButton.setVisibility(View.VISIBLE);

            // tweet contains bounding box coordinates, not the exact location
        } else if (status.getPlace() != null) {
            GeoLocation boundries[] = status.getPlace().getBoundingBoxCoordinates()[0];
            mapButton.setBounds(new double[] { boundries[0].getLatitude(), boundries[0].getLongitude(), boundries[2].getLatitude(),
                    boundries[2].getLongitude() });
            mapButton.setCoords(null);
            mapButton.setVisibility(View.VISIBLE);
            // no location data avalible, hide map button
        } else {
            mapButton.setVisibility(View.GONE);
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

        // set tweet ID and clickable status of the retweet button that belongs
        // to the status
        // if the status has already been retweeted, user won't be able to click
        // button again
        idButton = (IdButton) view.findViewById(R.id.tweet_retweet);
        idButton.setTweetId(status.getId());
        idButton.setClickable(!status.isRetweeted());
        idButton.setChecked(status.isRetweeted());

        // set status and tweet ID of the tweet that belongs to the Favorite
        // button
        idButton = (IdButton) view.findViewById(R.id.tweet_favorite);
        idButton.setTweetId(status.getId());
        idButton.setChecked(status.isFavorited());

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
            loadingTimeline = true;
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
            loadingTimeline = false;
        }
    }

    /**
     * Asynchronous load of new tweets to the timeline on scroll when the end of
     * list is reached
     * 
     */
    private class LoadMoreTweets extends AsyncTask<Void, Void, List<twitter4j.Status>> {

        protected void onPreExecute() {
            if (isTimelineLoading())
                cancel(true);
            else
                loadingTimeline = true;
        }

        @Override
        protected List<twitter4j.Status> doInBackground(Void... arg0) {
            try {
                return twitter.getHomeTimeline(new Paging(++pageCounter));
            } catch (TwitterException e) {
                Log.d("Twitter getHomeTimeline Error", e.getMessage());
                return null;
            }
        }

        protected void onPostExecute(List<twitter4j.Status> _statuses) {
            if (_statuses != null) {
                statuses.addAll(_statuses);
                notifyDataSetChanged();
                super.onPostExecute(_statuses);
            }
            loadingTimeline = false;
        }
    }
}
