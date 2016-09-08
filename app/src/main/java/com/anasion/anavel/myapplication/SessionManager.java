package com.anasion.anavel.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * Created by Vostro on 13/08/2016.
 */
public class SessionManager
{
    private static SessionManager sessionManager;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static Context context;

    int PRIVATE_MODE = 0;
    private static final String prefName = "AnasionLoginPref";
    private static final String KEY_isLogin = "isLogin";
    public static final String KEY_username = "username";
    public static final String KEY_password = "password";
    public static final String KEY_status = "status";
    public static final String KEY_about = "about";
    public static final String KEY_name = "name";
    public static final String KEY_profilImage = "profilimage";
    public static final String KEY_coverimage = "coverimage";
    public static final String KEY_clickImaage = "clickImage";

    private SessionManager(Context context)
    {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(prefName, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SessionManager getInstance(Context context)
    {
        if(sessionManager==null)
        {
            sessionManager = new SessionManager(context);
        }
        return sessionManager;
    }

    public static synchronized void createLoginSession(String username, String password, String status, String about, String name)
    {
        editor.putBoolean(KEY_isLogin, true);
        editor.putString(KEY_username, username);
        editor.putString(KEY_password, password);
        editor.putString(KEY_status, status);
        editor.putString(KEY_about, about);
        editor.putString(KEY_name, name);
        editor.commit();
    }

    public static synchronized void updateDataSession(String key, String data)
    {
        editor.putString(key, data);
        editor.commit();
    }

    public static synchronized void saveProfilImage(Bitmap bitmap)
    {
        editor.putString(KEY_profilImage, encodeTobase64(bitmap));
        editor.commit();
    }

    public static synchronized void saveCoverImage(Bitmap bitmap)
    {
        editor.putString(KEY_coverimage, encodeTobase64(bitmap));
        editor.commit();
    }

    public static synchronized void saveClickImage(Bitmap bitmap)
    {
        editor.putString(KEY_clickImaage, encodeTobase64(bitmap));
        editor.commit();
    }

    public static Bitmap getClickImage()
    {
        return decodeBase64(sharedPreferences.getString(KEY_clickImaage, null));
    }

    public static HashMap<String, Bitmap> getImage()
    {
        HashMap<String, Bitmap> userImage = new HashMap<String, Bitmap>();
        userImage.put(KEY_profilImage, decodeBase64(sharedPreferences.getString(KEY_profilImage, null)) );
        userImage.put(KEY_coverimage, decodeBase64(sharedPreferences.getString(KEY_coverimage, null))  );

        return userImage;
    }

    public static boolean checkLogin()
    {
        return sharedPreferences.getBoolean(KEY_isLogin,false);
    }

    public static HashMap<String, String> getDetail()
    {
        HashMap<String, String> userDetil = new HashMap<String, String>();
        userDetil.put(KEY_username, sharedPreferences.getString(KEY_username, null));
        userDetil.put(KEY_password, sharedPreferences.getString(KEY_password, null));
        userDetil.put(KEY_status, sharedPreferences.getString(KEY_status, null));
        userDetil.put(KEY_about, sharedPreferences.getString(KEY_about, null));
        userDetil.put(KEY_name, sharedPreferences.getString(KEY_name,null));

        return  userDetil;
    }

    public static void logoutUser()
    {
        editor.clear();
        editor.commit();
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
