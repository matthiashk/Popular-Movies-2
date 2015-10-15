package com.matthiasko.popularmovies2;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
import java.util.Vector;

/**
 * Created by matthiasko on 10/14/15.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private final Context mContext;

    public FetchMoviesTask(Context context) {
        mContext = context;
    }

    private void getMovieDataFromJson(String forecastJsonStr)
            throws JSONException {

        // the strings should match the api strings
        final String TMDB_TITLE = "title";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_PLOT = "overview";
        final String TMDB_USER_RATING = "vote_average";
        final String TMDB_RELEASE_DATE = "release_date";

        final String TMDB_POPULARITY = "popularity";
        final String TMDB_VOTE_COUNT = "vote_count";

        final String TMDB_MOVIE_ID = "id";

        try {

            JSONObject forecastJson = new JSONObject(forecastJsonStr);

            JSONArray jArray = forecastJson.getJSONArray("results");

            // Insert the new information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(jArray.length());

            for (int i = 0; i < jArray.length(); i++) {

                JSONObject oneObject = jArray.getJSONObject(i);
                // Pulling items from the array
                String title = oneObject.getString(TMDB_TITLE);
                String posterPath = oneObject.getString(TMDB_POSTER_PATH);
                String plot = oneObject.getString(TMDB_PLOT);
                double userRating = oneObject.getDouble(TMDB_USER_RATING);
                String releaseDate = oneObject.getString(TMDB_RELEASE_DATE);
                int popularity = oneObject.getInt(TMDB_POPULARITY);
                int voteCount = oneObject.getInt(TMDB_VOTE_COUNT);
                int movieID = oneObject.getInt(TMDB_MOVIE_ID);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieID);
                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
                movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT, plot);
                movieValues.put(MovieContract.MovieEntry.COLUMN_USER_RATING, userRating);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, voteCount);
                movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 0);

                cVVector.add(movieValues);
            }

            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }
        }

        catch(JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        /* Create api url request and send results to json parser. */

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr;

        // Get sort order from preferences.
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        String sortOrder = sharedPrefs.getString(
                mContext.getString(R.string.pref_sort_order_key),
                mContext.getString(R.string.pref_sort_order_default));

        try {
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_BY_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";
            final String VOTE_COUNT = "vote_count.gte";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(SORT_BY_PARAM, sortOrder)
                    .appendQueryParameter(VOTE_COUNT, "75")
                    .appendQueryParameter(API_KEY_PARAM, mContext.getResources().getString(R.string.api_key))
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
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
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
}
