package cz.fit.gja.twitter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends BaseActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {		
		super.onCreate(savedInstanceState);
		// TEST
		if( false && isLoggedIn() ) {
			startActivity(new Intent(this, TimelineActivity.class));
			finish();
		}
		
        setContentView(R.layout.login);
		initializeLoginForm();
    }
	
	private void initializeLoginForm() {
		View currentView = this.findViewById(android.R.id.content);
		
		Button loginButton = (Button)currentView.findViewById(R.id.button_login);
		if( loginButton != null ) {
			final EditText inputLogin = (EditText)currentView.findViewById(R.id.input_login);
			final EditText inputPassword = (EditText)currentView.findViewById(R.id.input_password);
			
			loginButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					doLogin(inputLogin.getText().toString(), inputPassword.getText().toString());
				}
			});
		}
	}	
	
	public void doLogin(String login, String password) {
		final Activity loginActivity = this;
		
		ProgressDialog.show(
			this,
			getString(R.string.progress_login_title),
			getString(R.string.progress_login),
			true,
			true,
			new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface arg0) {
					loginActivity.startActivity(new Intent(loginActivity, TimelineActivity.class));
					loginActivity.finish();
				}
			}
		);
	}
	
}
