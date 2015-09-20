package com.matthiasko.multipane;

import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActionBarActivity
    implements AlphaFragment.OnHeadlineSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OmegaFragment articleFrag = (OmegaFragment)
                getFragmentManager().findFragmentById(R.id.omega);

        if (articleFrag != null) {

            FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.omega, articleFrag, "articleFragment");
            fragmentTransaction.addToBackStack("articleFragment");
            fragmentTransaction.commit();

            //System.out.println("addingtobackstack");
        }

        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            //hideOmegaPane();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to hide the Alpha pane
     */
    private void hideOmegaPane() {
        View omegaPane = findViewById(R.id.omega);
        if (omegaPane.getVisibility() == View.VISIBLE) {
            omegaPane.setVisibility(View.GONE);
        }
    }

    /**
     * Method to show the Alpha pane
     */
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

    /**
     * Method to show the Alpha pane
     */
    private void showAlphaPane() {
        View alphaPane = findViewById(R.id.alpha);
        if (alphaPane.getVisibility() == View.GONE) {
            alphaPane.setVisibility(View.VISIBLE);
        }
    }



    public void onArticleSelected() {
        // The user selected the headline of an article from the HeadlinesFragment

        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            hideAlphaPane();
        }

        // Capture the article fragment from the activity layout
        OmegaFragment articleFrag = (OmegaFragment)
                getFragmentManager().findFragmentById(R.id.omega);

        if (articleFrag != null) {
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            articleFrag.updateArticleView();

        } else {

        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
            //getFragmentManager().executePendingTransactions();

            //System.out.println("POPBACKSTACK");
        } else {
            super.onBackPressed();
        }
    }
}
