package com.devgitesh.chatter.login_form;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devgitesh.chatter.MainActivity;
import com.devgitesh.chatter.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Sign_in_Activity extends AppCompatActivity {

    private TextInputEditText email, password;
    private ProgressBar progressBar_signIn;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            Intent i = new Intent(Sign_in_Activity.this, MainActivity.class);
            startActivity(i);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.signIn_email);
        password = findViewById(R.id.signIn_password);
        TextView forget = findViewById(R.id.signIn_forget);
        TextView signUp = findViewById(R.id.signUp_text);
        progressBar_signIn = findViewById(R.id.progressBar_SignIn);

        Button signIn = findViewById(R.id.signIn_button);
        signIn.setOnClickListener(View -> {

            String userEmail = Objects.requireNonNull(email.getText()).toString();
            String userPassword = Objects.requireNonNull(password.getText()).toString();

            if(!userEmail.equals("") && !userPassword.equals(""))
            {
                signInFirebase(userEmail, userPassword);
            } else
            {
                Toast.makeText(this, "Please fill correctly", Toast.LENGTH_LONG).show();
            }

        });
        forget.setOnClickListener(View -> {
            Intent i = new Intent(Sign_in_Activity.this, Forget_Activity.class);
            startActivity(i);

        });
        signUp.setOnClickListener(View -> {
            Intent i = new Intent(Sign_in_Activity.this, Sign_up_Activity.class);
            startActivity(i);

        });
    }

    public void signInFirebase(String email, String password) {
        progressBar_signIn.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        progressBar_signIn.setVisibility(View.INVISIBLE);
                        Toast.makeText(Sign_in_Activity.this, "Welcome Again!", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(Sign_in_Activity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(Sign_in_Activity.this, "Sign In Error", Toast.LENGTH_LONG).show();
                    }
                });
    }
}