package cz.fit.gja.twitter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TEST
        if (isTwitterLoggedInAlready()) {
            startActivity(new Intent(this, TimelineActivity.class));
            finish();
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
