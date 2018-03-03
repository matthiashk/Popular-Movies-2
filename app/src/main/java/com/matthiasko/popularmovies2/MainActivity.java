package com.matthiasko.popularmovies2;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements GridFragment.OnMovieSelectedListener {

    private Boolean isSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);

        GridFragment gridFragment = (GridFragment) getSupportFragmentManager()
                .findFragmentById(R.id.grid_fragment);

        int screenOrientation = getResources().getConfiguration().orientation;

        // detect landscape mode, show gridfragment if true
        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.show(gridFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.e("onQueryTextChange", "called");
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {


                System.out.println("SEARCHVIEW QUERY = " + query);



                Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(searchIntent);

                return false;
            }

        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentByTag("detailFragmentTag");

        GridFragment gridFragment = (GridFragment) getSupportFragmentManager()
                .findFragmentById(R.id.grid_fragment);

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settings);
            return true;
        }

        if (id == android.R.id.home) {

            // we need the selected state to determine if a movie has been selected in the
            // gridfragment, if not we dont want to show the detailfragment in portrait mode
            isSelected = false;

            View gridPane = findViewById(R.id.detail_fragment);

            // only execute if we are in portrait AND the gridPane is visible
            // we have to mirror this action for the back button on the action bar

            int screenOrientation = getResources().getConfiguration().orientation;
            if (screenOrientation == Configuration.ORIENTATION_PORTRAIT
                    && gridPane.getVisibility() == View.VISIBLE) {

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                //fragmentTransaction.setCustomAnimations(R.anim.exit_slide_in, R.anim.exit_slide_out);
                fragmentTransaction.show(gridFragment);
                fragmentTransaction.hide(detailFragment);
                fragmentTransaction.commit();

                // hide back button
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onArticleSelected(Bundle bundle) {

        isSelected = true;

        // depending on the orientation, show the detail fragment or just update the data
        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentByTag("detailFragmentTag");

        GridFragment gridFragment = (GridFragment) getSupportFragmentManager()
                .findFragmentById(R.id.grid_fragment);

        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            //fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
            fragmentTransaction.show(detailFragment);
            fragmentTransaction.hide(gridFragment);
            fragmentTransaction.commit();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (detailFragment != null) {
            // detail fragment not null so just update
            // send bundle from GridFragment to our DetailFragment
            detailFragment.updateArticleView(bundle);
        }
    }

    @Override
    protected void onResume() { // called on start and on rotation change
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // called on rotation, not on start

        if (isSelected == null) {
            isSelected = false;
        }

        // store if a movie is selected
        outState.putBoolean("selected", isSelected);
    }

    @Override
    protected void onPause() { // called on rotation, not on start
        super.onPause();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get selected state from onSaveInstanceState
        Boolean selected = savedInstanceState.getBoolean("selected");

        isSelected = selected;

        // change view depending on if an item is selected or not
        if (selected) {

            GridFragment gridFragment = (GridFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.grid_fragment);
            int screenOrientation = getResources().getConfiguration().orientation;
            if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(gridFragment);
                fragmentTransaction.commit();

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } else {

            DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager()
                    .findFragmentByTag("detailFragmentTag");
            int screenOrientation = getResources().getConfiguration().orientation;
            if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(detailFragment);
                fragmentTransaction.commit();

                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    @Override
    public void onBackPressed() {

        isSelected = false;

        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentByTag("detailFragmentTag");

        GridFragment gridFragment = (GridFragment) getSupportFragmentManager()
                .findFragmentById(R.id.grid_fragment);

        // only execute if we are in portrait AND the gridPane is visible
        // we have to mirror this action for the back button on the action bar

        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT
                && detailFragment.getView().getVisibility() == View.VISIBLE) {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.exit_slide_in, R.anim.exit_slide_out);
            fragmentTransaction.show(gridFragment);
            fragmentTransaction.hide(detailFragment);
            fragmentTransaction.commit();

            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            super.onBackPressed();
        }
    }
}