package com.landtanin.mymoviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MovieDetails extends AppCompatActivity {

    public TextView textView;
    public int moviePosition;
    public String showNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        textView = (TextView) findViewById(R.id.textView);

        moviePosition = getIntent().getExtras().getInt(Intent.EXTRA_REFERRER_NAME);

        showNo = Integer.toString(moviePosition);

        textView.setText(showNo);

    }
}
