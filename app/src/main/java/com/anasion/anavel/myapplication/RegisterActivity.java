package com.anasion.anavel.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    static final int DATE_DIALOG_ID = 999;

    public static final String server_Url = "http://anasion.com/register.php";
    public static final String KEY_Name = "name";
    public static final String KEY_Birthdate = "birthdate";
    public static final String KEY_Username = "username";
    public static final String KEY_Password = "password";
    public static final String KEY_Email = "email";

    private Context register_Context = null;
    private RequestQueue request_Queue = null;
    private StringRequest string_Request = null;

    protected EditText register_Name_Edittext = null;
    protected EditText register_Birthdate_Edittext = null;
    protected EditText register_Username_Edittext = null;
    protected EditText register_Email_Edittext = null;
    protected EditText register_Pass_Edittext = null;
    protected TextView register_Terms_Textview = null;
    protected Button register_Continue_Button = null;

    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;

    private String name = null;
    private String birthdate =null;
    private  String email = null;
    private String username= null;
    private String password=null;

    private void showDate(int year, int month, int day) {
        register_Birthdate_Edittext.setHint(new StringBuilder().append(year).append("-").append(month).append("-").append(day));
    }

    private void setDate(int year, int month, int day)
    {
        register_Birthdate_Edittext.setText(new StringBuilder().append(year).append("-").append(month).append("-").append(day));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener,
                        year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            setDate(year, month+1,day);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        register_Context = this;

        register_Name_Edittext = (EditText) findViewById(R.id.registerNameEdittext);
        register_Birthdate_Edittext =(EditText) findViewById(R.id.registerBirthdateEdittext);
        register_Username_Edittext = (EditText) findViewById(R.id.registerUsernameEdittext);
        register_Email_Edittext = (EditText) findViewById(R.id.registerEmailEdittext);
        register_Pass_Edittext = (EditText) findViewById(R.id.registerPassEdittext);
        register_Terms_Textview = (TextView) findViewById(R.id.registerTermsTextview);
        register_Continue_Button = (Button) findViewById(R.id.registerContinueButton);

        CustomTypeface.getInstance().setCustom(findViewById(R.id.activity_register), CustomTypeface.getInstance().getTypeface(register_Context, "REGISTER"));

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);

        register_Birthdate_Edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        register_Terms_Textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        register_Continue_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = register_Name_Edittext.getText().toString().trim();
                birthdate = register_Birthdate_Edittext.getText().toString().trim();
                username = register_Username_Edittext.getText().toString().trim();
                email = register_Email_Edittext.getText().toString().trim();
                password = register_Pass_Edittext.getText().toString().trim();

                if( name.length()==0 || birthdate.length()==0 || username.length()==0 || email.length()==0 || password.length()==0)
                {
                    Toast.makeText(register_Context, "Please fill field", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    registerFunction();
                }
            }
        });
    }

    private void registerFunction()
    {
        CheckConnection.deleteInstance();
        if(CheckConnection.getInstance(register_Context).isConnect()) {
            RegisterInBackground(name,birthdate,username,email,password);
        }
        else
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(register_Context);
            alertDialogBuilder.setMessage("Internet Connection not working");

            alertDialogBuilder.setPositiveButton("rentry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    registerFunction();
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

    private void RegisterInBackground(final String name, final String birthdate, final String username, final String email, final String password)
    {
        final ProgressDialog loading = ProgressDialog.show(this,"Add data...","Please wait...",false,false);
        request_Queue = Volley.newRequestQueue(register_Context);
        string_Request = new StringRequest(Request.Method.POST, server_Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if(s.equals("Thank you for register")) {
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Toast.makeText(register_Context, s, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                    loading.dismiss();
                }
                else
                {
                    loading.dismiss();
                    Toast.makeText(register_Context, s, Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                loading.dismiss();
                Toast.makeText(register_Context, volleyError.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put(KEY_Name, name);
                params.put(KEY_Birthdate, birthdate);
                params.put(KEY_Username, username);
                params.put(KEY_Email, email);
                params.put(KEY_Password, password);

                return params;
            }
        };

        CustomRequest.deleteInstance();
        CustomRequest.getInstance(register_Context).addToRequestQueue(string_Request);
        //request_Queue.add(string_Request);
    }
}
