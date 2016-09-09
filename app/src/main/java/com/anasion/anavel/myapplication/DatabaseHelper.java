package com.anasion.anavel.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vostro on 16/08/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    private static DatabaseHelper databaseHelper = null;
    private static final int DB_VERSION = 1;
    public static final String DB_NAME = "Anavel";
    public static final String TABLE_NAME = "image_data";
    public static final String TABLE_FOLLOW = "follow_data";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_INDEX = "indeks";
    public static final String COLUMN_IMAGELINK = "image_link";
    public static final String COLUMN_FOLLOWING = "following";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("+
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_USERNAME + " TEXT," +
            COLUMN_INDEX + " INTEGER," +
            COLUMN_IMAGELINK + " TEXT," +
            COLUMN_IMAGE + " TEXT);";

    private static final String CREATE_TABLE_FOLLOW = "CREATE TABLE " + TABLE_FOLLOW + "("+
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_USERNAME + " TEXT," +
            COLUMN_FOLLOWING + " TEXT);";

    public static synchronized DatabaseHelper getInstance(Context context)
    {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context.getApplicationContext());
        }
        return databaseHelper;
    }

    private DatabaseHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_FOLLOW);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLLOW);
        // create new table
        onCreate(db);
    }

    public void addEntry(Bitmap image, String username, String imagelink) throws SQLiteException {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(COLUMN_USERNAME, username);
        cv.put(COLUMN_INDEX, getMaxIndex()+1);
        cv.put(COLUMN_IMAGELINK, imagelink);
        cv.put(COLUMN_IMAGE, encodeTobase64(image));

        database.insert( TABLE_NAME, null, cv );
    }

    public void addEntry(Bitmap image, String username, Integer index, String imagelink) throws SQLiteException {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(COLUMN_USERNAME, username);
        cv.put(COLUMN_INDEX, index);
        cv.put(COLUMN_IMAGELINK, imagelink);
        cv.put(COLUMN_IMAGE, encodeTobase64(image));

        database.insert( TABLE_NAME, null, cv );
    }

    public void addFollowEntry(String username, String following)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(COLUMN_USERNAME, username);
        cv.put(COLUMN_FOLLOWING, following);

        database.insert( TABLE_FOLLOW, null, cv );
    }

    public void deleteEntry(String imageLink)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME, " image_link ='" +imageLink+ "'", null);
    }

    public void deleteFollowEntry(String username, String following)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_FOLLOW, " username ='" +username+ "' and following ='" +following+ "'", null);
    }

    public int getFollowingCount(String username)
    {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor mCount= database.rawQuery("select count(*) from "+ TABLE_FOLLOW + " where username='" +username+ "' ;", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        return  count;
    }

    public int getFollowerCount(String username)
    {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor mCount= database.rawQuery("select count(*) from "+ TABLE_FOLLOW + " where following='" +username+ "' ;", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        return  count;
    }

    public int getPostCount(String username)
    {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor mCount= database.rawQuery("select count(*) from "+ TABLE_NAME + " where username='" +username+ "' ;", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        return  count;
    }

    public void removeAll()
    {
        SQLiteDatabase database = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        database.delete(DatabaseHelper.TABLE_NAME, null, null);
        database.delete(DatabaseHelper.TABLE_FOLLOW, null, null);
        database.close();
    }

    public List<Beanclass> getEntry(String username){
        List<Beanclass> imageList = new ArrayList<Beanclass>();
        SQLiteDatabase database = this.getReadableDatabase();
        String sql = "SELECT image,image_link FROM "+ TABLE_NAME + " WHERE username='"+username+"' ORDER BY indeks ASC;";
        Cursor cursor = database.rawQuery(sql, null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    String name = cursor.getString(cursor
                            .getColumnIndex(COLUMN_IMAGE));
                    String url = cursor.getString(cursor
                            .getColumnIndex(COLUMN_IMAGELINK));
                    Beanclass bean = new Beanclass(decodeBase64(name),url);
                    imageList.add(bean);
                    cursor.moveToNext();
                }
            }
        }
        return imageList;
    }


    private Integer getMaxIndex()
    {
        Integer indeks = 0;
        SQLiteDatabase database = this.getReadableDatabase();
        final String MY_QUERY = "SELECT MAX(indeks) AS indeks FROM "+ TABLE_NAME +";";
        Cursor mCursor = database.rawQuery(MY_QUERY, null);

        if (mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            indeks = mCursor.getInt(mCursor.getColumnIndex("indeks"));
        }
        return indeks;
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
