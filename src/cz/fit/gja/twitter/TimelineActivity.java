package cz.fit.gja.twitter;

import android.os.Bundle;

public class TimelineActivity extends LoggedActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);
		
		setTitle(R.string.title_timeline);
    }
	
}
