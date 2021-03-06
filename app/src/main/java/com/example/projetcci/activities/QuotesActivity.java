package com.example.projetcci.activities;

import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetcci.models.Quote;
import com.example.projetcci.R;
import com.example.projetcci.network.RequestHandler;
import com.example.projetcci.utils.Validator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;

import static com.example.projetcci.utils.Constants.CREATE_QUOTE_BASE_URL;
import static com.example.projetcci.utils.Constants.GET_QUOTES_BASE_URL;

public class QuotesActivity extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    private EditText editCharacter, editQuote;
    private ProgressBar progressBar;
    private ListView listQuotes;

    private List<Quote> quotesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Get id and title of concerned movie from MovieDetailActivity
        final String title = getIntent().getExtras().getString("TITLE_MOVIE");

        actionBar.setTitle(title);

        editCharacter = findViewById(R.id.editCharacter);
        editQuote = findViewById(R.id.editQuote);
        Button addQuote = findViewById(R.id.btnAdd);
        progressBar = findViewById(R.id.progressBarQuotes);
        listQuotes = findViewById(R.id.listQuotes);

        quotesList = new ArrayList<>();

        //Add the new quote
        addQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createQuote();
            }
        });

        readQuotes();
    }

    /**
     * Create the quote and put it in a Hashmap, call the request
     * with the AsyncTask below
     */
    private void createQuote() {
        final int id = getIntent().getExtras().getInt("ID_MOVIE");

        String character = editCharacter.getText().toString().trim();
        String quote = editQuote.getText().toString().trim();

        /*
        Check if character and quote are valid
         */
        if (!Validator.checkContent(character) && !Validator.checkContent(quote)) {
            editCharacter.setError(getString(R.string.set_character));
            editQuote.setError(getString(R.string.set_quote));
            editCharacter.requestFocus();
            editQuote.requestFocus();
            return;
        } else if (!Validator.checkContent(character)) {
            editCharacter.setError(getString(R.string.set_character));
            editCharacter.requestFocus();
            return;
        } else if (!Validator.checkContent(quote)) {
            editQuote.setError(getString(R.string.set_quote));
            editQuote.requestFocus();
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("id_movie", String.valueOf(id));
        params.put("quote", quote);
        params.put("charac", character);

        PerformNetworkRequest request = new PerformNetworkRequest(CREATE_QUOTE_BASE_URL, params, CODE_POST_REQUEST);
        request.execute();

        Toast.makeText(getApplicationContext(), getString(R.string.quote_added), Toast.LENGTH_SHORT).show();
        finish();
        startActivity(getIntent());
    }

    /**
     * Get quotes from the concerned movie, call the GET request
     */
    private void readQuotes() {
        final int id = getIntent().getExtras().getInt("ID_MOVIE");

        PerformNetworkRequest request = new PerformNetworkRequest(GET_QUOTES_BASE_URL + id, null, CODE_GET_REQUEST);
        request.execute();
    }

    /**
     * Refresh the quotes list
     * @param quotes JSONarray of quotes from remote database
     * @throws JSONException JSONException
     */
    private void refreshQuotesList(JSONArray quotes) throws JSONException {
        quotesList.clear();

        for (int i = 0; i < quotes.length(); i++) {
            JSONObject obj = quotes.getJSONObject(i);

            quotesList.add(new Quote(
                    obj.getInt("id_movie"),
                    obj.getString("quote"),
                    obj.getString("charac")
            ));
        }

        QuotesAdapter adapter = new QuotesAdapter(quotesList);
        listQuotes.setAdapter(adapter);
    }

    /**
     * Class which calls the RequestHandler for the adequate request
     */
    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        final String url;
        final HashMap<String, String> params;
        final int requestCode;

        /**
         * Constructor of the API request
         * @param url of the request
         * @param params Hashmap with id_movie, quote and character
         * @param requestCode request code
         */
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        /**
         * Show the progress bar on screen
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            //Call the POST request
            if (requestCode == CODE_POST_REQUEST) {
                return requestHandler.sendPostRequest(url, params);
            }

            //Call the GET request
            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }

        /**
         * Call the function that refresh the quotes list
         * @param s the request
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(GONE);

            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    refreshQuotesList(object.getJSONArray("quotes"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The adapter for the quotes list
     */
    class QuotesAdapter extends ArrayAdapter<Quote> {
        final List<Quote> quotesList;

        /**
         * Set up the adapter with the necessary configuration
         * @param quotesList list of quotes retrieved from the remote DB
         */
        QuotesAdapter(List<Quote> quotesList) {
            super(QuotesActivity.this, R.layout.quote_card, quotesList);
            this.quotesList = quotesList;
        }

        /**
         * Sets the content of each quote card and return it
         * @param position in the list
         * @param convertView View
         * @param parent Viewgroup
         * @return a quote
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.quote_card, null, true);

            TextView character = listViewItem.findViewById(R.id.quote_character);
            TextView text = listViewItem.findViewById(R.id.quote_text);

            final Quote quote = quotesList.get(position);

            character.setText(quote.getCharacter());
            text.setText(quote.getQuote());

            return listViewItem;
        }
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
