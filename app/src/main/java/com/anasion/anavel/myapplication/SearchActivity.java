package com.anasion.anavel.myapplication;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SearchActivity extends AppCompatActivity {

    Context search_Context = null;

    protected EditText search_EditText = null;
    protected Button search_Button = null;
    protected Button result_Button = null;
    protected TextView result_Textview = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search_Context = this;

        search_EditText = (EditText) findViewById(R.id.searchEdittext);
        search_Button = (Button) findViewById(R.id.searchButton);
        result_Button = (Button) findViewById(R.id.searchResultButton);
        result_Textview = (TextView) findViewById(R.id.searchResultTextView);



        BitmapDrawable drawableLeft = new BitmapDrawable(getResources(), SessionManager.getInstance(search_Context).getImage().get(SessionManager.KEY_profilImage));
        result_Button.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
    }
}
