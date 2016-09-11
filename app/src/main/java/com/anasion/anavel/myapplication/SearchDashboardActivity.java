package com.anasion.anavel.myapplication;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchDashboardActivity extends AppCompatActivity {

    private Context searchDashboard_Context = null;

    protected ImageView search_Dashboard_Cover_ImageView = null;
    protected ImageView search_Dashboard_Profil_ImageView = null;
    protected TextView search_Dashboard_Name_TextView = null;
    protected TextView search_Dashboard_About_TextView = null;
    protected TextView search_Dashboard_Status_TextView = null;
    protected TextView search_Dashboard_Post_TextView = null;
    protected TextView search_Dashboard_Follower_TextView = null;
    protected TextView search_Dashboard_Following_TextView = null;
    protected ExpandableHeightGridView search_GridView = null;
    protected ScrollView search_Dashboard_Scroller = null;

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
        search_GridView = (ExpandableHeightGridView) findViewById(R.id.searchgridview);
        search_Dashboard_Scroller = (ScrollView) findViewById(R.id.dashboardSearchSearchScroller);

        CustomTypeface.getInstance().setCustom(findViewById(R.id.activity_searchdashboard), CustomTypeface.getInstance().getTypeface(searchDashboard_Context, "SEARCHDASHBOARD"));

        search_Dashboard_Name_TextView.setText(SearchData.getInstance(searchDashboard_Context).getStringData().get(SearchData.KEY_name));
        search_Dashboard_About_TextView.setText(SearchData.getInstance(searchDashboard_Context).getStringData().get(SearchData.KEY_about));
        search_Dashboard_Status_TextView.setText(SearchData.getInstance(searchDashboard_Context).getStringData().get(SearchData.KEY_status));
        search_Dashboard_Profil_ImageView.setImageBitmap(SearchData.getInstance(searchDashboard_Context).getImageData().get(SearchData.KEY_profilImage));
        search_Dashboard_Cover_ImageView.setImageBitmap(SearchData.getInstance(searchDashboard_Context).getImageData().get(SearchData.KEY_coverimage));
        search_Dashboard_Post_TextView.setText(SearchData.getInstance(searchDashboard_Context).getStringData().get(SearchData.KEY_postCount));
        search_Dashboard_Following_TextView.setText(SearchData.getInstance(searchDashboard_Context).getStringData().get(SearchData.KEY_followingCount));
        search_Dashboard_Follower_TextView.setText(SearchData.getInstance(searchDashboard_Context).getStringData().get(SearchData.KEY_followerCount));

        search_Dashboard_Scroller.setFocusable(false);
        search_GridView.setFocusable(false);

        imageCollection = DatabaseHelper.getInstance(searchDashboard_Context).getSearchEntry(SearchData.getInstance(searchDashboard_Context).getStringData().get(SearchData.KEY_username));
        search_GridView = (com.anasion.anavel.myapplication.ExpandableHeightGridView)findViewById(R.id.searchgridview);
        if(imageCollection.size()>0) {
            gridviewAdapter = new com.anasion.anavel.myapplication.GridviewAdapter(SearchDashboardActivity.this, imageCollection,"searchDashboard");
            search_GridView.setExpanded(true);

            search_GridView.setAdapter(gridviewAdapter);
        }

    }
}
