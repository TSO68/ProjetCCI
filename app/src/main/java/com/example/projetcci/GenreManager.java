package com.example.projetcci;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Manage genres table in the local database
 */
public class GenreManager {

    private static final String TABLE_NAME = "genres";
    public static final String KEY_ID_GENRE = "id_genre";
    public static final String KEY_NAME = "name";

    //Create the genres table in the DB
    public static final String CREATE_GENRE_TABLE = "CREATE TABLE "+TABLE_NAME+
            " (" +
            " "+KEY_ID_GENRE+" INTEGER primary key," +
            " "+KEY_NAME+" TEXT" +
            ");";
    private MyDatabase myDataBaseSQLite;
    private SQLiteDatabase db;

    //Get the DB instance
    public GenreManager(Context context)
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

    //Clear all rows in genres table
    public void clear() {
        db.delete(TABLE_NAME, "1", null);
    }

    /**
     * Create a genre in the DB
     * @param genre object
     * @return
     */
    public long createGenre(Genre genre) {

        ContentValues values = new ContentValues();
        values.put(KEY_ID_GENRE, genre.getId());
        values.put(KEY_NAME, genre.getName());

        return db.insert(TABLE_NAME,null,values);
    }

    /**
     * Get genre informations from DB
     * @param id of the genre
     * @return Genre object
     */
    public Genre getGenre(int id) {

        Genre g = new Genre(0,"");

        Cursor c = db.rawQuery("SELECT * FROM "+ TABLE_NAME +" WHERE "+ KEY_ID_GENRE + "="+id, null);
        if (c.moveToFirst()) {
            g.setId(c.getInt(c.getColumnIndex(KEY_ID_GENRE)));
            g.setName(c.getString(c.getColumnIndex(KEY_NAME)));

            c.close();
        }

        return g;
    }

    /**
     * Get all genres from DB
     * @return
     */
    public Cursor getGenres() {
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    /**
     * Check if genres list exists in DB
     * @return
     */
    public Boolean checkGenres() {

        Cursor c = db.rawQuery("SELECT * FROM "+ TABLE_NAME, null);

        if (c.moveToFirst()) {
            c.close();
            return true;
        } else {
            c.close();
            return false;
        }
    }
}
