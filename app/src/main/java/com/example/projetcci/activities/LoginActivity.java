package com.example.projetcci.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetcci.R;
import com.example.projetcci.utils.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Log in to the app
 */
public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    EditText editEmail, editPassword;
    Button btnLogin, btnToSignUp, btnToDiscover;
    TextView linkReset;

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText)findViewById(R.id.editPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnToSignUp = (Button) findViewById(R.id.btnToSignup);
        linkReset = (TextView) findViewById(R.id.linkReset);
        btnToDiscover = (Button) findViewById(R.id.btnToDiscover);

        mAuth = FirebaseAuth.getInstance();

        //Log in
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log_in();
            }
        });

        //Open SignUpActivity
        btnToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
            }
        });

        //Open ResetPasswordActivity
        linkReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Open DiscoverActivity
        btnToDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, DiscoverActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * If the user is logged, it will redirect on MainActivity
     */
    @Override
    protected void onStart() {
        super.onStart();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    /**
     * Connect to the app via FireBase
     */
    public void log_in() {

        final String email = editEmail.getText().toString();
        final String password = editPassword.getText().toString();

        /*
        Check if email and password are valid
         */
        if (!Validator.checkEmail(email) && !Validator.checkPassword(password)) {
            editEmail.setError(getString(R.string.check_email));
            editPassword.setError(getString(R.string.check_password));
            onLoginFailed();
            editEmail.requestFocus();
            editPassword.requestFocus();
            return;
        } else if (!Validator.checkEmail(email)) {
            editEmail.setError(getString(R.string.check_email));
            onLoginFailed();
            editEmail.requestFocus();
            return;
        } else if (!Validator.checkPassword(password)) {
            editPassword.setError(getString(R.string.check_password));
            onLoginFailed();
            editPassword.requestFocus();
            return;
        }

        //Charging indicator while authenticating
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AlertDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.authenticating));
        progressDialog.show();

        //Firebase authenticating with email and password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, getString(R.string.log_in_successful), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.log_in_failed), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    /**
     * Shows a Toast message if email and password didn't respect format
     */
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), getString(R.string.log_in_failed), Toast.LENGTH_LONG).show();
        btnLogin.setEnabled(true);
    }

    /**
     * By default, the Activity finishs and user is logged automatically
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    /**
     * Prevent user to go back on MainActivity when he signs out
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
