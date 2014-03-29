package cz.fit.gja.twitter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TweetActivity extends LoggedActivity
{
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.new_tweet);
		
		setTitle(R.string.title_tweet);
		
		View content = (View)findViewById(android.R.id.content);
		if( content != null ) {
			final EditText textarea = (EditText)content.findViewById(R.id.tweet);
			final TextView limit = (TextView)content.findViewById(R.id.characters_remaining);
			final int textColor = limit.getTextColors().getDefaultColor();
			
			if( textarea != null && limit != null ) {
				textarea.addTextChangedListener(new TextWatcher() {
					public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {}
					public void onTextChanged(CharSequence cs, int i, int i1, int i2) {}
					public void afterTextChanged(Editable edtbl) {
						Integer remaining = 140 - edtbl.length();
						limit.setText(remaining.toString());
						
						if( remaining < 0 ) {
							limit.setTextColor(Color.rgb(220, 0, 20));
						} else {
							limit.setTextColor(textColor);
						}
					}
				});
			}
		}
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
