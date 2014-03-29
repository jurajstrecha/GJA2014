package cz.fit.gja.twitter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cz.fit.gja.twitter.dialogs.ThumbnailDialog;

public class TweetActivity extends LoggedActivity
{
	static final int THUMBNAIL_SIZE = 160;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	
	String tweet = "";
	EditText textarea;
	ImageView thumbnail;
	Bitmap attachedImage;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.new_tweet);
		
		if( savedInstanceState != null ) {
			attachedImage = savedInstanceState.getParcelable("image");
			tweet = savedInstanceState.getString("tweet");
		}
		
		setTitle(R.string.title_tweet);
		initializeForm();
	}
	
	private void initializeForm() {
		View content = (View)findViewById(android.R.id.content);
		if( content != null ) {
			
			// Thumbnail
			thumbnail = (ImageView)content.findViewById(R.id.thumbnail);
			if( attachedImage != null ) {
				setThumbnail(attachedImage);
			}
			
			thumbnail.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					ThumbnailDialog dialog = new ThumbnailDialog();
					dialog.show(getFragmentManager(), "thumbnail");
				}
			});
			
			// Buttons
			ImageButton camera = (ImageButton)content.findViewById(R.id.camera);
			camera.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					buttonCamera();
				}
			});
			
			// Limit
			textarea = (EditText)content.findViewById(R.id.tweet);
			final TextView limit = (TextView)content.findViewById(R.id.characters_remaining);
			final int textColor = limit.getTextColors().getDefaultColor();
			
			if( textarea != null && limit != null ) {
				textarea.addTextChangedListener(new TextWatcher() {
					public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {}
					public void onTextChanged(CharSequence cs, int i, int i1, int i2) {}
					public void afterTextChanged(Editable edtbl) {
						Integer remaining = 140 - edtbl.length();
						limit.setText(remaining.toString());
						
						if( remaining < 0 ) {
							limit.setTextColor(Color.rgb(220, 0, 20));
						} else {
							limit.setTextColor(textColor);
						}
					}
				});
			}
			
			textarea.setText(tweet);
			
		}
    }
	
	private void buttonCamera() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && thumbnail != null) {
			Bundle extras = data.getExtras();
			attachedImage = (Bitmap) extras.get("data");
			setThumbnail(attachedImage);
		}
	}
	
	private void setThumbnail(Bitmap imageBitmap) {
		if( thumbnail == null ) {
			return;
		}
		
		if( imageBitmap != null ) {
			thumbnail.setImageBitmap(imageBitmap);
			if( thumbnail.getLayoutParams().width < thumbnail.getLayoutParams().height ) {
				thumbnail.getLayoutParams().width = THUMBNAIL_SIZE;
				thumbnail.getLayoutParams().height = (int)((float)imageBitmap.getHeight() * (THUMBNAIL_SIZE / (float)imageBitmap.getWidth()));
			} else {
				thumbnail.getLayoutParams().width = (int)((float)imageBitmap.getWidth()* (THUMBNAIL_SIZE / (float)imageBitmap.getHeight()));
				thumbnail.getLayoutParams().height = THUMBNAIL_SIZE;
			}
		} else {
			thumbnail.setImageBitmap(null);
			thumbnail.getLayoutParams().width = 0;
			thumbnail.getLayoutParams().height = 0;
		}
	}
	
	public void clearImage() {
		attachedImage = null;
		setThumbnail(null);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle icicle) {
		super.onSaveInstanceState(icicle);
		icicle.putString("tweet", textarea.getText().toString());
		icicle.putParcelable("image", attachedImage);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.tweet_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.tweet_submit:
				saveTweet();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void saveTweet() {
		Toast.makeText(this, "Tweet saved [test]", Toast.LENGTH_LONG).show();
		startActivity(new Intent(this, TimelineActivity.class));
		finish();
	}
	
}
