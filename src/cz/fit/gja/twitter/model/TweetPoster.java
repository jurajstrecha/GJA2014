package cz.fit.gja.twitter.model;

import android.graphics.Bitmap;
import cz.fit.gja.twitter.exceptions.SizeExceededException;

public class TweetPoster {

	final private Configuration config;
	final private String text;
	private Bitmap image;
	private String replyToId;

	public TweetPoster(Configuration config, String text) {
		this.config = config;
		this.text = text;
	}
	
	public void setImage(Bitmap image) {
		if( config != null && image.getByteCount() > config.getPhoto_size_limit() ) {
			throw new SizeExceededException();
		}
		
		this.image = image;
	}
	
	public void setIsReplyTo(String id) {
		this.replyToId = id;
	}
	
	public void commit() {
		
	}
	
}
