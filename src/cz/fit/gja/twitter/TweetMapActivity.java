package cz.fit.gja.twitter;

/**
 * File: TweetMapActivity.java
 * 
 *  Instance of this class receives numerical data representing position on the map.
 *  On initialization instance of this class displays part of the map given
 *  by the data contained wihtin a tweet. If user provided area boundries
 *  information only, no marker will be placed to the map. If exact coordinates
 *  are present blank default marker will be placed into the map and map camera
 *  will be centered according to the marker.
 */

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class TweetMapActivity extends LoggedActivity {

    private GoogleMap googleMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        setTitle(R.string.title_map);

        try {
            // Loading map
            initilizeMap(savedInstanceState);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initilizeMap(Bundle savedInstanceState) {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(), "Unable to load map", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (savedInstanceState != null) {
                if (savedInstanceState.getDoubleArray("bounds") != null) {
                    final double bounds[] = savedInstanceState.getDoubleArray("bounds");
                    final LatLngBounds llbounds = new LatLngBounds(new LatLng(bounds[0], bounds[1]), new LatLng(bounds[2], bounds[3]));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(llbounds, 0));
                } else {
                    final double coords[] = savedInstanceState.getDoubleArray("coords");
                    final LatLng llcoords = new LatLng(coords[0], coords[1]);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(llcoords));
                    googleMap.addMarker(new MarkerOptions().position(llcoords));
                }
            }
        }
    }
}
