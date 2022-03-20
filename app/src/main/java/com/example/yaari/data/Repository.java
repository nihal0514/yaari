package com.example.yaari.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.yaari.data.remote.ApiError;
import com.example.yaari.data.remote.ApiService;
import com.example.yaari.feature.auth.LoginActivity;
import com.example.yaari.feature.homepage.profile.ProfileActivity;
import com.example.yaari.model.GeneralResponse;
import com.example.yaari.model.Profile.ProfileResponse;
import com.example.yaari.model.auth.AuthResponse;
import com.example.yaari.model.friend.FriendResponse;
import com.example.yaari.model.post.PostResponse;
import com.example.yaari.model.publicpost.PublicPostResponse;
import com.example.yaari.model.search.SearchResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Repository {
    private static Repository instance=null;
    private final ApiService apiService;

    private Repository(ApiService apiService){
        this.apiService=apiService;
    }
    public static Repository getRepository(ApiService apiService){
        if(instance==null){
            instance=new Repository(apiService);

        }return instance;
    }
    public LiveData<AuthResponse> login(LoginActivity.UserInfo userInfo){
        //udemy ke video se dekhle difference between live data and mutable live data 11.00 time
        MutableLiveData<AuthResponse> auth=new MutableLiveData<>();
        Call<AuthResponse>call=apiService.login(userInfo);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if(response.isSuccessful()){
                    auth.postValue(response.body());
                    //postValue live data mai store kar ne ke liye hota hai
                }

            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage=ApiError.getErrorFromThrowable(t);
                AuthResponse authResponse=new AuthResponse(errorMessage.message, errorMessage.status);
                auth.postValue(authResponse);

            }
        });
        return auth;
    }
    public LiveData<ProfileResponse> fetchProfileInfo(Map<String,String> params){
        MutableLiveData<ProfileResponse> userInfo=new MutableLiveData<>();
        Call<ProfileResponse> call=apiService.fetchProfileInfo(params);
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if(response.isSuccessful()){
                    userInfo.postValue(response.body());

                }

            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage=ApiError.getErrorFromThrowable(t);
                ProfileResponse profileResponse=new ProfileResponse(errorMessage.message, errorMessage.status);
                userInfo.postValue(profileResponse);

            }
        });
        return userInfo;
    }
    public LiveData<GeneralResponse> uploadPost(MultipartBody multipartBody,Boolean isCoverOrProfileImage){
        MutableLiveData<GeneralResponse> postUpload=new MutableLiveData<>();
        Call<GeneralResponse> call=null;
        if(isCoverOrProfileImage){
            call=apiService.uploadImage(multipartBody);

        }else{
            call=apiService.uploadPost(multipartBody);

        }

        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if(response.isSuccessful()){
                    postUpload.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage=ApiError.getErrorFromThrowable(t);
                GeneralResponse generalResponse=new GeneralResponse(errorMessage.message, errorMessage.status);
                postUpload.postValue(generalResponse);

            }
        });
        return postUpload;
    }

    public LiveData<GeneralResponse> uploadPublicPost(MultipartBody multipartBody,Boolean isCoverOrProfileImage){
        MutableLiveData<GeneralResponse> postUpload=new MutableLiveData<>();
        Call<GeneralResponse> call=null;
        if(isCoverOrProfileImage){
            call=apiService.uploadImage(multipartBody);

        }else{
            call=apiService.uploadPublicPost(multipartBody);

        }

        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if(response.isSuccessful()){
                    postUpload.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage=ApiError.getErrorFromThrowable(t);
                GeneralResponse generalResponse=new GeneralResponse(errorMessage.message, errorMessage.status);
                postUpload.postValue(generalResponse);

            }
        });
        return postUpload;
    }
    public LiveData<SearchResponse> search(Map<String,String> params){
        MutableLiveData<SearchResponse> searchInfo=new MutableLiveData<>();
        Call<SearchResponse> call=apiService.search(params);
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if(response.isSuccessful()){
                    searchInfo.postValue(response.body());

                }

            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage=ApiError.getErrorFromThrowable(t);
                SearchResponse searchResponse=new SearchResponse(errorMessage.message, errorMessage.status);
                searchInfo.postValue(searchResponse);

            }
        });
        return searchInfo;
    }
    public MutableLiveData<FriendResponse> loadFriends(String uid){
        MutableLiveData<FriendResponse> friendInfo=new MutableLiveData<>();
        Call<FriendResponse> call=apiService.loadFriends(uid);
        call.enqueue(new Callback<FriendResponse>() {
            @Override
            public void onResponse(Call<FriendResponse> call, Response<FriendResponse> response) {
                if(response.isSuccessful()){
                    friendInfo.postValue(response.body());

                }

            }

            @Override
            public void onFailure(Call<FriendResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage=ApiError.getErrorFromThrowable(t);
                FriendResponse friendResponse=new FriendResponse(errorMessage.message, errorMessage.status);
                friendInfo.postValue(friendResponse);

            }
        });
        return friendInfo;
    }
    public LiveData<GeneralResponse> performOperation(ProfileActivity.PerformAction performAction){
        MutableLiveData<GeneralResponse> searchInfo=new MutableLiveData<>();
        Call<GeneralResponse> call=apiService.performAction(performAction);
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if(response.isSuccessful()){
                    searchInfo.postValue(response.body());

                }

            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage=ApiError.getErrorFromThrowable(t);
                GeneralResponse generalResponse=new GeneralResponse(errorMessage.message, errorMessage.status);
                searchInfo.postValue(generalResponse);

            }
        });
        return searchInfo;
    }
    public LiveData<PostResponse> getNewsFeed(Map<String,String> params){
        MutableLiveData<PostResponse> posts=new MutableLiveData<>();
        Call<PostResponse> call=apiService.getNewsFeed(params);
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if(response.isSuccessful()){
                    posts.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage=ApiError.getErrorFromThrowable(t);
                PostResponse postResponse=new PostResponse(errorMessage.message, errorMessage.status);
                posts.postValue(postResponse);

            }
        });
        return posts;
    }

    public LiveData<PublicPostResponse> getPublicPosts(Map<String,String> params){
        MutableLiveData<PublicPostResponse> posts=new MutableLiveData<>();
        Call<PublicPostResponse> call=apiService.getPublicPost(params);
        call.enqueue(new Callback<PublicPostResponse>() {
            @Override
            public void onResponse(Call<PublicPostResponse> call, Response<PublicPostResponse> response) {
                if(response.isSuccessful()){
                    posts.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<PublicPostResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage=ApiError.getErrorFromThrowable(t);
                PublicPostResponse postResponse=new PublicPostResponse(errorMessage.message, errorMessage.status);
                posts.postValue(postResponse);

            }
        });
        return posts;
    }
    public LiveData<PostResponse> getProfilePosts(Map<String,String> params){
        MutableLiveData<PostResponse> posts=new MutableLiveData<>();
        Call<PostResponse> call=apiService.loadProfilePosts(params);
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if(response.isSuccessful()){
                    posts.postValue(response.body());

                }

            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage=ApiError.getErrorFromThrowable(t);
                PostResponse postResponse=new PostResponse(errorMessage.message, errorMessage.status);
                posts.postValue(postResponse);

            }
        });
        return posts;
    }
}
