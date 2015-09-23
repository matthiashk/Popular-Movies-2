package com.matthiasko.multipane;

import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActionBarActivity implements GridFragment.OnHeadlineSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DetailFragment detailFragment = (DetailFragment) getFragmentManager()
                .findFragmentById(R.id.detail_fragment);

        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.hide(detailFragment);
            fragmentTransaction.commit();
        }

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
        DetailFragment detailFragment = (DetailFragment) getFragmentManager()
                .findFragmentById(R.id.detail_fragment);

        GridFragment gridFragment = (GridFragment) getFragmentManager()
                .findFragmentById(R.id.grid_fragment);
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home) {

            View gridPane = findViewById(R.id.detail_fragment);

            // only execute if we are in portrait AND the gridPane is visible
            // we have to mirror this action for the back button on the action bar...

            int screenOrientation = getResources().getConfiguration().orientation;
            if (screenOrientation == Configuration.ORIENTATION_PORTRAIT
                    && gridPane.getVisibility() == View.VISIBLE) {

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
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

    public void onArticleSelected() {
        // The user selected the headline of an article from the HeadlinesFragment

        DetailFragment detailFragment = (DetailFragment) getFragmentManager()
                .findFragmentById(R.id.detail_fragment);

        GridFragment gridFragment = (GridFragment) getFragmentManager()
                .findFragmentById(R.id.grid_fragment);

        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
            fragmentTransaction.show(detailFragment);
            fragmentTransaction.hide(gridFragment);
            fragmentTransaction.commit();

            // show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Capture the detail fragment from the activity layout

        if (detailFragment != null) {
            detailFragment.updateArticleView();

        } else {

            // ...

        }
    }

    @Override
    public void onBackPressed() {
        DetailFragment detailFragment = (DetailFragment) getFragmentManager()
                .findFragmentById(R.id.detail_fragment);

        GridFragment gridFragment = (GridFragment) getFragmentManager()
                .findFragmentById(R.id.grid_fragment);

        View gridPane = findViewById(R.id.detail_fragment);

        // only execute if we are in portrait AND the gridPane is visible
        // we have to mirror this action for the back button on the action bar...

        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT
                && gridPane.getVisibility() == View.VISIBLE) {

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
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
