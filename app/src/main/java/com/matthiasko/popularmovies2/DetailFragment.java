package com.matthiasko.popularmovies2;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.matthiasko.popularmovies2.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

/**
 * Created by matthiasko on 9/19/15.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mMovieUri;
    public static final String CONTENT_AUTHORITY = "com.matthiasko.popularmovies2";
    //public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //public static final String PATH_MOVIE = "movie";

    //public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

    private static final String[] PROJECTION = new String[] { "_id", "title", "poster_path", "plot",
            "user_rating", "release_date", "popularity", "vote_count", "movie_id"};

    private static final int X_DETAIL_LOADER = 0;

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

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_POSTER_PATH = 2;
    public static final int COL_MOVIE_PLOT = 3;
    public static final int COL_MOVIE_USER_RATING = 4;
    public static final int COL_MOVIE_RELEASE_DATE = 5;
    public static final int COL_MOVIE_POPULARITY = 6;
    public static final int COL_MOVIE_VOTE_COUNT = 7;

    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setHasOptionsMenu(true);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            movieTitle = extras.getString("title");
            posterPath = extras.getString("posterpath");
            plot = extras.getString("plot");
            userRating = extras.getDouble("userrating");
            releaseDate = extras.getString("releasedate");
        }

        // save the fragment that was already created here?
        //System.out.println("OMEGAFRAGMENT - ONCREATE");


    }*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //getFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);

        if (savedInstanceState == null) {

            //System.out.println("GRIDFRAGMENT - onActivityCreated - no savedInstanceState" );
        } else {

            /* // TODO: temp disabled

            TmdbMovie movie = savedInstanceState.getParcelable("movie");

            ((TextView) view.findViewById(R.id.details_movie_title))
                    .setText(movie.title);

            ((TextView) view.findViewById(R.id.details_plot))
                    .setText(movie.plot);

            // allow user to scroll the view containing the plot synopsis
            ((TextView) view.findViewById(R.id.details_plot))
                    .setMovementMethod(new ScrollingMovementMethod());

            // format the user rating, add '/10'
            String formattedUserRating = String.format("%.1f", movie.userRating) + "/10";

            ((TextView) view.findViewById(R.id.details_user_rating))
                    .setText(formattedUserRating);

            // extract the year from the release date string
            //String movieYear = releaseDate.substring(0, 4);
            ((TextView) view.findViewById(R.id.details_release_date))
                    .setText(movie.releaseDate);

            // showing thumbnail poster
            // construct url for the full posterpath
            String baseURL = "http://image.tmdb.org/t/p/";
            String thumbSize = "w185";
            String posterURL = null;
            posterURL = baseURL + thumbSize + movie.posterPath;

            ImageView imageView = ((ImageView) view.findViewById(R.id.details_imageview));

            Picasso.with(getActivity())
                    .load(posterURL)
                            //.resize(600, 900)
                    .into(imageView);

           */
        }

        /* If the Fragment was destroyed inbetween (screen rotation), we need to recover the savedState first */
        /* However, if it was not, it stays in the instance from the last onDestroyView() and we don't want to overwrite it */
        /*
        if(savedInstanceState != null && savedState == null) {
            savedState = savedInstanceState.getBundle("myBundle");
        }
        if(savedState != null) {
            //sampleText.setText(savedState.getCharSequence("WORKS!"));
        }
        savedState = null;
        */
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //getLoaderManager().initLoader(X_DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (mMovieUri == null) {
            return null;
        }
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                mMovieUri, // mMovieUri???
                PROJECTION,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {

            //int movieId = data.getInt(COL_MOVIE_ID);
            //System.out.println("DETAILFRAGMENT - onLoadFinished COL_MOVIE_ID = " + movieId);

            String lTitle = data.getString(COL_MOVIE_TITLE);
            ((TextView) getView().findViewById(R.id.details_movie_title))
                    .setText(lTitle);

            //System.out.println("lTitle = " + lTitle);

            String lPlot = data.getString(COL_MOVIE_PLOT);
            ((TextView) getView().findViewById(R.id.details_plot))
                    .setText(lPlot);

            // allow user to scroll the view containing the plot synopsis
            ((TextView) getView().findViewById(R.id.details_plot))
                    .setMovementMethod(new ScrollingMovementMethod());

            double lUserRating = data.getDouble(COL_MOVIE_USER_RATING);

            // format the user rating, add '/10'
            String formattedUserRating = String.format("%.1f", lUserRating) + "/10";

            ((TextView) getView().findViewById(R.id.details_user_rating))
                    .setText(formattedUserRating);

            String lReleaseDate = data.getString(COL_MOVIE_RELEASE_DATE);

            ((TextView) getView().findViewById(R.id.details_release_date))
                    .setText(lReleaseDate);

            String lPosterPath = data.getString(COL_MOVIE_POSTER_PATH);

            // construct url for the full posterpath
            String baseURL = "http://image.tmdb.org/t/p/";
            String thumbSize = "w185";
            String posterURL = null;
            posterURL = baseURL + thumbSize + lPosterPath;

            ImageView imageView = ((ImageView) getView().findViewById(R.id.details_imageview));

            Picasso.with(getActivity())
                    .load(posterURL)
                    .resize(600, 900)
                    .into(imageView);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    /* called when item is selected from the gridview from mainactivity */
    public void updateArticleView(Bundle bundle) {

        // bundle is URI as string from gridfragment

        String uriString = bundle.getString("movieURI");

        mMovieUri = Uri.parse(uriString);
        //System.out.println("mMovieUri = " + mMovieUri.toString());

        getLoaderManager().restartLoader(X_DETAIL_LOADER, null, this);
        //getLoaderManager().enableDebugLogging(true);
    }

    /* // TODO: temp disabled, do we need this?
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //savedState = saveState();
        //sampleText = null;
    }
    */

    /*
    private Bundle saveState() { // called either from onDestroyView() or onSaveInstanceState()
        //Bundle state = new Bundle();
        //state.putCharSequence("WORKS!", sampleText.getText());
        return state;
    }
    */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
            put movie details in bundle and reload in onactivitycreated

         */
        // TODO: temp disabled
        /*
        TmdbMovie movie = new TmdbMovie();

        movie.setTitle(movieTitle);
        //movie.setId();
        movie.setPosterPath(posterPath);
        movie.setPlot(plot);
        movie.setUserRating(userRating);
        movie.setReleaseDate(releaseDate);
        //movie.setVoteCount();
        //movie.setMovieId();

        outState.putParcelable("movie", movie);*/
    }

    /*
    public void updateDetails(Bundle landscapeBundle) { // TODO: needed?
        Bundle bundle = landscapeBundle;
        if (bundle != null) {
            //System.out.println("updateDetails - xxxmovieTitle:" + movieTitle);
            movieTitle = bundle.getString("title");
            posterPath = bundle.getString("posterpath");
            plot = bundle.getString("plot");
            userRating = bundle.getDouble("userrating");
            releaseDate = bundle.getString("releasedate");
        }
        //((TextView) view.findViewById(R.id.details_movie_title)).setText(movieTitle);
        // need to call refresh here?
        //System.out.println("xmovieTitle:" + movieTitle);
        //landscapeBundle = bundle;
    }*/
}