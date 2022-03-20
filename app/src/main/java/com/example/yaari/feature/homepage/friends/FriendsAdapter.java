package com.example.yaari.feature.homepage.friends;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yaari.R;
import com.example.yaari.data.remote.ApiClient;
import com.example.yaari.feature.homepage.profile.ProfileActivity;
import com.example.yaari.model.friend.Friend;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    Context context;
    List<Friend> friends;

    public FriendsAdapter(Context context, List<Friend> friends) {
        this.context = context;
        this.friends = friends;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Friend item=friends.get(position);
        holder.profileName.setText(item.getName());
        String image="";
        if(Uri.parse(item.getProfileUrl()).getAuthority()==null){
            image= ApiClient.BASE_URL+item.getProfileUrl();
        }else{
            image=item.getProfileUrl();
        }
        Glide.with(context).load(image).placeholder(R.drawable.default_profile_placeholder).into(holder.profileImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("uid",item.getUid()));
            }
        });



    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView profileName;
        Button btnAcceptRequest;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            profileImage=itemView.findViewById(R.id.profileimage);
            profileName=itemView.findViewById(R.id.profile_name);
            btnAcceptRequest=itemView.findViewById(R.id.btn_accept);
            btnAcceptRequest.setVisibility(View.GONE);
        }
    }
}
