package com.devgitesh.chatter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    List<String> userList;
    String userName;
    Context mContext;
    FirebaseDatabase database;
    DatabaseReference reference;

    public UsersAdapter(List<String> userList, String userName, Context mContext) {
        this.userList = userList;
        this.userName = userName;
        this.mContext = mContext;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        reference.child("users").child(userList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String otherName = Objects.requireNonNull(snapshot.child("userName").getValue()).toString();
                String imageURL = Objects.requireNonNull(snapshot.child("image").getValue()).toString();

                holder.textViewUsers.setText(otherName);

                if(imageURL.equals(""))
                {
                    holder.circleImageView.setImageResource(R.drawable.account);
                } else
                {
                    Picasso.get().load(imageURL).into(holder.circleImageView);
                }

                holder.cardView.setOnClickListener(v -> {
                    Intent intent = new Intent(mContext, MyChatActivity.class);
                    intent.putExtra("userName", userName);
                    intent.putExtra("otherName", otherName);
                    mContext.startActivity(intent);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textViewUsers;
        private final CircleImageView circleImageView;
        private final CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewUsers = itemView.findViewById(R.id.textViewUser);
            circleImageView = itemView.findViewById(R.id.user_Id);
            cardView = itemView.findViewById(R.id.cardView);


        }
    }
}
