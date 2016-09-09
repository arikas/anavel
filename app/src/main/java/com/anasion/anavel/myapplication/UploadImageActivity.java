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
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.Map;

public class UploadImageActivity extends AppCompatActivity {

    private Context upload_Context = null;
    private StringRequest string_Request = null;
    private ImageRequest imageRequest = null;

    public static final String server_Url = "http://anasion.com/saveimage.php";
    public static final String KEY_Username = "username";
    public static final String KEY_Image = "image";

    private static int maxSize;

    protected Button upload_Button = null;
    protected Button chose_Button = null;
    protected ImageView upload_ImageView = null;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadimage);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        upload_Context = this;

        chose_Button = (Button) findViewById(R.id.chooseImageButton);
        upload_Button = (Button) findViewById(R.id.uploadImageButton);
        upload_ImageView = (ImageView) findViewById(R.id.uploadImageImageview);

        CustomTypeface.getInstance().setCustom(findViewById(R.id.activity_uploadimage), CustomTypeface.getInstance().getTypeface(upload_Context, "DASHBOARD"));

        maxSize = 512;

        chose_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        upload_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageFunction();
            }
        });
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
                upload_ImageView.setImageBitmap(bitmap);

                bitmap = getResizedBitmap(bitmap, maxSize);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(upload_Context, "Please try again", Toast.LENGTH_LONG).show();
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    private void uploadImageFunction()
    {
        CheckConnection.deleteInstance();
        if(CheckConnection.getInstance(upload_Context).isConnect())
        {
            uploadImage();
        }
        else
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(upload_Context);
            alertDialogBuilder.setMessage("Internet Connection not working");

            alertDialogBuilder.setPositiveButton("rentry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    uploadImageFunction();
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

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        string_Request = new StringRequest(Request.Method.POST, server_Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONArray jsonArray = new JSONArray(s);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            final String message = jsonObject.getString("message");
                            final String upload_Url = jsonObject.getString("actualpath");
                            final String username = jsonObject.getString("username");

                            if(message.equals("Successfully Uploaded")) {

                                imageRequest = new ImageRequest(upload_Url, new Response.Listener<Bitmap>() {
                                    @Override
                                    public void onResponse(Bitmap response) {
                                        DatabaseHelper.getInstance(upload_Context).addEntry(response,username,upload_Url);

                                        Toast.makeText(UploadImageActivity.this, message , Toast.LENGTH_LONG).show();

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
                                        Toast.makeText(UploadImageActivity.this, "Something Wrong.." , Toast.LENGTH_LONG).show();
                                    }
                                });

                                CustomRequest.deleteInstance();
                                CustomRequest.getInstance(upload_Context).addToRequestQueue(imageRequest);
                            }
                            else
                            {
                                Toast.makeText(UploadImageActivity.this, message, Toast.LENGTH_LONG).show();
                                loading.dismiss();
                            }
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
                        Toast.makeText(UploadImageActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = getStringImage(bitmap);
                String username = SessionManager.getInstance(upload_Context).getDetail().get(SessionManager.KEY_username);

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_Username, username);
                params.put(KEY_Image, image);

                //returning parameters
                return params;
            }
        };

        CustomRequest.deleteInstance();
        CustomRequest.getInstance(upload_Context).addToRequestQueue(string_Request);
    }
}
