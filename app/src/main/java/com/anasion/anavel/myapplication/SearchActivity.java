package com.anasion.anavel.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

public class SearchActivity extends AppCompatActivity {

    Context search_Context = null;
    public static final String url = "http://anasion.com/searchuser.php";
    public static final String KEY_Username = "username";
    public static final String KEY_Request = "request";

    private int userProcess;
    private int ppcpCount;
    private int imageCount;
    private String username = null;
    private String status = null;
    private String about = null;
    private String name = null;
    private Bitmap profil = null;
    private Bitmap cover = null;
    private int following;
    private int follower;
    private int post;

    private ProgressDialog loading = null;
    protected EditText search_EditText = null;
    protected Button search_Button = null;
    protected Button result_Button = null;
    protected TextView result_Textview = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        search_Context = this;

        search_EditText = (EditText) findViewById(R.id.searchEdittext);
        search_Button = (Button) findViewById(R.id.searchButton);
        result_Button = (Button) findViewById(R.id.searchResultButton);
        result_Textview = (TextView) findViewById(R.id.searchResultTextView);

        CustomTypeface.getInstance().setCustom(findViewById(R.id.activity_search), CustomTypeface.getInstance().getTypeface(search_Context, "SEARCH"));

        resetResult();

        search_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                DatabaseHelper.getInstance(search_Context).removeSearch();
                SearchData.getInstance(search_Context).clearSearch();
                username = search_EditText.getText().toString().trim();
                searchFunction(username);
            }
        });

        result_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SearchDashboardActivity.class);
                startActivity(intent);
            }
        });

    }

    private void resetResult()
    {
        result_Button.setVisibility(View.GONE);
        result_Textview.setVisibility(View.GONE);
    }

    private void showResult(String res)
    {
        if(res == "NoFound")
        {
            result_Textview.setVisibility(View.VISIBLE);
            result_Textview.setText("No Result...");
        }
        else if(res == "Found")
        {
            result_Button.setVisibility(View.VISIBLE);
            Bitmap profilphoto = SearchData.getInstance(search_Context).getImageData().get(SearchData.KEY_profilImage);
            profilphoto = ImageProcess.getInstance().getCroppedBitmap(profilphoto);
            BitmapDrawable drawableLeft = new BitmapDrawable(getResources(), profilphoto);
            result_Button.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
            result_Button.setText(SearchData.getInstance(search_Context).getStringData().get(SearchData.KEY_name));
        }
        else if(res == "Error")
        {
            result_Textview.setVisibility(View.VISIBLE);
            result_Textview.setText("Search Error, Please Try Again...");
        }
    }

    private void searchFunction(final String username)
    {
        CheckConnection.deleteInstance();
        if(CheckConnection.getInstance(search_Context).isConnect()) {
            searchInBackground(username);
        }
        else
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(search_Context);
            alertDialogBuilder.setMessage("Internet Connection not working");

            alertDialogBuilder.setPositiveButton("rentry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    searchFunction(username);
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

    private void searchInBackground(final String username)
    {
        loading = ProgressDialog.show(this,"Searching...","Please wait...",false,false);
        StringRequest search = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String message = jsonObject.getString("message");
                    if(message.equals("Found User")) {
                        loading.setMessage("Get Data...");
                        getUserData(username);
                    }
                    else
                    {
                        resetResult();
                        showResult("NoFound");
                        loading.dismiss();
                        Toast.makeText(search_Context, message, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e)
                {
                    resetResult();
                    showResult("Error");
                    loading.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resetResult();
                showResult("Error");
                loading.dismiss();
                Toast.makeText(search_Context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put(KEY_Username, username);
                params.put(KEY_Request, "search");

                return params;
            }
        };

        CustomRequest.deleteInstance();
        CustomRequest.getInstance(search_Context).addToRequestQueue(search);
    }


    private void getUserData(final String username)
    {
        userProcess = 3;
        CustomRequest.deleteInstance();
        CustomRequest.getInstance(search_Context).addToRequestQueue(userdataString(username));
        CustomRequest.getInstance(search_Context).addToRequestQueue(userimageString(username));
        CustomRequest.getInstance(search_Context).addToRequestQueue(userfollowString(username));
    }

    private void responseAllData()
    {
        userProcess--;
        if(userProcess==0)
        {
            resetResult();
            SearchData.getInstance(search_Context).createData(username,name,status,about,following,follower,post,profil,cover);
            showResult("Found");
            Toast.makeText(search_Context, "User Found", Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }
    }

    // USERPROCESS1==========================================================================================
    private StringRequest userdataString(final String username)
    {
        StringRequest data = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    status = jsonObject.getString("status");
                    about = jsonObject.getString("about");
                    name = jsonObject.getString("name");
                    String profilLink = jsonObject.getString("pp");
                    String coverLink = jsonObject.getString("cp");
                    ppcpCount = 2;
                    userdataImage(profilLink,"pp");
                    userdataImage(coverLink,"cp");
                }
                catch (JSONException e)
                {
                    resetResult();
                    showResult("Error");
                    loading.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resetResult();
                showResult("Error");
                loading.dismiss();
                Toast.makeText(search_Context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put(KEY_Username, username);
                params.put(KEY_Request, "userdata");

                return params;
            }
        };

        return data;
    }

    private void userdataImage(final String link, final String ppcp)
    {
        ImageRequest imageRequest = new ImageRequest(link, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                if(ppcp.equals("pp"))
                {
                    profil = response;
                    ppcpCount--;
                }
                else
                {
                    cover=response;
                    ppcpCount--;
                }

                if(ppcpCount==0)
                {
                    responseAllData();
                }
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resetResult();
                showResult("Error");
                loading.dismiss();
                Toast.makeText(search_Context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        CustomRequest.getInstance(search_Context).addToRequestQueue(imageRequest);
    }


    // USERPROCESS2===========================================================================================
    private StringRequest userimageString(final String username)
    {
        StringRequest image = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    imageCount = jsonArray.length();
                    post = jsonArray.length();
                    for (Integer i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String image = jsonObject.getString("image");
                        System.out.print(image);
                        userimageImage(username,i,image);
                    }
                }
                catch (JSONException e)
                {
                    responseAllData();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resetResult();
                showResult("Error");
                loading.dismiss();
                Toast.makeText(search_Context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put(KEY_Username, username);
                params.put(KEY_Request, "userimage");

                return params;
            }
        };

        return image;
    }

    private void userimageImage(final String username, final int index, final String link)
    {
        ImageRequest imageRequest = new ImageRequest(link, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                DatabaseHelper.getInstance(search_Context).addSearchEntry(username, index, link, response);
                imageCount--;
                if(imageCount==0) {
                    responseAllData();
                }
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resetResult();
                showResult("Error");
                loading.dismiss();
                Toast.makeText(search_Context, error.toString() , Toast.LENGTH_LONG).show();
            }
        });

        CustomRequest.getInstance(search_Context).addToRequestQueue(imageRequest);
    }

    //USERPROCESS3=========================================================================================

    private StringRequest userfollowString(final String username)
    {
        StringRequest follow = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    following = Integer.parseInt(jsonObject.getString("following"));
                    follower = Integer.parseInt(jsonObject.getString("follower"));
                    responseAllData();
                }
                catch (JSONException e)
                {
                    resetResult();
                    showResult("Error");
                    loading.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resetResult();
                showResult("Error");
                loading.dismiss();
                Toast.makeText(search_Context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put(KEY_Username, username);
                params.put(KEY_Request, "userfollow");

                return params;
            }
        };

        return follow;
    }
}
