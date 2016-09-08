package com.anasion.anavel.myapplication;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Vostro on 11/08/2016.
 */

// Singleton class for http request
public class CustomRequest
{
    private static CustomRequest customRequest;
    private static RequestQueue requestQueue;
    private static Context context;

    private CustomRequest(Context c)
    {
        context = c;
        requestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue()
    {
        if(requestQueue==null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized CustomRequest getInstance(Context context)
    {
        if(customRequest==null)
        {
            customRequest = new CustomRequest(context);
        }
        return customRequest;
    }

    public <T>void addToRequestQueue(Request<T> request)
    {
        requestQueue.add(request);
    }

    public static void deleteInstance()
    {
        context=null;
        requestQueue=null;
        customRequest=null;
    }
}
