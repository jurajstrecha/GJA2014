package cz.fit.gja.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

/**
 * Base activity class handling requests for Twitter authentication and network availablility
 */
abstract public class BaseActivity extends Activity {

    // App keys
    static String                      TWITTER_CONSUMER_KEY       = "pnj4t5biRWiSWjE6ooJsf1Bk3";
    static String                      TWITTER_CONSUMER_SECRET    = "QwjTSn5wZgXTK4LqCLulSYebba5dtLAjLANoODjuFwAKk7eZBa";

    // Preference Constants
    static String                      PREFERENCE_NAME            = "twitter_oauth";
    static final String                PREF_KEY_OAUTH_TOKEN       = "oauth_token";
    static final String                PREF_KEY_OAUTH_SECRET      = "oauth_token_secret";
    static final String                PREF_KEY_TWITTER_LOGIN     = "isTwitterLogedIn";

    // Must by set in app settings
    static final String                TWITTER_CALLBACK_URL       = "oauth://t4jsample";

    // Twitter oauth urls
    static final String                URL_TWITTER_AUTH           = "auth_url";
    static final String                URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String                URL_TWITTER_OAUTH_TOKEN    = "oauth_token";

    // Twitter
    protected static Twitter           twitter;
    protected static RequestToken      requestToken;
    protected static Long              userId;

    // Shared Preferences
    protected static SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // because of android.os.NetworkOnMainThreadException
        // TODO
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //

        // Shared Preferences
        mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);

        // Check if Internet present
        if (!isNetworkAvailable()) {
            Toast.makeText(this, R.string.msg_no_connection, Toast.LENGTH_LONG).show();
            return;
        }

        // Check if twitter keys are set
        if (TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET.trim().length() == 0) {
            Log.e("Twitter keys error", "Please set your twitter oauth tokens first!");
            return;
        }

        // This if conditions is tested once is redirected from twitter page.
        // Parse the uri to get oAuth Verifier.
        if (!isTwitterLoggedInAlready()) {
            Uri uri = getIntent().getData();

            if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                // oAuth verifier
                String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

                try {
                    // Get the access token
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

                    // Shared Preferences
                    Editor e = mSharedPreferences.edit();

                    // After getting access token, access token secret
                    // store them in application preferences
                    e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                    e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());

                    // Store login status - true
                    e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
                    e.commit(); // save changes
                    Log.e("Twitter OAuth Token", "> " + accessToken.getToken());
                } catch (Exception e) {
                    // Check log for login errors
                    Log.e("Twitter Login Error", "> " + e.getMessage());
                }
            }
        }
    }

    /**
     * Check user already logged in your application using twitter Login flag is
     * fetched from Shared Preferences
     * 
     * @return true/false
     */
    protected boolean isTwitterLoggedInAlready() {
        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }

    /**
     * Check Internet connection
     * 
     * @return true/false
     */
    protected boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null)
            return false;

        NetworkInfo i = cm.getActiveNetworkInfo();

        if (i == null)
            return false;

        if (!i.isConnected())
            return false;

        if (!i.isAvailable())
            return false;

        return true;
    }

    protected void loginToTwitter() {
        // Check if Internet present
        if (!isNetworkAvailable()) {
            Toast.makeText(this, R.string.msg_no_connection, Toast.LENGTH_LONG).show();
            return;
        }

        // Check if already logged in
        if (!isTwitterLoggedInAlready()) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            Configuration configuration = builder.build();

            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            try {
                requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
                this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
                finish();

            } catch (TwitterException e) {
                e.printStackTrace();
                Log.e("Twitter Login Error", "> " + e.getMessage());
                Toast.makeText(this, R.string.msg_login_failed, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, R.string.msg_already_logged, Toast.LENGTH_LONG).show();
            finish();
        }

    }

    /**
     * Function to logout from twitter It will just clear the application shared
     * preferences
     */
    protected void logoutFromTwitter() {
        // Clear the shared preferences
        Editor e = mSharedPreferences.edit();
        e.remove(PREF_KEY_OAUTH_TOKEN);
        e.remove(PREF_KEY_OAUTH_SECRET);
        e.remove(PREF_KEY_TWITTER_LOGIN);
        e.commit();
    }

}
