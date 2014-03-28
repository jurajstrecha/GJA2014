package cz.fit.gja.twitter;

import android.os.Bundle;

public class AccountActivity extends LoggedActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.account);
		
		setTitle(R.string.title_account);
    }
	
}
