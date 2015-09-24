package com.matthiasko.multipane;

/**
 * Created by matthiasko on 9/13/15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper implements MovieListener{

    public static final String TABLE_MOVIES = "movies";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_POSTER_PATH = "poster_path";
    public static final String COLUMN_PLOT = "plot";
    public static final String COLUMN_USER_RATING = "user_rating"; // vote_average in api
    public static final String COLUMN_RELEASE_DATE = "release_date";

    // popularity and vote count needed for sorting
    public static final String COLUMN_POPULARITY = "popularity";
    public static final String COLUMN_VOTE_COUNT = "vote_count";

    public static final String COLUMN_MOVIE_ID = "movie_id";

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_MOVIES + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_TITLE + " text not null," +
            COLUMN_POSTER_PATH + " text not null," +
            COLUMN_PLOT + " text not null," +
            COLUMN_USER_RATING + " float," +
            COLUMN_RELEASE_DATE + " text not null," +
            COLUMN_POPULARITY + " integer," +
            COLUMN_VOTE_COUNT + " integer," +
            COLUMN_MOVIE_ID + " integer" +
            ");";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        onCreate(db);
    }

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

}