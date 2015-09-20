package com.matthiasko.multipane;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by matthiasko on 9/19/15.
 */
public class OmegaFragment extends Fragment {
    private TextView sampleText;
    private Bundle savedState = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);



        // save the fragment that was already created here?
        //System.out.println("OMEGAFRAGMENT - ONCREATE");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_omega, container, false);


        sampleText = (TextView)view.findViewById(R.id.omegaText);

        /* If the Fragment was destroyed inbetween (screen rotation), we need to recover the savedState first */
        /* However, if it was not, it stays in the instance from the last onDestroyView() and we don't want to overwrite it */
        if(savedInstanceState != null && savedState == null) {
            savedState = savedInstanceState.getBundle("myBundle");
        }
        if(savedState != null) {
            sampleText.setText(savedState.getCharSequence("WORKS!"));
        }
        savedState = null;

        return view;
    }

    public void updateArticleView() {
        ((TextView) getView().findViewById(R.id.omegaText))
                .setText("WORKS!");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        savedState = saveState();
        sampleText = null;
    }

    private Bundle saveState() { /* called either from onDestroyView() or onSaveInstanceState() */
        Bundle state = new Bundle();
        state.putCharSequence("WORKS!", sampleText.getText());
        return state;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBundle("myBundle", (savedState != null) ? savedState : saveState());

    }
}