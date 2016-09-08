package com.anasion.anavel.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity
{
    private Context splash_Context = null;
    private StringRequest string_Request = null;

    public static final String server_Url = "http://anasion.com/login.php";
    public static final String KEY_Username = "username";
    public static final String KEY_Password = "password";

    protected TextView splash_Progress_Text_View = null;
    protected ProgressBar splash_Progress_Bar = null;
    protected ImageView splash_Logo = null;
    protected Animation splash_Animation_FadeIn = null;
    protected Animation splash_Animation_FadeOut = null;

    public static int progress_Value;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        progress_Value =0;

        splash_Context = this;

        splash_Logo = (ImageView) findViewById(R.id.splashLogo);
        splash_Animation_FadeIn = AnimationUtils.loadAnimation(getBaseContext(), R.anim.splash_animation_fadein);
        splash_Animation_FadeOut = AnimationUtils.loadAnimation(getBaseContext(), R.anim.splash_animation_fadeout);
        splash_Progress_Text_View = (TextView) findViewById(R.id.splashProgressTextView);
        splash_Progress_Bar = (ProgressBar) findViewById(R.id.splashProgressBar);
        splash_Progress_Bar.setVisibility(View.VISIBLE);

        CustomTypeface.getInstance().setCustom(findViewById(R.id.activity_splash), CustomTypeface.getInstance().getTypeface(splash_Context, "SPLASH"));

        /*if(OpenCVLoader.initDebug())
        {
            splash_Progress_Text_View.setText("Berhasil");
        }*/

        splash_Logo.startAnimation(splash_Animation_FadeIn);

        splash_Animation_FadeIn.setAnimationListener(new Animation.AnimationListener() {


            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                checking();
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
    }

    private void checking()
    {
        CheckConnection.deleteInstance();
        if(CheckConnection.getInstance(splash_Context).isConnect()) {
            if (SessionManager.getInstance(splash_Context).checkLogin()) {
                splash_Progress_Text_View.setText("Checking Session...");
                splash_Progress_Bar.setProgress(25);
                loginInBackground(SessionManager.getInstance(splash_Context).getDetail().get(SessionManager.KEY_username), SessionManager.getInstance(splash_Context).getDetail().get(SessionManager.KEY_password));
            } else {
                splash_Progress_Text_View.setText("Redirecting...");
                splash_Progress_Bar.setProgress(100);

                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
        else
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(splash_Context);
            alertDialogBuilder.setMessage("Internet Connection not working");

            alertDialogBuilder.setPositiveButton("rentry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    checking();
                }
            });

            alertDialogBuilder.setNegativeButton("close",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }


    private void  loginInBackground(final String username, final String password)
    {
        string_Request = new StringRequest(Request.Method.POST, server_Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String message = jsonObject.getString("message");
                    String pp_Url = jsonObject.getString("pp");
                    String cp_Url = jsonObject.getString("cp");
                    String status = jsonObject.getString("status");
                    String about = jsonObject.getString("about");
                    String name = jsonObject.getString("name");

                    if(pp_Url != null && cp_Url != null)
                    {
                        splash_Progress_Text_View.setText("Checking Account...");
                        splash_Progress_Bar.setProgress(50);
                    }

                    getImageInBackground(pp_Url, "pp", message);
                    getImageInBackground(cp_Url, "cp", message);

                    SessionManager.getInstance(splash_Context).createLoginSession(username, password, status, about, name);

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(splash_Context, volleyError.toString(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put(KEY_Username, username);
                params.put(KEY_Password, password);

                return params;
            }
        };

        CustomRequest.deleteInstance();
        CustomRequest.getInstance(splash_Context).addToRequestQueue(string_Request);
    }

    private void getImageInBackground(final String imageLink, final String request, final String message)
    {
        ImageRequest imageRequest = new ImageRequest(imageLink, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                if(request.equals("pp"))
                {
                    SessionManager.getInstance(splash_Context).saveProfilImage(response);

                    splash_Progress_Text_View.setText("Get Account Data...");
                    splash_Progress_Bar.setProgress(75);
                }
                else
                {
                    SessionManager.getInstance(splash_Context).saveCoverImage(response);

                    splash_Progress_Text_View.setText("Redirecting...");
                    splash_Progress_Bar.setProgress(100);

                    //Toast.makeText(splash_Context, message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SplashActivity.this, "Error get user data.." , Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        CustomRequest.deleteInstance();
        CustomRequest.getInstance(splash_Context).addToRequestQueue(imageRequest);
    }


}
