package com.landtanin.mymoviesapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {

    public TextView title,synopsis,rating,releaseDate;
    public ImageView posterThumbnail;
//    public int moviePosition;
    public String titleStr,synopsisStr,ratingStr,releaseDateStr,posterStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        bindWidget();

        getMovieIntent();

        setLayoutWidget();

    }

    private void setLayoutWidget() {

        title.setText(titleStr + "\n");
        synopsis.setText("\nSynopsis : "+ synopsisStr);
        rating.setText("\nUser Rating : " + ratingStr);
        releaseDate.setText("\nRelease Date : " + releaseDateStr);
        Picasso.with(getBaseContext()).load(posterStr).into(posterThumbnail);

    }

    private void getMovieIntent() {

        titleStr = getIntent().getExtras().getString("title");
        synopsisStr = getIntent().getExtras().getString("synopsis");
        ratingStr = getIntent().getExtras().getString("rating");
        releaseDateStr = getIntent().getExtras().getString("releaseDate");
        posterStr = getIntent().getExtras().getString("poster");

    }

    private void bindWidget() {

        title = (TextView) findViewById(R.id.title);
        synopsis = (TextView) findViewById(R.id.synopsis);
        rating = (TextView) findViewById(R.id.rating);
        releaseDate = (TextView) findViewById(R.id.releaseDate);
        posterThumbnail = (ImageView) findViewById(R.id.posterView);

    }



}
