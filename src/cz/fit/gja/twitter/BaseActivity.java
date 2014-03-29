package cz.fit.gja.twitter;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

abstract public class BaseActivity extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {		
		super.onCreate(savedInstanceState);
		
		/*if( isNetworkAvailable() == false ) {
			Toast.makeText(this, R.string.msg_no_connection, Toast.LENGTH_LONG).show();
		}*/
    }
	
	protected boolean isLoggedIn() {
		return true;
	}
	
	protected boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		if( cm == null ) {
			return false;
		}
		
		NetworkInfo i = cm.getActiveNetworkInfo();
		if (i == null) {
			return false;
		}
		if (!i.isConnected()) {
			return false;
		}
		if (!i.isAvailable()) {
			return false;
		}
		return true;
	}
	
}
