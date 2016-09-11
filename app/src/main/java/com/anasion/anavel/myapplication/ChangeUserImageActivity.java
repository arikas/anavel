package com.anasion.anavel.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.Hashtable;
import java.util.Map;

public class ChangeUserImageActivity extends AppCompatActivity implements View.OnClickListener
{
    private Context changePP_Context = null;
    private StringRequest string_Request = null;
    private ImageRequest imageRequest = null;

    public static final String server_Url = "http://anasion.com/profilimagechange.php";
    public static final String KEY_ProfilImage = "profilimage";
    public static final String KEY_Username = "username";
    public static final String KEY_Password = "password";
    public static final String KEY_Request = "request";

    private static int maxSize;

    protected Button changePP_ChoosePP_Button = null;
    protected Button changePP_ChangePP_Button = null;
    protected ImageView changePP_ProfilImage_ImageView = null;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;

    private String request = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeuserimage);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        request = getIntent().getExtras().getString(KEY_Request);

        changePP_Context = this;

        changePP_ChoosePP_Button = (Button) findViewById(R.id.chooseProfilImageButton);
        changePP_ChangePP_Button = (Button) findViewById(R.id.changeProfilImageButton);
        changePP_ProfilImage_ImageView = (ImageView) findViewById(R.id.changeProfilImageImageview);

        CustomTypeface.getInstance().setCustom(findViewById(R.id.activity_changeprofilimage), CustomTypeface.getInstance().getTypeface(changePP_Context, "DASHBOARD"));

        if(request.equals("pp"))
        {
            changePP_ChangePP_Button.setText("CHANGE PROFIL IMAGE");
            maxSize = 128;
        }
        else
        {
            changePP_ChangePP_Button.setText("CHANGE COVER IMAGE");
            maxSize = 640;
        }

        changePP_ChoosePP_Button.setOnClickListener(this);
        changePP_ChangePP_Button.setOnClickListener(this);
    }

    private void uploadImage(){
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        string_Request = new StringRequest(Request.Method.POST, server_Url,
                new Response.Listener<String>() {
                    public String msg;
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONArray jsonArray = new JSONArray(s);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String message = jsonObject.getString("message");
                            msg = message;
                            int id;
                            if(request.equals("pp"))
                            {
                                id = jsonObject.getInt("id") + 10000;
                            }
                            else
                            {
                                id = jsonObject.getInt("id") + 20000;
                            }
                            String ppLink = String.valueOf(id);

                            String upload_Url = "http://anasion.com/upload/"+ppLink+".png";

                            imageRequest = new ImageRequest(upload_Url, new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap response) {

                                    if(request.equals("pp"))
                                    {
                                        SessionManager.getInstance(changePP_Context).saveProfilImage(response);
                                    }
                                    else
                                    {
                                        SessionManager.getInstance(changePP_Context).saveCoverImage(response);
                                    }

                                    Toast.makeText(ChangeUserImageActivity.this, msg , Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                    loading.dismiss();

                                }
                            }, 0, 0, null, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    loading.dismiss();
                                    Toast.makeText(ChangeUserImageActivity.this, "Something Wrong.." , Toast.LENGTH_LONG).show();
                                }
                            });

                            CustomRequest.deleteInstance();
                            CustomRequest.getInstance(changePP_Context).addToRequestQueue(imageRequest);

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        loading.dismiss();
                        Toast.makeText(ChangeUserImageActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = ImageProcess.getInstance().encodeTobase64(bitmap);
                String username = SessionManager.getInstance(changePP_Context).getDetail().get(SessionManager.KEY_username);
                String password = SessionManager.getInstance(changePP_Context).getDetail().get(SessionManager.KEY_password);

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_Request, request);
                params.put(KEY_ProfilImage, image);
                params.put(KEY_Username, username);
                params.put(KEY_Password, password);

                //returning parameters
                return params;
            }
        };

        CustomRequest.deleteInstance();
        CustomRequest.getInstance(changePP_Context).addToRequestQueue(string_Request);
    }


    private void showFileChooser() {
        Intent intent = new Intent();
        intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri URI = data.getData();
                String[] FILE = {MediaStore.Images.Media.DATA};


                Cursor cursor = getContentResolver().query(URI,
                        FILE, null, null, null);

                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(FILE[0]);
                String ImageDecode = cursor.getString(columnIndex);
                cursor.close();

                bitmap = BitmapFactory.decodeFile(ImageDecode);
                changePP_ProfilImage_ImageView.setImageBitmap(bitmap);

                bitmap = ImageProcess.getInstance().getResizedBitmap(bitmap, maxSize);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(changePP_Context, "Please try again", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onClick(View v) {

        if(v == changePP_ChoosePP_Button){
            showFileChooser();
        }

        if(v == changePP_ChangePP_Button){
            changeImageFunction();
        }
    }

    private void changeImageFunction()
    {
        CheckConnection.deleteInstance();
        if(CheckConnection.getInstance(changePP_Context).isConnect())
        {
            uploadImage();
        }
        else
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(changePP_Context);
            alertDialogBuilder.setMessage("Internet Connection not working");

            alertDialogBuilder.setPositiveButton("rentry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    changeImageFunction();
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
}
