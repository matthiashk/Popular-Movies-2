package com.matthiasko.popularmovies2;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by matthiasko on 3/3/18.
 */

public class SearchActivity extends AppCompatActivity implements SearchFragment.OnMovieSelectedListener {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);




        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            String query = intent.getStringExtra(SearchManager.QUERY);


            //System.out.println("SEARCHACTIVITY QUERY = " + query);

            Bundle bundle = new Bundle();
            bundle.putString("QUERY", query);
            // set Fragmentclass Arguments

            // Create a new Fragment to be placed in the activity layout
            SearchFragment firstFragment = new SearchFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(bundle);

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.search_fragment, firstFragment).commit();

        }
    }


    public void onArticleSelected(Bundle bundle) {

        //isSelected = true;

        // depending on the orientation, show the detail fragment or just update the data
        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentByTag("detailFragmentTag");

        SearchFragment searchFragment = (SearchFragment) getSupportFragmentManager()
                .findFragmentById(R.id.search_fragment);

        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            //fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
            fragmentTransaction.show(detailFragment);
            fragmentTransaction.hide(searchFragment);
            fragmentTransaction.commit();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (detailFragment != null) {
            // detail fragment not null so just update
            // send bundle from GridFragment to our DetailFragment
            detailFragment.updateArticleView(bundle);
        }
    }
}
