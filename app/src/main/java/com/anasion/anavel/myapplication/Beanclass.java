package com.anasion.anavel.myapplication;

import android.graphics.Bitmap;

/**
 * Created by Vostro on 10/08/2016.
 */
public class Beanclass
{
    private Bitmap image;

    public Beanclass(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
