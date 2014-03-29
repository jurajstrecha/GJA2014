package cz.fit.gja.twitter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class TweetActivity extends LoggedActivity
{
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.new_tweet);
		
		setTitle(R.string.title_tweet);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.tweet_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.tweet_submit:
				saveTweet();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void saveTweet() {
		Toast.makeText(this, "Tweet saved [test]", Toast.LENGTH_LONG).show();
		startActivity(new Intent(this, TimelineActivity.class));
		finish();
	}
	
}
