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

        // hide the detail fragment in portrait mode
        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_fragment);

        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.hide(detailFragment);
            fragmentTransaction.commit();
        }

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
                .findFragmentById(R.id.detail_fragment);

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

        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_fragment);

        GridFragment gridFragment = (GridFragment) getSupportFragmentManager()
                .findFragmentById(R.id.grid_fragment);

        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
            fragmentTransaction.show(detailFragment);
            fragmentTransaction.hide(gridFragment);
            fragmentTransaction.commit();

            // show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (detailFragment != null) {
            // send bundle from GridFragment to our DetailFragment
            detailFragment.updateArticleView(bundle);

        }
    }

    @Override
    public void onBackPressed() {
        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_fragment);

        GridFragment gridFragment = (GridFragment) getSupportFragmentManager()
                .findFragmentById(R.id.grid_fragment);

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
        } else {
            super.onBackPressed();
        }
    }
}