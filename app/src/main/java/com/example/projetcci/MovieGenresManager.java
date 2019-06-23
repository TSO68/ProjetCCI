package com.example.projetcci;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Manage movie_genres relationnal table in the local database
 */
public class MovieGenresManager {

    private static final String TABLE_NAME = "movie_genres";
    public static final String KEY_ID_GENRE = "genre_id";
    public static final String KEY_ID_MOVIE = "movie_id";

    //Create the movie_genres table in the DB
    public static final String CREATE_MOVIE_GENRES_TABLE = "CREATE TABLE "+TABLE_NAME+
            " (" +
            " "+ KEY_ID_GENRE +" INTEGER," +
            " "+ KEY_ID_MOVIE +" INTEGER," +
            "PRIMARY KEY(" + KEY_ID_GENRE + "," + KEY_ID_MOVIE + "));";
    private MyDatabase myDataBaseSQLite;
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
     * @return
     */
    public long createMovieGenre(int id_genre, int id_movie) {

        ContentValues values = new ContentValues();
        values.put(KEY_ID_GENRE, id_genre);
        values.put(KEY_ID_MOVIE, id_movie);

        return db.insert(TABLE_NAME,null,values);
    }


    //TODO : Correct this request and test it if necessary
    /**
     * Get the genre name
     * @param id_genre
     * @param id_movie
     * @return
     */
    public String getGenreName(int id_genre, int id_movie) {
        String name = "";

        /*
        SELECT genre.name FROM genre
        INNER JOIN movie_genres
        ON genre.id_genre = movie_genres.id_genre
        WHERE movie_genres.id_genre = 12
        AND movie_genres.id_movie = 299537
        */

        Cursor c = db.rawQuery("SELECT genre.name FROM genre INNER JOIN movie_genres ON genres.id_genre = movie_genres.genre_id WHERE "+ TABLE_NAME +" WHERE movie_genres.genre_id =" + id_genre + " movie_genres.id_movie =" + id_movie, null);

        if(c.moveToFirst()){
            name = c.getString(c.getColumnIndex("genre.name"));
        }
        c.close();

        return name;
    }
}
