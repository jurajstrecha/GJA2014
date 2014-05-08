package cz.fit.gja.twitter;

import cz.fit.gja.twitter.adapters.TweetAdapter;
import cz.fit.gja.twitter.model.Retweet;
import cz.fit.gja.twitter.model.SetFavoriteTweet;
import cz.fit.gja.twitter.view.MapButton;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TimelineActivity extends LoggedActivity {

    protected ListView       tweetList;
    protected TweetAdapter   tweetAdapter;
    private TimelineActivity timelineActivity;
    protected final int      INIT_TIMELINE_SIZE = 19;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);
        setTitle(R.string.title_timeline);
        timelineActivity = this;

        View currentView = this.findViewById(android.R.id.content);
        // final ProgressBar progressBar = (ProgressBar)
        // currentView.findViewById(R.id.tweets_progressBar);
        final TextView empty = (TextView) currentView.findViewById(R.id.tweets_empty);
		final ProgressBar pb = (ProgressBar) currentView.findViewById(R.id.progressBar);
        empty.setText(R.string.tweets_no_tweets);
        empty.setVisibility(View.GONE);

        tweetList = (ListView) currentView.findViewById(R.id.tweets);
        tweetList.setScrollContainer(false);

        tweetAdapter = new TweetAdapter(timelineActivity, twitter);
        tweetList.setAdapter(tweetAdapter);

        // when the user scrolls down and reaches the end of the list view, new
        // tweets are loaded and displayed
        tweetList.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // start loading new tweets a little earlier before the last
                // tweet in the list is displayed
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount && !tweetAdapter.isTimelineLoading() && lastItem >= INIT_TIMELINE_SIZE) {
                    tweetAdapter.loadMoreTimelineTweets();
                }

            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                ; // do nothing
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timeline_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.tweets_reload:
            tweetAdapter = new TweetAdapter(timelineActivity, twitter);
            tweetList.setAdapter(tweetAdapter);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Changes appearance of the button and Favorite status of the tweet when
     * the button is pressed
     * 
     * @param view
     *            button that has been touched
     */
    public void makeFavorite(View view) {
        new SetFavoriteTweet(tweetAdapter.getTwitter(), view).execute();
    }

    /**
     * On-click handler for the Retweet button
     * 
     * @param view
     *            button that has been touched
     */
    public void doRetweet(View view) {
        view.setClickable(false);
        new Retweet(view, tweetAdapter.getTwitter()).execute();
    }

    /**
     *	 
     */
    public void showTweetOnMap(View view) {
        MapButton mapButton = (MapButton) view;
        // start new activity displaying map according to data contained within
        // the button
        Intent intent = new Intent(getApplicationContext(), TweetMapActivity.class);
        if (mapButton.getBounds() != null) {
            intent.putExtra("bounds", mapButton.getBounds());
        } else {
            intent.putExtra("coords", mapButton.getCoords());
        }
        startActivity(intent);
    }

}
