package com.example.projetcci.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projetcci.R;
import com.example.projetcci.utils.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Allows user to reset his password with a link sent on user's email address by Firebase
 */
public class ResetPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText editEmailReset;
    private Button btnReset;

    private static final String TAG = "ResetActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        editEmailReset = findViewById(R.id.editEmailReset);
        btnReset = findViewById(R.id.btnReset);

        mAuth = FirebaseAuth.getInstance();

        //Reset password
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    /**
     * Trigger the email sended by Firebase that allows user to reset his password
     */
    private void resetPassword() {

        final String email = editEmailReset.getText().toString();

        /*
        Check if email is valid
         */
        if (!Validator.checkEmail(email)) {
            editEmailReset.setError(getString(R.string.check_email));
            onResetFailed();
            editEmailReset.requestFocus();
            return;
        }

        //Charging indicator while sending the email
        final ProgressDialog progressDialog = new ProgressDialog(ResetPasswordActivity.this, R.style.AlertDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.email_sending));
        progressDialog.show();

        //Firebase send the email which allows to reset the password
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.email_sent) + email, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.password_reset_failed), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    /**
     * Shows a Toast message if email didn't respect format
     */
    private void onResetFailed() {
        Toast.makeText(getBaseContext(), getString(R.string.password_reset_failed), Toast.LENGTH_LONG).show();
        btnReset.setEnabled(true);
    }

    /**
     * Sends user on LoginActivity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Back to LoginActivity
     */
    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        return true;
    }
}
