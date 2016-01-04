package com.kwonterry.fliptimer.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Terry Kwon on 1/1/2016.
 */
public class TimeDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "time.db";

    public TimeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TIME_TABLE = "CREATE TABLE " + TimeContract.TimeEntry.TABLE_NAME
                + " (" +
                TimeContract.TimeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TimeContract.TimeEntry.COLUMN_TIME + " TEXT NOT NULL, " +
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


    // TODO: 1/4/2016 FILL THE WORKTIME COLUMN = TIME - PREVIOUS TIME 
//    public int getTimeInterval() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT " + TimeContract.TimeEntry.COLUMN_TIME +
//                " FROM " + TimeContract.TimeEntry.TABLE_NAME, null);
//
//        while (cursor.moveToNext())
//    }

    public boolean insertData(String time, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TimeContract.TimeEntry.COLUMN_TIME, time);
        contentValues.put(TimeContract.TimeEntry.COLUMN_STATUS, status);

        long result = db.insert(TimeContract.TimeEntry.TABLE_NAME, null, contentValues);
        return (result != -1);
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TimeContract.TimeEntry.TABLE_NAME, null);
        return result;
    }

    public void removeAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TimeContract.TimeEntry.TABLE_NAME, null, null);
    }

}
