/**
 * Worked hours tracking application
 * 
 * @author Jiří "NoxArt" Petruželka
 * @copyright Jiří Petruželka 2013
 */
package cz.fit.gja.twitter.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import cz.fit.gja.twitter.R;
import cz.fit.gja.twitter.TweetActivity;

/**
 * Fragment for picking a date
 * 
 * @author Nox
 */
public class ThumbnailDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final TweetActivity activity = (TweetActivity) getActivity();

        String[] actions = new String[1];
        actions[0] = getString(R.string.tweet_thumbnail_clear);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.tweet_thumbnail_dialog))
               .setItems(actions, new DialogInterface.OnClickListener() {

                   public void onClick(DialogInterface dialog, int which) {
                       activity.clearImage();
                   }
               });

        return builder.create();
    }

}
