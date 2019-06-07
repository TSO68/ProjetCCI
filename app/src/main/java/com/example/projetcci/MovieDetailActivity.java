package com.example.projetcci;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.projetcci.Constants.API_KEY;
import static com.example.projetcci.Constants.BASE_URL;
import static com.example.projetcci.Constants.IMAGE_BASE_URL;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView backdrop_image, poster;
    private TextView title, overview, releaseDate, runtime, genresList, castList;
    private RatingBar rating;
    private Button tosee, seen, favorite;
    private Drawable playlist_add, playlist_add_check, done, close, star, star_border;

    private static final String TAG = "MovieDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_movie_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        tosee = findViewById(R.id.button_to_see);
        seen = findViewById(R.id.button_seen);
        favorite = findViewById(R.id.button_favorite);

        playlist_add = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_playlist_add);
        playlist_add_check = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_playlist_add_check);
        done = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_done);
        close = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_close);
        star = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_star);
        star_border = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_star_border);

        final MovieManager m = new MovieManager(this);
        m.open();

        final Movie details = (Movie) getIntent().getExtras().getSerializable("MOVIE_DETAILS");

        new loadDetails().execute();
        new loadCast().execute();

        actionBar.setTitle(details.getTitle());

        backdrop_image = findViewById(R.id.movie_backdrop_image);
        poster = findViewById(R.id.movie_poster);
        title = findViewById(R.id.movie_title);
        releaseDate = findViewById(R.id.movie_release_date);
        runtime = findViewById(R.id.movie_runtime);
        rating = (RatingBar) findViewById(R.id.movie_rating_bar);
        overview = findViewById(R.id.movie_overview);
        genresList = findViewById(R.id.movie_genres);
        castList = findViewById(R.id.movie_cast);

        tosee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(details.getToSee() == 1){
                    details.setToSee(0);
                    tosee.setCompoundDrawablesWithIntrinsicBounds(null, playlist_add,null,null);
                    Toast.makeText(getApplicationContext(),"Retiré des films à voir", Toast.LENGTH_SHORT).show();
                }else{
                    details.setToSee(1);
                    tosee.setCompoundDrawablesWithIntrinsicBounds(null, playlist_add_check,null,null);
                    Toast.makeText(getApplicationContext(),"Ajouté aux films à voir", Toast.LENGTH_SHORT).show();
                }
                m.updateMovie(details);
            }
        });

        seen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(details.getSeen() == 1){
                    details.setSeen(0);
                    seen.setCompoundDrawablesWithIntrinsicBounds(null, done,null,null);
                    Toast.makeText(getApplicationContext(),"Retiré des films vus", Toast.LENGTH_SHORT).show();
                }else{
                    details.setSeen(1);
                    seen.setCompoundDrawablesWithIntrinsicBounds(null, close,null,null);
                    Toast.makeText(getApplicationContext(),"Ajouté aux films vus", Toast.LENGTH_SHORT).show();
                }
                m.updateMovie(details);
            }
        });

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(details.getFavorite() == 1){
                    details.setFavorite(0);
                    favorite.setCompoundDrawablesWithIntrinsicBounds( null, star_border, null, null);
                    Toast.makeText(getApplicationContext(),"Retiré des favoris", Toast.LENGTH_SHORT).show();
                }else{
                    details.setFavorite(1);
                    favorite.setCompoundDrawablesWithIntrinsicBounds( null, star, null, null);
                    Toast.makeText(getApplicationContext(),"Ajouté aux favoris", Toast.LENGTH_SHORT).show();
                }
                m.updateMovie(details);
            }
        });

        if(m.CheckMovie(details.getId())){
            Movie movie = m.getMovie(details.getId());
            details.setToSee(movie.getToSee());
            details.setSeen(movie.getSeen());
            details.setFavorite(movie.getFavorite());

            if (movie.getToSee() == 1){
                tosee.setCompoundDrawablesWithIntrinsicBounds(null, playlist_add_check,null,null);
            }else {
                tosee.setCompoundDrawablesWithIntrinsicBounds(null, playlist_add,null,null);
            }

            if (movie.getSeen() == 1){
                seen.setCompoundDrawablesWithIntrinsicBounds(null, close,null,null );
            }else {
                seen.setCompoundDrawablesWithIntrinsicBounds(null, done,null,null);
            }

            if (movie.getFavorite() == 1){
                favorite.setCompoundDrawablesWithIntrinsicBounds(null, star,null,null);
            }else{
                favorite.setCompoundDrawablesWithIntrinsicBounds(null, star_border,null,null);
            }
        }else{
            m.createMovie(details);

            tosee.setCompoundDrawablesWithIntrinsicBounds(null, playlist_add,null,null);
            seen.setCompoundDrawablesWithIntrinsicBounds( null, done, null, null );
            favorite.setCompoundDrawablesWithIntrinsicBounds( null, star_border, null, null);
        }

        if (details != null){

            Glide.with(this)
                    .load(IMAGE_BASE_URL + details.getBackdropPath())
                    .apply(RequestOptions.placeholderOf(R.color.colorPrimary))
                    .into(backdrop_image);
            Glide.with(this)
                    .load(IMAGE_BASE_URL + details.getPosterPath())
                    .apply(RequestOptions.placeholderOf(R.color.colorPrimary))
                    .into(poster);
            title.setText(details.getTitle());

            String correctDate = formatDate(details.getReleaseDate());
            releaseDate.setText(correctDate);

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
            genresList.setText(genreStr);
        }
    }

    public String getLocale() {
        String countryCode = Locale.getDefault().getCountry();
        String languageCode = Locale.getDefault().getLanguage();
        String localeCode = languageCode + "-" + countryCode;

        return localeCode;
    }

    public static String formatDate(@NonNull String date) {
        String tempDate = date.replaceAll("-","");
        String day = tempDate.substring(6, 8);
        String month = tempDate.substring(4, 6);
        String year = tempDate.substring(0, 4);
        String newDate = day + "/" + month + "/" + year;
        return newDate;
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

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private class loadDetails extends AsyncTask<Void,Void,Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {

            final Movie details = (Movie) getIntent().getExtras().getSerializable("MOVIE_DETAILS");
            int idMovie = details.getId();

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(BASE_URL + "/movie/" + idMovie + "?api_key=" + API_KEY + "&language=" + getLocale())
                    .build();

            try {
                Response response = client.newCall(request).execute();
                JSONObject root = new JSONObject(response.body().string());

                int time = root.getInt("runtime");
                return time;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                System.out.println("End of content");
                return null;
            }
        }

        @Override
        protected void onPostExecute(Integer time) {
            runtime.setText(time + " min");
        }
    }

    private class loadCast extends AsyncTask<Void,Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {

            final Movie details = (Movie) getIntent().getExtras().getSerializable("MOVIE_DETAILS");
            int idMovie = details.getId();

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(BASE_URL + "/movie/" + idMovie + "/credits?api_key=" + API_KEY)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                JSONObject root = new JSONObject(response.body().string());
                JSONArray array = root.getJSONArray("cast");

                ArrayList<String> cast = new ArrayList<String>();

                for (int i = 0; i < 10; i++) {

                    JSONObject object = array.getJSONObject(i);

                    String name = object.getString("name");
                    String character = object.getString("character");
                    String member = name + " (" + character + ")";

                    cast.add(member);
                }

                return cast;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                System.out.println("End of content");
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> cast) {
            String castStr = "";
            for (String str : cast) {
                castStr += str + ", ";
            }
            castStr = castStr.length() > 0 ? castStr.substring(0,castStr.length() - 2) : castStr;
            castList.setText(castStr);
        }
    }
}