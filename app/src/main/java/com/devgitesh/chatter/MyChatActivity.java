package com.devgitesh.chatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyChatActivity extends AppCompatActivity {
    private RecyclerView recyclerViewMessage;
    private EditText editTextMessage;

    String userName, otherName;
    FirebaseDatabase database;
    DatabaseReference reference;

    MessageAdapter adapter;
    List<ModelClass> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chat);

        ImageView backImage = findViewById(R.id.imageViewBack);
        TextView textViewChat = findViewById(R.id.textViewChat);
        recyclerViewMessage =  findViewById(R.id.recycleViewMessage);
        editTextMessage = findViewById(R.id.editTextMessage);
        FloatingActionButton fabSend = findViewById(R.id.fabChat);

        recyclerViewMessage.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        userName = getIntent().getStringExtra("userName");
        otherName = getIntent().getStringExtra("otherName");
        textViewChat.setText(otherName);

        backImage.setOnClickListener(v -> {
            Intent intent = new Intent(MyChatActivity.this, MainActivity.class);
            startActivity(intent);
        });

        fabSend.setOnClickListener(v -> {
            String message = editTextMessage.getText().toString();
            if(!message.equals(""))
            {
                sendMessage(message);
                editTextMessage.setText("");
            }
        });

        getMessage();

    }

    public void sendMessage(String message)
    {
        String key = reference.child("Messages").child(userName).child(otherName).push().getKey();
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("message", message);
        messageMap.put("from", userName);
        assert key != null;
        reference.child("Messages").child(userName).child(otherName).child(key).setValue(messageMap)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        reference.child("Messages").child(otherName).child(userName).child(key).setValue(messageMap);
                    }
                });
    }

    public void  getMessage()
    {
        reference.child("Messages").child(userName).child(otherName)
                .addChildEventListener(new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        ModelClass modelClass = snapshot.getValue(ModelClass.class);
                        list.add(modelClass);
                        adapter.notifyDataSetChanged();
                        recyclerViewMessage.scrollToPosition(list.size()-1);

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        adapter = new MessageAdapter(list, userName);
        recyclerViewMessage.setAdapter(adapter);
    }
}