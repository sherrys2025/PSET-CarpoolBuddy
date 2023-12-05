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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthActivity extends AppCompatActivity {

//    private GoogleSignInClient mGoogleSignInClient;
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

//    void onActivityResult(...){
//
//    }
//    void uploadData(FirebaseUser mUser){
//
//    }
//    private void firebaseAuthWithGoogle(String idToken){
//
//    }

    public void signIn(View v){
        System.out.println("Sign in");
        String email = emailField.getText().toString();
        System.out.println(email);
        String password = passwordField.getText().toString();
        System.out.println(password);
        System.out.println();

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
                            errorMsg.setText(task.getException().getMessage());
                        }
                    }
                });
    }
    public void signUp(View v){
        System.out.println("Sign up");
        String email = emailField.getText().toString();
        System.out.println(email);
        String password = passwordField.getText().toString();
        System.out.println(password);
        System.out.println();

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
                            Log.w("SIGN UP", "signUpWithCustomToken:failure", task.getException());
                            errorMsg.setText(task.getException().getMessage());
                            //updateUI(null);
                        }
                    }
                });
    }

    void updateUI(FirebaseUser user){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}