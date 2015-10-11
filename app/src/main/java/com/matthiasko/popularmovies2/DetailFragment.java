package com.matthiasko.popularmovies2;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matthiasko on 9/19/15.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        FetchExtrasTask.FetchExtrasResponse {

    private Uri mMovieUri;

    private static final String[] PROJECTION = new String[] { "_id", "title", "poster_path", "plot",
            "user_rating", "release_date", "popularity", "vote_count", "movie_id", "favorite",
            "image", "trailers", "reviews"};

    private static final int X_DETAIL_LOADER = 0;

    //public static final int COL_ID = 0;
    public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_POSTER_PATH = 2;
    public static final int COL_MOVIE_PLOT = 3;
    public static final int COL_MOVIE_USER_RATING = 4;
    public static final int COL_MOVIE_RELEASE_DATE = 5;
    //public static final int COL_MOVIE_POPULARITY = 6;
    //public static final int COL_MOVIE_VOTE_COUNT = 7;
    public static final int COL_MOVIE_ID = 8;
    public static final int COL_FAVORITE = 9;
    public static final int COL_IMAGE = 10;
    public static final int COL_TRAILERS = 11;
    public static final int COL_REVIEWS = 12;

    private ArrayList<Button> mButtonList;
    private List<TextView> mTextViewList;
    private String mYouTubeUrl = null;
    private ShareActionProvider mShareActionProvider;
    private String mPosterURL = null;
    private String mMovieId = null;
    private int mFavorite;

    private String mTitle = null;
    private String mPlot = null;
    private String mReleaseDate = null;
    private String mPosterPath = null;
    // java double will be initialized to 0.0, a java Double will be initialized to null.
    private Double mUserRating = 0.0;

    private ArrayList<String> mTrailersNameArray = new ArrayList<>();
    private ArrayList<String> mTrailersUrlArray = new ArrayList<>();
    private ArrayList<String> mReviewsNameArray = new ArrayList<>();
    private ArrayList<String> mReviewsContentArray = new ArrayList<>();
    private int mLastButtonId;

    private RelativeLayout mRelativeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        /* called in between oncreate  */
        inflater.inflate(R.menu.menu_detail, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // check if the movie is a favorite and change menu icon if already favorited

        if (mFavorite == 0) {
            MenuItem favoriteButton = menu.findItem(R.id.action_favorite);
            favoriteButton.setIcon(R.drawable.ic_action_favorite);

        } else if (mFavorite == 1) {
            MenuItem favoriteButton = menu.findItem(R.id.action_favorite);
            favoriteButton.setIcon(R.drawable.ic_action_favorite_true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            case R.id.action_favorite:
                getActivity().invalidateOptionsMenu();

                // change mfavorite to 1 or toggle here
                if (mFavorite == 0) {
                    mFavorite = 1;

                } else if (mFavorite == 1) {
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
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mYouTubeUrl);
        return shareIntent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* called on start and on rotation */
        // container is null here b/c we are loading from xml?
        View view = inflater.inflate(R.layout.detail_fragment, container, false);

        if (savedInstanceState != null) {

            TmdbMovie movie = savedInstanceState.getParcelable("movie");

            // set variables here again, otherwise they will be null
            mTitle = movie.getTitle();
            mPlot = movie.getPlot();
            mReleaseDate = movie.getReleaseDate();
            mPosterPath = movie.getPosterPath();
            mUserRating = movie.getUserRating();
            mFavorite = movie.getFavoriteButtonState();

            ((TextView) view.findViewById(R.id.details_movie_title))
                    .setText(movie.getTitle());

            ((TextView) view.findViewById(R.id.details_plot))
                    .setText(movie.getPlot());

            ((TextView) view.findViewById(R.id.details_plot))
                    .setMovementMethod(new ScrollingMovementMethod());

            String formattedUserRating = String.format("%.1f", movie.getUserRating()) + "/10";

            ((TextView) view.findViewById(R.id.details_user_rating))
                    .setText(formattedUserRating);

            ((TextView) view.findViewById(R.id.details_release_date))
                    .setText(movie.getReleaseDate());

            String baseURL = "http://image.tmdb.org/t/p/";
            String thumbSize = "w185";
            String posterURL;
            posterURL = baseURL + thumbSize + movie.getPosterPath();

            ImageView imageView = ((ImageView) view.findViewById(R.id.details_imageview));

            Picasso.with(getActivity())
                    .load(posterURL)
                    .resize(600, 900)
                    .into(imageView);

            createTrailerElements(movie.getTrailerNames(), movie.getTrailerUrls(), view);
            createReviewElements(movie.getReviewNames(), movie.getReviewContent());
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        mTrailersNameArray.clear();
        mTrailersUrlArray.clear();
        mReviewsNameArray.clear();
        mReviewsContentArray.clear();

        removePreviousElements(mButtonList, mTextViewList, getView());

        if (mMovieUri == null) {
            return null;
        }
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                mMovieUri,
                PROJECTION,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // this block will get called multiple times
        // this will get called by FetchFavoriteTask also

        if (mTrailersNameArray != null) {
            removePreviousElements(mButtonList, mTextViewList, getView());
        }

        if (data != null && data.moveToFirst()) {

            // get movie id
            int movieId = data.getInt(COL_MOVIE_ID);

            // convert to string so we can send to asynctask
            mMovieId = String.valueOf(movieId);

            mFavorite = data.getInt(COL_FAVORITE);

            // update action bar favorite icon
            getActivity().invalidateOptionsMenu();

            mTitle = data.getString(COL_MOVIE_TITLE);
            ((TextView) getView().findViewById(R.id.details_movie_title))
                    .setText(mTitle);

            mPlot = data.getString(COL_MOVIE_PLOT);
            ((TextView) getView().findViewById(R.id.details_plot))
                    .setText(mPlot);

            // allow user to scroll the view containing the plot synopsis
            ((TextView) getView().findViewById(R.id.details_plot))
                    .setMovementMethod(new ScrollingMovementMethod());

            mUserRating = data.getDouble(COL_MOVIE_USER_RATING);

            // format the user rating, add '/10'
            String formattedUserRating = String.format("%.1f", mUserRating) + "/10";

            ((TextView) getView().findViewById(R.id.details_user_rating))
                    .setText(formattedUserRating);

            mReleaseDate = data.getString(COL_MOVIE_RELEASE_DATE);

            ((TextView) getView().findViewById(R.id.details_release_date))
                    .setText(mReleaseDate);

            mPosterPath = data.getString(COL_MOVIE_POSTER_PATH);

            // construct url for the full posterpath
            String baseURL = "http://image.tmdb.org/t/p/";
            String thumbSize = "w185";

            ImageView imageView = ((ImageView) getView().findViewById(R.id.details_imageview));

            // check if the movie is a favorite before displaying image
            // not a favorite, so fetch image from internet
            if (mFavorite == 0) {
                mPosterURL = baseURL + thumbSize + mPosterPath;
                Picasso.with(getActivity())
                        .load(mPosterURL)
                        .resize(600, 900)
                        .into(imageView);

            // is a favorite, so fetch image from db
            } else if (mFavorite == 1) {

                byte[] imageFromDB = data.getBlob(COL_IMAGE);

                // create file to save the image in.
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

            try {
                // dont populate array if the column is null
                // the column WILL be null since onloadfinished is called
                // before the column is updated
                if (data.getString(COL_TRAILERS) != null) {

                    mTrailersUrlArray = new ArrayList<>();
                    mTrailersNameArray = new ArrayList<>();

                    JSONObject json = new JSONObject(data.getString(COL_TRAILERS));
                    JSONArray items = json.optJSONArray("trailersArray");

                    for (int i = 0; i < items.length(); i++) {
                        // get trailer urls
                        // we need to get only even numbered positions here
                        if (i % 2 != 0) {
                            String stringValue = items.optString(i);
                            mTrailersUrlArray.add(stringValue);
                        }

                        // get trailer names, these will be in odd numbered positions
                        if (i % 2 == 0) {
                            String stringValue = items.optString(i);
                            mTrailersNameArray.add(stringValue);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                // dont populate array if the column is null
                // the column WILL be null since onloadfinished is called
                // before the column is updated
                if (data.getString(COL_REVIEWS) != null) {

                    mReviewsContentArray = new ArrayList<>();
                    mReviewsNameArray = new ArrayList<>();


                    JSONObject json = new JSONObject(data.getString(COL_REVIEWS));
                    JSONArray items = json.optJSONArray("reviewsArray");

                    for (int i = 0; i < items.length(); i++) {
                        if (i % 2 != 0) {
                            String stringValue = items.optString(i);
                            mReviewsContentArray.add(stringValue);
                        }

                        if (i % 2 == 0) {
                            String stringValue = items.optString(i);
                            mReviewsNameArray.add(stringValue);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            createTrailerElements(mTrailersNameArray, mTrailersUrlArray, getView());
            createReviewElements(mReviewsNameArray, mReviewsContentArray);
        }
    }

    public void createTrailerElements(ArrayList<String> trailerNames, ArrayList<String> trailerUrls, final View view) {
        // create trailer links here
        if (trailerNames != null) {

            mLastButtonId = trailerUrls.size(); // we need this to position the review textviews

            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.details_layout);

            // make buttons based on how many youtube links there are
            mButtonList = new ArrayList<Button>();
            for (int i = 0; i < trailerUrls.size(); i++) {
                mButtonList.add(new Button(getActivity()));
            }

            int z = 1; // use this to set button ids

            for (int i = 0; i < trailerUrls.size(); i++) {
                mButtonList.get(i).setLayoutParams(new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                mButtonList.get(i).setId(z);

                mButtonList.get(i).getBackground().setColorFilter(getResources().getColor(R.color.darkGrey2), PorterDuff.Mode.MULTIPLY);
                mButtonList.get(i).setTextColor(getResources().getColor(R.color.lighterGrey));

                //mButtonList.get(i).setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));

                mButtonList.get(i).setText(trailerNames.get(i));

                //mButtonList.get(i).setPadding(10, 0, 10, 10);

                //System.out.println("trailerNames.get(i) = " + trailerNames.get(i));

                // sample youtube url https://www.youtube.com/watch?v=JAUoeqvedMo

                // get first youtube link only to put into share intent
                if (i == 0) {
                    mYouTubeUrl = trailerUrls.get(i);
                    if (mShareActionProvider == null) {
                        mShareActionProvider = new ShareActionProvider(getActivity());
                        mShareActionProvider.setShareIntent(createShareIntent());
                    } else {
                        mShareActionProvider.setShareIntent(createShareIntent());
                    }
                }

                final Uri trailerUri = Uri.parse(trailerUrls.get(i));

                mButtonList.get(i).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent ytLink = new Intent(android.content.Intent.ACTION_VIEW);
                        ytLink.setData(trailerUri);
                        startActivity(ytLink);
                    }
                });

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.setMargins(10, 0, 10, 10);

                // setting up button placement in the view
                // set the first button under the plot textview
                if(i == 0) {
                    layoutParams.addRule(RelativeLayout.BELOW, R.id.details_plot);
                    mButtonList.get(i).setLayoutParams(layoutParams);
                    mRelativeLayout.addView(mButtonList.get(i));
                } else {
                    // set the other buttons under each other
                    layoutParams.addRule(RelativeLayout.BELOW, mButtonList.get(i).getId() - 1);
                    mButtonList.get(i).setLayoutParams(layoutParams);
                    mRelativeLayout.addView(mButtonList.get(i));
                }
                z++;
            }
        }
    }

    public void createReviewElements(ArrayList<String> reviewNames, ArrayList<String> reviewContent) {
        /* create review textviews */

        // this will match if no reviews
        if (reviewNames != null) {

            // process reviews here
            // setup textviews depending on how many reviews there are
            mTextViewList = new ArrayList<TextView>();
            for (int i = 0; i < reviewNames.size(); i++) {
                mTextViewList.add(new TextView(getActivity()));
            }

            int a = 1000; // use this to set textview ids ... ids must be unique!

            for (int i=0; i < reviewNames.size(); i++) {

                mTextViewList.get(i).setLayoutParams(new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                mTextViewList.get(i).setId(a);

                //mTextViewList.get(i).setBackgroundColor(getResources().getColor(R.color.lightGrey));
                mTextViewList.get(i).setTextColor(getResources().getColor(R.color.darkGrey));
                mTextViewList.get(i).setBackground(getResources().getDrawable(R.drawable.borderbottom));
                //mTextViewList.get(i).setBackground(getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));

                // add reviewer name + newline + review content
                String newLine = System.getProperty("line.separator");
                String reviewerName = reviewNames.get(i);

                mTextViewList.get(i).setText("Review by " + reviewerName + newLine + newLine + reviewContent.get(i));
                mTextViewList.get(i).setPadding(30, 40, 30, 40);
                mTextViewList.get(i).setTextSize(16);

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

                //layoutParams.setMargins(20, 10, 10, 10);

                if(i == 0) {
                    // set the review to be under the last button for trailers
                    layoutParams.addRule(RelativeLayout.BELOW, mButtonList.get(mLastButtonId-1).getId());
                    mTextViewList.get(i).setLayoutParams(layoutParams);
                    mRelativeLayout.addView(mTextViewList.get(i));
                } else {
                    layoutParams.addRule(RelativeLayout.BELOW, mTextViewList.get(i).getId() - 1);
                    mTextViewList.get(i).setLayoutParams(layoutParams);
                    mRelativeLayout.addView(mTextViewList.get(i));
                }
                a++; // increment our textview id
            }
        }
    }

    public void removePreviousElements(List<Button> buttonList, List<TextView> textViewList, View view) {
        // here is where we remove the previously created buttons from the view
        // we are calling this from oncreateview and onloadfinished

        if (buttonList != null) {

            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.details_layout);
            // remove buttons from view
            for (int i = 0; i < buttonList.size(); i++) {
                mRelativeLayout.removeView(buttonList.get(i));
            }
        }
        // remove previously created textviews of reviews from view
        if (textViewList != null) {

            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.details_layout);
            for (int i = 0; i < textViewList.size(); i++) {
                mRelativeLayout.removeView(textViewList.get(i));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    /* called when item is selected from the gridview from mainactivity */
    public void updateArticleView(Bundle bundle) {
        // bundle is URI as string from gridfragment
        String uriString = bundle.getString("movieURI");
        mMovieUri = Uri.parse(uriString);
        getLoaderManager().restartLoader(X_DETAIL_LOADER, null, this);
        //getLoaderManager().enableDebugLogging(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /* put movie details in bundle and reload in oncreateview */

        //System.out.println("onSaveInstanceState ---------");
        //System.out.println("onSaveInstanceState - mButtonList.size() = " + mButtonList.size());
        //System.out.println("onSaveInstanceState - mTitle = " + mTitle);
        //System.out.println("onSaveInstanceState - mTrailersNameArray = " + mTrailersNameArray.toString());

        TmdbMovie movie = new TmdbMovie();
        movie.setTitle(mTitle);
        movie.setPosterPath(mPosterPath);
        movie.setPlot(mPlot);
        movie.setUserRating(mUserRating);
        movie.setReleaseDate(mReleaseDate);
        movie.setTrailerNames(mTrailersNameArray);
        movie.setTrailerUrls(mTrailersUrlArray);
        movie.setReviewNames(mReviewsNameArray);
        movie.setReviewContent(mReviewsContentArray);
        movie.setFavoriteButtonState(mFavorite);

        outState.putParcelable("movie", movie);
    }

    // get result from fetchextrastask
    @Override
    public void onSuccess() {

        if (getView() != null) {
            // have the detail fragment scroll to top on click
            getView().findViewById(R.id.details_scrollview).post(new Runnable() {
                @Override
                public void run() {
                    ((ScrollView) getView().findViewById(R.id.details_scrollview)).
                            smoothScrollTo(0, (getView().findViewById(R.id.detail_fragment)).getTop());
                }
            });

        }

        getLoaderManager().restartLoader(X_DETAIL_LOADER, null, this);
    }
}