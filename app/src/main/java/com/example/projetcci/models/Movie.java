package com.example.projetcci.models;

import java.io.Serializable;

/**
 * Class with informations needed from each movie
 */
public class Movie implements Serializable {

    private int id;
    private String title;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private double myRating;
    private double TMDBRating;
    private String releaseDate;
    private String genres;
    private int runtime;
    private int toSee;
    private int seen;
    private int favorite;

    public Movie(int id, String title, String overview, String posterPath, String backdropPath, 
                 double myRating, double TMDBRating, String releaseDate, String genres,
                 int runtime, int toSee, int seen, int favorite) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.myRating = myRating;
        this.TMDBRating = TMDBRating;
        this.releaseDate = releaseDate;
        this.genres = genres;
        this.runtime = runtime;
        this.toSee = toSee;
        this.seen = seen;
        this.favorite = favorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public double getMyRating() {
        return myRating;
    }

    public void setMyRating(double myRating) {
        this.myRating = myRating;
    }

    public double getTMDBRating() {
        return TMDBRating;
    }

    public void setTMDBRating(double TMDBRating) {
        this.TMDBRating = TMDBRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public int getToSee() {
        return toSee;
    }

    public void setToSee(int toSee) {
        this.toSee = toSee;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }
}
