package com.anasion.anavel.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.StringRequest;

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
                splash_Progress_Bar.setProgress(50);
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
        splash_Progress_Text_View.setText("Redirecting...");
        splash_Progress_Bar.setProgress(100);

        //Toast.makeText(splash_Context, message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
        startActivity(intent);
        finish();
    }

}
