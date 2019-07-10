package com.example.projetcci.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.projetcci.models.Genre;
import com.example.projetcci.database.GenreManager;
import com.example.projetcci.database.MovieGenresManager;
import com.example.projetcci.database.MovieManager;
import com.example.projetcci.R;

import java.text.DecimalFormat;

/**
 * Shows statistics of the registred user
 */
public class StatisticsActivity extends AppCompatActivity {

    private TextView seenMovies, toSeeMovies, favoritesMovies, totalViewing, averageRating, favoriteGenre;

    private static final String TAG = "StatisticsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.menu_statistics));

        seenMovies = findViewById(R.id.nb_movies_seen);
        toSeeMovies = findViewById(R.id.nb_movies_to_see);
        favoritesMovies = findViewById(R.id.nb_movies_favorites);
        totalViewing = findViewById(R.id.total_viewing);
        averageRating = findViewById(R.id.average_rating);
        favoriteGenre = findViewById(R.id.genre_favorite);

        //Open DB
        final GenreManager g = new GenreManager(this);
        g.open();

        //Open DB
        final MovieManager m = new MovieManager(this);
        m.open();

        //Open DB
        final MovieGenresManager mg = new MovieGenresManager(this);
        mg.open();

        //Get the number of movies seen
        int seen = m.getCountSeenMovies();
        seenMovies.setText(String.valueOf(seen));

        //Get the number of movies to see
        int tosee = m.getCountToSeeMovies();
        toSeeMovies.setText(String.valueOf(tosee));

        //Get the number of favorites movies
        int favorites = m.getCountFavoritesMovies();
        favoritesMovies.setText(String.valueOf(favorites));

        //Get the average rating, adapt it with two decimals
        double average = m.getAverageRating();
        averageRating.setText(String.valueOf(new DecimalFormat("##.##").format(average)));

        //Get the total of viewing time
        int viewing = m.getTotalViewing();
        totalViewing.setText(viewing + " min");

        //Get id of favorite genre
        int favorite = mg.getFavoriteGenre();
        //Get informations of each genre with his id
        Genre aGenre = g.getGenre(favorite);
        //Get the genre name
        String genreName = aGenre.getName();
        favoriteGenre.setText(genreName);

    }

    /**
     * Back to previous activity
     */
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
