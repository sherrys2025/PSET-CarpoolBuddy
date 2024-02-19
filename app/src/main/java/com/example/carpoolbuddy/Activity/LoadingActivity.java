package com.example.carpoolbuddy.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.carpoolbuddy.Activity.Explore.VehicleProfileActivity;
import com.example.carpoolbuddy.R;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This is an Activity class that lets users sign in, sign up, or sign in with Google. This Activity processes
 * sign in after user has entered their email and password.
 *
 * @author sherrys2025
 * @version 1.0
 */
public class LoadingActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;

    public static GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private FirebaseFirestore firestore;
    private String type;

    private User user;
    private Alum alumUser;
    private Student studentUser;
    private Staff staffUser;

    /**
     * On creating this activity...
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        //Google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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

    /**
     * Sign in using Google
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
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

    /**
     * Sign into Firebase with the Google credentials
     * @param idToken GoogleSignInAccount's Id token
     */
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

    /**
     * Start Google sign in intent
     */
    public void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * If sign in fails, return to Auth Activity with error message
     * @param exceptionMessage error message to be displayed
     */
    private void displayErrorMessage(String exceptionMessage){
        Intent intent = new Intent(this, AuthActivity.class);
        intent.putExtra("errorMsg", exceptionMessage);
        startActivity(intent);
    }

    /**
     * Sign in without Google
     */
    public void signIn(){
        String email = getIntent().getStringExtra("email"); //retrieve information
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

    /**
     * Sign Up without Google
     */
    public void signUp(){
        String email = getIntent().getStringExtra("email"); //retrieve information
        String password = getIntent().getStringExtra("password");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        Log.d("SIGN UP", "signUpWithCustomToken:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        sendEmailVerification();
                        updateFirebase(user);

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("SIGN UP", "signUpWithEmail:failure", task.getException());
                        String exceptionMessage = task.getException().getMessage();
                        displayErrorMessage(exceptionMessage);
                    }
                });
    }

    /**
     * Send email verification after sign up
     */
    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    // Email sent
                });
    }

    /**
     * Update Firebase with new User
     * @param currentUser FirebaseUser's current user
     */
    private void updateFirebase(FirebaseUser currentUser){
        System.out.println("updating firebase, " + currentUser.getEmail());
        String email = currentUser.getEmail();
        if (email.contains("@student.")) { // if Student:
            Student user = new Student(currentUser);
            firestore.collection("users").document(user.getUid()).set(user)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()){
                            Log.w("Updated Firestore", "yay");
                            updateUI(currentUser);
                        } else {
                            Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                        }
                    });
        } else if (email.contains("@alumni.")) { // if Alumni:
            Alum user = new Alum(currentUser);
            firestore.collection("users").document(user.getUid()).set(user)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()){
                            Log.w("Updated Firestore", "yay");
                            updateUI(currentUser);
                        } else {
                            Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                        }
                    });
        } else { // if Staff:
            Staff user = new Staff(currentUser);
            firestore.collection("users").document(user.getUid()).set(user)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()){
                            Log.w("Updated Firestore", "yay");
                            updateUI(currentUser);
                        } else {
                            Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                        }
                    });
        }
    }

    /**
     * Lead to next intent based on whether user has been set up or not
     * @param user Firebase's current user
     */
    protected void updateUI(FirebaseUser user){

        final Class[] nextClass = new Class[1];
        DocumentReference docRef = firestore.collection("users").document(user.getUid());

        docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        User user1 = document.toObject(User.class);
                        if (!user1.isSetUp()){ // if not set up
                            nextClass[0] = SettingsActivity.class;
                        } else { // if set up
                            nextClass[0] = VehicleProfileActivity.class;
                        }
                        Intent intent = new Intent(this, nextClass[0]);
                        intent.putExtra("uid", user.getUid());
                        startActivity(intent);
                    }
                }).addOnFailureListener(e -> { // if failed to retrieve, create new user (only for new Google sign ins)
                    updateFirebase(user);
                });
    }


}