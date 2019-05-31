package com.example.projetcci;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import static com.example.projetcci.Constants.IMAGE_BASE_URL;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView image;
    private TextView title, overview, releaseDate,synopsys,titre;
    private static final String TAG = "Movidetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_movie_detail);

        final FloatingActionButton seen = findViewById(R.id.buttonseen);
        final FloatingActionButton tosee =findViewById(R.id.buttontosee);

        final Movie details = (Movie) getIntent().getExtras().getSerializable("MOVIE_DETAILS");

        image = findViewById(R.id.backdrop);
        title = findViewById(R.id.title);
        releaseDate = findViewById(R.id.releaseDate);
        RatingBar rating = (RatingBar) findViewById(R.id.ratingBar);
        overview = findViewById(R.id.overview);

        if (details != null){

            Glide.with(this)
                    .load(IMAGE_BASE_URL + details.getBackdropPath())
                    .apply(RequestOptions.placeholderOf(R.color.colorPrimary))
                    .into(image);
            title.setText(details.getTitle());
            releaseDate.setText(details.getReleaseDate());
            overview.setText(details.getOverview());
            double d = details.getTMDBRating();
            float f = (float) d;
            rating.setRating(f/2);

            String genreStr = "";
            for (String str : details.getGenres()) {
                str = getGenreName(str);
                genreStr += str + ", ";
            }
            genreStr = genreStr.length() > 0 ? genreStr.substring(0,genreStr.length() - 2) : genreStr;
            //title.setText(genreStr);
        }
    }

    private String getGenreName(@NonNull String genreId) {
        String genreName;
        
        switch(genreId)
        {
            case "28":
                genreName = "Action";
                break;
            case "12":
                genreName = "Adventure";
                break;
            case "16":
                genreName = "Animation";
                break;
            case "35":
                genreName = "Comedy";
                break;
            case "80":
                genreName = "Crime";
                break;
            case "99":
                genreName = "Documentary";
                break;
            case "18":
                genreName = "Drama";
                break;
            case "10751":
                genreName = "Family";
                break;
            case "14":
                genreName = "Fantasy";
                break;
            case "36":
                genreName = "History";
                break;
            case "27":
                genreName = "Horror";
                break;
            case "10402":
                genreName = "Music";
                break;
            case "9648":
                genreName = "Mystery";
                break;
            case "10749":
                genreName = "Romance";
                break;
            case "878":
                genreName = "Science Fiction";
                break;
            case "10770":
                genreName = "TV Movie";
                break;
            case "53":
                genreName = "Thriller";
                break;
            case "10752":
                genreName = "War";
                break;
            case "37":
                genreName = "Western";
                break;
            default:
                genreName = "Movie";
        }
        return genreName;
    }
}
