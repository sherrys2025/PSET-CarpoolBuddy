package com.example.carpoolbuddy.Activity;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.carpoolbuddy.Activity.Explore.VehicleProfileActivity;
import com.example.carpoolbuddy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * This is an Activity class that lets users sign in, sign up, or sign in with Google. This Activity is
 * the first to appear in this application.
 *
 * @author sherrys2025
 * @version 1.0
 */
public class AuthActivity extends AppCompatActivity {

    public static GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private EditText emailField;
    private EditText passwordField;

    private TextView errorMsg;
    private FirebaseFirestore firestore;


    /**
     * create Authorization page
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.editTextEmail);
        passwordField = findViewById(R.id.editTextPassword);
        errorMsg = findViewById(R.id.errorMsg);
        try {
            String errorMsg = getIntent().getStringExtra("errorMsg");
            displayErrorMessage(errorMsg);
        } catch (Exception e){
        }

    }

    /**
     * If user is signed in, open signed in page.
     */
    @Override
    public void onStart(){
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            updateUI(currentUser);
        }
    }

    /**
     * Start Loading Activity with intentions to sign in with Google
     * @param v View that called this method
     */
    public void googleSignIn(View v) {
        Intent intent = new Intent(this, LoadingActivity.class);
        intent.putExtra("type", "googleSignIn");
        startActivity(intent);
    }

    /**
     * If email or password is not entered properly, display error message
     * @param email
     * @param password
     * @return whether an error message has been displayed
     */
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

        return false;
    }

    /**
     * Display error message and format the words accordingly
     * @param exceptionMessage error message to be displayed
     */
    private void displayErrorMessage(String exceptionMessage){
        if (exceptionMessage.equals("The supplied auth credential is incorrect, malformed or has expired.")){
            errorMsg.setText("Incorrect password. Try again.");
        } else {
            if (exceptionMessage.length() < 40) {
                errorMsg.setText(exceptionMessage);
            } else {
                for (int i = 40; i > 0; i-=40) { // if error message is too long, turn into different lines
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

    /**
     * Start Loading Activity with intentions to sign in
     * @param v View that called this method
     */
    public void signIn(View v){
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if(checkEmailAndPassword(email, password)){
            return;
        }
        // start new intent
        Intent intent = new Intent(this, LoadingActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("type", "SignIn");
        startActivity(intent);
    }

    /**
     * Start Loading Activity with intentions to sign up
     * @param v View that called this method
     */
    public void signUp(View v){
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if(checkEmailAndPassword(email, password)){ //check email and password are proper
            return;
        }
        // start new intent
        Intent intent = new Intent(this, LoadingActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("type", "SignUp");
        startActivity(intent);
    }

    /**
     * Start intent to go to VehicleProfile page
     * @param user FirebaseUser
     */
    protected void updateUI(FirebaseUser user){
        Intent intent = new Intent(this, VehicleProfileActivity.class);
        startActivity(intent);
    }
}
