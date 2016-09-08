package com.anasion.anavel.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangeUserDataActivity extends AppCompatActivity {

    private Context changeUD_Context = null;

    public static final String server_Url = "http://anasion.com/profildatachange.php";
    public static final String KEY_Username = "username";
    public static final String KEY_About = "about";
    public static final String KEY_Status = "status";

    protected TextView changeUD_Editprofile_Textview = null;
    protected EditText changeUD_About_Edittext = null;
    protected EditText changeUD_Status_Edittext = null;
    protected Button changeUD_Submit_Button = null;

    private RequestQueue request_Queue = null;
    private StringRequest string_Request = null;

    private String about = null;
    private String status = null;
    private String username = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeuserdata);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        changeUD_Context = this;

        changeUD_Editprofile_Textview = (TextView) findViewById(R.id.changeUDCreateTextview);
        changeUD_About_Edittext = (EditText) findViewById(R.id.changeUDAboutEdittext);
        changeUD_Status_Edittext = (EditText) findViewById(R.id.changeUDStatusEdittext);
        changeUD_Submit_Button = (Button) findViewById(R.id.changeUDSubmitButton);

        changeUD_About_Edittext.setHint(SessionManager.getInstance(changeUD_Context).getDetail().get(SessionManager.KEY_about));
        changeUD_Status_Edittext.setHint(SessionManager.getInstance(changeUD_Context).getDetail().get(SessionManager.KEY_status));

        username = SessionManager.getInstance(changeUD_Context).getDetail().get(SessionManager.KEY_username);

        CustomTypeface.getInstance().setCustom(findViewById(R.id.activity_changeuserdata), CustomTypeface.getInstance().getTypeface(changeUD_Context, "DASHBOARD"));

        changeUD_Submit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(changeUD_Submit_Button.getWindowToken(), 0);

                about = changeUD_About_Edittext.getText().toString().trim();
                status = changeUD_Status_Edittext.getText().toString().trim();

                if( about.length()==0 && status.length()==0)
                {
                    Toast.makeText(changeUD_Context, "Please fill field", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    changeUDFunction();
                }
            }
        });
    }

    private void changeUDFunction()
    {
        CheckConnection.deleteInstance();
        if(CheckConnection.getInstance(changeUD_Context).isConnect()) {
            changeUDInBackground(about, status);
        }
        else
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(changeUD_Context);
            alertDialogBuilder.setMessage("Internet Connection not working");

            alertDialogBuilder.setPositiveButton("rentry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    changeUDFunction();
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

    private void changeUDInBackground(final String about, final String status)
    {
        final ProgressDialog loading = ProgressDialog.show(this,"Updating...","Please wait...",false,false);
        request_Queue = Volley.newRequestQueue(changeUD_Context);
        string_Request = new StringRequest(Request.Method.POST, server_Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String message = jsonObject.getString("message");
                    if (message.equals("Successfully Changed")) {
                        String aboutData = jsonObject.getString("about");
                        String statusData = jsonObject.getString("status");

                        SessionManager.getInstance(changeUD_Context).updateDataSession(SessionManager.KEY_about, aboutData);
                        SessionManager.getInstance(changeUD_Context).updateDataSession(SessionManager.KEY_status, statusData);

                        Toast.makeText(changeUD_Context, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        loading.dismiss();
                    }
                    else {
                        loading.dismiss();
                        Toast.makeText(changeUD_Context, message, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(changeUD_Context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put(KEY_Username, username);
                params.put(KEY_About, about);
                params.put(KEY_Status, status);

                return params;
            }
        };

        CustomRequest.deleteInstance();
        CustomRequest.getInstance(changeUD_Context).addToRequestQueue(string_Request);
    }
}
