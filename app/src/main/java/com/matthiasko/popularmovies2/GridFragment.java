package com.matthiasko.popularmovies2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.matthiasko.popularmovies2.data.MovieContract;
import com.matthiasko.popularmovies2.data.MovieContract.MovieEntry;

import java.io.File;

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

    // this PROJECTION is also used by ImageAdapter to get some of the values.
    private static final String[] PROJECTION = new String[] { "_id", "title", "poster_path", "plot",
            "user_rating", "release_date", "popularity", "vote_count", "movie_id", "favorite", "image"};

    OnMovieSelectedListener mCallback;

    private static final int DETAIL_LOADER = 0;

    public static final int COL_ID = 0;
    //public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_POSTER_PATH = 2;
    //public static final int COL_MOVIE_PLOT = 3;
    //public static final int COL_MOVIE_USER_RATING = 4;
    //public static final int COL_MOVIE_RELEASE_DATE = 5;
    //public static final int COL_MOVIE_POPULARITY = 6;
    //public static final int COL_MOVIE_VOTE_COUNT = 7;
    public static final int COL_MOVIE_ID = 8;
    public static final int COL_FAVORITE = 9;
    public static final int COL_IMAGE = 10;

    public GridFragment() {}

    private Context mGridContext;
    private int mScrollPosition;

    public interface OnMovieSelectedListener {
        public void onArticleSelected(Bundle bundle);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGridContext = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grid_fragment, container, false);

        mImageAdapter = new ImageAdapter(getActivity().getApplicationContext(), null, 0);
        mGridview = (GridView) view.findViewById(R.id.gridview);
        mGridview.setAdapter(mImageAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mScrollPosition = mGridview.getFirstVisiblePosition();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save the position of the gridview, so we can restore it on rotation
        outState.putInt("savedPosition", mScrollPosition);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnMovieSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMovieSelectedListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // set default values, only if they have not been set before
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_general, false);

        // get the sort order from preferences and store in mSortOrder
        // so we can detect if the user changed it later
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        // use this to detect preference changes instead...
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);

        mSortOrder = sharedPrefs.getString(
                getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_default));

        // check if db exists
        File pm2Db = getActivity().getDatabasePath("movies.db");

        if (!pm2Db.exists()) { // if no db found, fetch from internet
            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getActivity());
            fetchMoviesTask.execute();
        }

        if (savedInstanceState != null) {
            // we need this otherwise the gridview will have no onclick actions after rotations

            final DetailFragment xDetailFragment = (DetailFragment) getFragmentManager().findFragmentByTag("detailFragmentTag");

            mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    Cursor cursor = (Cursor) mGridview.getItemAtPosition(position);
                    if (cursor != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("movieURI", MovieContract.MovieEntry.
                                buildMovieUri(cursor.getInt(COL_ID)).toString());
                        mCallback.onArticleSelected(bundle);

                        // call fetchextrastask with movie id as parameter
                        String movieIDString = String.valueOf(cursor.getInt(COL_MOVIE_ID));

                        FetchExtrasTask extrasTask = new FetchExtrasTask(getActivity(), xDetailFragment);
                        extrasTask.execute(movieIDString);
                    }
                }
            });

            getLoaderManager().initLoader(DETAIL_LOADER, null, this);

            return;
        }

        final DetailFragment detailFragment = new DetailFragment();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.detail_fragment, detailFragment, "detailFragmentTag");
        fragmentTransaction.hide(detailFragment);
        fragmentTransaction.commit();

        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Cursor cursor = (Cursor) mGridview.getItemAtPosition(position);
                if (cursor != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("movieURI", MovieContract.MovieEntry.
                            buildMovieUri(cursor.getInt(COL_ID)).toString());
                    mCallback.onArticleSelected(bundle);

                    // call fetchextrastask with movie id as parameter
                    String movieIDString = String.valueOf(cursor.getInt(COL_MOVIE_ID));

                    FetchExtrasTask extrasTask = new FetchExtrasTask(getActivity(), detailFragment);
                    extrasTask.execute(movieIDString);
                }
            }
        });

        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        /* we want to fetch movies with the new sort order once before using db
         in order to populate the db with more movies */

        // check if the gridfragment is attached to the activity or else we crash
        if (isAdded()) {

            // get sort order from preferences
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(mGridContext);

            String sortOrder = sharedPrefs.getString(
                    getString(R.string.pref_sort_order_key),
                    getString(R.string.pref_sort_order_default));

            // set mSortOrder so we can read in onCreateLoader
            mSortOrder = sortOrder;
            // refresh loader so we can change sort order
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);

            // lets retrieve some more movies here from server,
            // fetch based on vote average
            if (sortOrder.equals("vote_average.desc")) {
                FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getActivity());
                fetchMoviesTask.execute();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // setup to display movies in the gridview based on the sort order
        String sortOrder = null;
        String selection = null;

        if (mSortOrder.equals("popularity.desc")) {

            sortOrder = MovieEntry.COLUMN_POPULARITY + " DESC";

        } else if (mSortOrder.equals("vote_average.desc")) {

            sortOrder = MovieEntry.COLUMN_USER_RATING + " DESC";

        } else if  (mSortOrder.equals("favorites")) {

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
