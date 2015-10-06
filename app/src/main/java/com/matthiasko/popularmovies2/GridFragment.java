package com.matthiasko.popularmovies2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.matthiasko.popularmovies2.data.MovieContract;
import com.matthiasko.popularmovies2.data.MovieContract.MovieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by matthiasko on 9/19/15.
 */
public class GridFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private ImageAdapter mImageAdapter;
    private GridView mGridview;
    private String mSortOrder;
    public static final String CONTENT_AUTHORITY = "com.matthiasko.popularmovies2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";
    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

    private static final String[] PROJECTION = new String[] { "_id", "title", "poster_path", "plot",
            "user_rating", "release_date", "popularity", "vote_count", "movie_id"};

    OnHeadlineSelectedListener mCallback;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_PLOT,
            MovieEntry.COLUMN_USER_RATING,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_POPULARITY,
            MovieEntry.COLUMN_VOTE_COUNT,
            MovieEntry.COLUMN_MOVIE_ID,
    };

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_POSTER_PATH = 2;
    public static final int COL_MOVIE_PLOT = 3;
    public static final int COL_MOVIE_USER_RATING = 4;
    public static final int COL_MOVIE_RELEASE_DATE = 5;
    public static final int COL_MOVIE_POPULARITY = 6;
    public static final int COL_MOVIE_VOTE_COUNT = 7;
    public static final int COL_MOVIE_ID = 8;

    public GridFragment() { }

    public interface OnHeadlineSelectedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onArticleSelected(Bundle bundle);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //System.out.println("GRIDFRAGMENT - onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grid_frament, container, false);

        mImageAdapter = new ImageAdapter(getActivity().getApplicationContext(), null, 0);
        mGridview = (GridView) view.findViewById(R.id.gridview);
        mGridview.setAdapter(mImageAdapter);
        /*
        if (savedInstanceState == null) {

            System.out.println("GRIDFRAGMENT - savedInstanceState is null");

        } else {

            System.out.println("GRIDFRAGMENT - savedInstanceState is NOT null");
        }*/
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
            use parcelable movie class and putparcable method to save movies in bundle
         */

        //Intent parcelIntent = new Intent();
        //ArrayList<TmdbMovie> dataList = new ArrayList<TmdbMovie>();
        /*
         * Add elements in dataLists e.g.
         * ParcelData pd1 = new ParcelData();
         * ParcelData pd2 = new ParcelData();
         *
         * fill in data in pd1 and pd2
         *
         * dataLists.add(pd1);
         * dataLists.add(pd2);
         */

        /*
        TmdbMovie movie1 = new TmdbMovie();

        movie1.setTitle("Mad Max");
        movie1.setPosterPath("myposterpath");
        movie1.setPlot("in a world...");
        movie1.setUserRating(9);
        movie1.setReleaseDate("1-1-2015");
        movie1.setPopularity(7);
        movie1.setVoteCount(11);
        movie1.setMovieId(0000001);

        dataList.add(movie1);
        */

        //TODO: fix
        //outState.putParcelableArrayList("custom_data_list", (ArrayList<TmdbMovie>)mUriPaths);
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

        final DetailFragment detailFragment = (DetailFragment) getFragmentManager()
                .findFragmentById(R.id.detail_fragment);

        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

            Cursor cursor = (Cursor) mGridview.getItemAtPosition(position);
            if (cursor != null) {
                Bundle bundle = new Bundle();
                bundle.putString("movieURI", MovieContract.MovieEntry.buildMovieUri(cursor.getInt(COL_ID)).toString());
                mCallback.onArticleSelected(bundle);

                // call fetchextrastask with movie id as parameter
                String movieIDString = String.valueOf(cursor.getInt(COL_MOVIE_ID));

                FetchExtrasTask extrasTask = new FetchExtrasTask(getActivity(), detailFragment);

                extrasTask.execute(movieIDString);
            }
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

        mSortOrder = sortOrder;
        //System.out.println("sortOrder = " + sortOrder);

        // check if db exists
        File pm2Db = getActivity().getDatabasePath("movies.db");

        if (pm2Db.exists()) {

            //Log.v("GRIDFRAGMENT", "db found. load from db...");
            // load from cursor here?

        } else {

            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getActivity());
            fetchMoviesTask.execute();
            //Log.v("GRIDFRAGMENT", "no db found. fetching...");
        }
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);

        if (savedInstanceState == null) {

            //System.out.println("GRIDFRAGMENT - onActivityCreated - no savedInstanceState" );
        } else {

            //List<String> uriPaths = new ArrayList<String>();

            //ArrayList<TmdbMovie> dataList = new ArrayList<TmdbMovie>();

            //Intent intent = getActivity().getIntent();

            //uriPaths = savedInstanceState.getStringArrayList("custom_data_list");

            //TmdbMovie movie1 = (TmdbMovie) dataList.get(0);

            //System.out.println("GRIDFRAGMENT - EXTRA = " + uriPaths.get(0));

            // get savedInstanceState here and set to adapter?

            //String fromBundle = savedInstanceState.getString("myKey");

            //System.out.println("GRIDFRAGMENT - fromBundle = " + fromBundle);

            //mImageAdapter = new ImageAdapter(getActivity().getApplicationContext(), uriPaths);
            //mGridview.setAdapter(mImageAdapter);
            //mImageAdapter.notifyDataSetChanged();

            // this fixed issues with displaying images after rotation change
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        /* we want to fetch movies with the new sort order once before using db
         in order to populate the db with more movies */

        // get sort order from preferences
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        String sortOrder = sharedPrefs.getString(
                getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_default));

        // set mSortOrder so we can read in onCreateLoader
        mSortOrder = sortOrder;
        //System.out.println("onSharedPreferenceChanged");
        // refresh loader so we can change sort order
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);

        // lets retrieve some more movies here from server
        // the following will only be run one time
        if (!sharedPrefs.getBoolean("firstTime", false)) {

            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getActivity());
            fetchMoviesTask.execute();

            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
            //Log.v("PREF", "FIRSTTIME...");
        }
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Void> { // TODO: put in seperate file

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

            //ArrayList<String> trailers = new ArrayList<String>();

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

                    /*JSONArray trailers = oneObject.getJSONArray("trailers");
                    JSONArray youtubeTrailers = trailers.getJSONArray(1);
                    String ytName = youtubeTrailers.getString(0);
                    String ytSize = youtubeTrailers.getString(1);
                    String ytSource = youtubeTrailers.getString(2);
                    String ytType = youtubeTrailers.getString(3);

                    System.out.println("ytName = " + ytName);
                    System.out.println("ytSize = " + ytSize);
                    System.out.println("ytSource = " + ytSource);
                    System.out.println("ytType = " + ytType);
*/








                    ContentValues movieValues = new ContentValues();

                    movieValues.put(MovieEntry.COLUMN_MOVIE_ID, movieID);
                    movieValues.put(MovieEntry.COLUMN_TITLE, title);
                    movieValues.put(MovieEntry.COLUMN_POSTER_PATH, posterPath);
                    movieValues.put(MovieEntry.COLUMN_PLOT, plot);
                    movieValues.put(MovieEntry.COLUMN_USER_RATING, userRating);
                    movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
                    movieValues.put(MovieEntry.COLUMN_POPULARITY, popularity);
                    movieValues.put(MovieEntry.COLUMN_VOTE_COUNT, voteCount);
                    movieValues.put(MovieEntry.COLUMN_FAVORITE, 0);

                    cVVector.add(movieValues);
                }

                int inserted = 0;
                // add to database
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
                }
                //Log.d(LOG_TAG, "FetchWeatherTask Complete. " + inserted + " Inserted");
            }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }

        @Override
        protected Void doInBackground(String... params) {

            /* Create api url request and send results to json parser. */

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            // Get sort order from preferences.
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
                //final String APPEND_TO_RESPONSE = "append_to_response";

                // TODO: REMOVE api key before submitting project!!!
                Uri builtUri = Uri.parse(MOVIE_BASE_URL)
                        .buildUpon()
                        .appendQueryParameter(SORT_BY_PARAM, sortOrder)
                        .appendQueryParameter(VOTE_COUNT, "75")
                        .appendQueryParameter(API_KEY_PARAM, "aa336466223f0deecbe36bf1aafd76d3")
                        //.appendQueryParameter(APPEND_TO_RESPONSE, "trailers,reviews")
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
        /*
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
        }*/
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // read sortOrder and make if/else based on sortOrder
        // 2 options are stored as popularity.desc and vote_count.desc

        String sortOrder = null;
        String selection = null;

        if (mSortOrder.equals("popularity.desc")) {

            sortOrder = MovieEntry.COLUMN_POPULARITY + " DESC";

        } else if (mSortOrder.equals("vote_average.desc")) {

            sortOrder = MovieEntry.COLUMN_USER_RATING + " DESC";

        } else if  (mSortOrder.equals("favorites")) {


            // TODO: Create selection to filter db for only favorites

            selection = MovieEntry.COLUMN_FAVORITE + "=" + 1;

        }




        return new CursorLoader(
                getActivity(),
                CONTENT_URI,
                PROJECTION,
                selection,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mImageAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        // clear old data here
        mImageAdapter.swapCursor(null);
    }
}
