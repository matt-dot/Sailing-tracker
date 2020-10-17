package com.example.sailing_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity{
    private static final String TAG = "EmailPassword";


    // Views
    EditText mEmailEt, mPasswordEt;
    Button mLoginBtn;
    TextView mRecoverPassTv;

    // Progress bar to display message while logging in user
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views and assign to variables
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mLoginBtn = findViewById(R.id.login_btn);
        mRecoverPassTv = findViewById(R.id.mRecoverPassTv);


        mPasswordEt.setTransformationMethod(new PasswordTransformationMethod());

        // In the onCreate() method, init the firebase auth instance
        mAuth = FirebaseAuth.getInstance();





        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in User..");

        // Handle register button click
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Input email, password
                String email = mEmailEt.getText().toString().trim();
                String password = mPasswordEt.getText().toString().trim();
                // Validate
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    // Set error and focus to email EditText
                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);

                } else {
                    loginUser(email, password); // Register the user
                }
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");

        // Recover password
        mRecoverPassTv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showRecoverPasswordDialog();

            }
        });
    }

    private void showRecoverPasswordDialog(){
        //Alert dialogue
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        // Set linear layout
        LinearLayout linearLayout = new LinearLayout(this);

        // Views to set in dialogue
        final EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        /* Sets the min width of a EditView to fit a text of n 'M' letters
        regardless of the actual text extension and text size */

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);


        builder.setView(linearLayout);

        // Buttons recover
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Input email
                String email = emailEt.getText().toString().trim();
                beginRecovery(email);

            }
        });

        // Buttons cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss dialog
                dialog.dismiss();
            }
        });

        // Show dialogue
        builder.create().show();

    }




    private void beginRecovery(String email){
        // Show progress dialog
        progressDialog.setMessage("Sending email...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Failed to send email", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();

                // Get and show full error message
                Toast.makeText(LoginActivity.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });


    }






    private void loginUser(String email, String password) {
        // Email and password pattern is valid, show progress dialogue and start registering user
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                                }
                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            // Error, dismiss progress dialogue and get and show the error message
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });

  }






    public void  updateUI(FirebaseUser account){
        if(account != null){
            Toast.makeText(this,"Login successful",Toast.LENGTH_LONG).show();
            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
        }else {
            Toast.makeText(this,"Login failed",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Go to previous activity
        return super.onSupportNavigateUp();
    }
}








