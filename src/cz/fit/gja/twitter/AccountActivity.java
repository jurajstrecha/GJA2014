package cz.fit.gja.twitter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * Activity that handles displaying and modifying of the logged account's basic
 * data
 * 
 * @author Nox
 */
public class AccountActivity extends LoggedActivity {

    EditText name;
    EditText location;
    EditText website;
    EditText description;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.account);
        setTitle(R.string.title_account);

        initializeForm();
    }

    /**
     * Binds form elements to local properties and fills them with data
     */
    private void initializeForm() {
        View currentView = this.findViewById(android.R.id.content);

        Map<String, String> accountData = getAccountData();
        if (accountData == null) {
            return;
        }

        name = (EditText) currentView.findViewById(R.id.account_name);
        name.setText(accountData.get("name"), TextView.BufferType.NORMAL);

        location = (EditText) currentView.findViewById(R.id.account_location);
        location.setText(accountData.get("location"), TextView.BufferType.NORMAL);

        website = (EditText) currentView.findViewById(R.id.account_website);
        website.setText(accountData.get("website"), TextView.BufferType.NORMAL);

        description = (EditText) currentView.findViewById(R.id.account_bio);
        description.setText(accountData.get("description"), TextView.BufferType.NORMAL);
    }

    /**
     * Fetches and returns user data based on userId
     * 
     * @return
     */
    private Map<String, String> getAccountData() {
        if (userId == null) {
            Toast.makeText(this, getString(R.string.account_msg_fetch_failed), Toast.LENGTH_LONG).show();
            return null;
        }

        Map<String, String> accountData = new HashMap<String, String>();

        try {
            User user = twitter.showUser(userId);

            accountData.put("name", user.getName());
            accountData.put("location", user.getLocation());
            accountData.put("website", user.getURL());
            accountData.put("description", user.getDescription());
        } catch (TwitterException ex) {
            Toast.makeText(this, getString(R.string.account_msg_fetch_failed), Toast.LENGTH_LONG).show();
            Logger.getLogger(AccountActivity.class.getName()).log(Level.SEVERE, null, ex);
        }

        return accountData;

    }

    /**
     * Adds save button
     * 
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.account_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handles issuing save button action
     * 
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.account_submit:
            submit();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Updates account data based on current form values
     * 
     * Redirects to timeline on success
     */
    private void submit() {
        try {
            twitter.updateProfile(name.getText().toString(), website.getText().toString(), location.getText().toString(), description.getText()
                                                                                                                                     .toString());

            startActivity(new Intent(this, TimelineActivity.class));
            finish();
        } catch (TwitterException ex) {
            Toast.makeText(this, getString(R.string.account_msg_update_failed), Toast.LENGTH_LONG).show();
            Logger.getLogger(AccountActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
