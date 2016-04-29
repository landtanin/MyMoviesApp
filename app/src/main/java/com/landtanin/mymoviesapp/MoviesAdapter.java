package com.landtanin.mymoviesapp;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by landtanin on 4/14/16 AD.
 */
public class MoviesAdapter extends ArrayAdapter<Movies> {

    public MoviesAdapter(Activity context, List<Movies> movies) {
        super(context,0, movies);
    }

    @Nullable
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //int: The position of the item within the adapter's data set of the item whose view we want.
        //View: the old view to reuse
        //ViewGroup: the parent that this view will eventually be attached to


        Movies movies = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.single_item, parent, false);
                     //Inflate a new view hierarchy from the specified xml resource.

        }

        ImageView posterView = (ImageView) convertView.findViewById(R.id.poster_image);
//        posterView.setImageResource(movies.image);
        Picasso.with(getContext()).load(String.valueOf(movies.poster)).into(posterView);

        return convertView;
    }

    @Override
    public void add(Movies object) {
        super.add(object);
    }

    @Override
    public Movies getItem(int position) {
        return super.getItem(position);
    }
}
