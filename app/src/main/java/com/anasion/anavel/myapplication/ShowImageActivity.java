package com.anasion.anavel.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ShowImageActivity extends AppCompatActivity {

    private Context showImage_Content = null;
    public static final String server_Url = "http://anasion.com/imageoperation.php";
    public static final String KEY_Request = "request";
    public static final String KEY_Image = "image";

    protected ImageView show_Image = null;
    protected LinearLayout option_Image = null;
    protected Button delete_Image = null;
    protected Button like_Image = null;
    private boolean isShow;
    private boolean isLike;
    private String url;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showimage);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        showImage_Content = this;

        show_Image = (ImageView) findViewById(R.id.showImageview);
        option_Image = (LinearLayout) findViewById(R.id.optionImageview);
        delete_Image = (Button) findViewById(R.id.showImageDeleteButton);
        like_Image = (Button) findViewById(R.id.showImageLikeButton);

        Bitmap bitmap = SessionManager.getInstance(showImage_Content).getClickImage();
        url = SessionManager.getInstance(showImage_Content).getClickUrl();


        option_Image.setVisibility(View.GONE);
        isShow = false;
        isLike = false;

        show_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShow)
                {
                    option_Image.setVisibility(View.GONE);
                    isShow = false;
                }
                else
                {
                    option_Image.setVisibility(View.VISIBLE);
                    isShow = true;
                }
            }
        });

        option_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                option_Image.setVisibility(View.GONE);
                isShow = false;
            }
        });

        delete_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading = ProgressDialog.show(showImage_Content,"Deleting...","Please wait...",false,false);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, server_Url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        DatabaseHelper.getInstance(showImage_Content).deleteEntry(url);

                        Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        Toast.makeText(showImage_Content, "Deleting Image Success", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(ShowImageActivity.this, error.toString() , Toast.LENGTH_LONG).show();
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put(KEY_Request, "delete");
                        params.put(KEY_Image, url );

                        return params;
                    }
                };

                CustomRequest.deleteInstance();
                CustomRequest.getInstance(showImage_Content).addToRequestQueue(stringRequest);
            }
        });

        like_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLike = !isLike;
                if(isLike)
                {
                    like_Image.setBackgroundResource(R.drawable.likecircle);
                }
                else
                {
                    like_Image.setBackgroundResource(R.drawable.favcircle);
                }
            }
        });

        show_Image.setImageBitmap(bitmap);
    }
}
