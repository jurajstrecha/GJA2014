package cz.fit.gja.twitter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.fit.gja.twitter.adapters.TweetAdapter;
import cz.fit.gja.twitter.adapters.UserAdapter;
import twitter4j.PagableResponseList;
import twitter4j.TwitterException;
import twitter4j.User;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
        final ProgressBar progressBar = (ProgressBar) currentView.findViewById(R.id.tweets_progressBar);
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
