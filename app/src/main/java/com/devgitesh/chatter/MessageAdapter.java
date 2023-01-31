package com.devgitesh.chatter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.messageViewHolder>{
    List<ModelClass> list;
    String userName;

    Boolean status;
    int send, receive;

    public MessageAdapter(List<ModelClass> list, String userName) {
        this.list = list;
        this.userName = userName;

        status = false;
        send = 1;
        receive = 2;
    }

    @NonNull
    @Override
    public messageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        if(viewType == send){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_send, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_received, parent, false);
        }
        return new messageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull messageViewHolder holder, int position) {

        holder.textView.setText(list.get(position).getMessage());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class messageViewHolder extends RecyclerView.ViewHolder{

        TextView textView;


        public messageViewHolder(@NonNull View itemView) {
            super(itemView);
            if(status)
            {
                textView = itemView.findViewById(R.id.textViewSend);
            } else {
                textView = itemView.findViewById(R.id.textViewReceived);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getFrom().equals(userName))
        {
            status = true;
            return send;
        } else {
            status = false;
            return receive;
        }
    }
}
