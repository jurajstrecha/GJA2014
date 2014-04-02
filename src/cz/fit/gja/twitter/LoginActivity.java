package cz.fit.gja.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TEST
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
                startActivity(new Intent(this, TimelineActivity.class));
                finish();                
            } catch (TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                logoutFromTwitter();
            }
            //Toast.makeText(this, twitter.getAuthorization().toString(), Toast.LENGTH_LONG).show();
        }

        setContentView(R.layout.login);
        initializeLoginForm();
    }

    private void initializeLoginForm() {

        //View currentView = this.findViewById(android.R.id.content);

        Button loginButton = (Button) findViewById(R.id.button_login);
        if (loginButton != null) {
            //final EditText inputLogin = (EditText) currentView.findViewById(R.id.input_login);
            //final EditText inputPassword = (EditText) currentView.findViewById(R.id.input_password);

            loginButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    //doLogin(inputLogin.getText().toString(), inputPassword.getText().toString());
                    loginToTwitter();
                }
            });
        }
    }
}
