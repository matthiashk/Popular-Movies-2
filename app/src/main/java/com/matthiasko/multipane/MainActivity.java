package com.matthiasko.multipane;

import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActionBarActivity implements AlphaFragment.OnHeadlineSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OmegaFragment omegaFrag = (OmegaFragment) getFragmentManager()
                .findFragmentById(R.id.omega);

        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.hide(omegaFrag);
            fragmentTransaction.commit();
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
        OmegaFragment omegaFrag = (OmegaFragment) getFragmentManager()
                .findFragmentById(R.id.omega);

        AlphaFragment alphaFrag = (AlphaFragment) getFragmentManager()
                .findFragmentById(R.id.alpha);
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home) {

            View omegaPane = findViewById(R.id.omega);

            // only execute if we are in portrait AND the omegaPane is visible
            // we have to mirror this action for the back button on the action bar...

            int screenOrientation = getResources().getConfiguration().orientation;
            if (screenOrientation == Configuration.ORIENTATION_PORTRAIT
                    && omegaPane.getVisibility() == View.VISIBLE) {

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);

                fragmentTransaction.show(alphaFrag);

                fragmentTransaction.hide(omegaFrag);

                fragmentTransaction.commit();

                // hide back button
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void hideOmegaPane() {
        View omegaPane = findViewById(R.id.omega);
        if (omegaPane.getVisibility() == View.VISIBLE) {
            omegaPane.setVisibility(View.GONE);
        }
    }

    private void showOmegaPane() {
        View omegaPane = findViewById(R.id.omega);
        if (omegaPane.getVisibility() == View.GONE) {

            omegaPane.setVisibility(View.VISIBLE);
        }
    }

    private void hideAlphaPane() {
        View alphaPane = findViewById(R.id.alpha);
        if (alphaPane.getVisibility() == View.VISIBLE) {
            alphaPane.setVisibility(View.GONE);
        }
    }

    private void showAlphaPane() {
        View alphaPane = findViewById(R.id.alpha);
        if (alphaPane.getVisibility() == View.GONE) {
            alphaPane.setVisibility(View.VISIBLE);
        }
    }

    public void onArticleSelected() {
        // The user selected the headline of an article from the HeadlinesFragment

        OmegaFragment omegaFrag = (OmegaFragment) getFragmentManager()
                .findFragmentById(R.id.omega);

        AlphaFragment alphaFrag = (AlphaFragment) getFragmentManager()
                .findFragmentById(R.id.alpha);

        //String backStateName = alphaFrag.getClass().getName();

        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
            fragmentTransaction.show(omegaFrag);
            fragmentTransaction.hide(alphaFrag);
            fragmentTransaction.commit();

            // show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Capture the article fragment from the activity layout
        OmegaFragment articleFrag = (OmegaFragment)
                getFragmentManager().findFragmentById(R.id.omega);

        if (articleFrag != null) {
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            articleFrag.updateArticleView();

        } else {

            // ...

        }
    }

    @Override
    public void onBackPressed() {

        View omegaPane = findViewById(R.id.omega);

        // only execute if we are in portrait AND the omegaPane is visible
        // we have to mirror this action for the back button on the action bar...

        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT
                && omegaPane.getVisibility() == View.VISIBLE) {
            hideOmegaPane();
            showAlphaPane();
            // hide back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            //System.out.println("MAIN - BACKPRESSED (BACK NAV BUTTON)");
        } else {
            super.onBackPressed();
        }
    }

}
