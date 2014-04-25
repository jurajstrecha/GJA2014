package cz.fit.gja.twitter;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import cz.fit.gja.twitter.adapters.FavoritesAdapter;

public class FavoritesActivity extends LoggedActivity {
	private FavoritesActivity 		favoritesActivity;
	private ListView 					favoritesList;
	private FavoritesAdapter 		favoritesAdapter;
	private  int 							INIT_TIMELINE_SIZE = 20;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);
        setTitle(R.string.title_favorites);
        favoritesActivity = this;
        
        View currentView = this.findViewById(android.R.id.content);
        //final ProgressBar progressBar = (ProgressBar) currentView.findViewById(R.id.tweets_progressBar);
        final TextView empty = (TextView) currentView.findViewById(R.id.tweets_empty);
        empty.setText(R.string.tweets_no_tweets);
        empty.setVisibility(View.GONE);

        favoritesList = (ListView) currentView.findViewById(R.id.tweets);
        favoritesList.setScrollContainer(false);

        favoritesAdapter = new FavoritesAdapter(favoritesActivity, twitter); 
        favoritesList.setAdapter(favoritesAdapter);

        // when the user scrolls down and reaches the end of the list view, new tweets are loaded and displayed
        favoritesList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// start loading new tweets a little earlier before the last tweet in the list is displayed
				final int lastItem = firstVisibleItem + visibleItemCount;
				if (lastItem == totalItemCount &&
						        !favoritesAdapter.isFavoritesLoading() &&
						        lastItem >= INIT_TIMELINE_SIZE) { 

					favoritesAdapter.loadMoreFavoriteTweets();
				}
				
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				; // do nothing
			}
        	
        });
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timeline_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.tweets_reload:
            favoritesAdapter = new FavoritesAdapter(favoritesActivity, twitter);
            favoritesList.setAdapter(favoritesAdapter);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }    
}
