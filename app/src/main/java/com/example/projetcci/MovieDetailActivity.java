package com.example.projetcci;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
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
    private static final String TAG = "MovieDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_movie_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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

        if(m.CheckMovie(details.getId())){
            Movie movie = m.getMovie(details.getId());
            details.setSeen(movie.getSeen());
            details.setToSee(movie.getToSee());
            if (movie.getSeen() == 1){
                actionBar.setIcon(R.drawable.ic_close);
            }else {
                actionBar.setIcon(R.drawable.ic_done);
            }
            if (movie.getFavorite() ==1 ){
                actionBar.setIcon(R.drawable.ic_star);
            }else{
                actionBar.setIcon(R.drawable.ic_star_border);
            }
        }else{
            m.createMovie(details);
            actionBar.setIcon(R.drawable.ic_done);
            actionBar.setIcon(R.drawable.ic_star_border);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final Movie details = (Movie) getIntent().getExtras().getSerializable("MOVIE_DETAILS");

        switch (item.getItemId()){
            case R.id.action_seen:
                if(details.getSeen() == 1){
                    details.setSeen(0);
                    item.setIcon(R.drawable.ic_close);
                    Toast.makeText(getApplicationContext(),"Retiré des films vus", Toast.LENGTH_LONG).show();
                }else{
                    details.setSeen(1);
                    item.setIcon(R.drawable.ic_done);
                    Toast.makeText(getApplicationContext(),"Ajouté aux films vus", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.action_save:
                if(details.getSeen() == 1){
                    details.setSeen(0);
                    item.setIcon(R.drawable.ic_star_border);
                    Toast.makeText(getApplicationContext(),"Retiré des coups de coeur", Toast.LENGTH_LONG).show();
                }else{
                    details.setSeen(1);
                    item.setIcon(R.drawable.ic_star);
                    Toast.makeText(getApplicationContext(),"Ajouté aux coups de coeur", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
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