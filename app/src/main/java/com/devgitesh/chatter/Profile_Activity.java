package com.devgitesh.chatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.devgitesh.chatter.login_form.Sign_up_Activity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_Activity extends AppCompatActivity {

    private CircleImageView profile_image;
    private TextInputEditText profile_Name;
    private Button saveProfile;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser user;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Uri imageUri;
    Boolean imageControl = false;

    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_image = findViewById(R.id.profile_image);
        profile_Name = findViewById(R.id.profileName);
        saveProfile = findViewById(R.id.saveProfile);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        user = auth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        getUserInfo();

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateProfile();
            }
        });
    }

    public void updateProfile()
    {
        String userName = profile_Name.getText().toString();
        reference.child("users").child(user.getUid()).child("userName").setValue(userName);

        if (imageControl) {
            UUID randomID = UUID.randomUUID();
            String imageName = "images/" + userName + "/" +randomID + "/" + ".jpg";
            storageReference.child(imageName).putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        StorageReference myStorageRef = firebaseStorage.getReference(imageName);
                        myStorageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                            String filePath = uri.toString();
                            reference.child("users").child(auth.getUid()).child("image").setValue(filePath)
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(Profile_Activity.this,
                                            "Write to database is Successful", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(Profile_Activity.this,
                                                    "Write to database is Unsuccessful", Toast.LENGTH_SHORT).show());
                        });
                    });
        } else {
            reference.child("users").child(auth.getUid()).child("image").setValue(image);
        }

        Toast.makeText(Profile_Activity.this, "Welcome!", Toast.LENGTH_LONG).show();
        Intent i = new Intent(Profile_Activity.this, MainActivity.class);
        i.putExtra("userName", userName);
        startActivity(i);
        finish();
    }

    public void getUserInfo()
    {
        reference.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("userName").getValue().toString();
                image = snapshot.child("image").getValue().toString();
                profile_Name.setText(name);
                if(image.equals(null)){
                    profile_image.setImageResource(R.drawable.person);
                } else {
                    Picasso.get().load(image).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
            Picasso.get().load(imageUri).into(profile_image);
            imageControl = true;
        } else {
            imageControl = false;
        }
    }


}