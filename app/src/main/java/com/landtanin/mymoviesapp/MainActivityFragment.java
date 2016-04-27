package com.landtanin.mymoviesapp;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivityFragment extends Fragment {

    private MoviesAdapter moviesAdapter = null;

    public GridView gridView;

    Movies[] movies = null;

    String sortby = "popularity";

    int sortNo = 1;


    public MainActivityFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflater - inflate any views in the fragment
        //container - parent view that th fragment's UI should be attached to
        //savedInstanceState - save current state

        View rootView = inflater.inflate(R.layout.main_fragment, container, false);
        //Inflate a new view hierarchy from the specified xml resource.

        if (moviesAdapter==null) {

            updatePoster();

        }



//        moviesAdapter = new MoviesAdapter(getActivity(), Arrays.asList(movies));
//        Arrays.asList - change array to List for ListView(or equivalent)


        moviesAdapter = new MoviesAdapter(getActivity(), new ArrayList<Movies>());

        gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(moviesAdapter);

        gridClickControl();

        return rootView;

    }

    private void gridClickControl() {

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), MovieDetails.class);
                startActivity(intent);
            }
        });
    }

    private void updatePoster() {
        FetchPosterTask moviesTask = new FetchPosterTask();
        moviesTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updatePoster();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mainfragment, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.sortby_pop) {

            sortNo = 1;

        } else if (id == R.id.sortby_rate) {

            sortNo = 2;

        }

        updatePoster();
        return super.onOptionsItemSelected(item);
    }

    public class FetchPosterTask extends AsyncTask<Void, Void, String[]> {

        private final String LOG_TAG = FetchPosterTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(Void... params) {


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

//            String pop = "popularity.desc";

            try{


                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String APIKEY_PARAM = "api_key";

                if (sortNo==1) {

                    sortby = "popularity.desc";

                } else if (sortNo==2) {

                    sortby = "vote_count.desc";

                }

                Uri buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortby)
                        .appendQueryParameter(APIKEY_PARAM, BuildConfig.API_KEY)
                        .build();

//                MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_count.desc&api_key=6b1d9d7eab961a1556fc6b1cd3eccc08";

                URL url = new URL(buildUri.toString());


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }

                // reader is BurreredReader that declared above
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();

                // verbose log statement aim to verified whether the data returned is corrected
                // Log.v(LOG_TAG, "Movies JSON String: " + moviesJsonStr);


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("LOG_TAG", "Error closing stream", e);
                    }
                }
            }

            try{
                return getPosterFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {

//            for (int i = 0; i<4;i++) {
//                movies[i] = new Movies(strings[i]);
//            }

            if (strings != null) {

                movies = new Movies[strings.length];

                moviesAdapter.clear();

                for (int i = 0; i<strings.length; i++) {

                    movies[i] = new Movies(strings[i]);

                    moviesAdapter.add(movies[i]);

                }

            }


        }

        //------------------------------------------JSON---------------------------------------------------------

        private String[] getPosterFromJson(String moviesJsonStr) throws JSONException {



            final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";
            final String BASE_POSTER_SIZE = "w500"; // w92, w154, w185, w342, w500, w780 or original

            JSONObject poster = new JSONObject(moviesJsonStr);
            JSONArray resultsArray = poster.getJSONArray("results");

            int number = resultsArray.length();

            String[] resultStrs = new String[number];
            for(int i = 0; i < number-1; i++) {

                JSONObject posterInfo = resultsArray.getJSONObject(i);
                String posterPath = posterInfo.getString("poster_path");

                resultStrs[i] = BASE_POSTER_URL + BASE_POSTER_SIZE + posterPath;

            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Movie Poster: " + s);
            }


            return resultStrs;


        }

    }


}
