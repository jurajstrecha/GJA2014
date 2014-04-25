package cz.fit.gja.twitter.adapters;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.fit.gja.twitter.R;
import cz.fit.gja.twitter.TweetActivity;
import cz.fit.gja.twitter.model.ImageLoader;
import twitter4j.MediaEntity;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FavoritesAdapter extends BaseAdapter {
	private Context						  context;
	private LayoutInflater 			  layoutInflater;
	private Twitter						  twitter;
	private List<twitter4j.Status> favoriteStatuses = new ArrayList<twitter4j.Status>();
	private boolean              		  loadingFavoriteTweets = false;
	private int 				   				  favoritePageCounter = 1;
	
	public FavoritesAdapter(Context context, Twitter twitter) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.twitter = twitter;
        
		new LoadFavorites().execute();
	}
	
	/**
	 * Checks whether favorite tweets list is being loaded or not
	 * 
	 * @return true if tweets loading in progress, false otherwise
	 */
	public boolean isFavoritesLoading() {
		return loadingFavoriteTweets;
	}
	
    /**
     * Asynchronously loads next page of favorite tweets to the favorite tweets list 
     * 
     */
    public void loadMoreFavoriteTweets() {
   		new LoadMoreFavorites().execute();
    }
	  
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // final TweetAdapter tweetAdapter = this;
        TextView textView;
        ImageView imageView;
        ProgressBar progressBar;
        Button button;

        final twitter4j.Status status = getItem(i);
        final User user = status.getUser();
        view = this.layoutInflater.inflate(R.layout.item_tweet, null);

        textView = (TextView) view.findViewById(R.id.tweet_fullName);
        if (textView != null) {
            textView.setText(user.getName());
        }

        textView = (TextView) view.findViewById(R.id.tweet_time);
        if (textView != null) {
            SimpleDateFormat format = new SimpleDateFormat("hh:mm d.M.y", Locale.getDefault());
            textView.setText(format.format(status.getCreatedAt()));
        }

        textView = (TextView) view.findViewById(R.id.tweet_twitterName);
        if (textView != null) {
            textView.setText("@" + user.getScreenName());
        }

        textView = (TextView) view.findViewById(R.id.tweet_text);
        if (textView != null) {
        	// if the tweet text contains URL addresses, make them clickable and open them in a web browser
        	textView.setMovementMethod(LinkMovementMethod.getInstance());
        	String text = status.getText();
        	if (text.contains("http://") || text.contains("https://")) {
        		// replace plaintext URL with hyperlink surrounded by HTML marks for link
        		text = text.replaceAll("\\b((http|https)://[^\\s]+)\\b", "<a href=\"$1\">$1</a>");
        	}

            // transform HTML formating to clickable text in text view
            textView.setText(Html.fromHtml(text));

        }

        imageView = (ImageView) view.findViewById(R.id.tweet_portrait);
        if (imageView != null) {
            try {
                URL url = new URL(user.getBiggerProfileImageURL());
                if (ImageLoader.hasImage(url)) {
                    imageView.setImageBitmap(ImageLoader.getImage(url));
                } else {
                    new ImageLoader(imageView, null, url).execute();
                }
            } catch (MalformedURLException e) {
                //e.printStackTrace();
                Log.e("Wrong image url", e.getMessage());
            }
        }

        final MediaEntity[] mediaEntities = status.getMediaEntities();

        imageView = (ImageView) view.findViewById(R.id.tweet_image);
        progressBar = (ProgressBar) view.findViewById(R.id.tweet_image_progress);
        if (imageView != null && progressBar != null) {
            if (mediaEntities.length >= 1) {
                try {
                    URL url = new URL(mediaEntities[0].getMediaURL());
                    if (mediaEntities[0].getType().equals("photo")) {
                        if (ImageLoader.hasImage(url)) {
                            imageView.setImageBitmap(ImageLoader.getImage(url));
                        } else {
                            progressBar.setVisibility(View.VISIBLE);
                            new ImageLoader(imageView, progressBar, url).execute();
                        }
                        imageView.setOnClickListener(new View.OnClickListener() {

                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                if (mediaEntities[0].getExpandedURL() != null)
                                    intent.setData(Uri.parse(mediaEntities[0].getExpandedURL()));
                                else
                                    intent.setData(Uri.parse(mediaEntities[0].getURL()));
                                context.startActivity(intent);
                            }
                        });

                    }
                } catch (MalformedURLException e) {
                    Log.e("Wrong image url", e.getMessage());
                }
            }
        }

        button = (Button) view.findViewById(R.id.tweet_reply);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TweetActivity.class);
                intent.putExtra("tweet", "@" + user.getScreenName() + " ");
                intent.putExtra("replyToId", status.getId());
                intent.putExtra("replyToText", status.getText());
                context.startActivity(intent);
                // refresh)).execute();
            }
        });

        button = (Button) view.findViewById(R.id.tweet_retweet);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // final Handler refresh = new Handler(Looper.getMainLooper());
                // (new UnfollowUser(twitter, user.getScreenName(), adapter,
                // refresh)).execute();
            }
        });

        button = (Button) view.findViewById(R.id.tweet_favorite);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // final Handler refresh = new Handler(Looper.getMainLooper());
                // (new UnfollowUser(twitter, user.getScreenName(), adapter,
                // refresh)).execute();
            }
        });

        return view;
    }
    
    /**
     * AsyncTask to load Timeline
     * */
    private class LoadFavorites extends AsyncTask<Void, Void, List<twitter4j.Status>> {
    	protected void onPreExecute() {
    		if (isFavoritesLoading())
    			cancel(true);
    		else {
    			loadingFavoriteTweets = true;
    		}    		
    	}
    	
        protected List<twitter4j.Status> doInBackground(Void... args) {
            try {
                return twitter.getFavorites();
            } catch (TwitterException e) {
                Log.d("Twitter getFavorites Error", e.getMessage());
                return null;
            }

        }

        protected void onPostExecute(List<twitter4j.Status> _statuses) {
            favoriteStatuses = _statuses;
            notifyDataSetChanged();
            super.onPostExecute(_statuses);
            loadingFavoriteTweets = false;
        }
    }

    /**
     * Asynchronous load of new tweets to the timeline on scroll when the end of list is reached
     *
     */
    private class LoadMoreFavorites extends AsyncTask<Void, Void, List<twitter4j.Status>> {
    	protected void onPreExecute() {
    		if (isFavoritesLoading())
    			cancel(true);
    		else {
    			loadingFavoriteTweets = true;
    		}
    	}
    	
		@Override
		protected List<twitter4j.Status> doInBackground(Void... arg0) {
            try {
                return twitter.getFavorites(new Paging(++favoritePageCounter));
            } catch (TwitterException e) {
                Log.d("Twitter getFavorites Error", e.getMessage());
                return null;
            }
		}
    	
		protected void onPostExecute(List<twitter4j.Status> _statuses) {
			if (_statuses != null) {
				favoriteStatuses.addAll(_statuses);
				notifyDataSetChanged();
				super.onPostExecute(_statuses);
			}
			loadingFavoriteTweets = false;
		}
    }
    
    @Override
    public int getCount() {
        return this.favoriteStatuses.size();
    }

    @Override
    public twitter4j.Status getItem(int i) {
        return this.favoriteStatuses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
