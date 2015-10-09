package com.matthiasko.popularmovies2;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final ImageView posterView;

        public ViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.posterview);
        }
    }

    public ImageAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.posterView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // create full url for the poster needed from tmdb.org
        String baseURL = "http://image.tmdb.org/t/p/";
        String thumbSize = "w185";
        String posterPath = null;
        String finalURL = null;

        posterPath = cursor.getString(GridFragment.COL_MOVIE_POSTER_PATH);
        finalURL = baseURL + thumbSize + posterPath;

        Picasso.with(context)
                .load(finalURL)
                .resize(600, 900)
                .into(viewHolder.posterView);
    }
}