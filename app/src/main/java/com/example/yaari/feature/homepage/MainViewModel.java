package com.example.yaari.feature.homepage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.yaari.data.Repository;
import com.example.yaari.feature.auth.LoginActivity;
import com.example.yaari.feature.homepage.profile.ProfileActivity;
import com.example.yaari.model.GeneralResponse;
import com.example.yaari.model.auth.AuthResponse;
import com.example.yaari.model.friend.Friend;
import com.example.yaari.model.friend.FriendResponse;
import com.example.yaari.model.post.PostResponse;
import com.example.yaari.model.publicpost.PublicPostResponse;

import java.util.Map;

public class MainViewModel extends ViewModel {
    private Repository repository;
//ye ham data cache karte hai
    //1step-cache
    private MutableLiveData<FriendResponse> friends=null;

    public MainViewModel(Repository repository) {
        this.repository = repository;
    }
    public LiveData<FriendResponse> loadFriends(String uid){
        //2 step-cache
        if(friends==null){
            friends=repository.loadFriends(uid);

        }
        //3-step-cache
        return friends;
    }

    public LiveData<GeneralResponse> performAction(ProfileActivity.PerformAction performAction){
        return this.repository.performOperation(performAction);
    }
    public LiveData<PostResponse> getNewsFeed(Map<String,String> params){
        return this.repository.getNewsFeed(params);
    }

    public LiveData<PublicPostResponse> getPublicPosts(Map<String,String> params){
        return this.repository.getPublicPosts(params);
    }
}
