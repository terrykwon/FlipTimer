package com.kwonterry.fliptimer.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Terry Kwon on 1/1/2016.
 */
public class TimeDbHelper extends SQLiteOpenHelper{

    private final String LOG_TAG = TimeDbHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "time.db";

    public TimeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TIME_TABLE = "CREATE TABLE " + TimeContract.TimeEntry.TABLE_NAME
                + " (" +
                TimeContract.TimeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TimeContract.TimeEntry.COLUMN_TIME + " INTEGER NOT NULL, " +
                TimeContract.TimeEntry.COLUMN_STATUS + " INTEGER NOT NULL, " +
                TimeContract.TimeEntry.COLUMN_WORKTIME + " INTEGER NULL " +
                " );";

        db.execSQL(SQL_CREATE_TIME_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TimeContract.TimeEntry.TABLE_NAME);
        onCreate(db);
    }


    // when time is in millis (long)
    public boolean insertData(long time, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Long previousTime = getPreviousTime();

        contentValues.put(TimeContract.TimeEntry.COLUMN_TIME, time);
        contentValues.put(TimeContract.TimeEntry.COLUMN_STATUS, status);

        if (previousTime != 0) {
            contentValues.put(TimeContract.TimeEntry.COLUMN_WORKTIME, time - previousTime);
        }

        long result = db.insert(TimeContract.TimeEntry.TABLE_NAME, null, contentValues);
        return (result != -1);
    }

    // Returns stored time of previous row.
    // Returns null if none.
    public long getPreviousTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + TimeContract.TimeEntry.COLUMN_TIME +
                " FROM " + TimeContract.TimeEntry.TABLE_NAME, null);

        if (cursor.moveToLast()) {
            long prevTime = cursor.getLong(cursor
                    .getColumnIndexOrThrow(TimeContract.TimeEntry.COLUMN_TIME));
            cursor.close();
            return prevTime;
        } else {
            cursor.close();
            return 0;
        }
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TimeContract.TimeEntry.TABLE_NAME
                + " ORDER BY " + TimeContract.TimeEntry._ID + " DESC", null);
        return result;
    }

    // Clears all data. Not sure if this is the right way though.
    public void removeAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TimeContract.TimeEntry.TABLE_NAME, null, null);
    }

}
