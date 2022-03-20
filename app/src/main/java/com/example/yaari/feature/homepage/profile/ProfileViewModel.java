package com.example.yaari.feature.homepage.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.yaari.data.Repository;
import com.example.yaari.model.GeneralResponse;
import com.example.yaari.model.Profile.ProfileResponse;
import com.example.yaari.model.post.PostResponse;

import java.util.Map;

import okhttp3.MultipartBody;

public class ProfileViewModel extends ViewModel {

    private Repository repository;

    public ProfileViewModel(Repository repository) {
        this.repository = repository;
    }
    public LiveData<ProfileResponse> fetchProfileResponse(Map<String,String> params){
        return repository.fetchProfileInfo(params);

    }
    public LiveData<PostResponse> getProfilePosts(Map<String,String> params){
        return repository.getProfilePosts(params);

    }
    public LiveData<GeneralResponse> uploadPost(MultipartBody multipartBody, Boolean isCoverOrProfileImage){
        return this.repository.uploadPost(multipartBody,isCoverOrProfileImage);
    }
    public LiveData<GeneralResponse> performAction(ProfileActivity.PerformAction performAction){
        return this.repository.performOperation(performAction);
    }
}
