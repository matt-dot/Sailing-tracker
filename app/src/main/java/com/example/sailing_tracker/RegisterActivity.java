package com.example.sailing_tracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";

    // Views
    EditText mEmailEt, mPasswordEt, mConfirmPasswordEt;
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
        mConfirmPasswordEt = findViewById(R.id.confirmPasswordEt);


        // In the onCreate() method, initialize the FirebaseAuth instance.
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering  User..");




        // Handle register button click
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Input email, password
                String email = mEmailEt.getText().toString().trim();
                String password = mPasswordEt.getText().toString().trim();
                String confirmPassword = mConfirmPasswordEt.getText().toString().trim();



                // Validate the user entered data
                // Email must be valid i.e. not have @
                // Password must conform to the RegEx (8 >= password <= 20, at least 1 capital, at least 1 lowercase
                // at least one symbol)
                // User must enter the same password twice
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    // Set error and focus to email EditText
                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);

                } else if (!isValidPassword(password)) {
                    mPasswordEt.setError("Password requires at least one lowercase, uppercase, between 8 and 20 characters (inclusive) and at least 1 symbol ");
                    mPasswordEt.setFocusable(true);

                } else if(!password.equals(confirmPassword)){
                    mPasswordEt.setError("Passwords do not match");
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
        // Calls the mAuth method to handle the authentication with firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            // Check that the user is not already lined in
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Get email and userID from authentication
                            assert user != null;
                            String email = user.getEmail();
                            String uid = user.getUid();

                            // When a user is registered store info in firebase realtime database
                            // using HashMap
                            HashMap<Object, String> hashMap = new HashMap<>();
                            // Put info into HashMap
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", ""); // To be added later in profile
                            hashMap.put("image", ""); //
                            // Firebase database instance
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            // Path toe store user data named "Users"
                            DatabaseReference reference = database.getReference("Users");
                            // Put data within HashMap in database
                            reference.child(uid).setValue(hashMap);
                            // Call the updateUI method, this changes the UI to the profile
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // Call the updateUI method, do not change UI as authentication has failed
                            updateUI(null);
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Error, dismiss progress dialogue and get and show the generated error message
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    // Method to change the UI according to whether authentication has been successful for not
    public void  updateUI(FirebaseUser account){
            // When account is not null update the UI to show the profile page
            if(account != null){
                Toast.makeText(this,"Registration successful",Toast.LENGTH_LONG).show();
                startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
            // The authentication has failed, do not change UI
            }else {

                Toast.makeText(this,"Registration failed",Toast.LENGTH_LONG).show();
            }
        }

    // Method to compare the password entered to the defined RegEx
    public static boolean isValidPassword(String password)
    {

        // RegEx to check valid password
        // RegEx is defined as having at least 1 number, at least 1 lower case, at least 1 upper case
        // at least 1 symbol, and need to be between 8 and 20 characters
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        // Compile the ReGex
        Pattern pattern = Pattern.compile(regex);

        // If the password is empty
        // return false
        if (password == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given password
        // and regular expression.
        Matcher matcher = pattern.matcher(password);

        // Return if the password
        // matched the ReGex
        return matcher.matches();
    }

    // Allows navigation between pages
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Go to previous activity
        return super.onSupportNavigateUp();
    }
}
