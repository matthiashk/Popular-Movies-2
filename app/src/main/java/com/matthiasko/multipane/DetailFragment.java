package com.matthiasko.multipane;

import android.app.Fragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by matthiasko on 9/19/15.
 */
public class DetailFragment extends Fragment {

    public static String movieTitle = null;
    public static String posterPath = null;
    public static String plot = null;
    public static double userRating = 0;
    public static String releaseDate = null;
    //public static View view;

    private Bundle savedState = null;

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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
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


        //sampleText = (TextView)view.findViewById(R.id.omegaText);

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

        /*
        * display only in portrait mode AND when we are not hidden
        *
        *
        *
        *
        * */



        //((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Log.v("DETAILFRAG", "ONVIEWCREATED");
        //System.out.println("movieTitle:" + movieTitle);
        //System.out.println("releaseDate:" + releaseDate);
        ((TextView) getView().findViewById(R.id.details_movie_title))
                .setText(movieTitle);

        ((TextView) getView().findViewById(R.id.details_plot))
                .setText(plot);

        // allow user to scroll the view containing the plot synopsis
        ((TextView) getView().findViewById(R.id.details_plot))
                .setMovementMethod(new ScrollingMovementMethod());

        // format the user rating, add '/10'
        String formattedUserRating = String.format("%.1f", userRating) + "/10";

        ((TextView) getView().findViewById(R.id.details_user_rating))
                .setText(formattedUserRating);

        // extract the year from the release date string
        //String movieYear = releaseDate.substring(0, 4);
        ((TextView) getView().findViewById(R.id.details_release_date))
                .setText(releaseDate);

        // construct url for the full posterpath
        String baseURL = "http://image.tmdb.org/t/p/";
        String thumbSize = "w185";
        String posterURL = null;
        posterURL = baseURL + thumbSize + posterPath;

        ImageView imageView = ((ImageView) getView().findViewById(R.id.details_imageview));

        Picasso.with(getActivity())
                .load(posterURL)
                        //.placeholder(R.drawable.weather)
                .centerCrop()
                .resize(600, 900)
                .into(imageView);
        //System.out.println("ORIENTATION:" + getRotation(getActivity()));
    }

    public void updateArticleView(Bundle bundle) {

        movieTitle = bundle.getString("title");
        posterPath = bundle.getString("posterpath");
        plot = bundle.getString("plot");
        userRating = bundle.getDouble("userrating");
        releaseDate = bundle.getString("releasedate");
        //System.out.println("updateArticleView - movieTitle = " + movieTitle);

        ((TextView) getView().findViewById(R.id.details_movie_title))
                .setText(movieTitle);

        ((TextView) getView().findViewById(R.id.details_plot))
                .setText(plot);

        // allow user to scroll the view containing the plot synopsis
        ((TextView) getView().findViewById(R.id.details_plot))
                .setMovementMethod(new ScrollingMovementMethod());

        // format the user rating, add '/10'
        String formattedUserRating = String.format("%.1f", userRating) + "/10";

        ((TextView) getView().findViewById(R.id.details_user_rating))
                .setText(formattedUserRating);

        // extract the year from the release date string
        //String movieYear = releaseDate.substring(0, 4);
        ((TextView) getView().findViewById(R.id.details_release_date))
                .setText(releaseDate);

        // construct url for the full posterpath
        String baseURL = "http://image.tmdb.org/t/p/";
        String thumbSize = "w185";
        String posterURL = null;
        posterURL = baseURL + thumbSize + posterPath;

        ImageView imageView = ((ImageView) getView().findViewById(R.id.details_imageview));

        Picasso.with(getActivity())
                .load(posterURL)
                        //.placeholder(R.drawable.weather)
                .centerCrop()
                .resize(600, 900)
                .into(imageView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //savedState = saveState();
        //sampleText = null;
    }

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

        //outState.putBundle("myBundle", (savedState != null) ? savedState : saveState());

    }

    public void updateDetails(Bundle landscapeBundle) {
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
    }
}