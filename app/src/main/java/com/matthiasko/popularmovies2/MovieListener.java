package com.matthiasko.popularmovies2;

import java.util.ArrayList;

/**
 * Created by matthiasko on 9/13/15.
 */
// TODO: remove...
public interface MovieListener {

    public void addMovie(TmdbMovie movie);

    public ArrayList<TmdbMovie> getAllMovies();

    public int getMovieCount();
}
