package com.matthiasko.popularmovies2;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
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
import android.widget.Toast;

import com.matthiasko.popularmovies2.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
            "user_rating", "release_date", "popularity", "vote_count", "movie_id", "favorite", "image"};

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

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_POSTER_PATH = 2;
    public static final int COL_MOVIE_PLOT = 3;
    public static final int COL_MOVIE_USER_RATING = 4;
    public static final int COL_MOVIE_RELEASE_DATE = 5;
    public static final int COL_MOVIE_POPULARITY = 6;
    public static final int COL_MOVIE_VOTE_COUNT = 7;
    public static final int COL_MOVIE_ID = 8;
    public static final int COL_FAVORITE = 9;
    public static final int COL_IMAGE = 10;


    //private ArrayList<YTObject> mYoutubeArrayList;

    private Wrapper mWrapper;
    private List<Button> mButtonList;
    private List<TextView> mTextViewList;
    private String mYouTubeUrl;

    private ShareActionProvider mShareActionProvider;

    private String mPosterURL = null;
    private String mMovieId = null;
    private int mFavorite;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        //System.out.println("onPrepareOptionsMenu mFavorite = " + mFavorite);

        // check if the movie is a favorite and change menu icon if already favorited
        // SHOULD THIS BE IN ONCREATEOPTIONSMENU? OR onOptionsItemSelected?
        if (mFavorite == 0) {

            //System.out.println("onPrepareOptionsMenu - mFavorite == 0");

            MenuItem favoriteButton = menu.findItem(R.id.action_favorite);
            favoriteButton.setIcon(R.drawable.ic_action_favorite);

        } else if (mFavorite == 1) {

            //System.out.println("onPrepareOptionsMenu - mFavorite == 1");

            MenuItem favoriteButton = menu.findItem(R.id.action_favorite);
            favoriteButton.setIcon(R.drawable.ic_action_favorite_true);
        }






        /*
        if (logoImage == null) {

            System.out.println("logoImage is NULL");



        } else {

            ImageView imageView = ((ImageView) getView().findViewById(R.id.testImageView));

            imageView.setImageBitmap(BitmapFactory.decodeByteArray(logoImage, 0, 400));
        }*/






        //System.out.println("onPrepareOptionsMenu");
        // change favorites icon here
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //System.out.println("HOME PRESSED");
                //getFragmentManager().popBackStack();
                return true;
            case R.id.action_favorite:
                getActivity().invalidateOptionsMenu();

                // CHANGE MFAVORITE TO 1 OR TOGGLE HERE

                if (mFavorite == 0) {

                    mFavorite = 1;

                } else if (mFavorite == 1) {

                    //mFavorite = 0;

                }


                // call fetchfavoritetask to get the image into the db
                // and set favorite to true in db
                if (mPosterURL != null) {

                    FetchFavoriteTask favoriteTask = new FetchFavoriteTask(getActivity());

                    favoriteTask.execute(mPosterURL, mMovieId);
                }

                String favoriteToast = "Movie has been added to Favorites.";

                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(getActivity(), favoriteToast, duration);
                toast.show();




                return true;
            }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareIntent() {

        //System.out.println("mYouTubeUrl = " + mYouTubeUrl);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mYouTubeUrl);
        return shareIntent;
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

            // get movie id
            int movieId = data.getInt(COL_MOVIE_ID);

            // convert to string so we can send to asynctask
            mMovieId = String.valueOf(movieId);

            mFavorite = data.getInt(COL_FAVORITE);

            // update action bar favorite icon
            getActivity().invalidateOptionsMenu();

            //System.out.println("onLoadFinished - mFavorite = " + mFavorite);

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

            ImageView imageView = ((ImageView) getView().findViewById(R.id.details_imageview));

            // check if the movie is a favorite before displaying image
            if (mFavorite == 0) { // not a favorite, so fetch image from internet

                mPosterURL = baseURL + thumbSize + lPosterPath;

                Picasso.with(getActivity())
                        .load(mPosterURL)
                        .resize(600, 900)
                        .into(imageView);

            } else if (mFavorite == 1) { // is a favorite, so fetch image from db

                //System.out.println("FETCHING FROM DB...");

                byte[] imageFromDB = data.getBlob(COL_IMAGE);

                // create file to save the image in
                // use movie id as filename
                File file = new File(getActivity().getFilesDir(), mMovieId);

                // check if the file is already on disk
                // if it is, we dont need to write to file, just load it
                if (file.isFile()) {
                } else {
                    // save image from byte array to file
                    try {

                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                        bos.write(imageFromDB);
                        bos.flush();
                        bos.close();

                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }

                Picasso.with(getActivity())
                        .load(file)
                        .resize(600, 900)
                        .into(imageView);
            }

            // create trailer links here
            if (mWrapper != null) {

                //System.out.println("creating links ...");

                // here is where we remove the previously created buttons from the view
                if (mButtonList == null) {

                    //System.out.println("mButtonList is NULL");

                } else {

                    RelativeLayout relativeLayout = (RelativeLayout) getView().findViewById(R.id.details_layout);

                    //System.out.println("removing BUTTON VIEWS");

                    // remove buttons from view
                    for (int i=0; i < mButtonList.size(); i++) {

                        relativeLayout.removeView(mButtonList.get(i));

                        //mButtonList.get(i).findViewById(mButtonList.get(i).getId()).setVisibility(View.GONE);

                    }
                    //System.out.println("mButtonList size = " + mButtonList.size());


                    //ready = "OK";
                    mButtonList.clear();
                }

                if (mTextViewList == null) {

                    //System.out.println("mButtonList is NULL");

                } else {

                    RelativeLayout relativeLayout = (RelativeLayout) getView().findViewById(R.id.details_layout);

                    //System.out.println("removing BUTTON VIEWS");

                    // remove buttons from view
                    for (int i=0; i < mTextViewList.size(); i++) {

                        relativeLayout.removeView(mTextViewList.get(i));

                        //mButtonList.get(i).findViewById(mButtonList.get(i).getId()).setVisibility(View.GONE);

                    }
                    //System.out.println("mButtonList size = " + mButtonList.size());


                    //ready = "OK";
                    mTextViewList.clear();
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
                for (int i = 0; i < mWrapper.ytObjectArrayList.size(); i++) {
                    mButtonList.add(new Button(getActivity()));
                }


                int z = 1; // use this to set button ids

                //System.out.println("mYoutubeArrayList size = " + mYoutubeArrayList.size());

                int lastButtonId = mWrapper.ytObjectArrayList.size();

                for (int i=0; i < mWrapper.ytObjectArrayList.size(); i++) {

                    mButtonList.get(i).setLayoutParams(new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));

                    mButtonList.get(i).setId(z);

                    mButtonList.get(i).setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));

                    mButtonList.get(i).setText(mWrapper.ytObjectArrayList.get(i).getYtName());

                    // sample youtube url https://www.youtube.com/watch?v=JAUoeqvedMo

                    final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?";
                    final String QUERY_PARAM = "v";

                    final Uri builtUri = Uri.parse(YOUTUBE_BASE_URL)
                            .buildUpon()
                            .appendQueryParameter(QUERY_PARAM, mWrapper.ytObjectArrayList.get(i).getYtSource())
                            .build();

                    // get first youtube link only
                    if (i == 0) {

                        mYouTubeUrl = builtUri.toString();

                        mShareActionProvider.setShareIntent(createShareIntent());

                    }

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

                        if(i==0) {

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

                            //System.out.println("creating BUTTON VIEWS");
                        }
                        z++;



                    }


                // this will match if no reviews
                if (mWrapper.reviewObjectArrayList.size() == 0) {

                    //System.out.println("NO REVIEWS...");
                } else { // process reviews here



/*
                    TextView testTextView = new TextView(getActivity());

                    testTextView.setLayoutParams(new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));

                    testTextView.setId(a);

                    testTextView.get(i).setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));

                    testTextView.get(i).setText(mWrapper.reviewObjectArrayList.get(i).getRContent());

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);

                    layoutParams.addRule(RelativeLayout.BELOW, mButtonList.get(lastButtonId-1).getId());

                    testTextView.get(i).setLayoutParams(layoutParams);

                    relativeLayout.addView(testTextView.get(i));

                    */


                    /*
                    int buttonLocation[] = new int[2];

                    mButtonList.get(lastButtonId-1).getLocationOnScreen(buttonLocation);

                    System.out.println("buttonLocation = " + buttonLocation[0] + " " + buttonLocation[1]);


                    for (int i = 0; i < mButtonList.size(); i++) {

                        System.out.println("mButton ids = " + mButtonList.get(i).getId());

                    }



                    */

                    // System.out.println("mButtonList size= " + mButtonList.size());

                    //System.out.println("mButtonId = " + mButtonList.get(lastButtonId-1).getId());

                    //mButtonList.get(lastButtonId-1).getId()




                    // setup textviews depending on how many reviews there are
                    mTextViewList = new ArrayList<TextView>();
                    for (int i = 0; i < mWrapper.reviewObjectArrayList.size(); i++) {
                        mTextViewList.add(new TextView(getActivity()));
                    }

                    int a = 1000; // use this to set textview ids ... ids must be unique!
                    for (int i=0; i < mWrapper.reviewObjectArrayList.size(); i++) {

                        mTextViewList.get(i).setLayoutParams(new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));

                        mTextViewList.get(i).setId(a);

                        mTextViewList.get(i).setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));

                        mTextViewList.get(i).setText(mWrapper.reviewObjectArrayList.get(i).getRContent());


                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);

                        if(i==0) {

                            layoutParams.addRule(RelativeLayout.BELOW, mButtonList.get(lastButtonId-1).getId());

                            //layoutParams.leftMargin = 0;
                            //layoutParams.topMargin = 600;

                            mTextViewList.get(i).setLayoutParams(layoutParams);

                            relativeLayout.addView(mTextViewList.get(i));
                        }

                        else

                        {
                            //System.out.println("button ID = " + mButtonList.get(i).getId());
                            layoutParams.addRule(RelativeLayout.BELOW, mTextViewList.get(i).getId() - 1);

                            mTextViewList.get(i).setLayoutParams(layoutParams);

                            relativeLayout.addView(mTextViewList.get(i));
                        }
                        a++; // increment our textview id


                    }
                }


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
    public void onSuccess(Wrapper result) {

        /*
            the old button views dont get removed
             b/c the new links are getting created first...

         */



        //String ready = null;





        mWrapper = result;

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