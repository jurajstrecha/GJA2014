package cz.fit.gja.twitter.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.net.URL;

/**
 * AsyncTask for loading images on background
 */
public class ImageLoader extends AsyncTask<Void, Void, Bitmap> {

    private ImageView                      view;
    private ProgressBar                    progressBar;
    private URL                            url;
    // maximum amount of device memory avalible for program use
    protected static int                   maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    // use 1/8 of the avalible memory for bitmap caching
    // the use of LruCache prevents OutOfMemory exception causing application
    // crash,
    // because it frees oldest entry when the buffer size is reached
    protected static LruCache<URL, Bitmap> images    = new LruCache<URL, Bitmap>(maxMemory / 8) {

                                                         protected int sizeOf(URL key, Bitmap bitmap) {
                                                             return bitmap.getByteCount() / 1024;
                                                         }
                                                     };

    public ImageLoader(ImageView view, ProgressBar progressBar, URL url) {
        this.view = view;
        this.progressBar = progressBar;
        this.url = url;
    }

    public static boolean hasImage(URL url) {
        return images.get(url) != null;
    }

    public static Bitmap getImage(URL url) {
        return images.get(url);
    }

    @Override
    protected Bitmap doInBackground(final Void... params) {
        try {
            // Create image from url
            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bitmap;
        } catch (IOException e) {
            Log.e("ImageLoader Error", "> " + e.getMessage());
        }
        return null;
    }

    protected void onPostExecute(Bitmap bitmap) {
        // When image is ready...
        images.put(url, bitmap);
        view.setImageBitmap(bitmap);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        // view.setVisibility(View.VISIBLE);
    }
}
