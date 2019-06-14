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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.example.projetcci.Constants.CUSTOM_BASE_URL;

public class CommentActivity extends AppCompatActivity {

    EditText editAuthor, editText;
    Button btnSend;

    private static final int CODE_POST_REQUEST = 1025;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.menu_share));

        editAuthor = (EditText) findViewById(R.id.editAuthor);
        editText = (EditText) findViewById(R.id.editText);

        btnSend = (Button) findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createComment();
            }
        });
    }

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

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST) {
                return requestHandler.sendPostRequest(url, params);
            }

            return null;
        }

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

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
