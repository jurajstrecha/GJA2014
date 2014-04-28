package cz.fit.gja.twitter;

import cz.fit.gja.twitter.adapters.TweetAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

public class TimelineActivity extends LoggedActivity {

    protected ListView         tweetList;
    protected TweetAdapter     tweetAdapter;
    private TimelineActivity   timelineActivity;
    protected final int		   INIT_TIMELINE_SIZE = 19;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);
        setTitle(R.string.title_timeline);
        timelineActivity = this;

        View currentView = this.findViewById(android.R.id.content);
        //final ProgressBar progressBar = (ProgressBar) currentView.findViewById(R.id.tweets_progressBar);
        final TextView empty = (TextView) currentView.findViewById(R.id.tweets_empty);
        empty.setText(R.string.tweets_no_tweets);
        empty.setVisibility(View.GONE);

        tweetList = (ListView) currentView.findViewById(R.id.tweets);
        tweetList.setScrollContainer(false);

        tweetAdapter = new TweetAdapter(timelineActivity, twitter);
        tweetList.setAdapter(tweetAdapter);
        
        // when the user scrolls down and reaches the end of the list view, new tweets are loaded and displayed
        tweetList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// start loading new tweets a little earlier before the last tweet in the list is displayed
				final int lastItem = firstVisibleItem + visibleItemCount;
				if (lastItem == totalItemCount &&
						        !tweetAdapter.isTimelineLoading() &&
						        lastItem >= INIT_TIMELINE_SIZE) { 

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
}
