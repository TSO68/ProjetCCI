package com.example.projetcci;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.projetcci.Constants.BASE_URL;
import static com.example.projetcci.Constants.API_KEY;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView moviesView;
    private MoviesAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    private List<Movie> moviesList;
    private List<Genre> genresList;

    private int currentPage = 1;
    private boolean search=false;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        moviesView = (RecyclerView) findViewById(R.id.movies_list);
        moviesList = new ArrayList<>();
        load_movies(currentPage);
        gridLayoutManager = new GridLayoutManager(this,1);
        moviesView.setLayoutManager(gridLayoutManager);
        adapter = new MoviesAdapter(moviesList);
        moviesView.setAdapter(adapter);

    }

    public String getLocale() {
        String countryCode = Locale.getDefault().getCountry();
        String languageCode = Locale.getDefault().getLanguage();
        String localeCode = languageCode + "-" + countryCode;

        return localeCode;
    }

    private void load_movies(int id) {

        AsyncTask<Integer, Void, Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(BASE_URL + "/discover/movie?api_key=" + API_KEY + "&language=" + getLocale() + "&sort_by=popularity.desc&include_adult=false&include_video=false&page=" + integers[0])
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    JSONObject root = new JSONObject(response.body().string());
                    JSONArray array = root.getJSONArray("results");

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);
                        Movie data = new Movie(object.getInt("id"), object.getString("title"),
                                object.getString("overview"), object.getString("poster_path"),
                                object.getString("backdrop_path"), 0, object.getDouble("vote_average"),
                                object.getString("release_date"), 0, 0, 0);
                        if (TextUtils.isEmpty(data.getOverview())) {
                            data.setOverview("Pas de description disponible");
                        }
                        moviesList.add(data);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    System.out.println("End of content");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                adapter.notifyDataSetChanged();
            }
        };

        task.execute(id);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
