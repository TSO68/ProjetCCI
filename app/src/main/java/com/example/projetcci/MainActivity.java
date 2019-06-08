package com.example.projetcci;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

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

    private int currentPage = 1;
    private boolean movieSearch = false;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        moviesView = (RecyclerView) findViewById(R.id.movies_list);
        moviesList = new ArrayList<>();
        load_movies(currentPage);
        gridLayoutManager = new GridLayoutManager(this,2);
        moviesView.setLayoutManager(gridLayoutManager);
        adapter = new MoviesAdapter(this, moviesList);
        moviesView.setAdapter(adapter);

        final SearchView searchMovies = findViewById(R.id.searchView);
        searchMovies.setFocusable(false);

        searchMovies.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                load_search(query);
                if (TextUtils.isEmpty(query)){
                    currentPage=1;
                    movieSearch = false;
                    moviesList.clear();
                    load_movies(currentPage);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchMovies.getWindowToken(), 0);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    load_search(newText);
                } else {
                    currentPage=1;
                    movieSearch = false;
                    moviesList.clear();
                    load_movies(currentPage);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchMovies.getWindowToken(), 0);
                    return false;
                }
                return false; }
        });

        searchMovies.setOnCloseListener(new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                currentPage=1;
                movieSearch = false;
                moviesList.clear();
                load_movies(currentPage);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchMovies.getWindowToken(), 0);
                return false;
            }
        });

        moviesView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (gridLayoutManager.findLastCompletelyVisibleItemPosition() == moviesList.size()-1){
                    if (!movieSearch) {
                        currentPage = currentPage + 1;
                        load_movies(currentPage);
                    }
                }
            }
        });
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

                        JSONArray genresIds = object.optJSONArray("genre_ids");
                        ArrayList<String> genre = new ArrayList<String>();

                        for (int j = 0; j < genresIds.length(); j++) {
                            genre.add(genresIds.getString(j));
                        }

                        Movie data = new Movie(object.getInt("id"), object.getString("title"),
                                object.getString("overview"), object.getString("poster_path"),
                                object.getString("backdrop_path"), 0, object.getDouble("vote_average"),
                                object.getString("release_date"), genre, 0,0,0);
                        if (TextUtils.isEmpty(data.getOverview())) {
                            data.setOverview(getString(R.string.no_description));
                        }

                        if (object == null) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Object is null", Toast.LENGTH_LONG);
                            toast.show();
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

    private void load_search(final String search){

        AsyncTask<String,Void,Void> task = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(BASE_URL + "/search/movie?api_key=" + API_KEY + "&language=" + getLocale() + "&query=" +strings[0])
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    JSONObject root = new JSONObject(response.body().string());
                    JSONArray array = root.getJSONArray("results");
                    if (array.length()!=0){
                        moviesList.clear();
                        movieSearch=true;

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject object = array.getJSONObject(i);

                            JSONArray genresIds = object.optJSONArray("genre_ids");
                            ArrayList<String> genre = new ArrayList<String>();

                            for (int j = 0; j < genresIds.length(); j++) {
                                genre.add(genresIds.getString(j));
                            }

                            Movie data = new Movie(object.getInt("id"), object.getString("title"),
                                    object.getString("overview"), object.getString("poster_path"),
                                    object.getString("backdrop_path"), 0, object.getDouble("vote_average"),
                                    object.getString("release_date"), genre, 0,0,0);
                            if (TextUtils.isEmpty(data.getOverview())) {
                                data.setOverview(getString(R.string.no_description));
                            }

                            if (object == null) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Object is null", Toast.LENGTH_LONG);
                                toast.show();
                            }

                            moviesList.add(data);
                        }
                    }else{
                        movieSearch=false;
                        currentPage=1;
                        //  Toast.makeText(getApplicationContext(), "Aucun résultat disponible" ,Toast.LENGTH_SHORT).show();
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
        task.execute(search);
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
        Intent intent;

        if (id == R.id.nav_search) {
            intent = new Intent(getApplicationContext(), MainActivity.class);
            getApplicationContext().startActivity(intent);
        } else if (id == R.id.nav_to_see) {
            intent = new Intent(getApplicationContext(), ToSeeActivity.class);
            getApplicationContext().startActivity(intent);
        } else if (id == R.id.nav_seen) {
            intent = new Intent(getApplicationContext(), SeenActivity.class);
            getApplicationContext().startActivity(intent);
        } else if (id == R.id.nav_favorite) {
            intent = new Intent(getApplicationContext(), FavoritesActivity.class);
            getApplicationContext().startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
