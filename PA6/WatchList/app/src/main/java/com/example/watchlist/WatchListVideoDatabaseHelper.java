/**
 * Program handles queries against the database using VideoDetailsActivity
 * CPSC 312-01, Fall 2021
 * Programming Assignment #7
 *
 * @author Zac Foteff
 * @version v2.0 11/23/21
 */

package com.example.watchlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class WatchListVideoDatabaseHelper extends SQLiteOpenHelper {
    static final String TAG = "DB Calls";
    static final String DATABASE_NAME = "watchlistVideosDatabase.db";
    static final int DATABASE_VERSION = 1;

    static final String VIDEOS_TABLE = "tableVideos";
    static final String ID = "_id";
    static final String TITLE = "title";
    static final String TYPE = "type";
    static final String WATCHED = "watches";
    static final String DRAWABLE_ID = "drawableId";

    public WatchListVideoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreate = "CREATE TABLE " + VIDEOS_TABLE + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE + " TEXT NOT NULL, " +
                TYPE + " TEXT NOT NULL, " +
                WATCHED + " BOOLEAN NOT NULL, " +
                DRAWABLE_ID + " INTEGER NOT NULL)";
        Log.d(TAG, "onCreate: "+sqlCreate);
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertVideo(WatchListVideo video) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, video.getTitle());
        contentValues.put(TYPE, video.getType());
        contentValues.put(WATCHED, video.hasWatched());
        contentValues.put(DRAWABLE_ID, video.getDrawable());

        Log.d(TAG, "insertVideo: "+contentValues.toString());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(VIDEOS_TABLE, null, contentValues);
        db.close();
    }

    public WatchListVideo selectVideo(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(VIDEOS_TABLE, new String[] {
                TITLE,
                TYPE,
                WATCHED,
                DRAWABLE_ID},
                ID + "="+(id+1), null,
                null,
                null,null);
        WatchListVideo video = null;
        if (cursor.moveToNext()) {
            String title = cursor.getString(0);
            String type = cursor.getString(1);
            boolean watched = (cursor.getInt(2))>0;
            int drawable_id = cursor.getInt(3);
            video = new WatchListVideo(title, type, watched, drawable_id);
        }

        Log.d(TAG, "selectVideo: ");
        return video;
    }

    public Cursor getSelectAllCursor() {
        // we need to construct a query to get a cursor
        // to step through records
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(VIDEOS_TABLE, new String[]{
                TITLE,
                TYPE,
                WATCHED,
                DRAWABLE_ID},
                null, null, null,
                null, null);
        return cursor;
    }

    public List<WatchListVideo> selectAllVideos() {
        List<WatchListVideo> videoList = new ArrayList<>();
        Cursor cursor = getSelectAllCursor();

        while (cursor.moveToNext()) {
            WatchListVideo video = new WatchListVideo(cursor.getString(0), cursor.getString(1), (cursor.getInt(2)>0), cursor.getInt(3));
            videoList.add(video);
        }

        Log.d(TAG, "selectAllVideos: ");
        return videoList;
    }

    public void updateVideo (WatchListVideo video) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, video.getTitle());
        contentValues.put(TYPE, video.getType());
        contentValues.put(WATCHED, video.hasWatched());
        contentValues.put(DRAWABLE_ID, video.getDrawable());

        SQLiteDatabase db = getWritableDatabase();
        db.update(VIDEOS_TABLE, contentValues, null, null);
        Log.d(TAG, "updateVideo: ");
        db.close();
    }

    public int getNumberOfElements () {
        int result = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = getSelectAllCursor();

        while (cursor.moveToNext())
            result += 1;

        return result;
    }

    public void deleteVideos(List<WatchListVideo> videos) {
        SQLiteDatabase db = getWritableDatabase();
        for (WatchListVideo video : videos)
            db.delete(VIDEOS_TABLE, TITLE+"=\""+video.getTitle()+"\"", null);
        Log.d(TAG, "deleteMultiple: ");
        db.close();
    }
}
