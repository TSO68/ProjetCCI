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

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    EditText editNewEmail, editNewPassword, editConfirmPassword;
    Button btnSignup;
    TextView linkLogin;

    private static final String TAG = "SignupActivity";

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish();
        }
    }

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

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_up();
            }
        });

        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        String email = editNewEmail.getText().toString();
        String password = editNewPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editNewEmail.setError("Please enter a valid email address");
            valid = false;
        } else {
            editNewEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            editNewPassword.setError("Password need more than 4 characters");
            valid = false;
        } else {
            editNewPassword.setError(null);
        }

        if (confirmPassword.isEmpty() || confirmPassword.length() < 4 || !(confirmPassword.equals(password))) {
            editConfirmPassword.setError("Password does not match");
            valid = false;
        } else {
            editConfirmPassword.setError(null);
        }

        return valid;
    }

    public void sign_up() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String email = editNewEmail.getText().toString();
        String password = editNewPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignUpActivity.this,"Sign up successful",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this,"Sign up failed",Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        return true;
    }
}
