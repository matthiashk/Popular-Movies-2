package com.matthiasko.popularmovies2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * The ImageAdapter used for the GridView.
 */

public class ImageAdapter extends BaseAdapter {

    /*
        we have to change mTmdbMovieList to an arraylist of TmdbMovie objects
        and extract the poster information from there
        eventually we will have to extract a stored image


     */

    //private List<String> mTmdbMovieList;

    private ArrayList<TmdbMovie> mTmdbMovieList;

    private Context mContext;

    public ImageAdapter(Context context, ArrayList<TmdbMovie> imageUrls) {
        this.mContext = context;
        this.mTmdbMovieList = imageUrls;
    }

    public int getCount() {
        return mTmdbMovieList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(params));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        /*
            process posterpath extracted from TmdbMovie object here...

         */

        String baseURL = "http://image.tmdb.org/t/p/";

        String thumbSize = "w185";

        String posterPath = null;



        String finalURL = null;

        posterPath = mTmdbMovieList.get(position).posterPath;
        finalURL = baseURL + thumbSize + posterPath;




        Picasso.with(mContext)
                .load(finalURL)
                .resize(600, 900)
                .into(imageView);

        return imageView;
    }
}