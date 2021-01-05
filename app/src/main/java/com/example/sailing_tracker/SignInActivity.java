package com.example.sailing_tracker;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class SignInActivity extends AppCompatActivity{
    private static final String TAG = "EmailPassword";
    private static final int RC_SIGN_IN = 100 ;
    GoogleSignInClient mGoogleSignInClient;


    // Views
    EditText mEmailEt, mPasswordEt;
    Button mLoginBtn;
    TextView mRecoverPassTv;
    SignInButton mGoogleLoginBtn;

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
        mLoginBtn = findViewById(R.id.signIn_btn);
        mRecoverPassTv = findViewById(R.id.mRecoverPassTv);
        mGoogleLoginBtn = findViewById(R.id.googleLoginBtn);


        // Before mAuth
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();




        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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

        // Handle Google Login button click
        mGoogleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Begin Google login process
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

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
                    Toast.makeText(SignInActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(SignInActivity.this, "Failed to send email", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();

                // Get and show full error message
                Toast.makeText(SignInActivity.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void loginUser(String email, String password) {
        // Email and password pattern is valid, show progress dialogue and start registering user
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        progressDialog.dismiss();

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
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
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
            Toast.makeText(SignInActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });

  }

    public void  updateUI(FirebaseUser account){
        if(account != null){
            Toast.makeText(this,"Login successful",Toast.LENGTH_LONG).show();
            startActivity(new Intent(SignInActivity.this, DashboardActivity.class));
        }else {
            Toast.makeText(this,"Login failed",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Go to previous activity
        return super.onSupportNavigateUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();


                            // If user is signing first time then get and show user info of google account

                            if(task.getResult().getAdditionalUserInfo().isNewUser()){
                                // Get email and userID from authentication
                                String email = user.getEmail();
                                String uid = user.getUid();
                                // When a user is registered store info in firebase realtime database
                                // using HashMap
                                HashMap<Object, String> hashMap = new HashMap<>();
                                // Put info into HashMap
                                hashMap.put("email", email);
                                hashMap.put("uid", uid);
                                hashMap.put("name", ""); // To be added later in profile
                                hashMap.put("phone", ""); //
                                hashMap.put("image", ""); //
                                hashMap.put("boatClass", ""); //
                                // Firebase database instance
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                // Path toe store user data named "Users"
                                DatabaseReference reference = database.getReference("Users");
                                // Put data within HashMap in database
                                reference.child(uid).setValue(hashMap);

                            }
                            Log.d(TAG, "signInWithCredential:success");
                            // Show user email in toast
                            assert user != null;
                            Toast.makeText(SignInActivity.this, ""+user.getEmail(), Toast.LENGTH_SHORT).show();
                           updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Login failed..", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Get and  show error message
              Toast.makeText(SignInActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        });


    }


}


// TODO: 17/12/2020 Upload not working causes crash to sign in screen 
// TODO: 17/12/2020  





