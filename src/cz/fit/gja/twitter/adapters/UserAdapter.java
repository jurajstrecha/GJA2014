/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fit.gja.twitter.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cz.fit.gja.twitter.R;
import cz.fit.gja.twitter.model.FollowUser;
import cz.fit.gja.twitter.model.UnfollowUser;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.User;

public class UserAdapter extends BaseAdapter {
	
	protected Context context;
	protected Twitter twitter;
	protected LayoutInflater inflater;
	protected List<User> list = new ArrayList<User>();
	protected Map<String, Bitmap> portraits = new HashMap<String, Bitmap>();
	protected List<String> unfollowed = new ArrayList<String>();

	public UserAdapter(Context context, Twitter twitter) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.twitter = twitter;
	}

	public void addUsers(PagableResponseList<User> response) {
		for(User user: response) {
			this.list.add(user);
		}
	}

	public int getCount() {
		return this.list.size();
	}

	@Override
	public int getViewTypeCount(){
		return 1;
	}

	public View getView(int position, View itemView, ViewGroup parent){
		final UserAdapter adapter = this;
		TextView tv;
		ImageView iv;
		Button b;
		
		final User user = getItem(position);
		itemView = this.inflater.inflate(R.layout.item_user, null);
		
		if( user == null ) {
			return itemView;
		}

		tv = (TextView)itemView.findViewById(R.id.fullName);
		if( tv != null ) {
			tv.setText(user.getName());
		}
		
		tv = (TextView)itemView.findViewById(R.id.twitterName);
		if( tv != null ) {
			tv.setText("@" + user.getScreenName());
		}
		
		tv = (TextView)itemView.findViewById(R.id.bio);
		if( tv != null ) {
			tv.setText(user.getDescription());
		}
		
		iv = (ImageView)itemView.findViewById(R.id.portrait);
		if( iv != null ) {
			if( portraits.containsKey(user.getScreenName()) ) {
				iv.setImageBitmap(portraits.get(user.getScreenName()));
			} else {
				final Handler refresh = new Handler(Looper.getMainLooper());
				(new PortraitLoader(user, (ImageView)itemView.findViewById(R.id.portrait), refresh)).execute();
			}
		}
		
		if( unfollowed.contains(user.getScreenName()) ) {
			b = (Button)itemView.findViewById(R.id.unfollow);
			b.setVisibility(View.GONE);
			b = (Button)itemView.findViewById(R.id.follow);
			b.setVisibility(View.VISIBLE);
		} else {
			b = (Button)itemView.findViewById(R.id.follow);
			b.setVisibility(View.GONE);
			b = (Button)itemView.findViewById(R.id.unfollow);
			b.setVisibility(View.VISIBLE);
		}
		
		b = (Button)itemView.findViewById(R.id.follow);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler refresh = new Handler(Looper.getMainLooper());
				(new FollowUser(twitter, user.getScreenName(), adapter, refresh)).execute();
			}
		});
		
		b = (Button)itemView.findViewById(R.id.unfollow);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler refresh = new Handler(Looper.getMainLooper());
				(new UnfollowUser(twitter, user.getScreenName(), adapter, refresh)).execute();
			}
		});
		
		return itemView;
	}
	
	public void followed(String screenName) {
		if( unfollowed.contains(screenName) ) {
			Toast.makeText(context, String.format(context.getString(R.string.user_msg_followed), "@" + screenName), Toast.LENGTH_SHORT).show();
			unfollowed.remove(screenName);
		}
	}
	
	public void unfollowed(String screenName) {
		if( unfollowed.contains(screenName) == false ) {
			Toast.makeText(context, String.format(context.getString(R.string.user_msg_unfollowed), "@" + screenName), Toast.LENGTH_SHORT).show();
			unfollowed.add(screenName);
		}
	}
	
	public User getItem(int i) {
		return this.list.get(i);
	}

	public long getItemId(int i) {
		return i;
	}
	
	private class PortraitLoader extends AsyncTask<Void, Void, Void> {

		private final Integer IO_BUFFER_SIZE = 8000;
		private final User user;
		private final ImageView view;
		private final Handler refresh;

		public PortraitLoader(User user, ImageView view, Handler refresh) {
			this.user = user;
			this.view = view;
			this.refresh = refresh;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			if( portraits.containsKey(user.getScreenName()) ) {
				return null;
			}
			
			final String url = user.getBiggerProfileImageURL();
			refresh.post(new Runnable() {
				public void run() {
					try {
						BufferedInputStream in = new BufferedInputStream(new URL(url).openStream(), IO_BUFFER_SIZE);
						final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
						BufferedOutputStream out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
						int byte_;
						while ((byte_ = in.read()) != -1) {
							out.write(byte_);
						}
						out.flush();

						final byte[] data = dataStream.toByteArray();
						Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
						
						view.setImageBitmap(bitmap);
						portraits.put(user.getScreenName(), bitmap);
					} catch(MalformedURLException ex) {
						Logger.getLogger(UserAdapter.class.getName()).log(Level.SEVERE, null, ex);
					} catch (IOException ex) {
						Logger.getLogger(UserAdapter.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			});
			
			return null;
		}
		
	}
	
	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		if (observer != null) {
			super.unregisterDataSetObserver(observer);
		}
	} 
	
}