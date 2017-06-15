package com.hmproductions.captionme.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.hmproductions.captionme.data.CaptionContract.CaptionEntry;

/**
 * Created by Harsh Mahajan on 12/6/2017.
 */

public class CaptionProvider extends ContentProvider {

    static UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    CaptionDbHelper mDatabaseHelper;
    SQLiteDatabase mDatabase;

    private static final int URI_WITHOUT_PATH = 100;
    private static final int URI_WITH_PATH = 101;

    static {
        mUriMatcher.addURI(CaptionContract.CONTENT_AUTHORITY, CaptionContract.PATH_CAPTION, URI_WITHOUT_PATH);
        mUriMatcher.addURI(CaptionContract.CONTENT_AUTHORITY, CaptionContract.PATH_CAPTION + "/#", URI_WITH_PATH);
    }

    @Override
    public boolean onCreate() {

        mDatabaseHelper = new CaptionDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        mDatabase = mDatabaseHelper.getReadableDatabase();

        // TODO (1): Check if this works here -> mDatabaseHelper.close();

        Cursor cursor;

        switch (mUriMatcher.match(uri))
        {
            case URI_WITHOUT_PATH :
                cursor = mDatabase.query(CaptionEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case URI_WITH_PATH :
                selection = CaptionContract.CaptionEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = mDatabase.query(CaptionEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default : throw new IllegalArgumentException("Cannot serve URI request at this moment.");
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        mDatabase = mDatabaseHelper.getWritableDatabase();

        long colID;
        switch (mUriMatcher.match(uri)) {
            case URI_WITHOUT_PATH:
                colID = mDatabase.insert(CaptionEntry.TABLE_NAME, null, values);
                if (colID == -1)
                    Toast.makeText(getContext(), "Failed to insert", Toast.LENGTH_SHORT).show();
                else
                    getContext().getContentResolver().notifyChange(uri, null);
                break;

            default:
                throw new IllegalArgumentException("Cannot serve URI request at this moment.");
        }
        Log.v("provider", String.valueOf(colID));
        return ContentUris.withAppendedId(uri, colID);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        mDatabase = mDatabaseHelper.getWritableDatabase();

        int noOfRowsDeleted;

        switch (mUriMatcher.match(uri))
        {
            case URI_WITHOUT_PATH :
                noOfRowsDeleted = mDatabase.delete(CaptionEntry.TABLE_NAME, null,null);
                getContext().getContentResolver().notifyChange(uri, null);
                break;

            case URI_WITH_PATH :
                selection = CaptionEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                noOfRowsDeleted = mDatabase.delete(CaptionEntry.TABLE_NAME,selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;

            default: throw new IllegalArgumentException("Cannot serve URI request at this moment.");
        }

        return noOfRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int noOfRowsUpdated;

        mDatabase = mDatabaseHelper.getWritableDatabase();

        switch (mUriMatcher.match(uri))
        {
            case URI_WITH_PATH :
                selection = CaptionEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                noOfRowsUpdated = mDatabase.update(CaptionEntry.TABLE_NAME, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;

            default: throw new IllegalArgumentException("Cannot serve URI request at this moment.");
        }

        return noOfRowsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
