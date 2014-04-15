package cz.fit.gja.twitter;

import cz.fit.gja.twitter.adapters.TweetAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class TimelineActivity extends LoggedActivity {

    private ListView         tweetList;
    private TweetAdapter     tweetAdapter;
    private TimelineActivity timelineActivity;

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
