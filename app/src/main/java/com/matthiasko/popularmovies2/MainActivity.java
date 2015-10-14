package com.matthiasko.popularmovies2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActionBarActivity implements GridFragment.OnMovieSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // we need to hide detail fragment because it will auto load with the activity_main.xml...
        // BUT we should only hide if we are in portrait mode and detail fragment is empty

        // hide the detail fragment in portrait mode
        /*
        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_fragment);

        //System.out.println("detailFragment movie title = " + detailFragment.getTitle());

        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.hide(detailFragment);
            fragmentTransaction.commit();
        }*/

        GridFragment gridFragment = (GridFragment) getSupportFragmentManager()
                .findFragmentById(R.id.grid_fragment);

        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentByTag("detailFragmentTag");

        int screenOrientation = getResources().getConfiguration().orientation;

        // detect landscape mode, show gridfragment if true

        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.show(gridFragment);
            fragmentTransaction.commit();
        }



        /*
        if (detailFragment != null) {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.hide(gridFragment);
            fragmentTransaction.commit();



        }*/


        // dont show landscape mode on phones
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            View gridPane = findViewById(R.id.detail_fragment);

            // only execute if we are in portrait AND the gridPane is visible
            // we have to mirror this action for the back button on the action bar

            int screenOrientation = getResources().getConfiguration().orientation;
            if (screenOrientation == Configuration.ORIENTATION_PORTRAIT
                    && gridPane.getVisibility() == View.VISIBLE) {

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.exit_slide_in, R.anim.exit_slide_out);
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




        // depending on the orientation, show the detail fragment or just update the data
        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentByTag("detailFragmentTag");


        GridFragment gridFragment = (GridFragment) getSupportFragmentManager()
                .findFragmentById(R.id.grid_fragment);

        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {



            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
            //fragmentTransaction.add(R.id.detail_fragment, detailFragment, "detailFragmentTag");
            fragmentTransaction.show(detailFragment);
            fragmentTransaction.hide(gridFragment);
            fragmentTransaction.commit();

            // show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (detailFragment == null) {




        } else { // detail fragment not null so just update
            // send bundle from GridFragment to our DetailFragment
            detailFragment.updateArticleView(bundle);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // called on start and on rotation change
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // called on rotation, not on start

        /* causes crash 'cannot perform action after onSaveInstanceState'
        GridFragment gridFragment = (GridFragment) getSupportFragmentManager()
                .findFragmentById(R.id.grid_fragment);

        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.hide(gridFragment);
            fragmentTransaction.commit();
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        // called on rotation, not on start

        /* doesnt hide gridview
        GridFragment gridFragment = (GridFragment) getSupportFragmentManager()
                .findFragmentById(R.id.grid_fragment);

        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.hide(gridFragment);
            fragmentTransaction.commit();
        }*/
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        GridFragment gridFragment = (GridFragment) getSupportFragmentManager()
                .findFragmentById(R.id.grid_fragment);

        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.hide(gridFragment);
            fragmentTransaction.commit();

            // show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentByTag("detailFragmentTag");

        GridFragment gridFragment = (GridFragment) getSupportFragmentManager()
                .findFragmentById(R.id.grid_fragment);

        View gridPane = findViewById(R.id.detail_fragment);

        // only execute if we are in portrait AND the gridPane is visible
        // we have to mirror this action for the back button on the action bar


/*
        if (detailFragment.getView().getVisibility() == View.VISIBLE) {
            System.out.println("detail fragment view is visible");
        } else {
            System.out.println("detail fragment view is NOT visible");
        }*/

        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT
                && detailFragment.getView().getVisibility() == View.VISIBLE) {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.exit_slide_in, R.anim.exit_slide_out);
            fragmentTransaction.show(gridFragment);
            fragmentTransaction.hide(detailFragment);
            fragmentTransaction.commit();

            // hide back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            super.onBackPressed();
        }
    }
}