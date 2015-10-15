package com.matthiasko.popularmovies2;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

        int favorite = cursor.getInt(GridFragment.COL_FAVORITE);

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.posterView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // create full url for the poster needed from tmdb.org
        String baseURL = "http://image.tmdb.org/t/p/";
        String thumbSize = "w185";
        String posterPath;
        String finalURL;

        posterPath = cursor.getString(GridFragment.COL_MOVIE_POSTER_PATH);
        finalURL = baseURL + thumbSize + posterPath;

        // check if the movie is a favorite before displaying image
        // not a favorite, so fetch image from internet
        if (favorite == 0) {

            Picasso.with(context)
                    .load(finalURL)
                    .resize(600, 900)
                    .into(viewHolder.posterView);

        // is a favorite, so fetch image from db
        } else if (favorite == 1) {

            byte[] imageFromDB = cursor.getBlob(GridFragment.COL_IMAGE);

            // create file to save the image in.
            // use movie id as filename
            File file = new File(context.getFilesDir(), cursor.getString(GridFragment.COL_MOVIE_ID));

            //String movieId = cursor.getString(GridFragment.COL_MOVIE_ID);
            //System.out.println("ImageAdapter - movieId = " + movieId);

            // check if the file is already on disk
            // if it is, we dont need to write to file, just load it
            if (file.isFile()) {
                //System.out.println("ImageAdapter - file found for " + movieId);
            } else {
                // save image from byte array to file
                try {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    bos.write(imageFromDB);
                    bos.flush();
                    bos.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Picasso.with(context)
                    .load(file)
                    .placeholder(R.drawable.picasso_placeholder)
                    .error(R.drawable.picasso_error_placeholder)
                    .resize(600, 900)
                    .into(viewHolder.posterView);
        }
    }
}