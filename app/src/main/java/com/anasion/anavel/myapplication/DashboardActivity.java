package com.anasion.anavel.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private Context dashboard_Context = null;

    protected Button dashboard_Logout_Button = null;
    protected ImageView dashboard_Cover_Imageview = null;
    protected ImageView dashboard_Profilimage_Imageview = null;
    protected TextView dashboard_Name_Textview = null;
    protected TextView dashboard_Address_Textview = null;
    protected TextView dashboard_Status_Textview = null;
    protected Button dashboard_Editprofile_Button = null;
    protected TextView dashboard_Post_Textview = null;
    protected TextView dashboard_Follower_Textview = null;
    protected TextView dashboard_Following_Textview = null;
    protected ExpandableHeightGridView gridView = null;
    protected ScrollView dashboard_Scroller = null;
    protected ImageView dashboard_Upload = null;
    protected ImageView dashboard_Home = null;
    protected ImageView dashboard_Account = null;
    protected ImageView dashboard_Fav = null;
    protected ImageView dashboard_Search = null;

    ExpandableHeightGridView gridview;

    private ArrayList<Beanclass> beans;
    private GridviewAdapter gridviewAdapter;
    private List<Beanclass> imageCollection = new ArrayList<Beanclass>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        dashboard_Context = this;

        dashboard_Logout_Button = (Button) findViewById(R.id.dashboardLogoutButton);
        dashboard_Cover_Imageview = (ImageView) findViewById(R.id.dashboardCoverimageImageview);
        dashboard_Profilimage_Imageview = (ImageView) findViewById(R.id.dashboardProfilimageImageview);
        dashboard_Name_Textview = (TextView) findViewById(R.id.dashboardNameTextview);
        dashboard_Address_Textview = (TextView) findViewById(R.id.dashboardAdressTextview);
        dashboard_Status_Textview = (TextView) findViewById(R.id.dashboardStatusTextview);
        dashboard_Editprofile_Button = (Button) findViewById(R.id.dashboardEditprofileButton);
        dashboard_Post_Textview = (TextView) findViewById(R.id.dashboardPostTextview);
        dashboard_Follower_Textview = (TextView) findViewById(R.id.dashboardFollowerTextview);
        dashboard_Following_Textview = (TextView) findViewById(R.id.dashboardFollowingTextview);
        dashboard_Scroller = (ScrollView) findViewById(R.id.dashboardScroller);
        dashboard_Upload = (ImageView) findViewById(R.id.dashboardUpload);
        dashboard_Home = (ImageView) findViewById(R.id.dashboardHome);
        dashboard_Account = (ImageView) findViewById(R.id.dashboardAccount);
        dashboard_Fav = (ImageView) findViewById(R.id.dashboardFav);
        dashboard_Search = (ImageView) findViewById(R.id.dashboardSearch);
        gridview = (ExpandableHeightGridView) findViewById(R.id.gridview);

        dashboard_Post_Textview.setText(String.valueOf(DatabaseHelper.getInstance(dashboard_Context).getPostCount(SessionManager.getInstance(dashboard_Context).getDetail().get(SessionManager.KEY_username))));
        dashboard_Follower_Textview.setText(String.valueOf(DatabaseHelper.getInstance(dashboard_Context).getFollowerCount(SessionManager.getInstance(dashboard_Context).getDetail().get(SessionManager.KEY_username))));
        dashboard_Following_Textview.setText(String.valueOf(DatabaseHelper.getInstance(dashboard_Context).getFollowingCount(SessionManager.getInstance(dashboard_Context).getDetail().get(SessionManager.KEY_username))));

        CustomTypeface.getInstance().setCustom(findViewById(R.id.activity_dashboard), CustomTypeface.getInstance().getTypeface(dashboard_Context, "DASHBOARD"));

        dashboard_Name_Textview.setText(SessionManager.getInstance(dashboard_Context).getDetail().get(SessionManager.KEY_name));
        dashboard_Address_Textview.setText(SessionManager.getInstance(dashboard_Context).getDetail().get(SessionManager.KEY_about));
        dashboard_Status_Textview.setText(SessionManager.getInstance(dashboard_Context).getDetail().get(SessionManager.KEY_status));


        imageCollection = DatabaseHelper.getInstance(dashboard_Context).getEntry(SessionManager.getInstance(dashboard_Context).getDetail().get(SessionManager.KEY_username));

        dashboard_Scroller.setFocusable(false);
        gridview.setFocusable(false);

        dashboard_Editprofile_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ChangeUserDataActivity.class);
                startActivity(intent);
            }
        });

        dashboard_Logout_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager.getInstance(dashboard_Context).logoutUser();
                DatabaseHelper.getInstance(dashboard_Context).removeAll();
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        dashboard_Profilimage_Imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ChangeUserImageActivity.class);
                intent.putExtra("request", "pp");
                startActivity(intent);
            }
        });

        dashboard_Cover_Imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ChangeUserImageActivity.class);
                intent.putExtra("request", "cp");
                startActivity(intent);
            }
        });

        dashboard_Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dashboard_Account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dashboard_Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), UploadImageActivity.class);
                startActivity(intent);
            }
        });

        dashboard_Fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dashboard_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        Bitmap bitmap= BitmapFactory.decodeResource(this.getResources(),
                R.drawable.defaultpp);

        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);
        paint.setAntiAlias(true);
        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);

        dashboard_Profilimage_Imageview.setImageBitmap(circleBitmap);

        dashboard_Profilimage_Imageview.setImageBitmap(SessionManager.getInstance(dashboard_Context).getImage().get(SessionManager.KEY_profilImage));
        dashboard_Cover_Imageview.setImageBitmap(SessionManager.getInstance(dashboard_Context).getImage().get(SessionManager.KEY_coverimage));

        gridview = (com.anasion.anavel.myapplication.ExpandableHeightGridView)findViewById(R.id.gridview);
        beans= new ArrayList<Beanclass>();

        for (int i= 0; i< imageCollection.size(); i++) {

            beans.add(imageCollection.get(i));
        }
        gridviewAdapter = new com.anasion.anavel.myapplication.GridviewAdapter(DashboardActivity.this, beans);
        gridview.setExpanded(true);

        gridview.setAdapter(gridviewAdapter);

    }

}
