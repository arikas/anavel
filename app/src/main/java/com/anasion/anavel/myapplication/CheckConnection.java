package com.anasion.anavel.myapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Vostro on 17/08/2016.
 */
public class CheckConnection
{
    private static CheckConnection checkConnection;
    private static Context context;
    private CheckConnection(Context context)
    {
        this.context = context;
    }

    public static CheckConnection getInstance(Context context)
    {
        if(checkConnection == null)
        {
            checkConnection = new CheckConnection(context);
        }
        return checkConnection;
    }

    public static boolean isConnect()
    {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void deleteInstance()
    {
        context=null;
        checkConnection = null;
    }

}
