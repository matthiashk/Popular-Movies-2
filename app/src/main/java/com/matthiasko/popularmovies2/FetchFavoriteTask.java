package com.matthiasko.popularmovies2;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.matthiasko.popularmovies2.data.MovieContract;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by matthiasko on 10/4/15.
 */
public class FetchFavoriteTask  extends AsyncTask<String, Void, Void> {

    private final Context mContext;
    private String mUrl;
    private int mMovieId;

    // get movie poster image from url and store into byte array, so we can store the image
    // in the database as a blob type and retrieve later
    private byte[] getMoviePosterImage(String url){
        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(500);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            return baf.toByteArray();
        } catch (Exception e) {
            Log.d("FetchFavoriteTask", "Error: " + e.toString());
        }
        return null;
    }

    public FetchFavoriteTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {

        // get the movie poster image and store in database
        // also change favorite status to true
        if (params.length == 0) {
            return null;
        }

        mUrl = params[0];
        String movieId = params[1];
        // convert string id to int
        mMovieId = Integer.parseInt(movieId);

        // call getMoviePosterImage method to set byte array code
        byte[] imageInByte = getMoviePosterImage(mUrl);

        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_IMAGE, imageInByte);
        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1); // change favorite to true

        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=" + mMovieId;
        String[] selectionArgs = null;

        // update movie entry, adding image and favorite status
        mContext.getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, movieValues,
                selection, selectionArgs);

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }
}
