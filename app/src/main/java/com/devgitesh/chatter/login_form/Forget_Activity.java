package com.devgitesh.chatter.login_form;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.devgitesh.chatter.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class Forget_Activity extends AppCompatActivity {

    private TextInputEditText email;
    private ProgressBar progressBar_Forget;
    private FirebaseAuth auth;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.forget_email);
        progressBar_Forget = findViewById(R.id.progressBar_forget);

        Button reset = findViewById(R.id.reset_button);
        reset.setOnClickListener(view -> {
            String userEmail = email.getText().toString();

            if (!userEmail.equals("")) {
                restPassword(userEmail);
            }
        });
    }

    public void restPassword(String userEmail) {

        progressBar_Forget.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        progressBar_Forget.setVisibility(View.INVISIBLE);
                        Toast.makeText(Forget_Activity.this
                                , "Please check your to reset password", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Forget_Activity.this, Sign_in_Activity.class);
                        startActivity(i);
                        finish();

                    } else {
                        Toast.makeText(Forget_Activity.this
                                , task.getException().getLocalizedMessage()
                                , Toast.LENGTH_LONG).show();
                    }

                });
    }
}