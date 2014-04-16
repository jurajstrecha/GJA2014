/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fit.gja.twitter.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader extends AsyncTask<Void, Void, Bitmap> {

    private ImageView           view;
    private ProgressBar         progressBar;
    private URL                 url;
    protected static Map<URL, Bitmap> images = new HashMap<URL, Bitmap>();

    public ImageLoader(ImageView view, ProgressBar progressBar, URL url) {
        this.view = view;
        this.progressBar = progressBar;
        this.url = url;
    }

    public static boolean hasImage(URL url) {
        return images.containsKey(url);
    }

    public static Bitmap getImage(URL url) {
        return images.get(url);
    }

    @Override
    protected Bitmap doInBackground(final Void... params) {

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bitmap;
        } catch (IOException e) {
            Log.e("ImageLoader Error", "> " + e.getMessage());
        }

        return null;
    }

    protected void onPostExecute(Bitmap bitmap) {
        images.put(url, bitmap);
        view.setImageBitmap(bitmap);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        //view.setVisibility(View.VISIBLE);
    }
}
