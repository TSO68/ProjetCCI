package com.example.projetcci;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Display the list of favorite movies
 */
public class FavoritesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseUser currentUser;

    private RecyclerView moviesView;
    private MoviesAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    private List<Movie> moviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_see);

        Toolbar toolbar = findViewById(R.id.toolbar_my_movies);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        moviesView = (RecyclerView) findViewById(R.id.my_movies_list);
        moviesList = new ArrayList<>();

        gridLayoutManager = new GridLayoutManager(this,2);
        moviesView.setLayoutManager(gridLayoutManager);
        adapter = new MoviesAdapter(this, moviesList);
        moviesView.setAdapter(adapter);

        ArrayList<String> genresList = new ArrayList<String>();

        //Open the DB and retrieve informations of favorite movies
        final MovieManager m = new MovieManager(this);
        m.open();
        Cursor c = m.getMoviesFavorite();
        if (c.moveToFirst())
        {
            do {
                Movie data = new Movie(c.getInt(c.getColumnIndex(MovieManager.KEY_ID_MOVIE)),
                        c.getString(c.getColumnIndex(MovieManager.KEY_TITLE)),
                        c.getString(c.getColumnIndex(MovieManager.KEY_OVERVIEW)),
                        c.getString(c.getColumnIndex(MovieManager.KEY_POSTER_PATH)),
                        c.getString(c.getColumnIndex(MovieManager.KEY_BACKDROP_PATH)),
                        c.getInt(c.getColumnIndex(MovieManager.KEY_MY_RATING)),
                        c.getDouble(c.getColumnIndex(MovieManager.KEY_TMDB_RATING)),
                        c.getString(c.getColumnIndex(MovieManager.KEY_RELEASE_DATE)),
                        genresList,
                        c.getInt(c.getColumnIndex(MovieManager.KEY_TOSEE)),
                        c.getInt(c.getColumnIndex(MovieManager.KEY_SEEN)),
                        c.getInt(c.getColumnIndex(MovieManager.KEY_FAVORITE)));
                moviesList.add(data);
            }
            while (c.moveToNext());
        }
        c.close();
        adapter.notifyDataSetChanged();
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
     * Open the drawer menu and send the user on the desired activity
     */
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
            intent = new Intent(getApplicationContext(), CommentActivity.class);
            getApplicationContext().startActivity(intent);
        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_disconnect) {
            FirebaseAuth.getInstance().signOut();

            if ( currentUser == null) {
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                getApplicationContext().startActivity(intent);
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
