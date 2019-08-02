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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Signing up to the app
 */
public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    EditText editNewEmail, editNewPassword, editConfirmPassword;
    Button btnSignup, btnToLogin;

    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        editNewEmail = (EditText) findViewById(R.id.editNewEmail);
        editNewPassword = (EditText) findViewById(R.id.editNewPassword);
        editConfirmPassword = (EditText) findViewById(R.id.editConfirmPassword);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        btnToLogin = (Button) findViewById(R.id.btnToLogin);

        mAuth = FirebaseAuth.getInstance();

        //Sign up
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_up();
            }
        });

        //Open LoginActivity
        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
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
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish();
        }
    }

    /**
     * Sign up to the app via Firebase
     */
    public void sign_up() {

        final String email = editNewEmail.getText().toString();
        final String password = editNewPassword.getText().toString();
        final String confirmPassword = editConfirmPassword.getText().toString();

        /*
        Check if email, password and confirmed password are valid
         */
        if (!Validator.checkEmail(email) && !Validator.checkPassword(password)) {
            editNewEmail.setError(getString(R.string.check_email));
            editNewPassword.setError(getString(R.string.check_password));
            onSignupFailed();
            editNewEmail.requestFocus();
            editNewPassword.requestFocus();
            return;
        } else if (!Validator.checkEmail(email)) {
            editNewEmail.setError(getString(R.string.check_email));
            onSignupFailed();
            editNewEmail.requestFocus();
            return;
        } else if (!Validator.checkPassword(password)) {
            editNewPassword.setError(getString(R.string.check_password));
            onSignupFailed();
            editNewPassword.requestFocus();
            return;
        } else if (!Validator.checkConfirmPassword(confirmPassword, password)) {
            editConfirmPassword.setError(getString(R.string.check_confirmed_password));
            onSignupFailed();
            editConfirmPassword.requestFocus();
            return;
        }

        //Charging indicator while the account creation
        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this, R.style.AlertDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.creating_account));
        progressDialog.show();

        ////Firebase account creating with email and password
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignUpActivity.this, getString(R.string.sign_up_successful), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, getString(R.string.sign_up_failed), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    /**
     * Shows a Toast message if email, password and confirmed password didn't respect format
     */
    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), getString(R.string.sign_up_failed), Toast.LENGTH_LONG).show();
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
