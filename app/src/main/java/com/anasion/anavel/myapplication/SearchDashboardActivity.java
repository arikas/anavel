package com.anasion.anavel.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchDashboardActivity extends AppCompatActivity {

    private Context searchDashboard_Context = null;
    public static final String url = "http://anasion.com/addfollow.php";
    public static final String KEY_Username = "username";
    public static final String KEY_Following = "following";
    public static final String KEY_Request = "request";

    protected ImageView search_Dashboard_Cover_ImageView = null;
    protected ImageView search_Dashboard_Profil_ImageView = null;
    protected TextView search_Dashboard_Name_TextView = null;
    protected TextView search_Dashboard_About_TextView = null;
    protected TextView search_Dashboard_Status_TextView = null;
    protected TextView search_Dashboard_Post_TextView = null;
    protected TextView search_Dashboard_Follower_TextView = null;
    protected TextView search_Dashboard_Following_TextView = null;
    protected TextView search_Dashboard_Follow_Textview = null;
    protected ExpandableHeightGridView search_GridView = null;
    protected ScrollView search_Dashboard_Scroller = null;
    protected LinearLayout search_Dashboard_Upload = null;
    protected LinearLayout search_Dashboard_Account = null;
    protected LinearLayout search_Dashboard_Search = null;

    private int userFollowed;
    protected ProgressDialog loading = null;

    private GridviewAdapter gridviewAdapter;
    private ArrayList<Beanclass> imageCollection = new ArrayList<Beanclass>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchdashboard);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        searchDashboard_Context = this;
        search_Dashboard_Profil_ImageView = (ImageView) findViewById(R.id.dashboardSearchProfilimageImageview);
        search_Dashboard_Cover_ImageView = (ImageView) findViewById(R.id.dashboardSearchCoverimageImageview);
        search_Dashboard_Name_TextView = (TextView) findViewById(R.id.dashboardSearchNameTextview);
        search_Dashboard_About_TextView = (TextView) findViewById(R.id.dashboardSearchAboutTextview);
        search_Dashboard_Status_TextView = (TextView) findViewById(R.id.dashboardSearchStatusTextview);
        search_Dashboard_Post_TextView = (TextView) findViewById(R.id.dashboardSearchPostTextview);
        search_Dashboard_Follower_TextView = (TextView) findViewById(R.id.dashboardSearchFollowerTextview);
        search_Dashboard_Following_TextView = (TextView) findViewById(R.id.dashboardSearchFollowingTextview);
        search_Dashboard_Follow_Textview = (TextView) findViewById(R.id.dashboardSearchFollowButton);
        search_GridView = (ExpandableHeightGridView) findViewById(R.id.searchgridview);
        search_Dashboard_Scroller = (ScrollView) findViewById(R.id.dashboardSearchSearchScroller);
        search_Dashboard_Upload = (LinearLayout) findViewById(R.id.searchDashboardUpload);
        search_Dashboard_Account = (LinearLayout) findViewById(R.id.searchDashboardAccount);
        search_Dashboard_Search = (LinearLayout) findViewById(R.id.searchDashboardSearch);

        CustomTypeface.getInstance().setCustom(findViewById(R.id.activity_searchdashboard), CustomTypeface.getInstance().getTypeface(searchDashboard_Context, "SEARCHDASHBOARD"));

        search_Dashboard_Name_TextView.setText(SearchData.getInstance(searchDashboard_Context).getStringData().get(SearchData.KEY_name));
        search_Dashboard_About_TextView.setText(SearchData.getInstance(searchDashboard_Context).getStringData().get(SearchData.KEY_about));
        search_Dashboard_Status_TextView.setText(SearchData.getInstance(searchDashboard_Context).getStringData().get(SearchData.KEY_status));
        search_Dashboard_Profil_ImageView.setImageBitmap(SearchData.getInstance(searchDashboard_Context).getImageData().get(SearchData.KEY_profilImage));
        search_Dashboard_Cover_ImageView.setImageBitmap(SearchData.getInstance(searchDashboard_Context).getImageData().get(SearchData.KEY_coverimage));
        search_Dashboard_Post_TextView.setText(SearchData.getInstance(searchDashboard_Context).getStringData().get(SearchData.KEY_postCount));
        search_Dashboard_Following_TextView.setText(SearchData.getInstance(searchDashboard_Context).getStringData().get(SearchData.KEY_followingCount));
        search_Dashboard_Follower_TextView.setText(SearchData.getInstance(searchDashboard_Context).getStringData().get(SearchData.KEY_followerCount));

        userFollowed = Integer.parseInt(SearchData.getInstance(searchDashboard_Context).getStringData().get(SearchData.KEY_followerCount));
        search_Dashboard_Scroller.setFocusable(false);
        search_GridView.setFocusable(false);

        imageCollection = DatabaseHelper.getInstance(searchDashboard_Context).getSearchEntry(SearchData.getInstance(searchDashboard_Context).getStringData().get(SearchData.KEY_username));
        search_GridView = (com.anasion.anavel.myapplication.ExpandableHeightGridView)findViewById(R.id.searchgridview);

        if(DatabaseHelper.getInstance(searchDashboard_Context).isFollow(
                SessionManager.getInstance(searchDashboard_Context).getDetail().get(SessionManager.KEY_username),
                SearchData.getInstance(searchDashboard_Context).getStringData().get(SearchData.KEY_username)
        ))
        {
            setFollow("FOLLOWED");
        }
        else {
            setFollow("FOLLOWING");
        }

        if(imageCollection.size()>0) {
            gridviewAdapter = new com.anasion.anavel.myapplication.GridviewAdapter(SearchDashboardActivity.this, imageCollection,"searchDashboard");
            search_GridView.setExpanded(true);
            search_GridView.setAdapter(gridviewAdapter);
        }

        search_Dashboard_Follow_Textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DatabaseHelper.getInstance(searchDashboard_Context).isFollow(
                        SessionManager.getInstance(searchDashboard_Context).getDetail().get(SessionManager.KEY_username),
                        SearchData.getInstance(searchDashboard_Context).getStringData().get(SearchData.KEY_username)
                ))
                {
                    unFollow(
                            SessionManager.getInstance(searchDashboard_Context).getDetail().get(SessionManager.KEY_username),
                            SearchData.getInstance(searchDashboard_Context).getStringData().get(SearchData.KEY_username)
                    );
                }
                else{
                    addFollow(
                            SessionManager.getInstance(searchDashboard_Context).getDetail().get(SessionManager.KEY_username),
                            SearchData.getInstance(searchDashboard_Context).getStringData().get(SearchData.KEY_username)
                    );
                }
            }
        });

        search_Dashboard_Account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
                startActivity(intent);
            }
        });

        search_Dashboard_Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), UploadImageActivity.class);
                startActivity(intent);
            }
        });

        search_Dashboard_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setFollow(String request)
    {
        if(request.equals("FOLLOWING"))
        {
            search_Dashboard_Follow_Textview.setText("FOLLOWING");
            search_Dashboard_Follow_Textview.setBackgroundResource(R.drawable.textviewfollowing);
        }
        else
        {
            search_Dashboard_Follow_Textview.setText("FOLLOWED");
            search_Dashboard_Follow_Textview.setBackgroundResource(R.drawable.textviewfollowed);
        }
    }

    private void addFollow(final String username, final String following)
    {
        loading = ProgressDialog.show(this,"Following","Please wait...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    DatabaseHelper.getInstance(searchDashboard_Context).addFollowEntry(username,following);
                    userFollowed+=1;
                    SearchData.getInstance(searchDashboard_Context).updateFollower(userFollowed);
                    search_Dashboard_Follower_TextView.setText(Integer.toString(userFollowed));
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String message = jsonObject.getString("message");
                    loading.dismiss();
                    Toast.makeText(searchDashboard_Context, message, Toast.LENGTH_SHORT).show();
                    setFollow("FOLLOWED");
                }
                catch (JSONException e)
                {
                    loading.dismiss();
                    Toast.makeText(searchDashboard_Context, "Request Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(searchDashboard_Context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put(KEY_Request, "add");
                params.put(KEY_Username, username);
                params.put(KEY_Following, following);

                return params;
            }
        };

        CustomRequest.deleteInstance();
        CustomRequest.getInstance(searchDashboard_Context).addToRequestQueue(stringRequest);
    }

    private void unFollow(final String username, final String following)
    {
        loading = ProgressDialog.show(this,"Unfollow","Please wait...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    DatabaseHelper.getInstance(searchDashboard_Context).deleteFollowEntry(username,following);
                    userFollowed-=1;
                    SearchData.getInstance(searchDashboard_Context).updateFollower(userFollowed);
                    search_Dashboard_Follower_TextView.setText(Integer.toString(userFollowed));
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String message = jsonObject.getString("message");
                    loading.dismiss();
                    Toast.makeText(searchDashboard_Context, message, Toast.LENGTH_SHORT).show();
                    setFollow("FOLLOWING");
                }
                catch (JSONException e)
                {
                    loading.dismiss();
                    Toast.makeText(searchDashboard_Context, "Request Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(searchDashboard_Context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put(KEY_Request, "delete");
                params.put(KEY_Username, username);
                params.put(KEY_Following, following);

                return params;
            }
        };

        CustomRequest.deleteInstance();
        CustomRequest.getInstance(searchDashboard_Context).addToRequestQueue(stringRequest);
    }

}
