package com.example.projetcci.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetcci.models.Movie;
import com.example.projetcci.database.MovieManager;
import com.example.projetcci.adapters.MoviesAdapter;
import com.example.projetcci.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Display the list of favorite movies
 */
public class FavoritesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean isAuthListenerSet = false;

    private static final String TAG = "FavoritesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = findViewById(R.id.toolbar_my_movies);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null); //Keep icons in white
        //Set color of the drawer menu title
        Menu menu = navigationView.getMenu();
        MenuItem title = menu.findItem(R.id.titleDrawer);
        SpannableString s = new SpannableString(title.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.DrawerTitle), 0, s.length(), 0);
        title.setTitle(s);

        View headerView = navigationView.getHeaderView(0);
        TextView userEmail = headerView.findViewById(R.id.txtUserEmail);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String currentUserEmail = currentUser.getEmail();
            userEmail.setText(currentUserEmail);
        }

        RecyclerView moviesView = findViewById(R.id.my_movies_list);
        List<Movie> moviesList = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        moviesView.setLayoutManager(gridLayoutManager);
        MoviesAdapter adapter = new MoviesAdapter(this, moviesList);
        moviesView.setAdapter(adapter);

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
                        c.getDouble(c.getColumnIndex(MovieManager.KEY_MY_RATING)),
                        c.getDouble(c.getColumnIndex(MovieManager.KEY_TMDB_RATING)),
                        c.getString(c.getColumnIndex(MovieManager.KEY_RELEASE_DATE)),
                        c.getString(c.getColumnIndex(MovieManager.KEY_GENRES)),
                        c.getInt(c.getColumnIndex(MovieManager.KEY_RUNTIME)),
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

    /**
     * Add the FirebaseAuth.AuthStateListener mAuthListener when activity is started
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (!isAuthListenerSet) {
            FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
            isAuthListenerSet = true;
        }
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

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu A Menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Set the action depanding on the item
     * @param item a item in the menu
     * @return the item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.action_settings) {
            intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Open the drawer menu and send the user on the desired activity
     */
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
        } else if (id == R.id.nav_statistics) {
            intent = new Intent(this, StatisticsActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_share) {
            intent = new Intent(this, CommentActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_settings) {
            intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Listener which checks if user is still logged or not
     */
    private final FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                Toast.makeText(FavoritesActivity.this, getString(R.string.log_out_successful), Toast.LENGTH_SHORT).show();
                Intent intent;
                intent = new Intent(FavoritesActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    };

    /**
     * Remove the FirebaseAuth.AuthStateListener mAuthListener when activity is stopped
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
            isAuthListenerSet = false;
        }
    }
}
