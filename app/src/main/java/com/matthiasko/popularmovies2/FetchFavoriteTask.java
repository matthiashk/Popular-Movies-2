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
public class FetchFavoriteTask  extends AsyncTask<String, Void, Wrapper> {

    //private FetchExtrasResponse fetchExtrasResponse;

    //public void setFetchExtrasResponse(FetchExtrasResponse fetchExtrasResponse){
    //    this.fetchExtrasResponse = fetchExtrasResponse;
    //}

    private final String LOG_TAG = FetchFavoriteTask.class.getSimpleName();
    private final Context mContext;
    private String mUrl;
    private int mMovieId;

    public static String MY_FILE_NAME = "movie.jpg";

    //byte[] logoImage = getLogoImage(mUrl);

    private byte[] getLogoImage(String url){
        try {

            //System.out.println("FetchFavoriteTask url = " + url);

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
            Log.d("ImageManager", "Error: " + e.toString());
        }
        return null;
    }

    public FetchFavoriteTask(Context context) {
        mContext = context;


        //this.fetchExtrasResponse = fetchExtrasResponse;
    }

        @Override
    protected Wrapper doInBackground(String... params) {

        // get the value passed from caller DetailFragment
        // FetchFavoriteTask.execute(mPosterUrl); <- mPosterUrl
        if (params.length == 0) {
            return null;
        }

        mUrl = params[0];
            String movieId = params[1];
            // convert string id to int
            mMovieId = Integer.parseInt(movieId);

            //System.out.println("movieId = " + movieId);

        // run and set byte array code
        byte[] imageInByte = getLogoImage(mUrl);


            ContentValues movieValues = new ContentValues();


            movieValues.put(MovieContract.MovieEntry.COLUMN_IMAGE, imageInByte);
            movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1); // change favorite to true

            String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=" + mMovieId;

            String[] selectionArgs = null;


            // update movie entry, adding image and favorite status
            mContext.getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, movieValues,
                    selection, selectionArgs);

        /*
        // create file to save the image in
        File file = new File(mContext.getFilesDir(), MY_FILE_NAME);

        // save image from byte array to file
        try {

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(imageInByte);
            bos.flush();
            bos.close();

        } catch (IOException e) {

            e.printStackTrace();
        }*/

        return null;
    }


    @Override
    protected void onPostExecute(Wrapper wrapper) {
        super.onPostExecute(wrapper);
        /*
        for(int i=0; i < wrapper.ytObjectArrayList.size(); i++){
            System.out.println(wrapper.ytObjectArrayList.get(i).getYtName());
        }
        */

        //fetchExtrasResponse.onSuccess(wrapper);
    }
}
