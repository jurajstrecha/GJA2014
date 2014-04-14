package cz.fit.gja.twitter;

import java.util.List;

import twitter4j.TwitterException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class TimelineActivity extends LoggedActivity {

    private TableLayout      tableLayout;
    private TimelineActivity timelineActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);
        timelineActivity = this;

        setTitle(R.string.title_timeline);

        tableLayout = (TableLayout) findViewById(R.id.timeline_tableLayout);

        TableRow tableRow = new TableRow(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                                                                       TableRow.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(layoutParams);
        TextView textView = new TextView(this);
        textView.setText("bla");
        tableRow.addView(textView);
        tableLayout.addView(tableRow);

        new LoadTimeline().execute();
    }

    /**
     * Function to get Timeline
     * */
    private class LoadTimeline extends AsyncTask<Void, Void, Void> {

        private List<twitter4j.Status> statuses;

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Toast.makeText(getApplicationContext(), "Loading timeline...",
            // Toast.LENGTH_SHORT).show();
        }

        /**
         * Getting Timeline
         */
        protected Void doInBackground(Void... args) {
            // Log.d("Tweet Text", "> " + args[0]);
            // String status = args[0];
            try {
                statuses = twitter.getHomeTimeline();
                // Log.d("Status", "> " + response.getText());
            } catch (TwitterException e) {
                // Error in updating status
                Log.d("Twitter Update Error", e.getMessage());
            }
            return null;
        }

        /**
         * After completing background task. Show the data in UI. Always use
         * runOnUiThread(new Runnable()) to update UI from background thread,
         * otherwise you will get error.
         */
        protected void onPostExecute(Void arg) {
            // dismiss the dialog after getting all products
            // pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // Toast.makeText(getApplicationContext(),
                    // "Showing home timeline", Toast.LENGTH_SHORT).show();
                    for (twitter4j.Status status : statuses) {
                        // System.out.println(status.getUser().getName() + ":" +
                        // status.getText());
                        TableRow tableRow = new TableRow(timelineActivity);
                        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                                                                                       TableRow.LayoutParams.WRAP_CONTENT);
                        tableRow.setLayoutParams(layoutParams);
                        TextView textView = new TextView(timelineActivity);
                        textView.setText(status.getUser().getName() + ":" + status.getText());
                        tableRow.addView(textView);
                        tableLayout.addView(tableRow);
                    }
                    // Clearing EditText field
                    // txtUpdate.setText("");
                }
            });
        }
    }
}
