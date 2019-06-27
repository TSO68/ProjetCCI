package com.example.projetcci;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    Button btnSignup;
    TextView linkLogin;

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
        linkLogin = (TextView) findViewById(R.id.linkLogin);

        mAuth = FirebaseAuth.getInstance();

        //Sign up
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_up();
            }
        });

        //Open LoginActivity
        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
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
     * Check if email and passwords respect format
     * @return valid true or false
     */
    public boolean validate() {
        boolean valid = true;

        String email = editNewEmail.getText().toString();
        String password = editNewPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();

        //Check the email content and pattern
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editNewEmail.setError(getString(R.string.check_email));
            valid = false;
        } else {
            editNewEmail.setError(null);
        }

        //Check the password content and length
        if (password.isEmpty() || password.length() < 4) {
            editNewPassword.setError(getString(R.string.check_password));
            valid = false;
        } else {
            editNewPassword.setError(null);
        }

        //Check the confirmed password content, length and matching with password
        if (confirmPassword.isEmpty() || confirmPassword.length() < 4 || !(confirmPassword.equals(password))) {
            editConfirmPassword.setError(getString(R.string.check_confirmed_password));
            valid = false;
        } else {
            editConfirmPassword.setError(null);
        }

        return valid;
    }

    /**
     * Sign up to the app via Firebase
     */
    public void sign_up() {

        //Check the validation of the signup form
        if (!validate()) {
            onSignupFailed();
            return;
        }

        //Charging indicator while the account creation
        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.creating_account));
        progressDialog.show();

        String email = editNewEmail.getText().toString();
        String password = editNewPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();

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
