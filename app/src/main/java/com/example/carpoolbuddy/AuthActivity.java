package com.example.carpoolbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
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

import java.util.ArrayList;

public class AuthActivity extends AppCompatActivity {

    public static GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private EditText emailField;
    private EditText passwordField;

    private TextView errorMsg;
    private FirebaseFirestore firestore;


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
        try {
            String errorMsg = getIntent().getStringExtra("errorMsg");
            displayErrorMessage(errorMsg);
        } catch (Exception e){
        }

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

    // [START googlesignin]
    public void googleSignIn(View v) {
        Intent intent = new Intent(this, LoadingActivity.class);
        intent.putExtra("type", "googleSignIn");
        startActivity(intent);
    }
    // [END googlesignin]

//    void uploadData(FirebaseUser mUser){
//
//    }

    private boolean checkEmailAndPassword(String email, String password){
        if(email.equals(null) || email.equals("")) {
            errorMsg.setText("Please fill in the email.");
            return true;
        }
        if(password.equals(null) || password.equals("")) {
            errorMsg.setText("Please fill in the password.");
            return true;
        }
        if(!email.endsWith("cis.edu.hk")) {
            displayErrorMessage("Please use a CIS email address.");
            return true;
        }

        System.out.println(email);
        System.out.println(password);
        return false;
    }

    private void displayErrorMessage(String exceptionMessage){
        if (exceptionMessage.equals("The supplied auth credential is incorrect, malformed or has expired.")){
            errorMsg.setText("Incorrect password. Try again.");
        } else {
            if (exceptionMessage.length() < 40) {
                errorMsg.setText(exceptionMessage);
            } else {
                for (int i = 40; i > 0; i-=40) {
                    while (exceptionMessage.charAt(i) != ' '){
                        i--;
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
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if(checkEmailAndPassword(email, password)){
            return;
        }

        Intent intent = new Intent(this, LoadingActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("type", "SignIn");
        startActivity(intent);
    }
    public void signUp(View v){
        System.out.println("Sign up");

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if(checkEmailAndPassword(email, password)){
            return;
        }

        Intent intent = new Intent(this, LoadingActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("type", "SignUp");
        startActivity(intent);
    }

    protected void updateUI(FirebaseUser user){
        Intent intent = new Intent(this, VehicleProfileActivity.class);
        startActivity(intent);
    }
}
