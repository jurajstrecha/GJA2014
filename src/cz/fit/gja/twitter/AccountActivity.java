package cz.fit.gja.twitter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends LoggedActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.account);
		
		setTitle(R.string.title_account);
		initializeForm();
    }
	
	private void initializeForm() {
		View currentView = this.findViewById(android.R.id.content);
		
		Map<String,String> accountData = getAccountData();
		final EditText name = (EditText)currentView.findViewById(R.id.account_name);
		name.setText(accountData.get("name"), TextView.BufferType.NORMAL);
		final EditText location = (EditText)currentView.findViewById(R.id.account_location);
		location.setText(accountData.get("location"), TextView.BufferType.NORMAL);
		final EditText website = (EditText)currentView.findViewById(R.id.account_website);
		website.setText(accountData.get("website"), TextView.BufferType.NORMAL);
		final EditText bio = (EditText)currentView.findViewById(R.id.account_bio);
		bio.setText(accountData.get("bio"), TextView.BufferType.NORMAL);
	}
	
	private Map<String,String> getAccountData() {
		Map<String,String> accountData = new HashMap<String, String>();
		accountData.put("name", "Josef Novák");
		accountData.put("location", "Czech republic, Brno");
		accountData.put("website", "http://www.google.com");
		accountData.put("bio", "mutated cleric ninja");
		
		return accountData;
	}
	
}
