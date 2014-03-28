package cz.fit.gja.twitter;

import android.os.Bundle;

public class TweetActivity extends LoggedActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.new_tweet);
		
		setTitle(R.string.title_tweet);
    }
	
}
