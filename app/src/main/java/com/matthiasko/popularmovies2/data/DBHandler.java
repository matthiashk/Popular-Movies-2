package com.matthiasko.popularmovies2.data;

/**
 * Created by matthiasko on 9/13/15.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.matthiasko.popularmovies2.data.MovieContract.MovieEntry;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        final String DATABASE_CREATE = "create table "
                + MovieEntry.TABLE_NAME + "(" +
                MovieEntry.COLUMN_ID + " integer primary key autoincrement, " +
                MovieEntry.COLUMN_TITLE + " text not null," +
                MovieEntry.COLUMN_POSTER_PATH + " text not null," +
                MovieEntry.COLUMN_PLOT + " text not null," +
                MovieEntry.COLUMN_USER_RATING + " float," +
                MovieEntry.COLUMN_RELEASE_DATE + " text not null," +
                MovieEntry.COLUMN_POPULARITY + " integer," +
                MovieEntry.COLUMN_VOTE_COUNT + " integer," +
                MovieEntry.COLUMN_MOVIE_ID + " integer unique" +
                ");";

        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // comment out the following line if you want to upgrade the db without wiping data
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }

    /*
    @Override
    public void addMovie(TmdbMovie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, movie.getTitle());
            values.put(COLUMN_POSTER_PATH, movie.getPosterPath());
            values.put(COLUMN_PLOT, movie.getPlot());
            values.put(COLUMN_USER_RATING, movie.getUserRating());
            values.put(COLUMN_RELEASE_DATE, movie.getReleaseDate());
            values.put(COLUMN_POPULARITY, movie.getPopularity());
            values.put(COLUMN_VOTE_COUNT, movie.getVoteCount());
            values.put(COLUMN_MOVIE_ID, movie.getMovieId());

            db.insert(TABLE_MOVIES, null, values);
            db.close();
        }catch (Exception e){
            Log.e("problem", e + "");
        }
    }

    @Override
    public ArrayList<TmdbMovie> getAllMovies() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<TmdbMovie> movieList = null;
        try{
            movieList = new ArrayList<TmdbMovie>();
            String QUERY = "SELECT * FROM "+TABLE_MOVIES;
            Cursor cursor = db.rawQuery(QUERY, null);
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    TmdbMovie movie = new TmdbMovie();
                    movie.setId(cursor.getInt(0));
                    movie.setTitle(cursor.getString(1));
                    movie.setPosterPath(cursor.getString(2));
                    movie.setPlot(cursor.getString(3));
                    movie.setUserRating(cursor.getFloat(4));
                    movie.setReleaseDate(cursor.getString(5));
                    movie.setPopularity(cursor.getInt(6));
                    movie.setVoteCount(cursor.getInt(7));
                    movie.setMovieId(cursor.getInt(8));
                    movieList.add(movie);
                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return movieList;
    }

    @Override
    public int getMovieCount() {
        int num = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        try{
            String QUERY = "SELECT * FROM "+TABLE_MOVIES;
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            db.close();
            return num;
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return 0;
    }

    public ArrayList<TmdbMovie> getAllSortedMovies(String sortOrder) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<TmdbMovie> movieList = null;
        try{
            movieList = new ArrayList<TmdbMovie>();
            //String QUERY = "SELECT * FROM "+TABLE_MOVIES;
            //Cursor cursor = db.rawQuery(QUERY, null);

            if (sortOrder.equals("popularity.desc")) {

                Cursor cursor = db.query(TABLE_MOVIES, null, null, null, null, null, COLUMN_POPULARITY + " DESC");

                if(!cursor.isLast())
                {
                    while (cursor.moveToNext())
                    {
                        TmdbMovie movie = new TmdbMovie();
                        movie.setId(cursor.getInt(0));
                        movie.setTitle(cursor.getString(1));
                        movie.setPosterPath(cursor.getString(2));
                        movie.setPlot(cursor.getString(3));
                        movie.setUserRating(cursor.getFloat(4));
                        movie.setReleaseDate(cursor.getString(5));
                        movie.setPopularity(cursor.getInt(6));
                        movie.setVoteCount(cursor.getInt(7));
                        movie.setMovieId(cursor.getInt(8));
                        movieList.add(movie);
                    }
                }

            } else if (sortOrder.equals("vote_count.desc")) {

                Cursor cursor = db.query(TABLE_MOVIES, null, null, null, null, null, COLUMN_VOTE_COUNT + " DESC");

                if(!cursor.isLast())
                {
                    while (cursor.moveToNext())
                    {
                        TmdbMovie movie = new TmdbMovie();
                        movie.setId(cursor.getInt(0));
                        movie.setTitle(cursor.getString(1));
                        movie.setPosterPath(cursor.getString(2));
                        movie.setPlot(cursor.getString(3));
                        movie.setUserRating(cursor.getFloat(4));
                        movie.setReleaseDate(cursor.getString(5));
                        movie.setPopularity(cursor.getInt(6));
                        movie.setVoteCount(cursor.getInt(7));
                        movie.setMovieId(cursor.getInt(8));
                        movieList.add(movie);
                    }
                }
            }

            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return movieList;
    }
    */

}