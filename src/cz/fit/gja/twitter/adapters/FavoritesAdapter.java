package cz.fit.gja.twitter.adapters;


import java.util.ArrayList;
import java.util.List;

import cz.fit.gja.twitter.FavoritesActivity;


import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import android.content.Context;

import android.os.AsyncTask;

import android.util.Log;


public class FavoritesAdapter extends TweetAdapter {
	private List<twitter4j.Status> favoriteStatuses = new ArrayList<twitter4j.Status>();
	private boolean              		     loadingFavoriteTweets = false;
	private int 				   				 favoritePageCounter = 1;
    private FavoritesActivity favoritesActivity = null;
	
	public FavoritesAdapter(Context context, Twitter twitter, FavoritesActivity favoritesActivity) {
		super(context, twitter);
		this.favoritesActivity = favoritesActivity;
		
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
            // remove message noticing that there are no tweets in favorites from favorites timeline
            if (!favoriteStatuses.isEmpty())
            	favoritesActivity.setNoTweetsMessageVisibility(false);
            else
            	favoritesActivity.setNoTweetsMessageVisibility(true);
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
}
