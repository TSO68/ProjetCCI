package com.example.projetcci;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Manage local database
 */
public class MovieManager {

    private static final String TABLE_NAME = "movies";
    public static final String KEY_ID_MOVIE = "id_movie";
    public static final String KEY_TITLE = "title";
    public static final String KEY_OVERVIEW ="overview";
    public static final String KEY_POSTER_PATH = "poster_path";
    public static final String KEY_BACKDROP_PATH = "backdrop_path";
    public static final String KEY_MY_RATING = "my_rating";
    public static final String KEY_TMDB_RATING = "tmdb_rating";
    public static final String KEY_RELEASE_DATE = "release_date";
    //public static final String KEY_GENRES = "genres";
    public static final String KEY_TOSEE = "tosee";
    public static final String KEY_SEEN = "seen";
    public static final String KEY_FAVORITE = "favorite";

    //Create the table movies in the DB
    public static final String CREATE_MOVIE_TABLE = "CREATE TABLE "+TABLE_NAME+
            " (" +
            " "+KEY_ID_MOVIE+" INTEGER primary key," +
            " "+KEY_TITLE+" TEXT," +
            " "+KEY_OVERVIEW+" TEXT," +
            " "+KEY_POSTER_PATH+" TEXT," +
            " "+KEY_BACKDROP_PATH+" TEXT," +
            " "+KEY_MY_RATING+" REAL," +
            " "+KEY_TMDB_RATING+" REAL," +
            " "+KEY_RELEASE_DATE+" TEXT," +
            " "+KEY_TOSEE+" INTEGER,"+
            " "+KEY_SEEN+" INTEGER,"+
            " "+KEY_FAVORITE+" INTEGER"+
            ");";
    private MyDatabase myDataBaseSQLite;
    private SQLiteDatabase db;

    //Get the DB instance
    public MovieManager(Context context)
    {
        myDataBaseSQLite = MyDatabase.getInstance(context);
    }

    //Allow to open the DB
    public void open()
    {
        db = myDataBaseSQLite.getWritableDatabase();
    }

    //Allow to close the DB
    public void close()
    {
        db.close();
    }

    //Clear all rows in movies table
    public void clear() {
        db.delete(TABLE_NAME, "1", null);
    }

    /**
     * Create a movie in DB
     * @param movie object
     */
    public long createMovie(Movie movie) {

        ContentValues values = new ContentValues();
        values.put(KEY_ID_MOVIE, movie.getId());
        values.put(KEY_TITLE, movie.getTitle());
        values.put(KEY_OVERVIEW, movie.getOverview());
        values.put(KEY_POSTER_PATH, movie.getPosterPath());
        values.put(KEY_BACKDROP_PATH, movie.getBackdropPath());
        values.put(KEY_MY_RATING, movie.getMyRating());
        values.put(KEY_TMDB_RATING, movie.getTMDBRating());
        values.put(KEY_RELEASE_DATE, movie.getReleaseDate());

        /*ArrayList<String> genres = new ArrayList<String>();

        for (int i = 0; i < genres.size(); i++) {
            values.put(KEY_GENRES, genres.get(i));
        }*/

        values.put(KEY_TOSEE, movie.getToSee());
        values.put(KEY_SEEN, movie.getSeen());
        values.put(KEY_FAVORITE, movie.getFavorite());

        return db.insert(TABLE_NAME,null,values);
    }

    /**
     * Update a movie in DB
     * @param movie object
     */
    public int updateMovie(Movie movie) {

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, movie.getTitle());
        values.put(KEY_OVERVIEW, movie.getOverview());
        values.put(KEY_POSTER_PATH, movie.getPosterPath());
        values.put(KEY_BACKDROP_PATH, movie.getBackdropPath());
        values.put(KEY_MY_RATING, movie.getMyRating());
        values.put(KEY_TMDB_RATING, movie.getTMDBRating());
        values.put(KEY_RELEASE_DATE, movie.getReleaseDate());

        /*ArrayList<String> genres = new ArrayList<String>();

        for (int i = 0; i < genres.size(); i++) {
            values.put(KEY_GENRES, genres.get(i));
        }*/

        values.put(KEY_TOSEE, movie.getToSee());
        values.put(KEY_SEEN, movie.getSeen());
        values.put(KEY_FAVORITE, movie.getFavorite());
        String where = KEY_ID_MOVIE + " = ?";
        String[] whereArgs = {movie.getId()+""};

        return db.update(TABLE_NAME, values, where, whereArgs);
    }

    /**
     * Delete a movie in DB
     * @param movie object
     */
    public int deleteMovie(Movie movie) {

        String where = KEY_ID_MOVIE + " = ?";
        String[] whereArgs = {movie.getId()+""};

        return db.delete(TABLE_NAME, where, whereArgs);
    }

    /**
     * Get movie informations from DB
     * @param id of the movie
     */
    public Movie getMovie(int id) {

        ArrayList<String> genres = new ArrayList<String>();

        Movie a = new Movie(0,"","","","",0,0,
                "", genres, 0, 0 ,0);

        Cursor c = db.rawQuery("SELECT * FROM "+ TABLE_NAME +" WHERE "+KEY_ID_MOVIE + "="+id, null);
        if (c.moveToFirst()) {
            a.setId(c.getInt(c.getColumnIndex(KEY_ID_MOVIE)));
            a.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
            a.setOverview(c.getString(c.getColumnIndex(KEY_OVERVIEW)));
            a.setPosterPath(c.getString(c.getColumnIndex(KEY_POSTER_PATH)));
            a.setBackdropPath(c.getString(c.getColumnIndex(KEY_BACKDROP_PATH)));
            a.setMyRating(c.getInt(c.getColumnIndex(KEY_MY_RATING)));
            a.setTMDBRating(c.getDouble(c.getColumnIndex(KEY_TMDB_RATING)));
            a.setReleaseDate(c.getString(c.getColumnIndex(KEY_RELEASE_DATE)));

            /*ArrayList<String> genresList = new ArrayList<String>();

            c.moveToFirst();
            while(!c.isAfterLast()) {
                genresList.add(c.getString(c.getColumnIndex(KEY_GENRES)));
                c.moveToNext();
            }*/

            a.setToSee(c.getInt(c.getColumnIndex(KEY_TOSEE)));
            a.setSeen(c.getInt(c.getColumnIndex(KEY_SEEN)));
            a.setFavorite(c.getInt(c.getColumnIndex(KEY_FAVORITE)));
            c.close();
        }

        return a;
    }

    /**
     * Check if movie exists in DB
     * @param id of the movie
     */
    public Boolean CheckMovie(int id) {

        ArrayList<String> genres = new ArrayList<String>();

        Movie a = new Movie(0,"","","","",0,0,
                "", genres, 0, 0 ,0);

        Cursor c = db.rawQuery("SELECT * FROM "+ TABLE_NAME + " WHERE " + KEY_ID_MOVIE + "=" +id, null);

        if (c.moveToFirst()) {
            return true;
        }else{
            return false;
        }

    }

    /**
     * Get list of movies to see
     */
    public Cursor getMoviesToSee(){
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "+ KEY_TOSEE + "=1",null);
    }

    /**
     * Get list of movies seen
     */
    public Cursor getMoviesSeen(){
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "+ KEY_SEEN + "=1",null);
    }

    /**
     * Get list of favorites movies
     */
    public Cursor getMoviesFavorite(){
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "+ KEY_FAVORITE + "=1",null);
    }

    /**
     * Get all movies in DB
     */
    public Cursor getMovies() {
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}
