package com.example.carpoolbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.app.Activity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private String selected;
    private Spinner sUserType;
    private EditText emailField;
    private EditText passwordField;

    private TextView errorMsg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("print");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END config_signin]

        mAuth = FirebaseAuth.getInstance();
//        firestore = FirebaseFirestore.getInstance();

        emailField = findViewById(R.id.editTextEmail);
        passwordField = findViewById(R.id.editTextPassword);
        errorMsg = findViewById(R.id.errorMsg);
    }
    @Override
    public void onStart(){
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            updateUI(currentUser);
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
                            Log.d("GoogleActivity", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("GoogleActivity", "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    // [END auth_with_google]

    // [START googlesignin]
    public void googleSignIn(View v) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END googlesignin]

//    void uploadData(FirebaseUser mUser){
//
//    }

    private void collectEmailAndPassword(String email, String password){
        email = emailField.getText().toString();
        System.out.println(email);
        password = passwordField.getText().toString();
        if(email.equals(null) || email.equals("")) {
            errorMsg.setText("Please fill in the email.");
            return;
        }
        if(password.equals(null) || password.equals("")) {
            errorMsg.setText("Please fill in the password.");
            return;
        }
        System.out.println(password);
        System.out.println();
    }

    private void displayErrorMessage(String exceptionMessage){
        if (exceptionMessage.equals("The supplied auth credential is incorrect, malformed or has expired.")){
            errorMsg.setText("Incorrect password. Try again.");
        } else {
            if (exceptionMessage.length() < 20) {
                errorMsg.setText(exceptionMessage);
            } else {
                for (int i = 20; i > 0; i--) {
                    while (exceptionMessage.charAt(i) != ' '){
                        i++;
                    }
                    exceptionMessage = exceptionMessage.substring(0,i) + "\n" + exceptionMessage.substring(i);
                    System.out.println(exceptionMessage);
                }
                errorMsg.setText(exceptionMessage);
            }
        }
    }

    public void signIn(View v){
        System.out.println("Sign in");
        String email = "", password = "";
        collectEmailAndPassword(email, password);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
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
                    }
                });
    }
    public void signUp(View v){
        System.out.println("Sign up");
        String email = "", password = "";
        collectEmailAndPassword(email, password);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("SIGN UP", "signUpWithCustomToken:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SIGN UP", "signUpWithEmail:failure", task.getException());
                            String exceptionMessage = task.getException().getMessage();
                            displayErrorMessage(exceptionMessage);
                        }
                    }
                });
    }

    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Email sent
                    }
                });
        // [END send_email_verification]
    }

    protected void updateUI(FirebaseUser user){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
