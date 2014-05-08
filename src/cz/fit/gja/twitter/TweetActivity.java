package cz.fit.gja.twitter;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Activity for writing and posting new tweets and replies
 */
public class TweetActivity extends LoggedActivity {

    public interface OnTweetSubmitted {

        public void handle(boolean wasSuccessful);
    }

    static final int THUMBNAIL_SIZE        = 420;
    static final int MAX_PICTURE_SIZE      = 768;
    static final int REQUEST_IMAGE_CAPTURE = 1; // id for request
    static final int REQUEST_IMAGE_SELECT  = 2; // id for request

	// current values
    String           tweet                 = "";
    Bitmap           attachedBitmap;
    Long             replyToId;
    String           replyToText;

	// objects in view
    EditText         textarea;
    ImageView        imageView;
    LinearLayout     form;
    LinearLayout     replyingTo;
    ProgressBar      spinner;

    String           mCurrentPhotoPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_tweet);

		// received data from caller
        Intent intent = this.getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null && extras.containsKey("replyToId")) {
                tweet = extras.getString("tweet");
                replyToId = extras.getLong("replyToId");
                replyToText = extras.getString("replyToText");
            }
        }

		// restoring state after pause
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("image")) {
                attachedBitmap = savedInstanceState.getParcelable("image");
            }

            if (savedInstanceState.containsKey("tweet")) {
                tweet = savedInstanceState.getString("tweet");
            }

            if (savedInstanceState.containsKey("replyTo")) {
                replyToId = savedInstanceState.getLong("replyTo");
                replyToText = savedInstanceState.getString("replyToText");
            }
        }

        setTitle(replyToId == null ? R.string.title_tweet : R.string.title_tweet_reply);
        initializeForm();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (attachedBitmap != null && imageView != null) {
            imageView.setImageBitmap(attachedBitmap);
        }
    }

	/**
	 * Binds form elements to local properties and fills them with data
	 */
    private void initializeForm() {
        View content = (View) findViewById(android.R.id.content);
        if (content != null) {

            // Progress spinner
            spinner = (ProgressBar) content.findViewById(R.id.progressBar);
            spinner.setVisibility(View.GONE);

            // Replying to
            replyingTo = (LinearLayout) content.findViewById(R.id.replyingTo);
            if (replyingTo != null) {
                if (replyToId == null) {
                    replyingTo.setVisibility(View.GONE);
                } else {
                    TextView replyingToText = (TextView) content.findViewById(R.id.replyingToText);
                    replyingToText.setText(replyToText);
                }
            }

            // Layout
            form = (LinearLayout) content.findViewById(R.id.form);

            // Thumbnail
            imageView = (ImageView) content.findViewById(R.id.thumbnail);
            /*
             * if (attachedImage != null) { setThumbnail(attachedImage); }
             */

            imageView.setOnClickListener(new View.OnClickListener() {

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

	/**
	 * Requests the image selection action to allow user to access gallery and select image
	 */
    private void buttonGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_SELECT);
    }

	/**
	 * Requests for camera and redirection of the photo taken to the application
	 */
    private void buttonCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Error occurred while creating the File", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

	/**
	 * Creates temporaty image file
	 * 
	 * @return
	 * @throws IOException 
	 */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, /* prefix */
                                         ".jpg", /* suffix */
                                         storageDir /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromPath(String picturePath, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // BitmapFactory.decodeResource(res, resId, options);
        BitmapFactory.decodeFile(picturePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(picturePath, options);
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "Wrong resultCode", Toast.LENGTH_SHORT).show();
            Log.e("Wrong onActivityResult", "" + resultCode);
            return;
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && imageView != null) {
            int orientation = 0;
            try {
                ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            attachedBitmap = decodeSampledBitmapFromPath(mCurrentPhotoPath, MAX_PICTURE_SIZE, MAX_PICTURE_SIZE);
            switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                attachedBitmap = rotateBitmap(attachedBitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                attachedBitmap = rotateBitmap(attachedBitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                attachedBitmap = rotateBitmap(attachedBitmap, 270);
                break;
            // etc.
            }
            imageView.setImageBitmap(attachedBitmap);
            // setThumbnail(attachedImage);
        } else if (requestCode == REQUEST_IMAGE_SELECT && imageView != null) {
            Uri selectedImageUri = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            attachedBitmap = decodeSampledBitmapFromPath(picturePath, MAX_PICTURE_SIZE, MAX_PICTURE_SIZE);
            imageView.setImageBitmap(attachedBitmap);
        }
    }

    public void clearImage() {
        attachedBitmap = null;
        imageView.setImageBitmap(null);
        // setThumbnail(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        icicle.putString("tweet", textarea.getText().toString());
        icicle.putParcelable("image", attachedBitmap);
        if (replyToId != null) {
            icicle.putLong("replyToId", replyToId);
            icicle.putString("replyToText", replyToText);
        }
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

	/**
	 * Posts the tweet, redirects to timeline on success
	 */
    private void saveTweet() {
        TweetPoster poster = new TweetPoster(twitter, null, tweet);
        if (attachedBitmap != null) {
            try {
                poster.setImage(attachedBitmap, this.getFilesDir());
            } catch (IOException ex) {
                Toast.makeText(this, "Tweet failed", Toast.LENGTH_SHORT).show();
                form.setVisibility(View.VISIBLE);
                replyingTo.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);
                return;
            }
        }
        if (replyToId != null) {
            poster.setIsReplyTo(replyToId);
        }

        final TweetActivity activity = this;

        form.setVisibility(View.GONE);
        replyingTo.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);

        poster.send(new OnTweetSubmitted() {

            public void handle(final boolean successful) {
                activity.runOnUiThread(new Runnable() {

                    public void run() {
                        if (successful == false) {
                            Toast.makeText(activity, "Tweet failed", Toast.LENGTH_SHORT).show();
                            form.setVisibility(View.VISIBLE);
                            replyingTo.setVisibility(View.VISIBLE);
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
