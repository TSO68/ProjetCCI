package com.example.projetcci;


import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.example.projetcci.Constants.CUSTOM_BASE_URL;

/**
 * Send comments on remote database
 */
public class CommentActivity extends AppCompatActivity {

    EditText editAuthor, editText;
    Button btnSend;

    FirebaseUser currentUser;

    private static final int CODE_POST_REQUEST = 1025;
    private static final String TAG = "CommentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.menu_opinion));

        editAuthor = (EditText) findViewById(R.id.editAuthor);
        editText = (EditText) findViewById(R.id.editText);

        btnSend = (Button) findViewById(R.id.btnSend);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String currentUserEmail = currentUser.getEmail();
            editAuthor.setText(currentUserEmail);
        }

        //Send the comment
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createComment();
            }
        });
    }

    /**
     * Create the comment and put it in a Hashmap, call the request
     * with the AsyncTask below
     */
    private void createComment() {
        String author = editAuthor.getText().toString().trim();
        String text = editText.getText().toString().trim();

        if (TextUtils.isEmpty(author)) {
            editAuthor.setError(getString(R.string.error_author));
            editAuthor.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(text)) {
            editText.setError(getString(R.string.error_text));
            editText.requestFocus();
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("author", author);
        params.put("text", text);

        PerformNetworkRequest request = new PerformNetworkRequest(CUSTOM_BASE_URL, params, CODE_POST_REQUEST);
        request.execute();
    }

    /**
     * Class which calls the RequestHandler for the adequate request
     */
    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        /**
         * Constructor of the API request
         * @param url of the request
         * @param params Hashmap with the author and the text
         * @param requestCode request code
         */
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            //Controls if the request match the code
            if (requestCode == CODE_POST_REQUEST) {
                return requestHandler.sendPostRequest(url, params);
            }

            return null;
        }

        /**
         * Show a Toast message if the request works
         * @param s the request
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.comment_sent), Toast.LENGTH_LONG).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
