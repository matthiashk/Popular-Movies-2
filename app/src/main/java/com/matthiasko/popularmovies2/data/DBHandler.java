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
                MovieEntry.COLUMN_MOVIE_ID + " integer unique," +
                MovieEntry.COLUMN_FAVORITE + " integer," +
                MovieEntry.COLUMN_IMAGE + " blob" +
                ");";

        // no sqlite bool type use integer 0 for false, 1 for true

        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // comment out the following line if you want to upgrade the db without wiping data
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}