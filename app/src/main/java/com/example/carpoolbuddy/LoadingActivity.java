package com.example.carpoolbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.carpoolbuddy.Users.Alum;
import com.example.carpoolbuddy.Users.Staff;
import com.example.carpoolbuddy.Users.Student;
import com.example.carpoolbuddy.Users.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoadingActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;

    public static GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private FirebaseFirestore firestore;
    private String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END config_signin]

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
        progressBar.animate();
        type = getIntent().getStringExtra("type");

        switch (type){
            case "SignIn":
                signIn();
                break;
            case "SignUp":
                signUp();
                break;
            default:
                googleSignIn();
                break;
        }

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
                Log.d("GoogleActivity", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("GoogleActivity", "Google sign in failed", e);
                System.out.println(e.getMessage());
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("GoogleActivity SIGNIN", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(!user.getEmail().endsWith("cis.edu.hk")) {
                                FirebaseAuth.getInstance().signOut();
                                mGoogleSignInClient.signOut();
                                displayErrorMessage("Please use a CIS email address.");
                            } else {
                                updateUI(user);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("GoogleActivity SIGNIN", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
    // [END auth_with_google]

    // [START googlesignin]
    public void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END googlesignin]

//    void uploadData(FirebaseUser mUser){
//
//    }

    private void displayErrorMessage(String exceptionMessage){
        Intent intent = new Intent(this, AuthActivity.class);
        intent.putExtra("errorMsg", exceptionMessage);
        startActivity(intent);
    }

    public void signIn(){
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("SIGN IN", "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("SIGN IN", "signInWithEmail:failure", task.getException());
                        String exceptionMessage = task.getException().getMessage();
                        displayErrorMessage(exceptionMessage);
                    }
                });
    }
    public void signUp(){
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        Log.d("SIGN UP", "signUpWithCustomToken:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        sendEmailVerification();
                        updateFirebase(user);
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("SIGN UP", "signUpWithEmail:failure", task.getException());
                        String exceptionMessage = task.getException().getMessage();
                        displayErrorMessage(exceptionMessage);
                    }
                });
    }

    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    // Email sent
                });
        // [END send_email_verification]
    }

    private void updateFirebase(FirebaseUser currentUser){
        System.out.println("updating firebase");
        String email = currentUser.getEmail();
        if (email.contains("@student.")) {
            Student user = new Student(currentUser);
            firestore.collection("users").document(user.getUid()).set(user)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()){
                            Log.w("Updated Firestore", "yay");
                        } else {
                            Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                        }
                    });
        } else if (email.contains("@alumni.")) {
            Alum user = new Alum(currentUser);
            firestore.collection("users").document(user.getUid()).set(user)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()){
                            Log.w("Updated Firestore", "yay");
                        } else {
                            Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                        }
                    });
        } else {
            Staff user = new Staff(currentUser);
            firestore.collection("users").document(user.getUid()).set(user)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()){
                            Log.w("Updated Firestore", "yay");
                        } else {
                            Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                        }
                    });
        }
    }

    protected void updateUI(FirebaseUser user){

        final Class[] nextClass = new Class[1];
        firestore.collection("users").document(user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot ds = task.getResult();
                User user1 = ds.toObject(User.class);

                if (user1.isSetUp()){
                    nextClass[0] = SettingsActivity.class;
                } else {
                    nextClass[0] = VehicleProfileActivity.class;
                }
            } else {
                Log.w("Failed get user", task.getException());
            }
        });

        nextClass[0] = MainActivity.class;
        Intent intent = new Intent(this, nextClass[0]);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }
}