/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fit.gja.twitter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TweetAdapter extends BaseAdapter {

    public int getCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getItem(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public View getView(int i, View view, ViewGroup vg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /*
     * protected Integer view = R.layout.browse_row_smallpath_view; protected
     * Integer view_headered = R.layout.browse_row_view_headered;
     * 
     * protected LayoutInflater inflater; protected VideoFileMetadata[] data;
     * 
     * public TweetAdapter(Context context, VideoFileMetadata[] data){
     * this.inflater = LayoutInflater.from(context); this.data = data; }
     * 
     * public void setData(VideoFileMetadata[] data) { this.data = data; }
     * 
     * public int getCount() { return this.data.length; }
     */

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    /*
     * public View getView(int position, View convertView, ViewGroup parent){
     * TextView tv; VideoFileMetadata entry = data[position];
     * 
     * if( entry == null ) { return this.inflater.inflate(view, null); }
     * 
     * convertView = this.inflater.inflate(view, null);
     * 
     * tv = (TextView)convertView.findViewById(R.id.filepath); if( tv != null )
     * { tv.setText(entry.getFilepath()); }
     * 
     * tv = (TextView)convertView.findViewById(R.id.filename);
     * tv.setText(entry.getFinalName());
     * 
     * tv = (TextView)convertView.findViewById(R.id.duration);
     * tv.setText(entry.getFormattedDuration());
     * 
     * return convertView; }
     * 
     * public VideoFileMetadata getItem(int i) { return this.data[i]; }
     */

    public long getItemId(int i) {
        return i;
    }
}
