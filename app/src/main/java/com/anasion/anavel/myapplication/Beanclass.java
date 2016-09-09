package com.anasion.anavel.myapplication;

import android.graphics.Bitmap;

/**
 * Created by Vostro on 10/08/2016.
 */
public class Beanclass
{
    private Bitmap image;

    private String url;

    public Beanclass(Bitmap image, String url) {
        this.image = image;
        this.url = url;
    }

    public Bitmap getImage() {
        return image;
    }
    public String getUrl() {return url;}

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
