package com.matthiasko.popularmovies2;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.matthiasko.popularmovies2.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matthiasko on 9/19/15.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, FetchExtrasResponse {

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

    private ArrayList<YTObject> mYoutubeArrayList;
    private List<Button> mButtonList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //System.out.println("HOME PRESSED");
                //getFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // only called one time
        // container is null here b/c we are loading from xml?
        //System.out.println("DETAILFRAGMENT - onCreateView");

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

        //System.out.println("DETAILFRAGMENT - onViewCreated");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //getLoaderManager().initLoader(X_DETAIL_LOADER, null, this);
/*
        if (savedInstanceState == null) {

            System.out.println("onActivityCreated - savedInstanceState NULL");
        }

        mBundle = savedInstanceState;
*/
        //System.out.println("DETAILFRAGMENT - onActivityCreated");
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

            //System.out.println("GETVIEW IS NOT NULL");

            //int movieId = data.getInt(COL_MOVIE_ID);
            //System.out.println("DETAILFRAGMENT - onLoadFinished COL_MOVIE_ID = " + movieId);

            String lTitle = data.getString(COL_MOVIE_TITLE);
            ((TextView) getView().findViewById(R.id.details_movie_title))
                    .setText(lTitle);

            /*
            if (mTitle != null) {

                mTextView.setText(mTitle);
            } else {
                mTextView.setText(lTitle);
            }
            */

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



            // create trailer links here
            if (mYoutubeArrayList != null) {

                //System.out.println("creating links ...");

                // here is where we remove the previously created buttons from the view
                if (mButtonList == null) {

                    //System.out.println("mButtonList is NULL");

                } else {

                    RelativeLayout relativeLayout = (RelativeLayout) getView().findViewById(R.id.details_layout);

                    //System.out.println("removing VIEWS");

                    // remove buttons from view
                    for (int i=0; i < mButtonList.size(); i++) {

                        relativeLayout.removeView(mButtonList.get(i));

                        //mButtonList.get(i).findViewById(mButtonList.get(i).getId()).setVisibility(View.GONE);

                    }
                    //System.out.println("mButtonList size = " + mButtonList.size());


                    //ready = "OK";
                    mButtonList.clear();
                }

                View view = getView(); //returns base view of the fragment
                if ( view == null)
                    return;
                if ( !(view instanceof ViewGroup)){
                    return;
                }
                //ViewGroup viewGroup = (ViewGroup) view;


                /*
                // setup int to dp conversion
                DisplayMetrics metrics;
                metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int width = getDPI(100, metrics);
                int height = getDPI(50, metrics);
                */
/*
                // setup button
                Button myButton = new Button(getActivity());
                myButton.setLayoutParams(new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                myButton.setId(R.id.myButton);
                myButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                myButton.setText("YouTube Link to Trailers");

                // add button to existing layout
                RelativeLayout relativeLayout = (RelativeLayout) getView().findViewById(R.id.details_layout);

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.addRule(RelativeLayout.BELOW, R.id.details_plot);

                myButton.setLayoutParams(layoutParams);

                relativeLayout.addView(myButton);
*/
                RelativeLayout relativeLayout = (RelativeLayout) getView().findViewById(R.id.details_layout);



                // make buttons based on how many youtube links there are
                mButtonList = new ArrayList<Button>();
                for (int i = 0; i < mYoutubeArrayList.size(); i++) {
                    mButtonList.add(new Button(getActivity()));
                }


                int z = 1;

                //System.out.println("mYoutubeArrayList size = " + mYoutubeArrayList.size());

                for (int i=0; i < mYoutubeArrayList.size(); i++) {


                    mButtonList.get(i).setLayoutParams(new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));

                    mButtonList.get(i).setId(z);

                    mButtonList.get(i).setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));

                    mButtonList.get(i).setText(mYoutubeArrayList.get(i).getYtName());

                    // sample youtube url https://www.youtube.com/watch?v=JAUoeqvedMo

                    final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?";
                    final String QUERY_PARAM = "v";


                    final Uri builtUri = Uri.parse(YOUTUBE_BASE_URL)
                            .buildUpon()
                            .appendQueryParameter(QUERY_PARAM, mYoutubeArrayList.get(i).getYtSource())
                            .build();

                    //System.out.println("builturi = " + builtUri.toString());

                    //URL url = new URL(builtUri.toString());



                    mButtonList.get(i).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent ytLink = new Intent(android.content.Intent.ACTION_VIEW);
                            ytLink.setData(builtUri);
                            startActivity(ytLink);
                        }
                    });

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);

                        if(i==0)

                        {

                            layoutParams.addRule(RelativeLayout.BELOW, R.id.details_plot);

                            mButtonList.get(i).setLayoutParams(layoutParams);

                            relativeLayout.addView(mButtonList.get(i));
                        }

                        else

                        {


                            //System.out.println("button ID = " + mButtonList.get(i).getId());

                            layoutParams.addRule(RelativeLayout.BELOW, mButtonList.get(i).getId() - 1);

                            mButtonList.get(i).setLayoutParams(layoutParams);

                            relativeLayout.addView(mButtonList.get(i));
                        }


                        z++;

                    }


                            //mYoutubeArrayList.clear();

                }
        }
    }

    // convert int to dpi
    public static int getDPI(int size, DisplayMetrics metrics){
        return (size * metrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT;
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

    // get result from fetchextrastask
    @Override
    public void onSuccess(ArrayList<YTObject> result) {

        /*
            the old button views dont get removed
             b/c the new links are getting created first...

         */



        //String ready = null;



        mYoutubeArrayList = result;

        getLoaderManager().restartLoader(X_DETAIL_LOADER, null, this);


        //mYoutubeArrayList = result;




        //String testString = "LALALAL";

        //mTitle = testString;

        // get size of arraylist, this will tell us how many links to create

        // get info from ytobject so we can make link name + uri to video

        //System.out.println("result at index 0 = " + result.get(0).toString() );



        //for (int i=0; i < result.size(); i++) {




            //System.out.println("YTObject = " + result.get(i).getYtName() + result.get(i).getYtSize()
            //+ result.get(i).getYtSource() + result.get(i).getYtType());

            // get name store into string
            // get source store into string

            // sample youtube url https://www.youtube.com/watch?v=JAUoeqvedMo



        //}











        /*

        // create buttons in a loop


        // get array size

        // create button names based on array size



        for (int i=0; i < result.size(); i++) {

            Button button + i = new button;


         }


         for (int i=0; i < result.size(); i++) {
         button.settext(name)
            button.seturi(completeduri)

        }






         */


 //... trying to refresh detailfragment...

        /*
        DetailFragment detailFragment = (DetailFragment) getFragmentManager()
                .findFragmentById(R.id.detail_fragment);

        if (detailFragment == null) {

            System.out.println("detailFragment NULL");
        }


        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(detailFragment);
        fragmentTransaction.attach(detailFragment);
        fragmentTransaction.commit();
*/



    }

    /*
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        RelativeLayout relativeLayout = (RelativeLayout) getView().findViewById(R.id.details_layout);


        if (mButtonList == null) {

        } else {

            // remove buttons from view
            for (int i=0; i < mButtonList.size(); i++) {

                relativeLayout.removeView(mButtonList.get(i));

            }
            //System.out.println("mButtonList size = " + mButtonList.size());
        }
    }
    */
}