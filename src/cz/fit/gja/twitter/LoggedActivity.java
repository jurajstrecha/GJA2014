package cz.fit.gja.twitter;

import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

abstract public class LoggedActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        
        if (isTwitterLoggedInAlready()) {
            
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

            // Access Token
            String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
            // Access Token Secret
            String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

            AccessToken accessToken = new AccessToken(access_token, access_token_secret);
            twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
            userId = accessToken.getUserId();
            User user;
            // Check login (tokens)
            try {
                user = twitter.showUser(userId);
                Log.d("twitter UserName", user.getName());
                //startActivity(new Intent(this, TimelineActivity.class));
                //finish();                
            } catch (TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                logoutFromTwitter();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            //Toast.makeText(this, twitter.getAuthorization().toString(), Toast.LENGTH_LONG).show();
        } else {
            logoutFromTwitter();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
			Intent intent = new Intent(this, TimelineActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
        case R.id.action_new_tweet:
            startActivity(new Intent(this, TweetActivity.class));
            return true;
		case R.id.action_following:
            startActivity(new Intent(this, FollowingActivity.class));
            return true;
		case R.id.action_followers:
            startActivity(new Intent(this, FollowersActivity.class));
            return true;
        case R.id.action_account:
            startActivity(new Intent(this, AccountActivity.class));
            return true;
        case R.id.action_logout:
            logoutFromTwitter();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}
