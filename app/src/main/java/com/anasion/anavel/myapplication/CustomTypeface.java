package com.anasion.anavel.myapplication;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vostro on 11/08/2016.
 */

// SingletonClassCustomTypeface.
public class CustomTypeface
{
    private static CustomTypeface customTypeface;
    public static final Map<String,String> typefaceMAP = new HashMap<String, String>();

    private CustomTypeface()
    {
        typefaceMAP.put("SPLASH","font/Lato-Light.ttf");
        typefaceMAP.put("LOGIN","font/Lato-Light.ttf");
        typefaceMAP.put("REGISTER","font/Lato-Light.ttf");
        typefaceMAP.put("DASHBOARD","font/Roboto-Regular.ttf");
        typefaceMAP.put("SEARCH","font/Lato-Light.ttf");
        typefaceMAP.put("SEARCHDASHBOARD","font/Roboto-Regular.ttf");
    }

    public static CustomTypeface getInstance()
    {
        if(customTypeface == null)
        {
            customTypeface = new CustomTypeface();
        }
        return customTypeface;
    }

    public static Typeface getTypeface(Context c , String Key) {

        Typeface typeface = Typeface.createFromAsset(c.getAssets(), typefaceMAP.get(Key));

        return typeface;

    }

    public static void setCustom(View v, Typeface typeface) {
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                setCustom(child,typeface);
            }
        } else if (v instanceof TextView) {
            ((TextView) v).setTypeface(typeface);
        }
    }


}
