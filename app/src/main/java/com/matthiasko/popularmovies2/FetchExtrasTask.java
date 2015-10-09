package com.matthiasko.popularmovies2;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.matthiasko.popularmovies2.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by matthiasko on 9/29/15.
 */
public class FetchExtrasTask extends AsyncTask<String, Void, Void> {

    private FetchExtrasResponse fetchExtrasResponse;
    private final String LOG_TAG = FetchExtrasTask.class.getSimpleName();
    private final Context mContext;
    private String mMovieId;

    /* Replace API_KEY here. Also replace API_KEY in GridFragment. */
    private final String API_KEY = "aa336466223f0deecbe36bf1aafd76d3";

    public FetchExtrasTask(Context context, FetchExtrasResponse fetchExtrasResponse) {
        mContext = context;
        this.fetchExtrasResponse = fetchExtrasResponse;
    }

    // send result to onpostexecute
    // get trailer and review info from json
    // and store in our database
    private void getMovieDataFromJson(String extrasJsonStr)
            throws JSONException {

        ArrayList<String> trailersArray = new ArrayList<String>();
        ArrayList<String> reviewsArray = new ArrayList<String>();

        try {

            JSONObject extrasJson = new JSONObject(extrasJsonStr);
            JSONObject trailers = extrasJson.getJSONObject("trailers");
            JSONArray youtubeTrailers = trailers.getJSONArray("youtube");

            final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?";
            final String QUERY_PARAM = "v";

            for (int i = 0; i < youtubeTrailers.length(); i++) {

                JSONObject objectInArray = youtubeTrailers.getJSONObject(i);

                String ytName = objectInArray.getString("name");
                String ytSize = objectInArray.getString("size");
                String ytSource = objectInArray.getString("source");
                String ytType = objectInArray.getString("type");

                final Uri builtUri = Uri.parse(YOUTUBE_BASE_URL)
                        .buildUpon()
                        .appendQueryParameter(QUERY_PARAM, ytSource)
                        .build();

                trailersArray.add(ytName);
                trailersArray.add(builtUri.toString());
            }

            JSONObject reviews = extrasJson.getJSONObject("reviews");
            JSONArray reviewResults = reviews.getJSONArray("results");

            for (int i = 0; i < reviewResults.length(); i++) {

                JSONObject objectInArray = reviewResults.getJSONObject(i);

                //String rId = objectInArray.getString("id");
                String rAuthor = objectInArray.getString("author");
                String rContent = objectInArray.getString("content");
                //String rUrl = objectInArray.getString("url");

                reviewsArray.add(rAuthor);
                reviewsArray.add(rContent);
            }

            JSONObject json = new JSONObject();
            json.put("trailersArray", new JSONArray(trailersArray));
            String trailersArrayList = json.toString();

            JSONObject reviewsJson = new JSONObject();
            reviewsJson.put("reviewsArray", new JSONArray(reviewsArray));
            String reviewsArrayList = reviewsJson.toString();

            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.COLUMN_TRAILERS, trailersArrayList);
            movieValues.put(MovieContract.MovieEntry.COLUMN_REVIEWS, reviewsArrayList);

            String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=" + mMovieId;
            String[] selectionArgs = null;

            // update movie entry, adding image and favorite status to database
            mContext.getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, movieValues,
                    selection, selectionArgs);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {

        // create url request using the movie id and send result to getMovieDataFromJson method
        if (params.length == 0) {
            return null;
        }
        mMovieId = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        try {
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie?";
            final String API_KEY_PARAM = "api_key";
            final String APPEND_TO_RESPONSE = "append_to_response";

            // TODO: REMOVE api key before submitting project!!!
            Uri builtUri = Uri.parse(MOVIE_BASE_URL)
                    .buildUpon()
                    .appendPath(mMovieId)
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .appendQueryParameter(APPEND_TO_RESPONSE, "trailers,reviews")
                    .build();

            URL url = new URL(builtUri.toString());
            //System.out.println("builtUri = " + builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            moviesJsonStr = buffer.toString();

            getMovieDataFromJson(moviesJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        finally {

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        // let the detail fragment know the data was fetched
        fetchExtrasResponse.onSuccess();
    }
}
