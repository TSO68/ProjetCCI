package com.example.projetcci.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Manage movie_genres relationnal table in the local database
 */
public class MovieGenresManager {

    private static final String TABLE_NAME = "movie_genres";
    private static final String KEY_ID_GENRE = "genre_id";
    private static final String KEY_ID_MOVIE = "movie_id";

    //Create the movie_genres table in the DB
    public static final String CREATE_MOVIE_GENRES_TABLE = "CREATE TABLE "+TABLE_NAME+
            " (" +
            " "+ KEY_ID_GENRE +" INTEGER," +
            " "+ KEY_ID_MOVIE +" INTEGER," +
            "PRIMARY KEY(" + KEY_ID_GENRE + "," + KEY_ID_MOVIE + "));";
    private final MyDatabase myDataBaseSQLite;
    private SQLiteDatabase db;

    //Get the DB instance
    public MovieGenresManager(Context context)
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
     * Create a duet genre-movie in the DB
     * @param id_genre id of the genre
     * @param id_movie id of the movie
     */
    public void createMovieGenre(int id_genre, int id_movie) {

        ContentValues values = new ContentValues();
        values.put(KEY_ID_GENRE, id_genre);
        values.put(KEY_ID_MOVIE, id_movie);

        db.insert(TABLE_NAME, null, values);
    }

    /**
     * Check if couple with id of the genre and id of the movie already exists in DB
     * @param idGenre id of the genre
     * @param idMovie id of the movie
     * @return a boolean
     */
    public Boolean checkMovieGenres(int idGenre, int idMovie) {

        Cursor c = db.rawQuery("SELECT * FROM "+ TABLE_NAME + " WHERE " + KEY_ID_GENRE
                + "=" + idGenre + " AND " + KEY_ID_MOVIE + "=" + idMovie, null);

        if (c.moveToFirst()) {
            c.close();
            return true;
        }else{
            c.close();
            return false;
        }
    }

    /**
     * Get the genre name
     * @param id_genre id of the genre
     * @param id_movie id of the movie
     * @return name of the genre
     */
    public String getGenreName(int id_genre, int id_movie) {
        String name = "";

        Cursor c = db.rawQuery("SELECT genres.name FROM genres INNER JOIN " + TABLE_NAME + " ON genres.id_genre = " + KEY_ID_GENRE
                + " WHERE " + KEY_ID_GENRE + " = " + id_genre + " AND " + KEY_ID_MOVIE + " = " + id_movie, null);

        if(c.moveToFirst()){
            name = c.getString(c.getColumnIndex("genres.name"));
        }
        c.close();

        return name;
    }

    /*
    Statistics part
     */

    /**
     * Get the most watched genre from the user
     * @return id of the genre
     */
    public int getFavoriteGenre() {

        Cursor c = db.rawQuery("SELECT " +  KEY_ID_GENRE + ", COUNT(*) AS count FROM " + TABLE_NAME
                + " WHERE " + KEY_ID_MOVIE + " IN (SELECT movies.id_movie FROM movies WHERE seen = 1) "
                + " GROUP BY " + KEY_ID_GENRE + " ORDER BY count DESC LIMIT 1", null);

        int genre = 0;
        if (c != null) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                genre = c.getInt(0);
            }
            c.close();
        }

        return genre;
    }
}
