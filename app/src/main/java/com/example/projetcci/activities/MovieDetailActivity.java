package com.example.projetcci.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projetcci.database.GenreManager;
import com.example.projetcci.models.Movie;
import com.example.projetcci.database.MovieGenresManager;
import com.example.projetcci.database.MovieManager;
import com.example.projetcci.R;
import com.example.projetcci.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static com.example.projetcci.utils.Constants.API_KEY;
import static com.example.projetcci.utils.Constants.BASE_URL;
import static com.example.projetcci.utils.Constants.IMAGE_BASE_URL;

/**
 * Display detailled informations of each movie
 */
public class MovieDetailActivity extends AppCompatActivity {

    private TextView runtime, castList;
    private RatingBar myRating;
    private Button tosee, seen, favorite, record, stopRecord, play, stopPlay;
    private Drawable playlist_add, playlist_add_check, done, close, star, star_border;

    private String AudioSavePathInDevice = null;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private static final int RequestPermissionCode = 1;

    private static final String TAG = "MovieDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_movie_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ImageView backdrop_image = findViewById(R.id.movie_backdrop_image);
        ImageView poster = findViewById(R.id.movie_poster);
        TextView title = findViewById(R.id.movie_title);
        TextView releaseDate = findViewById(R.id.movie_release_date);
        runtime = findViewById(R.id.movie_runtime);
        RatingBar tmdbRating = findViewById(R.id.movie_tmdb_rating_bar);
        TextView overview = findViewById(R.id.movie_overview);
        TextView genresList = findViewById(R.id.movie_genres);
        castList = findViewById(R.id.movie_cast);
        myRating = findViewById(R.id.movie_my_rating_bar);

        //Button to QuotesActivity
        Button quotes = findViewById(R.id.button_quotes);

        //Buttons for DB
        tosee = findViewById(R.id.button_to_see);
        seen = findViewById(R.id.button_seen);
        favorite = findViewById(R.id.button_favorite);

        //Retrieve drawable to display
        playlist_add = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_playlist_add);
        playlist_add_check = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_playlist_add_check);
        done = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_done);
        close = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_close);
        star = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_star);
        star_border = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_star_border);

        //Buttons for audio recording & playing
        record = findViewById(R.id.button_record);
        stopRecord = findViewById(R.id.button_stop_record);
        play = findViewById(R.id.button_play);
        stopPlay = findViewById(R.id.button_stop_play);

        //Disable buttons when Activity is launched
        stopRecord.setEnabled(false);
        stopPlay.setEnabled(false);

        //Get user's email for audio folders
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String user = currentUser.getEmail();

        //Open DB
        final GenreManager g = new GenreManager(this);
        g.open();

        //Open DB
        final MovieManager m = new MovieManager(this);
        m.open();

        //Open DB
        final MovieGenresManager mg = new MovieGenresManager(this);
        mg.open();

        //Get values from the concerned list activity (Main, ToSee, etc...)
        final Movie details = (Movie) getIntent().getExtras().getSerializable("MOVIE_DETAILS");

        new loadDetails().execute();
        new loadCast().execute();

        actionBar.setTitle(details.getTitle());

        //Send the user to QuotesActivity with id and title of the movie as Extras
        quotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovieDetailActivity.this, QuotesActivity.class);
                intent.putExtra("ID_MOVIE", details.getId());
                intent.putExtra("TITLE_MOVIE", details.getTitle());
                startActivity(intent);
            }
        });

        //Set the movie as to see and update DB
        tosee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(details.getToSee() == 1){
                    details.setToSee(0);
                    tosee.setCompoundDrawablesWithIntrinsicBounds(null, playlist_add,null,null);
                    Toast.makeText(getApplicationContext(), getString(R.string.remove_to_see), Toast.LENGTH_SHORT).show();
                }else{
                    details.setToSee(1);
                    tosee.setCompoundDrawablesWithIntrinsicBounds(null, playlist_add_check,null,null);
                    Toast.makeText(getApplicationContext(), getString(R.string.add_to_see), Toast.LENGTH_SHORT).show();
                }
                m.updateMovie(details);
            }
        });

        //Set the movie as seen and update DB
        //Update icons of each button
        seen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(details.getSeen() == 1){
                    details.setSeen(0);
                    seen.setCompoundDrawablesWithIntrinsicBounds(null, done,null,null);
                    Toast.makeText(getApplicationContext(), getString(R.string.remove_seen), Toast.LENGTH_SHORT).show();
                }else{
                    details.setSeen(1);
                    seen.setCompoundDrawablesWithIntrinsicBounds(null, close,null,null);
                    Toast.makeText(getApplicationContext(), getString(R.string.add_seen), Toast.LENGTH_SHORT).show();
                }
                m.updateMovie(details);
            }
        });

        //Set the movie as favorite and update the DB
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(details.getFavorite() == 1){
                    details.setFavorite(0);
                    favorite.setCompoundDrawablesWithIntrinsicBounds( null, star_border, null, null);
                    Toast.makeText(getApplicationContext(), getString(R.string.remove_favorite), Toast.LENGTH_SHORT).show();
                }else{
                    details.setFavorite(1);
                    favorite.setCompoundDrawablesWithIntrinsicBounds( null, star, null, null);
                    Toast.makeText(getApplicationContext(),getString(R.string.add_favorite), Toast.LENGTH_SHORT).show();
                }
                m.updateMovie(details);
            }
        });

        //Set user's rating and update the DB
        myRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                double myRat = myRating.getRating();
                details.setMyRating(myRat);
                //Toast.makeText(getApplicationContext(),"You gave " + myRat + " to " + details.getTitle(),
                // Toast.LENGTH_SHORT).show();
                m.updateMovie(details);
            }
        });

        //Check if movie exists and DB, add it if not
        //Set icons of each button
        if(m.checkMovie(details.getId())){
            Movie movie = m.getMovie(details.getId());
            details.setToSee(movie.getToSee());
            details.setSeen(movie.getSeen());
            details.setFavorite(movie.getFavorite());

            //Set user's rating bar value
            Movie myMovie = m.getMyRatingById(details.getId());
            double myD = myMovie.getMyRating();
            float myF = (float) myD;
            myRating.setRating(myF);

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

        //Display details
        if (details != null){

            Glide.with(this)
                    .load(IMAGE_BASE_URL + details.getBackdropPath())
                    .apply(RequestOptions.placeholderOf(R.color.gray))
                    .into(backdrop_image);
            Glide.with(this)
                    .load(IMAGE_BASE_URL + details.getPosterPath())
                    .apply(RequestOptions.placeholderOf(R.color.gray))
                    .into(poster);
            title.setText(details.getTitle());

            //Correct date style
            String correctDate = Utils.formatDate(details.getReleaseDate());
            releaseDate.setText(correctDate);

            overview.setText(details.getOverview());
            double d = details.getTMDBRating();
            float f = (float) d;
            tmdbRating.setRating(f/2);

            //Get the string of all genres and convert it in array of every genre splitted with a comma
            String genresString = details.getGenres();
            ArrayList<String> genresArray = new ArrayList<>(Arrays.asList(genresString.split(",")));

            StringBuilder genreStr = new StringBuilder();
            for (String str : genresArray) {
                int genreId = 0;
                String genreName;

                //Needed if movie has no genre recorded by TMDB
                if (!str.equals("")) {
                    //Convert every genre in an int
                    genreId = Integer.parseInt(str);
                    //Add it to the relationnal table if it doesn't exist yet
                    if (!mg.checkMovieGenres(genreId, details.getId())) {
                        mg.createMovieGenre(genreId, details.getId());
                    }
                }

                //Needed if movie has no genre recorded by TMDB
                if (genreId != 0) {
                    //Get informations of each genre with his id
                    genreName = mg.getGenreName(genreId, details.getId());
                } else {
                    genreName = getResources().getString(R.string.no_genre);
                }

                genreStr.append(genreName).append(", ");
            }

            genreStr = new StringBuilder(genreStr.length() > 0 ? genreStr.substring(0, genreStr.length() - 2) : genreStr.toString());
            genresList.setText(genreStr.toString());
        }

        //Record the audio file
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {

                    //Create root directory for the app, if it doesn't exist yet
                    String rootFolder = File.separator + "Movie7";
                    File dirApp = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), rootFolder);
                    if (!dirApp.exists())
                    {
                        dirApp.mkdir();
                    }

                    //Create each user directory inside app directory, if it doesn't exist yet
                    String userFolder = File.separator + user;
                    File dirUser = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                            + rootFolder, userFolder);
                    if (!dirUser.exists())
                    {
                        dirUser.mkdir();
                    }

                    //Create the root for the movie with its Id, if it doesn't exist yet
                    String movieFolder = File.separator + details.getId();
                    File dirMovie = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                            + rootFolder + userFolder, movieFolder);
                    if (!dirMovie.exists())
                    {
                        dirMovie.mkdir();
                    }

                    //Set the audio path
                    AudioSavePathInDevice = dirMovie + "/" + "myopinion.3gp";

                    MediaRecorderReady();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    record.setEnabled(false);
                    stopRecord.setEnabled(true);

                    Toast.makeText(MovieDetailActivity.this, getString(R.string.start_recording),
                            Toast.LENGTH_SHORT).show();
                } else {
                    requestPermission();
                }

            }
        });

        //Stop the recording of the audio file
        stopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();

                record.setEnabled(true);
                stopRecord.setEnabled(false);
                play.setEnabled(true);
                stopPlay.setEnabled(false);

                Toast.makeText(MovieDetailActivity.this, getString(R.string.complete_recording),
                        Toast.LENGTH_SHORT).show();
            }
        });

        //Play the audio file corresponding to the movie
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {
                record.setEnabled(false);
                stopRecord.setEnabled(false);
                play.setEnabled(false);
                stopPlay.setEnabled(true);

                mediaPlayer = new MediaPlayer();

                try {
                    //Get root, user and movie directories, and file path
                    String rootFolder = File.separator + "Movie7";
                    String userFolder = File.separator + user;
                    String movieFolder = File.separator + details.getId();
                    File dirMovie = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                            + rootFolder + userFolder, movieFolder);
                    String audioFile = dirMovie + File.separator + "myopinion.3gp";

                    mediaPlayer.setDataSource(audioFile);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(MovieDetailActivity.this, getString(R.string.play_recording),
                        Toast.LENGTH_SHORT).show();

                //Listen if file is ending, stop playing at the end
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        record.setEnabled(true);
                        stopRecord.setEnabled(false);
                        play.setEnabled(true);
                        stopPlay.setEnabled(false);

                        Toast.makeText(MovieDetailActivity.this, getString(R.string.file_end),
                                Toast.LENGTH_SHORT).show();

                        if (mediaPlayer != null){
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.release();
                            MediaRecorderReady();
                        }
                    }
                });
            }
        });

        //Stop playing the audio file
        stopPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                record.setEnabled(true);
                stopRecord.setEnabled(false);
                play.setEnabled(true);
                stopPlay.setEnabled(false);

                Toast.makeText(MovieDetailActivity.this, getString(R.string.play_stopped),
                        Toast.LENGTH_SHORT).show();

                if (mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });
    }

    /**
     * Retrieve some additionnal details of each movie, that "discover" didn't display
     */
    private class loadDetails extends AsyncTask<Void,Void,Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {

            final Movie details = (Movie) getIntent().getExtras().getSerializable("MOVIE_DETAILS");
            int idMovie = details.getId();

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(BASE_URL + "/movie/" + idMovie + "?api_key=" + API_KEY + "&language=" + Utils.getLocale())
                    .build();

            try {
                Response response = client.newCall(request).execute();
                JSONObject root = new JSONObject(response.body().string());

                //Get JSON with result from request
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

        /**
         * Set the textView runtime
         * @param time retrieved from API
         */
        @Override
        protected void onPostExecute(Integer time) {
            //Replace if time is null in TMDB API
            if (String.valueOf(time).equals("null")){
                time = 0;
            }
            runtime.setText(getString(R.string.time, time));

            //Open DB
            final MovieManager m = new MovieManager(MovieDetailActivity.this);
            m.open();

            //Update movie's row with its runtime
            final Movie details = (Movie) getIntent().getExtras().getSerializable("MOVIE_DETAILS");
            m.updateRuntime(time, details.getId());

        }
    }

    /**
     * Retrieve cast of each movie
     */
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
                //Get JSON with results from request
                Response response = client.newCall(request).execute();
                JSONObject root = new JSONObject(response.body().string());
                JSONArray array = root.getJSONArray("cast");

                ArrayList<String> cast = new ArrayList<>();

                for (int i = 0; i < 10; i++) {

                    JSONObject object = array.getJSONObject(i);

                    //Get actor and character names and put it in one single string
                    String name = object.getString("name");
                    String character = object.getString("character");
                    String member = name + " (" + character + ")";

                    //Add it to a List
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

        /**
         * Set the textView castList
         * @param cast retrieved from API
         */
        @Override
        protected void onPostExecute(ArrayList<String> cast) {
            StringBuilder castStr = new StringBuilder();

            //Replace if cast is null in TMDB API
            if (cast == null) {
                castStr = new StringBuilder(getResources().getString(R.string.no_cast));
                castList.setText(castStr.toString());
            }else {
                for (String str : cast) {
                    castStr.append(str).append(", ");
                }
                castStr = new StringBuilder(castStr.length() > 0 ? castStr.substring(0, castStr.length() - 2) : castStr.toString());
                castList.setText(castStr.toString());
            }
        }
    }

    /**
     * Set informations about the audio file : source, format, encoding
     */
    private void MediaRecorderReady(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    /**
     * Request permissions for the audio file recording
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(MovieDetailActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    /**
     * Request permissions to the user
     * @param requestCode 1
     * @param permissions WRITE_EXTERNAL_STORAGE, RECORD_AUDIO
     * @param grantResults true or false
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == RequestPermissionCode) {
            if (grantResults.length > 0) {
                boolean StoragePermission = grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED;
                boolean RecordPermission = grantResults[1] ==
                        PackageManager.PERMISSION_GRANTED;

                if (StoragePermission && RecordPermission) {
                    Toast.makeText(MovieDetailActivity.this, getString(R.string.permissions_granted),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MovieDetailActivity.this, getString(R.string.permissions_denied),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Check if permissions are granted or not
     * @return true or false
     */
    private boolean checkPermission() {
        int write_external = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int record_audio = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return write_external == PackageManager.PERMISSION_GRANTED &&
                record_audio == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Back to previous activity
     */
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu A Menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
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
        final Movie details = (Movie) getIntent().getExtras().getSerializable("MOVIE_DETAILS");
        String movieTitle = details.getTitle();

        //Open the share action with the movie title
        if (id == R.id.action_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.viewing_sharing, movieTitle));
            sendIntent.setType("text/plain");

            //Open a chooser between different app (Twitter, Messages, Whatsapp etc.)
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }

        return super.onOptionsItemSelected(item);
    }
}