package com.anasion.anavel.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import java.util.HashMap;

/**
 * Created by Vostro on 10/09/2016.
 */
public class SearchData {
    private static SearchData searchData;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static Context context;

    int PRIVATE_MODE = 0;
    private static final String prefName = "AnasionSearchPref";
    public static final String KEY_username = "s_username";
    public static final String KEY_name = "s_name";
    public static final String KEY_status = "s_status";
    public static final String KEY_about = "s_about";
    public static final String KEY_profilImage = "s_profilimage";
    public static final String KEY_coverimage = "s_coverimage";
    public static final String KEY_followingCount = "s_followingcount";
    public static final String KEY_followerCount = "s_followercount";
    public static final String KEY_postCount = "s_postcount";

    private SearchData(Context context)
    {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(prefName, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SearchData getInstance(Context context)
    {
        if(searchData==null)
        {
            searchData = new SearchData(context);
        }
        return searchData;
    }

    public static synchronized void createData(String username, String name, String status, String about, int followingCount, int followerCount,int post, Bitmap pp, Bitmap cp)
    {
        editor.putString(KEY_username, username);
        editor.putString(KEY_name, name);
        editor.putString(KEY_status, status);
        editor.putString(KEY_about, about);
        editor.putInt(KEY_followingCount, followingCount);
        editor.putInt(KEY_followerCount, followerCount);
        editor.putInt(KEY_postCount, post);
        editor.putString(KEY_profilImage, ImageProcess.getInstance().encodeTobase64(pp));
        editor.putString(KEY_coverimage, ImageProcess.getInstance().encodeTobase64(cp));

        editor.commit();
    }

    public static HashMap<String, String> getStringData()
    {
        HashMap<String, String> stringData = new HashMap<String, String>();
        stringData.put(KEY_username, sharedPreferences.getString(KEY_username, null));
        stringData.put(KEY_name, sharedPreferences.getString(KEY_name, null));
        stringData.put(KEY_status, sharedPreferences.getString(KEY_status, null));
        stringData.put(KEY_about, sharedPreferences.getString(KEY_about, null));
        stringData.put(KEY_followingCount, String.valueOf(sharedPreferences.getInt(KEY_followingCount, 0)));
        stringData.put(KEY_followerCount, String.valueOf(sharedPreferences.getInt(KEY_followerCount, 0)));
        stringData.put(KEY_postCount, String.valueOf(sharedPreferences.getInt(KEY_postCount, 0)));

        return  stringData;
    }

    public static HashMap<String, Bitmap> getImageData()
    {
        HashMap<String, Bitmap> imageData = new HashMap<String, Bitmap>();
        imageData.put(KEY_profilImage, ImageProcess.getInstance().decodeBase64(sharedPreferences.getString(KEY_profilImage, null)) );
        imageData.put(KEY_coverimage, ImageProcess.getInstance().decodeBase64(sharedPreferences.getString(KEY_coverimage, null))  );

        return imageData;
    }


    public static void clearSearch()
    {
        editor.clear();
        editor.commit();
    }

}
