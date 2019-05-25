package com.example.projetcci;

import java.util.List;

public class Movie {

    private int id;
    private String title;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private int myRating;
    private double TMDBRating;
    private String releaseDate;
    //private List<Genre> genres;
    private int toSee;
    private int seen;
    private int favorite;

    public Movie(int id, String title, String overview, String posterPath, String backdropPath, 
                 int myRating, double TMDBRating, String releaseDate, /*List<Genre> genres, */
                 int toSee, int seen, int favorite) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.myRating = myRating;
        this.TMDBRating = TMDBRating;
        this.releaseDate = releaseDate;
        //this.genres = genres;
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

    public int getMyRating() {
        return myRating;
    }

    public void setMyRating(int myRating) {
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

    /*public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }*/

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
