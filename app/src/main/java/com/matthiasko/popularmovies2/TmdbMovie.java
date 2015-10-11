package com.matthiasko.popularmovies2;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/* Class for storing movie data */
public class TmdbMovie implements Parcelable {

    private long id;
    private String title;
    private String posterPath;
    private String plot;
    private double userRating;
    private String releaseDate;
    private int popularity;
    private int voteCount;
    private int movieId;

    private ArrayList<String> trailerNames;
    private ArrayList<String> trailerUrls;
    private ArrayList<String> reviewNames;
    private ArrayList<String> reviewContent;

    private int favoriteButtonState;

    public TmdbMovie() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public double getUserRating() {
        return userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setTrailerNames(ArrayList<String> trailerNames) {
        this.trailerNames = trailerNames;
    }

    public ArrayList<String> getTrailerNames() { return trailerNames; }


    public void setTrailerUrls(ArrayList<String> trailerUrls) {
        this.trailerUrls = trailerUrls;
    }

    public ArrayList<String> getTrailerUrls() { return trailerUrls; }


    public void setReviewNames(ArrayList<String> reviewNames) {
        this.reviewNames = reviewNames;
    }

    public ArrayList<String> getReviewNames() { return reviewNames; }

    public void setReviewContent(ArrayList<String> reviewContent) {
        this.reviewContent = reviewContent;
    }

    public ArrayList<String> getReviewContent() { return reviewContent; }

    public int getFavoriteButtonState() {
        return favoriteButtonState;
    }

    public void setFavoriteButtonState(int favoriteButtonState) {
        this.favoriteButtonState = favoriteButtonState;
    }

    // parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(plot);
        dest.writeDouble(userRating);
        dest.writeString(releaseDate);
        dest.writeInt(popularity);
        dest.writeInt(voteCount);
        dest.writeInt(movieId);
        dest.writeStringList(trailerNames);
        dest.writeStringList(trailerUrls);
        dest.writeStringList(reviewNames);
        dest.writeStringList(reviewContent);
        dest.writeInt(favoriteButtonState);
    }

    public static final Parcelable.Creator<TmdbMovie> CREATOR = new Parcelable.Creator<TmdbMovie>() {
        public TmdbMovie createFromParcel(Parcel pc) {
            return new TmdbMovie(pc);
        }

        public TmdbMovie[] newArray(int size) {
            return new TmdbMovie[size];
        }
    };

    public TmdbMovie (Parcel source) {

        id = source.readInt();
        title = source.readString();
        posterPath = source.readString();
        plot = source.readString();
        userRating = source.readDouble();
        releaseDate = source.readString();
        popularity = source.readInt();
        voteCount = source.readInt();
        movieId = source.readInt();
        trailerNames = source.createStringArrayList();
        trailerUrls = source.createStringArrayList();
        reviewNames = source.createStringArrayList();
        reviewContent = source.createStringArrayList();
        favoriteButtonState = source.readInt();
    }
}
