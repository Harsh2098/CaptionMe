package com.hmproductions.captionme.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hmproductions.captionme.data.CaptionContract.CaptionEntry;

/**
 * Created by Harsh Mahajan on 12/6/2017.
 */

public class CaptionDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "caption";

    private static final String SQL_CREATE_TABLE =
                    "CREATE TABLE " + CaptionEntry.TABLE_NAME + " (" +
                    CaptionEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CaptionEntry.COLUMN_IMAGE_PATH + " TEXT NOT NULL," +
                    CaptionEntry.COLUMN_CAPTION + " TEXT );";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + CaptionEntry.TABLE_NAME;


    public CaptionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }
}
