package cz.fit.gja.twitter;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import cz.fit.gja.twitter.adapters.FavoritesAdapter;

public class FavoritesActivity extends TimelineActivity {

    private FavoritesActivity favoritesActivity;
    private FavoritesAdapter  favoritesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);
        setTitle(R.string.title_favorites);
        favoritesActivity = this;

        View currentView = this.findViewById(android.R.id.content);
        // final ProgressBar progressBar = (ProgressBar)
        // currentView.findViewById(R.id.tweets_progressBar);
        final TextView empty = (TextView) currentView.findViewById(R.id.tweets_empty);
        empty.setText(R.string.tweets_no_tweets);
        // empty.setVisibility(View.GONE);

        tweetList = (ListView) currentView.findViewById(R.id.tweets);
        tweetList.setScrollContainer(false);

        favoritesAdapter = new FavoritesAdapter(favoritesActivity, twitter, this);
        tweetList.setAdapter(favoritesAdapter);

        // when the user scrolls down and reaches the end of the list view, new
        // tweets are loaded and displayed
        tweetList.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // start loading new tweets a little earlier before the last
                // tweet in the list is displayed
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount && !favoritesAdapter.isFavoritesLoading() && lastItem >= INIT_TIMELINE_SIZE) {

                    favoritesAdapter.loadMoreFavoriteTweets();
                }

            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                ; // do nothing
            }

        });

    }

    /**
     * From adapter set the visibility of the timeline message announcing that
     * there are no tweet loaded
     * 
     */
    public void setNoTweetsMessageVisibility(boolean value) {
        View currentView = this.findViewById(android.R.id.content);
        final TextView empty = (TextView) currentView.findViewById(R.id.tweets_empty);
        if (value)
            empty.setVisibility(View.VISIBLE);
        else
            empty.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.tweets_reload:
            favoritesAdapter = new FavoritesAdapter(favoritesActivity, twitter, this);
            tweetList.setAdapter(favoritesAdapter);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
