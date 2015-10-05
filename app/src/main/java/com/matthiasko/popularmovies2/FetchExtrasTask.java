package com.matthiasko.popularmovies2;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
public class FetchExtrasTask extends AsyncTask<String, Void, Wrapper> {

    private FetchExtrasResponse fetchExtrasResponse;

    //public void setFetchExtrasResponse(FetchExtrasResponse fetchExtrasResponse){
    //    this.fetchExtrasResponse = fetchExtrasResponse;
    //}


    private final String LOG_TAG = FetchExtrasTask.class.getSimpleName();
    private final Context mContext;

    public FetchExtrasTask(Context context, FetchExtrasResponse fetchExtrasResponse) {
        mContext = context;
        this.fetchExtrasResponse = fetchExtrasResponse;
    }

    // send result to onpostexecute
    private Wrapper getMovieDataFromJson(String extrasJsonStr)
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

        ArrayList<YTObject> result = new ArrayList<YTObject>();

        ArrayList<ReviewObject> reviewsArray = new ArrayList<ReviewObject>();

        Wrapper wrapper = new Wrapper();

        try {

            JSONObject extrasJson = new JSONObject(extrasJsonStr);

            //JSONArray jArray = extrasJson.getJSONArray("trailers");

            // Insert the new weather information into the database
            //Vector<ContentValues> cVVector = new Vector<ContentValues>(jArray.length());

            //System.out.println("extrasJson = " + extrasJson.toString());

            JSONObject trailers = extrasJson.getJSONObject("trailers");

            JSONArray youtubeTrailers = trailers.getJSONArray("youtube");

            //System.out.println("trailers = " + trailers.toString());

            //System.out.println("youtubeTrailers = " + youtubeTrailers.toString());

            for (int i = 0; i < youtubeTrailers.length(); i++) {

                JSONObject objectInArray = youtubeTrailers.getJSONObject(i);

                String ytName = objectInArray.getString("name");
                String ytSize = objectInArray.getString("size");
                String ytSource = objectInArray.getString("source");
                String ytType = objectInArray.getString("type");

                result.add(new YTObject(ytName, ytSize, ytSource, ytType));
            }


            JSONObject reviews = extrasJson.getJSONObject("reviews");

            JSONArray reviewResults = reviews.getJSONArray("results");


            for (int i = 0; i < reviewResults.length(); i++) {

                JSONObject objectInArray = reviewResults.getJSONObject(i);

                String rId = objectInArray.getString("id");
                String rAuthor = objectInArray.getString("author");
                String rContent = objectInArray.getString("content");
                String rUrl = objectInArray.getString("url");

                reviewsArray.add(new ReviewObject(rId, rAuthor, rContent, rUrl));
            }



            wrapper.ytObjectArrayList = result;
            wrapper.reviewObjectArrayList = reviewsArray;





            /*  TODO: put trailers info in database??
                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieID);
                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
                movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT, plot);
                movieValues.put(MovieContract.MovieEntry.COLUMN_USER_RATING, userRating);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, voteCount);

                cVVector.add(movieValues);
                */

            /*
            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }*/
            //Log.d(LOG_TAG, "FetchWeatherTask Complete. " + inserted + " Inserted");
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        return wrapper;
    }

    @Override
    protected Wrapper doInBackground(String... params) {

            /* Create api url request and send results to json parser. */


        /*
                WE NEED TO GET MOVIE ID FROM GRIDFRAGMENT ON ITEM SELECT...



         */

        if (params.length == 0) {
            return null;
        }
        String movieId = params[0];

        //System.out.println("movieId = " + movieId);

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;



        try {
            final String MOVIE_BASE_URL =
                    "http://api.themoviedb.org/3/movie?";
            final String API_KEY_PARAM = "api_key";
            final String APPEND_TO_RESPONSE = "append_to_response";

            // TODO: REMOVE api key before submitting project!!!
            Uri builtUri = Uri.parse(MOVIE_BASE_URL)
                    .buildUpon()
                    .appendPath(movieId)
                    .appendQueryParameter(API_KEY_PARAM, "aa336466223f0deecbe36bf1aafd76d3")
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

            return getMovieDataFromJson(moviesJsonStr);
            //Log.v(LOG_TAG, moviesJsonStr);

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
    protected void onPostExecute(Wrapper wrapper) {
        super.onPostExecute(wrapper);
        /*
        for(int i=0; i < wrapper.ytObjectArrayList.size(); i++){
            System.out.println(wrapper.ytObjectArrayList.get(i).getYtName());
        }
        */

        fetchExtrasResponse.onSuccess(wrapper);
    }
}
