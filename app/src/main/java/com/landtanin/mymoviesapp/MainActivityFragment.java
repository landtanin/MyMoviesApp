package com.landtanin.mymoviesapp;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Arrays;

public class MainActivityFragment extends Fragment {

    private MoviesAdapter moviesAdapter;
    //    private ArrayAdapter<String> mMoviesAdapter;
//
//    Movies[] movies = {new Movies(R.drawable.bond),
//            new Movies(R.drawable.ironman),
//            new Movies(R.drawable.guardian),
//            new Movies(R.drawable.darknight)};
    Movies[] movies = null;




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

        FetchPosterTask moviesTask = new FetchPosterTask();
        moviesTask.execute();

        moviesAdapter = new MoviesAdapter(getActivity(), Arrays.asList(movies));

        // Arrays.asList - change array to List for ListView(or equivalent)

        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(moviesAdapter);


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mainfragment, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public class FetchPosterTask extends AsyncTask<Void, Void, String[]> {

        private final String LOG_TAG = FetchPosterTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

            try{

                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=6b1d9d7eab961a1556fc6b1cd3eccc08";

                URL url = new URL(MOVIE_BASE_URL);


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

//            movies = new Movies[strings.length];
            movies = new Movies[4];
            for (int i = 0; i<4;i++) {
                movies[i] = new Movies(strings[i]);
            }

        }

        //------------------------------------------JSON---------------------------------------------------------

        private String[] getPosterFromJson(String moviesJsonStr) throws JSONException {



            final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";
            final String BASE_POSTER_SIZE = "w92";

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
