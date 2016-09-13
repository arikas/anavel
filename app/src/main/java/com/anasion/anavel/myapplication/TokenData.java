package com.anasion.anavel.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Vostro on 13/09/2016.
 */
public class TokenData {

    private static TokenData tokenData;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static Context context;

    int PRIVATE_MODE = 0;
    private static final String prefName = "AnasionTokenPref";
    public static final String KEY_token = "token";

    private TokenData(Context context)
    {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(prefName, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public static synchronized TokenData getInstance(Context context)
    {
        if(tokenData==null)
        {
            tokenData = new TokenData(context);
        }
        return tokenData;
    }

    public static synchronized void saveToken(String token)
    {
        editor.putString(KEY_token, token);
        editor.commit();
    }

    public static String getToken()
    {
        return sharedPreferences.getString(KEY_token, null);
    }
}
