package com.devgitesh.chatter.login_form;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.devgitesh.chatter.MainActivity;
import com.devgitesh.chatter.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Sign_up_Activity extends AppCompatActivity {

    private TextInputEditText email, user, password;
    private CircleImageView user_Image;
    private ProgressBar progressBar_SignUp;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    Boolean imageControl = false;
    Uri imageUri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        user_Image = findViewById(R.id.user_image);
        email = findViewById(R.id.signUp_email);
        password = findViewById(R.id.signUp_password);
        user = findViewById(R.id.signIn_userName);
        progressBar_SignUp = findViewById(R.id.progressBar_SignUp);

        user_Image.setOnClickListener(v -> imageChooser());

        Button signUp = findViewById(R.id.signUp_button);
        signUp.setOnClickListener(View -> {

            String userEmail = Objects.requireNonNull(email.getText()).toString();
            String userName = Objects.requireNonNull(user.getText()).toString();
            String userPassword = Objects.requireNonNull(password.getText()).toString();

            if (!userEmail.equals("") && !userName.equals("") && !userPassword.equals("")) {
                signUpFirebase(userEmail, userPassword, userName);
            } else {
                Toast.makeText(this, "Please fill correctly", Toast.LENGTH_LONG).show();
            }

        });
    }

    public void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(user_Image);
            imageControl = true;
        } else {
            imageControl = false;
        }
    }

    public void signUpFirebase(String email, String password, String userName) {
        progressBar_SignUp.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reference.child("users").child(Objects.requireNonNull(auth.getUid())).child("userName").setValue(userName);

                        if (imageControl) {
                            UUID randomID = UUID.randomUUID();
                            String imageName = "images/" + userName + "/" +randomID + "/" + ".jpg";
                            storageReference.child(imageName).putFile(imageUri)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        StorageReference myStorageRef = firebaseStorage.getReference(imageName);
                                        myStorageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            String filePath = uri.toString();
                                            reference.child("users").child(auth.getUid()).child("image").setValue(filePath)
                                                    .addOnSuccessListener(aVoid -> Toast.makeText(Sign_up_Activity.this,
                                                            "Write to database is Successful", Toast.LENGTH_SHORT).show()).addOnFailureListener(e ->
                                                            Toast.makeText(Sign_up_Activity.this,
                                                                    "Write to database is Unsuccessful", Toast.LENGTH_SHORT).show());
                                        });
                                    });
                        } else {
                            reference.child("users").child(auth.getUid()).child("image").setValue(null);
                        }

                        progressBar_SignUp.setVisibility(View.INVISIBLE);
                        Toast.makeText(Sign_up_Activity.this, "Welcome!", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(Sign_up_Activity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(Sign_up_Activity.this, "Sign Up error", Toast.LENGTH_LONG).show();

                    }
                });
    }
}