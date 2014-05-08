package cz.fit.gja.twitter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Activity for intro screen for logging into Twitter
 */
public class LoginActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isTwitterLoggedInAlready()) {
            startActivity(new Intent(this, TimelineActivity.class));
            finish();
        }

        setContentView(R.layout.login);
        initializeLoginForm();
    }

    private void initializeLoginForm() {
        Button loginButton = (Button) findViewById(R.id.button_login);
        if (loginButton != null) {
            loginButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    loginToTwitter();
                }
            });
        }
    }
}
