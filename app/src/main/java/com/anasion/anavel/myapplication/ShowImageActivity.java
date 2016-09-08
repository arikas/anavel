package com.anasion.anavel.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class ShowImageActivity extends AppCompatActivity {

    private Context showImage_Content = null;
    private String KEY_IMAGE = "Image";

    protected ImageView show_Image = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showimage);
        showImage_Content = this;

        show_Image = (ImageView) findViewById(R.id.showImageview);

        Bitmap bitmap = SessionManager.getInstance(showImage_Content).getClickImage();

        show_Image.setImageBitmap(bitmap);
    }
}
