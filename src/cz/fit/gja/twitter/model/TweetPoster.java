package cz.fit.gja.twitter.model;

import android.graphics.Bitmap;
import cz.fit.gja.twitter.TweetActivity;
import cz.fit.gja.twitter.exceptions.SizeExceededException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Handles posting of a tweet
 */
public class TweetPoster {

    final private Twitter       twitter;
    final private Configuration config;
    final private StatusUpdate  status;

    public TweetPoster(Twitter twitter, Configuration config, String text) {
        this.twitter = twitter;
        this.config = config;
        this.status = new StatusUpdate(text);
    }

	/**
	 * Sets given bitmap as an image to be attached to tweet
	 * 
	 * @param image
	 * @param tempDirectory
	 * @throws IOException 
	 */
    public void setImage(Bitmap image, File tempDirectory) throws IOException {
        File file = null;
        try {
            if (config != null && image.getByteCount() > config.getPhoto_size_limit()) {
                throw new SizeExceededException();
            }

            file = File.createTempFile("twitterImage", ".twitter", tempDirectory);
            FileOutputStream fos = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);

            this.status.media(file);
            file.deleteOnExit();
        } catch (IOException ex) {
            if (file != null) {
                file.delete();
            }

            Logger.getLogger(TweetPoster.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

	/**
	 * If the tweet is a reply to someone mark it as such
	 * @param id 
	 */
    public void setIsReplyTo(long id) {
        this.status.setInReplyToStatusId(id);
    }

	/**
	 * Sends the update
	 * 
	 * @param handler 
	 */
    public void send(TweetActivity.OnTweetSubmitted handler) {
        final TweetActivity.OnTweetSubmitted callback = handler;
        (new Thread(new Runnable() {

            public void run() {
                boolean wasSuccessful = true;
                try {
                    twitter.updateStatus(status);
                } catch (TwitterException ex) {
                    wasSuccessful = false;
                    Logger.getLogger(TweetPoster.class.getName()).log(Level.SEVERE, null, ex);
                }

                callback.handle(wasSuccessful);
            }
        })).start();
    }

}
