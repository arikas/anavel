package com.anasion.anavel.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * Created by Vostro on 24/07/2016.
 */
public class LoginActivity extends AppCompatActivity
{
    private Context login_Context = null;
    private StringRequest string_Request = null;

    public static final String server_Url = "http://anasion.com/login.php";
    public static final String server_Url2 = "http://anasion.com/getimage.php";
    public static final String server_Url3 = "http://anasion.com/follow.php";
    public static final String KEY_Username = "username";
    public static final String KEY_Password = "password";
    public static final String KEY_Request = "request";

    private String username = null;
    private String password = null;

    protected EditText login_Username_EditText = null;
    protected EditText login_Password_EditText = null;
    protected Button login_Login_Button = null;
    protected TextView login_Create_Textview = null;

    protected ProgressDialog loading = null;
    private int imageRequestPending;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        login_Context = this;

        login_Username_EditText = (EditText) findViewById(R.id.loginUsernameEditText);
        login_Password_EditText = (EditText) findViewById(R.id.loginPasswordEditText);
        login_Login_Button = (Button) findViewById(R.id.loginLoginButton);
        login_Create_Textview = (TextView) findViewById(R.id.loginCreateTextview);
        imageRequestPending =0;

        CustomTypeface.getInstance().setCustom(findViewById(R.id.activity_login), CustomTypeface.getInstance().getTypeface(login_Context, "LOGIN"));

        login_Login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = login_Username_EditText.getText().toString().trim();
                password = login_Password_EditText.getText().toString().trim();

                if( username.length()==0 || password.length()==0)
                {
                    Toast.makeText(login_Context, "Please fill field", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    loginFunction();
                }
            }
        });

        login_Create_Textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginFunction()
    {
        CheckConnection.deleteInstance();
        if(CheckConnection.getInstance(login_Context).isConnect()) {
            loginInBackground(username, password);
        }
        else
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(login_Context);
            alertDialogBuilder.setMessage("Internet Connection not working");

            alertDialogBuilder.setPositiveButton("rentry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    loginFunction();
                }
            });

            alertDialogBuilder.setNegativeButton("back",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private void  loginInBackground(final String username, final String password)
    {
        loading = ProgressDialog.show(this,"Verifying...","Please wait...",false,false);
        string_Request = new StringRequest(Request.Method.POST, server_Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String message = jsonObject.getString("message");
                    if(message.equals("Login Success")) {
                        String pp_Url = jsonObject.getString("pp");
                        String cp_Url = jsonObject.getString("cp");
                        String status = jsonObject.getString("status");
                        String about = jsonObject.getString("about");
                        String name = jsonObject.getString("name");

                        loading.setMessage("Get User Information...");
                        getImageInBackground(pp_Url, "pp", loading);
                        getImageInBackground(cp_Url, "cp", loading);
                        getImageCollection(message, loading);

                        SessionManager.getInstance(login_Context).createLoginSession(username, password, status, about, name);
                    }
                    else
                    {
                        loading.dismiss();
                        Toast.makeText(login_Context, message, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                loading.dismiss();
                Toast.makeText(login_Context, volleyError.toString(), Toast.LENGTH_SHORT).show();
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
        CustomRequest.getInstance(login_Context).addToRequestQueue(string_Request);
    }

    private void getImageInBackground(final String imageLink, final String request, final ProgressDialog loading)
    {
        ImageRequest imageRequest = new ImageRequest(imageLink, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                if(request.equals("pp"))
                {
                    SessionManager.getInstance(login_Context).saveProfilImage(response);

                    loading.setMessage("Get User Data...");
                }
                else
                {
                    SessionManager.getInstance(login_Context).saveCoverImage(response);
                }

            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(LoginActivity.this, error.toString() , Toast.LENGTH_LONG).show();
            }
        });

        CustomRequest.deleteInstance();
        CustomRequest.getInstance(login_Context).addToRequestQueue(imageRequest);
    }

    private void getImageCollection(final String message, final ProgressDialog loading)
    {
        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, server_Url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    imageRequestPending = jsonArray.length();
                    if (jsonArray.length()>0) {
                        for (Integer i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String image = jsonObject.getString("image");
                            //imageRequestPending++;
                            getImage(image, loading,i);
                        }
                    }
                    else {
                        loading.dismiss();
                        Toast.makeText(login_Context, "Server Error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    Toast.makeText(login_Context, message, Toast.LENGTH_SHORT).show();
                    loading.dismiss();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(LoginActivity.this, error.toString() , Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_Username, username);

                return params;
            }
        };

        CustomRequest.deleteInstance();
        CustomRequest.getInstance(login_Context).addToRequestQueue(stringRequest1);
    }

    private void getImage(final String imageLink, final ProgressDialog loading, final Integer imageIndex)
    {
        ImageRequest imageRequest = new ImageRequest(imageLink, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                DatabaseHelper.getInstance(login_Context).addEntry(response, username, imageIndex);
                imageRequestPending--;
                if(imageRequestPending==0) {
                    getFollow(server_Url3, loading);
                }
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(LoginActivity.this, error.toString() , Toast.LENGTH_LONG).show();
            }
        });

        CustomRequest.deleteInstance();
        CustomRequest.getInstance(login_Context).addToRequestQueue(imageRequest);
    }

    private void getFollow(final String urlLink, final ProgressDialog loading)
    {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, urlLink, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length()>0) {
                        for (Integer i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String following = jsonObject.getString("following");

                            DatabaseHelper.getInstance(login_Context).addFollowEntry(username,following);
                        }

                        loading.setMessage("Redirecting...");
                        Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        Toast.makeText(login_Context, "Login Success", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                    else {
                        loading.dismiss();
                        Toast.makeText(login_Context, "Server Error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    Toast.makeText(login_Context, "Login Success", Toast.LENGTH_SHORT).show();
                    loading.dismiss();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(LoginActivity.this, error.toString() , Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_Username, username);
                params.put(KEY_Request, "following");
                return params;
            }
        };

        CustomRequest.deleteInstance();
        CustomRequest.getInstance(login_Context).addToRequestQueue(stringRequest2);
    }
}
