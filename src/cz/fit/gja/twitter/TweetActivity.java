package cz.fit.gja.twitter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cz.fit.gja.twitter.dialogs.ThumbnailDialog;
import cz.fit.gja.twitter.model.TweetPoster;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TweetActivity extends LoggedActivity {
	
	public interface OnTweetSubmitted {
		public void handle(boolean wasSuccessful);
	}

    static final int THUMBNAIL_SIZE        = 160;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_SELECT  = 2;

    String           tweet                 = "";
    Bitmap           attachedImage;
    Long             replyToId;
	String           replyToText;

    EditText         textarea;
    ImageView        thumbnail;
	LinearLayout     form;
	ProgressBar      spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_tweet);

        if (savedInstanceState != null) {
            attachedImage = savedInstanceState.getParcelable("image");
            tweet = savedInstanceState.getString("tweet");
            replyToId = savedInstanceState.getLong("replyTo");
			replyToText = savedInstanceState.getString("replyToText");
        }

        setTitle(replyToId == null ? R.string.title_tweet : R.string.title_tweet_reply);
        initializeForm();
    }

    private void initializeForm() {
        View content = (View) findViewById(android.R.id.content);
        if (content != null) {
			
			// Progress spinner
			spinner = (ProgressBar)content.findViewById(R.id.progressBar);
			spinner.setVisibility(View.GONE);
			
			// Replying to
			LinearLayout replyingTo = (LinearLayout)content.findViewById(R.id.replyingTo);
			if( replyingTo != null ) {
				if( replyToId == null ) {
					replyingTo.setVisibility(View.GONE);
				} else {
					TextView replyingToText = (TextView)content.findViewById(R.id.replyingToText);
					replyingToText.setText(replyToText);
				}
			}
			
			// Layout
			form = (LinearLayout)content.findViewById(R.id.form);

            // Thumbnail
            thumbnail = (ImageView) content.findViewById(R.id.thumbnail);
            if (attachedImage != null) {
                setThumbnail(attachedImage);
            }

            thumbnail.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    ThumbnailDialog dialog = new ThumbnailDialog();
                    dialog.show(getFragmentManager(), "thumbnail");
                }
            });

            // Buttons
            ImageButton gallery = (ImageButton) content.findViewById(R.id.select_image);
            gallery.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    buttonGallery();
                }
            });

            ImageButton camera = (ImageButton) content.findViewById(R.id.camera);
            camera.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    buttonCamera();
                }
            });

            // Limit
            textarea = (EditText) content.findViewById(R.id.tweet);
            final TextView limit = (TextView) content.findViewById(R.id.characters_remaining);
            final int textColor = limit.getTextColors().getDefaultColor();

            if (textarea != null && limit != null) {
                textarea.addTextChangedListener(new TextWatcher() {

                    public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {
                    }

                    public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
                    }

                    public void afterTextChanged(Editable edtbl) {
                        tweet = edtbl.toString();

                        Integer remaining = 140 - edtbl.length();
                        limit.setText(remaining.toString());

                        if (remaining < 0) {
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

    private void buttonGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_SELECT);
    }

    private void buttonCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            // log? toast?
            return;
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && thumbnail != null) {
            Bundle extras = data.getExtras();
            attachedImage = (Bitmap) extras.get("data");
            setThumbnail(attachedImage);
        } else if (requestCode == REQUEST_IMAGE_SELECT && thumbnail != null) {
            Uri selectedImageUri = data.getData();
            try {
                attachedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                                                                  selectedImageUri);
                setThumbnail(attachedImage);
            } catch (IOException ex) {
                Logger.getLogger(TweetActivity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void setThumbnail(Bitmap imageBitmap) {
        if (thumbnail == null) {
            return;
        }

        if (imageBitmap != null) {
            thumbnail.setImageBitmap(imageBitmap);
            if (thumbnail.getLayoutParams().width < thumbnail.getLayoutParams().height) {
                thumbnail.getLayoutParams().width = THUMBNAIL_SIZE;
                thumbnail.getLayoutParams().height = (int) ((float) imageBitmap.getHeight() * (THUMBNAIL_SIZE / (float) imageBitmap.getWidth()));
            } else {
                thumbnail.getLayoutParams().width = (int) ((float) imageBitmap.getWidth() * (THUMBNAIL_SIZE / (float) imageBitmap.getHeight()));
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
        TweetPoster poster = new TweetPoster(twitter, null, tweet);
        if (attachedImage != null) {
			try {
				poster.setImage(attachedImage, this.getFilesDir());
			} catch (IOException ex) {
				Toast.makeText(this, "Tweet failed", Toast.LENGTH_SHORT).show();
				form.setVisibility(View.VISIBLE);
				spinner.setVisibility(View.GONE);
				return;
			}
        }
        if (replyToId != null) {
            poster.setIsReplyTo(replyToId);
        }
		
		final TweetActivity activity = this;
		
		form.setVisibility(View.GONE);
		spinner.setVisibility(View.VISIBLE);
		
        poster.send(new OnTweetSubmitted() {
			public void handle(final boolean successful) {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						if( successful == false ) {
							Toast.makeText(activity, "Tweet failed", Toast.LENGTH_SHORT).show();
							form.setVisibility(View.VISIBLE);
							spinner.setVisibility(View.GONE);
						} else {
							Toast.makeText(activity, "Tweet succeeded", Toast.LENGTH_SHORT).show();

							startActivity(new Intent(activity, TimelineActivity.class));
							finish();
						}
					}
				});
			}
		});
    }

}
