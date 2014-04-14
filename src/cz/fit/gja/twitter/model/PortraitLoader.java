/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fit.gja.twitter.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ImageView;
import cz.fit.gja.twitter.adapters.UserAdapter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.User;

/**
 * 
 * @author Nox
 */
public class PortraitLoader extends AsyncTask<Void, Void, Void> {

    private final Integer                IO_BUFFER_SIZE = 8000;
    private final User                   user;
    private final ImageView              view;
    private final Handler                refresh;
    protected static Map<String, Bitmap> portraits      = new HashMap<String, Bitmap>();

    public PortraitLoader(User user, ImageView view, Handler refresh) {
        this.user = user;
        this.view = view;
        this.refresh = refresh;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (portraits.containsKey(user.getScreenName())) {
            return null;
        }
        final String url = user.getBiggerProfileImageURL();
        refresh.post(new Runnable() {

            public void run() {
                try {
                    BufferedInputStream in = new BufferedInputStream(new URL(url).openStream(),
                                                                     IO_BUFFER_SIZE);
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
                } catch (MalformedURLException ex) {
                    Logger.getLogger(UserAdapter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(UserAdapter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        return null;
    }

    public static boolean hasPortrait(String screenName) {
        return portraits.containsKey(screenName);
    }

    public static Bitmap getPortrait(String screenName) {
        return portraits.get(screenName);
    }

}
