package com.example.projetcci.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.projetcci.models.Movie;

/**
 * Manage movie table in the local database
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
    public static final String KEY_GENRES = "genres";
    public static final String KEY_RUNTIME = "runtime";
    public static final String KEY_TOSEE = "tosee";
    public static final String KEY_SEEN = "seen";
    public static final String KEY_FAVORITE = "favorite";

    //Create the movies table in the DB
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
            " "+KEY_GENRES+" TEXT," +
            " "+KEY_RUNTIME+" INTEGER,"+
            " "+KEY_TOSEE+" INTEGER,"+
            " "+KEY_SEEN+" INTEGER,"+
            " "+KEY_FAVORITE+" INTEGER"+
            ");";
    private final MyDatabase myDataBaseSQLite;
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
    public void createMovie(Movie movie) {

        ContentValues values = new ContentValues();
        values.put(KEY_ID_MOVIE, movie.getId());
        values.put(KEY_TITLE, movie.getTitle());
        values.put(KEY_OVERVIEW, movie.getOverview());
        values.put(KEY_POSTER_PATH, movie.getPosterPath());
        values.put(KEY_BACKDROP_PATH, movie.getBackdropPath());
        values.put(KEY_MY_RATING, movie.getMyRating());
        values.put(KEY_TMDB_RATING, movie.getTMDBRating());
        values.put(KEY_RELEASE_DATE, movie.getReleaseDate());
        values.put(KEY_GENRES, movie.getGenres());
        values.put(KEY_RUNTIME, movie.getRuntime());
        values.put(KEY_TOSEE, movie.getToSee());
        values.put(KEY_SEEN, movie.getSeen());
        values.put(KEY_FAVORITE, movie.getFavorite());

        db.insert(TABLE_NAME, null, values);
    }

    /**
     * Update a movie in DB
     * @param movie object
     */
    public void updateMovie(Movie movie) {

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, movie.getTitle());
        values.put(KEY_OVERVIEW, movie.getOverview());
        values.put(KEY_POSTER_PATH, movie.getPosterPath());
        values.put(KEY_BACKDROP_PATH, movie.getBackdropPath());
        values.put(KEY_MY_RATING, movie.getMyRating());
        values.put(KEY_TMDB_RATING, movie.getTMDBRating());
        values.put(KEY_RELEASE_DATE, movie.getReleaseDate());
        values.put(KEY_GENRES, movie.getGenres());
        values.put(KEY_TOSEE, movie.getToSee());
        values.put(KEY_SEEN, movie.getSeen());
        values.put(KEY_FAVORITE, movie.getFavorite());
        String where = KEY_ID_MOVIE + " = ?";
        String[] whereArgs = {movie.getId()+""};

        db.update(TABLE_NAME, values, where, whereArgs);
    }

    /**
     * Update the runtime of a movie
     * @param runtime of the movie
     * @param id of the movie
     */
    public void updateRuntime(int runtime, int id) {

        ContentValues values = new ContentValues();
        values.put(KEY_RUNTIME, runtime);
        String where = KEY_ID_MOVIE + " = ?";
        String[] whereArgs = {id+""};

        db.update(TABLE_NAME, values, where, whereArgs);
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
     * @return Movie object
     */
    public Movie getMovie(int id) {

        Movie a = new Movie(0,"","","","",0,0,
                "", "", 0,0, 0 ,0);

        Cursor c = db.query(TABLE_NAME, null, KEY_ID_MOVIE + " = ?",
                new String[] {String.valueOf(id)}, null, null, null);

        if (c.moveToFirst()) {
            a.setId(c.getInt(c.getColumnIndex(KEY_ID_MOVIE)));
            a.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
            a.setOverview(c.getString(c.getColumnIndex(KEY_OVERVIEW)));
            a.setPosterPath(c.getString(c.getColumnIndex(KEY_POSTER_PATH)));
            a.setBackdropPath(c.getString(c.getColumnIndex(KEY_BACKDROP_PATH)));
            a.setMyRating(c.getDouble(c.getColumnIndex(KEY_MY_RATING)));
            a.setTMDBRating(c.getDouble(c.getColumnIndex(KEY_TMDB_RATING)));
            a.setReleaseDate(c.getString(c.getColumnIndex(KEY_RELEASE_DATE)));
            a.setGenres(c.getString(c.getColumnIndex(KEY_GENRES)));
            a.setRuntime(c.getInt(c.getColumnIndex(KEY_RUNTIME)));
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
    public Boolean checkMovie(int id) {

        Cursor c = db.query(TABLE_NAME, null, KEY_ID_MOVIE + " = ?",
                new String[] {String.valueOf(id)}, null, null, null);

        if (c.moveToFirst()) {
            c.close();
            return true;
        } else {
            c.close();
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

    /**
     * Get user's rating of a movie
     * @param id of the movie
     * @return user's rating
     */
    public Movie getMyRatingById(int id) {

        Movie a = new Movie(0,"","","","",0,0,
                "", "", 0,0, 0 ,0);

        Cursor c = db.rawQuery("SELECT " + KEY_MY_RATING + " FROM " + TABLE_NAME + " WHERE " + KEY_ID_MOVIE + " = ?",
                new String[] {String.valueOf(id)});

        if (c.moveToFirst()) {
            a.setMyRating(c.getDouble(c.getColumnIndex(KEY_MY_RATING)));
            c.close();
        }

        return a;
    }

    /*
    Statistics part
     */

    /**
     * Get the number of movies to see
     * @return the number of movies
     */
    public int getCountToSeeMovies() {

        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + KEY_TOSEE + "=1", null);

        int count = 0;
        if (c != null) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                count = c.getInt(0);
            }
            c.close();
        }

        return count;
    }

    /**
     * Get the number of movies seen
     * @return the number of movies
     */
    public int getCountSeenMovies() {

        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + KEY_SEEN + "=1", null);

        int count = 0;
        if (c != null) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                count = c.getInt(0);
            }
            c.close();
        }

        return count;
    }

    /**
     * Get the number of favorite movies
     * @return the number of movies
     */
    public int getCountFavoritesMovies() {

        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + KEY_FAVORITE + "=1", null);

        int count = 0;
        if (c != null) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                count = c.getInt(0);
            }
            c.close();
        }

        return count;
    }

    /**
     * Get the sum of runtime of each movies seen by the user
     * @return the total of viewing time
     */
    public int getTotalViewing() {

        Cursor c = db.rawQuery("SELECT SUM("+ KEY_RUNTIME +") FROM " + TABLE_NAME + " WHERE " + KEY_SEEN + "=1", null);

        int sum = 0;
        if (c != null) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                sum = c.getInt(0);
            }
            c.close();
        }

        return sum;
    }

    /**
     * Get the average rating of the user
     * @return the average rating
     */
    public double getAverageRating() {

        Cursor c = db.rawQuery("SELECT AVG("+ KEY_MY_RATING +") FROM " + TABLE_NAME + " WHERE " + KEY_MY_RATING + "!=0", null);

        double average = 0;
        if (c != null) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                average = c.getDouble(0);
            }
            c.close();
        }

        return average;
    }
}
