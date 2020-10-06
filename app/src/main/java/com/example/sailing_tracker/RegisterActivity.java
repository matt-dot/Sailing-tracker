package com.example.sailing_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";



    // Views
    EditText mEmailEt, mPasswordEt;;
    Button mRegisterBtn;

    // Progressbar to display while registering user
    ProgressDialog progressDialog;

    // Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Init
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id. passwordEt);
        mRegisterBtn = findViewById(R.id.registerBtn);

        // In the onCreate() method, initialize the FirebaseAuth instance.
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User..");




        // Handle register button click
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pattern pattern = Pattern.compile("^(?=\\P{Ll}*\\p{Ll})(?=\\P{Lu}*\\p{Lu})(?=\\P{N}*\\p{N})[\\s\\S]{8,}$\n");
                // Input email, password
                String email = mEmailEt.getText().toString().trim();
                String password = mPasswordEt.getText().toString().trim();

                Matcher matcher = pattern.matcher(password);
                boolean matchFound = matcher.find();

                // Validate
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    // Set error and focus to email EditText
                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);
                } else if(password.length() < 6) {
                    // Set error and focus to the password EditText
                    mPasswordEt.setError("Password length at least 6 characters");
                    mPasswordEt.setFocusable(true);
                } else if (!matchFound){
                    mPasswordEt.setError("Password requires at least one lowercase, uppercase, number, and symbol exist in a 8+");
                    mPasswordEt.setFocusable(true);
                } else {
                    registerUser(email, password); // Register the user
                }
            }
        });
    }


    private void registerUser(String email, String password) {
        // Email and password pattern is valid, show progress dialogue and start registering user
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Error, dismiss progress dialogue and get and show the error message
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

        public void  updateUI(FirebaseUser account){
            if(account != null){
                Toast.makeText(this,"Registration successful",Toast.LENGTH_LONG).show();
                startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
            }else {
                Toast.makeText(this,"Registration failed",Toast.LENGTH_LONG).show();
            }
        }




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Go to previous activity
        return super.onSupportNavigateUp();
    }
}
