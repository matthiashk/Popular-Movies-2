package com.matthiasko.multipane;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
import java.util.List;

/**
 * Created by matthiasko on 9/19/15.
 */
public class GridFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public ArrayList<TmdbMovie> finalMovieData = new ArrayList<TmdbMovie>();
    private ImageAdapter mImageAdapter;
    private GridView mGridview;
    public static ArrayList<String> posterUrls = new ArrayList<String>();
    DBHandler handler;

    OnHeadlineSelectedListener mCallback;

    public interface OnHeadlineSelectedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onArticleSelected(Bundle bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grid_frament, container, false);

        /*
        Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                mCallback.onArticleSelected();
            }
        });
        */


        mGridview = (GridView) view.findViewById(R.id.gridview);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new DBHandler(getActivity());
        //mGridview = (GridView) findViewById(R.id.gridview);
        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Bundle bundle = new Bundle();
                bundle.putString("title", finalMovieData.get(position).title);
                bundle.putString("posterpath", finalMovieData.get(position).posterPath);
                bundle.putString("plot", finalMovieData.get(position).plot);
                bundle.putDouble("userrating", finalMovieData.get(position).userRating);
                bundle.putString("releasedate", finalMovieData.get(position).releaseDate);

                mCallback.onArticleSelected(bundle);

            }
        });

        // set default values, only if they have not been set before
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_general, false);

        // get the sort order from preferences and store in mSortOrder
        // so we can detect if the user changed it later
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        // use this to detect preference changes instead...
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);

        String sortOrder = sharedPrefs.getString(
                getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_default));

        //mSortOrder = sortOrder;
        // check if db exists
        if (handler.getMovieCount() == 0) {

            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
            fetchMoviesTask.execute();

            //Log.v("DB", "fetching...");

        } else {

            finalMovieData = handler.getAllMovies();

            posterUrls.clear();

            // get the array of strings containing the poster paths and set to posterUrls
            if (finalMovieData != null) {
                for (TmdbMovie item : finalMovieData) {
                    posterUrls.add(item.posterPath);
                }
            }

            // create new array containing complete poster urls
            ArrayList<String> posterUrlsFinal = new ArrayList<String>();

            String baseURL = "http://image.tmdb.org/t/p/";

            String thumbSize = "w185";

            String posterPath = null;

            String finalURL = null;

            //Log.d("this is my array", "posterUrls: " + Arrays.toString(posterUrls));

            for (int i = 0; i < posterUrls.size(); i++) {
                posterPath = posterUrls.get(i);
                finalURL = baseURL + thumbSize + posterPath;
                posterUrlsFinal.add(finalURL);
            }

            List<String> uriPaths = new ArrayList<String>();
            // clear the default image list before adding
            uriPaths.clear();

            for (int i = 0; i < posterUrlsFinal.size(); i++) {
                uriPaths.add(posterUrlsFinal.get(i));
            }

            mImageAdapter = new ImageAdapter(getActivity().getApplicationContext(), uriPaths);
            mGridview.setAdapter(mImageAdapter);
            mImageAdapter.notifyDataSetChanged();

            Log.v("DB", "loading from db...");
            //System.out.println("finalMovieData size: " + finalMovieData.size());
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // we want to fetch movies with the new sort order once before using db
        // in order to populate the db with more movies

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        String sortOrder = sharedPrefs.getString(
                getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_default));

        // the following will only be run one time
        if (!sharedPrefs.getBoolean("firstTime", false)) {

            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
            fetchMoviesTask.execute();

            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();

            Log.v("PREF", "FIRSTTIME...");

        } else {

            // code for next time the pref is changed
            // fetch from db

            finalMovieData.clear();

            finalMovieData = handler.getAllSortedMovies(sortOrder);

            posterUrls.clear();

            // get the array of strings containing the poster paths and set to posterUrls
            if (finalMovieData != null) {
                for (TmdbMovie item : finalMovieData) {
                    posterUrls.add(item.posterPath);
                }
            }

            // create new array containing complete poster urls
            ArrayList<String> posterUrlsFinal = new ArrayList<String>();

            String baseURL = "http://image.tmdb.org/t/p/";

            String thumbSize = "w185";

            String posterPath = null;

            String finalURL = null;

            //Log.d("this is my array", "posterUrls: " + Arrays.toString(posterUrls));

            for (int i = 0; i < posterUrls.size(); i++) {
                posterPath = posterUrls.get(i);
                finalURL = baseURL + thumbSize + posterPath;
                posterUrlsFinal.add(finalURL);
            }

            List<String> uriPaths = new ArrayList<String>();
            // clear the default image list before adding
            uriPaths.clear();

            for (int i = 0; i < posterUrlsFinal.size(); i++) {
                uriPaths.add(posterUrlsFinal.get(i));
            }

            mImageAdapter = new ImageAdapter(getActivity().getApplicationContext(), uriPaths);
            mGridview.setAdapter(mImageAdapter);
            mImageAdapter.notifyDataSetChanged();

            //Log.v("PREF", "USING DB...");
        }

    }


    public void resultsFromFetch(ArrayList<TmdbMovie> movieData) {
        // getting movieData from onpostexecute
        finalMovieData = movieData;
    }

    /**
     * Gets movie data from JSON and store into an arraylist.
     */

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<TmdbMovie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private ArrayList<TmdbMovie> getMovieDataFromJson(String forecastJsonStr)
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

            JSONObject forecastJson = new JSONObject(forecastJsonStr);

            JSONArray jArray = forecastJson.getJSONArray("results");

            ArrayList<TmdbMovie> movieData = new ArrayList<TmdbMovie>();

            for (int i = 0; i < jArray.length(); i++) {
                try {

                    //Movie movie = new Movie();

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

                    //Log.v("getMovieDataFromJson", oneObjectsItem);
                    movieData.add(new TmdbMovie(title, posterPath, plot, userRating, releaseDate, popularity, voteCount, movieID));

                    handler.addMovie(new TmdbMovie(title, posterPath, plot, userRating, releaseDate, popularity, voteCount, movieID));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return movieData;
        }

        @Override
        protected ArrayList<TmdbMovie> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            // get sort order from preferences
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            String sortOrder = sharedPrefs.getString(
                    getString(R.string.pref_sort_order_key),
                    getString(R.string.pref_sort_order_default));

            try {
                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";
                final String VOTE_COUNT = "vote_count.gte";

                // todo: REMOVE api key before submitting project!!!
                Uri builtUri = Uri.parse(MOVIE_BASE_URL)
                        .buildUpon()
                        .appendQueryParameter(SORT_BY_PARAM, sortOrder)
                        .appendQueryParameter(VOTE_COUNT, "10")
                        .appendQueryParameter(API_KEY_PARAM, "aa336466223f0deecbe36bf1aafd76d3")
                        .build();

                URL url = new URL(builtUri.toString());
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
                //Log.v(LOG_TAG, moviesJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                return null;
            } finally {

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

            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<TmdbMovie> movieData) {

            resultsFromFetch(movieData);

            posterUrls.clear();

            // get the array of strings containing the poster paths and set to posterUrls
            if (movieData != null) {
                for (TmdbMovie item : movieData) {
                    posterUrls.add(item.posterPath);
                }
            }

            // create new array containing complete poster urls
            ArrayList<String> posterUrlsFinal = new ArrayList<String>();

            String baseURL = "http://image.tmdb.org/t/p/";

            String thumbSize = "w185";

            String posterPath = null;

            String finalURL = null;

            //Log.d("this is my array", "posterUrls: " + Arrays.toString(posterUrls));

            for (int i = 0; i < posterUrls.size(); i++) {
                posterPath = posterUrls.get(i);
                finalURL = baseURL + thumbSize + posterPath;
                posterUrlsFinal.add(finalURL);
            }

            List<String> uriPaths = new ArrayList<String>();
            // clear the default image list before adding
            uriPaths.clear();

            for (int i = 0; i < posterUrlsFinal.size(); i++) {
                uriPaths.add(posterUrlsFinal.get(i));
            }

            mImageAdapter = new ImageAdapter(getActivity().getApplicationContext(), uriPaths);
            mGridview.setAdapter(mImageAdapter);
            mImageAdapter.notifyDataSetChanged();
        }
    }

}
