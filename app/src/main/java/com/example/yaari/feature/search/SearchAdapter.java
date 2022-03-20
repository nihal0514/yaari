package com.example.yaari.feature.search;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yaari.R;
import com.example.yaari.data.remote.ApiClient;
import com.example.yaari.feature.homepage.profile.ProfileActivity;
import com.example.yaari.model.search.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    Context context;
    List<User>userList;

    public SearchAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_search,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        User user=userList.get(position);
        String userImage="";
        Uri userUri=Uri.parse(user.getProfileUrl());
        if(userUri.getAuthority()==null){
            userImage= ApiClient.BASE_URL+user.getProfileUrl();
        }else{
            userImage=user.getProfileUrl();
        }if(!userImage.isEmpty()){
            Glide.with(context).load(userImage).placeholder(R.drawable.default_profile_placeholder).into(holder.profileImage);
        }
        holder.profileName.setText(user.getName());
        holder.itemView.setOnClickListener(l_->{
            context.startActivity(new Intent(context,ProfileActivity.class).putExtra("uid",userList.get(holder.getAdapterPosition()).getUid()));
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView profileName;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            profileImage=itemView.findViewById(R.id.user_image);
            profileName=itemView.findViewById(R.id.user_name);
        }


    }
}
