package com.example.projetcci;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mymovies.sqlite";
    private static final int DATABASE_VERSION = 1;
    private static MyDatabase sInstance;

    public static synchronized MyDatabase getInstance(Context context) {
        if (sInstance == null) { sInstance = new MyDatabase(context); }
        return sInstance;
    }

    private MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(MovieManager.CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int j) {
        onCreate(sqLiteDatabase);
    }
}
