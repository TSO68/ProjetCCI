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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.projetcci.Constants.BASE_URL;
import static com.example.projetcci.Constants.API_KEY;

/**
 * Display the "discover" list from TMDB and search movies
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseUser currentUser;
    TextView userEmail;

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

        View headerView = navigationView.getHeaderView(0);
        userEmail = (TextView) headerView.findViewById(R.id.txtUserEmail);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String currentUserEmail = currentUser.getEmail();
            userEmail.setText(currentUserEmail);
        }

        moviesView = (RecyclerView) findViewById(R.id.movies_list);
        moviesList = new ArrayList<>();
        load_movies(currentPage);
        gridLayoutManager = new GridLayoutManager(this,2);
        moviesView.setLayoutManager(gridLayoutManager);
        adapter = new MoviesAdapter(this, moviesList);
        moviesView.setAdapter(adapter);

        final SearchView searchMovies = findViewById(R.id.searchView);
        searchMovies.setFocusable(false);

        //Search new movies on submit in the SearchView
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

            //Search new movies on each change in the SearchView
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

        //Reload the "discover" list when the Searchview is emptied
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

        //Add new movies on display when scrolling
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

    /**
     * Search the Locale of the device
     * @return a string with language-country of the device
     */
    public String getLocale() {
        String countryCode = Locale.getDefault().getCountry();
        String languageCode = Locale.getDefault().getLanguage();
        String localeCode = languageCode + "-" + countryCode;

        return localeCode;
    }

    /**
     * Load movies from the "discover" list of TMDB API
     * @param id number of the page from the list
     */
    private void load_movies(int id) {

        AsyncTask<Integer, Void, Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(BASE_URL + "/discover/movie?api_key=" + API_KEY + "&language=" + getLocale() + "&sort_by=popularity.desc&include_adult=false&include_video=false&page=" + integers[0])
                        .build();
                try {
                    //Get JSON with results from request
                    Response response = client.newCall(request).execute();
                    JSONObject root = new JSONObject(response.body().string());
                    JSONArray array = root.getJSONArray("results");

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);

                        //Get the genre ids list of each movies
                        JSONArray genresIds = object.optJSONArray("genre_ids");
                        ArrayList<String> genre = new ArrayList<String>();

                        for (int j = 0; j < genresIds.length(); j++) {
                            genre.add(genresIds.getString(j));
                        }

                        //Get datas of each movies
                        Movie data = new Movie(object.getInt("id"), object.getString("title"),
                                object.getString("overview"), object.getString("poster_path"),
                                object.getString("backdrop_path"), 0, object.getDouble("vote_average"),
                                object.getString("release_date"), genre, 0,0,0);
                        //Replace overview if empty
                        if (TextUtils.isEmpty(data.getOverview())) {
                            data.setOverview(getString(R.string.no_description));
                        }

                        //Show if JSON is null
                        if (object == null) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Object is null", Toast.LENGTH_LONG);
                            toast.show();
                        }

                        //Add data to the list of movies
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

    /**
     * Get movies from the user's search
     * @param search string get from the SearchView
     */
    private void load_search(final String search){

        AsyncTask<String,Void,Void> task = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(BASE_URL + "/search/movie?api_key=" + API_KEY + "&language=" + getLocale() + "&query=" +strings[0])
                        .build();
                try {
                    //Get JSON with results from request
                    Response response = client.newCall(request).execute();
                    JSONObject root = new JSONObject(response.body().string());
                    JSONArray array = root.getJSONArray("results");
                    if (array.length()!=0){ //If results exist
                        moviesList.clear();
                        movieSearch=true;

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject object = array.getJSONObject(i);

                            //Get the genre ids list of each movies
                            JSONArray genresIds = object.optJSONArray("genre_ids");
                            ArrayList<String> genre = new ArrayList<String>();

                            for (int j = 0; j < genresIds.length(); j++) {
                                genre.add(genresIds.getString(j));
                            }

                            //Get datas of each movies
                            Movie data = new Movie(object.getInt("id"), object.getString("title"),
                                    object.getString("overview"), object.getString("poster_path"),
                                    object.getString("backdrop_path"), 0, object.getDouble("vote_average"),
                                    object.getString("release_date"), genre, 0,0,0);
                            //Replace overview if empty
                            if (TextUtils.isEmpty(data.getOverview())) {
                                data.setOverview(getString(R.string.no_description));
                            }

                            //Show if JSON is null
                            if (object == null) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Object is null", Toast.LENGTH_LONG);
                                toast.show();
                            }

                            //Add data to the list of movies
                            moviesList.add(data);
                        }
                    }else{ //If no results
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

    //When back button is pressed
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

    /**
     * Open the drawer menu and send user on the desired activity
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.nav_search) {
            intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_to_see) {
            intent = new Intent(this, ToSeeActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_seen) {
            intent = new Intent(this, SeenActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_favorite) {
            intent = new Intent(this, FavoritesActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_share) {
            intent = new Intent(this, CommentActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_settings) {
            intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_disconnect) {
            FirebaseAuth.getInstance().signOut();

            if ( currentUser == null) {
                intent = new Intent(this, LoginActivity.class);
                this.startActivity(intent);
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
