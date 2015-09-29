package com.matthiasko.popularmovies2;

import android.os.Parcel;
import android.os.Parcelable;

/** Class for storing movie data
 *
 */
public class TmdbMovie implements Parcelable {

    long id;
    String title;
    String posterPath;
    String plot;
    double userRating;
    String releaseDate;
    int popularity;
    int voteCount;
    int movieId;

    //public TmdbMovie() {}

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

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }


    public TmdbMovie(String title, String posterPath, String plot, double userRating, String releaseDate,
                 int popularity, int voteCount, int movieId) {

        this.title = title;
        this.posterPath = posterPath;
        this.plot = plot;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.popularity = popularity;
        this.voteCount = voteCount;
        this.movieId = movieId;
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
    }
}
