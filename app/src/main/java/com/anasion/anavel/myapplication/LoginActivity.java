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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

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

    public static final String server_Url = "http://anasion.com/login.php";
    public static final String server_Url2 = "http://anasion.com/getimage.php";
    public static final String server_Url3 = "http://anasion.com/follow.php";
    public static final String server_Url4 = "http://anasion.com/fcm.php";
    public static final String KEY_Username = "username";
    public static final String KEY_Token = "token";
    public static final String KEY_Password = "password";
    public static final String KEY_Request = "request";

    private int logProcess;
    private String username = null;
    private String password = null;
    private String status = null;
    private String about = null;
    private String name = null;
    private Bitmap profil = null;
    private Bitmap cover = null;
    private int ppcpCount;
    private int imageCount;

    protected EditText login_Username_EditText = null;
    protected EditText login_Password_EditText = null;
    protected Button login_Login_Button = null;
    protected TextView login_Create_Textview = null;

    protected ProgressDialog loading = null;

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

        CustomTypeface.getInstance().setCustom(findViewById(R.id.activity_login), CustomTypeface.getInstance().getTypeface(login_Context, "LOGIN"));

        login_Login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper.getInstance(login_Context).removeAll();
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
            loginProcess(username, password);
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

    private void loginProcess(final String username, final String password)
    {
        loading = ProgressDialog.show(this,"Veryfying...","Please wait...",false,false);
        logProcess=3;
        CustomRequest.deleteInstance();
        CustomRequest.getInstance(login_Context).addToRequestQueue(getUserData(username,password));
        CustomRequest.getInstance(login_Context).addToRequestQueue(getUserImage(username));
        CustomRequest.getInstance(login_Context).addToRequestQueue(getUserFollow(username));
    }

    private void loginResponse()
    {
        logProcess--;
        if(logProcess==0)
        {
            loading.setMessage("Redirecting...");
            SessionManager.getInstance(login_Context).createLoginSession(username,password,status,about,name,profil,cover);

            FirebaseMessaging.getInstance().subscribeToTopic("test");
            FirebaseInstanceId.getInstance().getToken();

            sendFCM(username, TokenData.getInstance(login_Context).getToken());
        }
    }

    //GETUSERDATA===========================================================================================================
    private StringRequest getUserData(final String username, final String password)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String message = jsonObject.getString("message");
                    if(message.equals("Login Success")) {
                        String pp_Url = jsonObject.getString("pp");
                        String cp_Url = jsonObject.getString("cp");
                        status = jsonObject.getString("status");
                        about = jsonObject.getString("about");
                        name = jsonObject.getString("name");
                        ppcpCount=2;
                        loading.setMessage("Get User Information...");
                        getUserDataImage(pp_Url, "pp");
                        getUserDataImage(cp_Url, "cp");
                    }
                    else
                    {
                        loading.dismiss();
                        Toast.makeText(login_Context, message, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e)
                {
                    loading.dismiss();
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

        return stringRequest;
    }

    private void getUserDataImage(final String link, final String request)
    {
        ImageRequest imageRequest = new ImageRequest(link, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                if(request.equals("pp")) {
                    profil = response;
                    ppcpCount--;
                }
                else {
                    cover=response;
                    ppcpCount--;
                }

                if(ppcpCount==0) {
                    loginResponse();
                }

            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(LoginActivity.this, error.toString() , Toast.LENGTH_LONG).show();
            }
        });

        CustomRequest.getInstance(login_Context).addToRequestQueue(imageRequest);
    }

    //GETUSERIMAGE==========================================================================================================
    private StringRequest getUserImage(final String username)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_Url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    imageCount = jsonArray.length();
                    for (Integer i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String image = jsonObject.getString("image");
                        getImageCollection(image ,i);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    loginResponse();
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
        return stringRequest;
    }

    private void getImageCollection(final String link, final Integer index)
    {
        ImageRequest imageRequest = new ImageRequest(link, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                DatabaseHelper.getInstance(login_Context).addEntry(response, username, index, link);
                imageCount--;
                if(imageCount==0) {
                    loginResponse();
                }
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(LoginActivity.this, error.toString() , Toast.LENGTH_LONG).show();
            }
        });

        CustomRequest.getInstance(login_Context).addToRequestQueue(imageRequest);
    }

    //GETUSERFOLLOW=========================================================================================================
    private StringRequest getUserFollow(final String username)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_Url3, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (Integer i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String following = jsonObject.getString("following");
                        String uname =  jsonObject.getString("username");
                        DatabaseHelper.getInstance(login_Context).addFollowEntry(uname,following);
                    }
                    loginResponse();
                } catch (JSONException e) {
                    e.printStackTrace();
                    loginResponse();
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
                params.put(KEY_Request, "follow");
                params.put(KEY_Username, username);
                return params;
            }
        };

        return stringRequest;
    }

    //SEND DATA TOKEN & USERNAME TO FCM TABLE===============================================================================
    private void sendFCM(final String username, final String token)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_Url4, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                Toast.makeText(login_Context,"Login Success" , Toast.LENGTH_SHORT).show();
                loading.dismiss();
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
                params.put(KEY_Token, token);
                return params;
            }
        };

        CustomRequest.getInstance(login_Context).addToRequestQueue(stringRequest);
    }
}
