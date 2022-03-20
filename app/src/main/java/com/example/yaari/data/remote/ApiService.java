package com.example.yaari.data.remote;

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
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiService {
    @POST("login")
    Call<AuthResponse> login(@Body LoginActivity.UserInfo userInfo);
    //ye post request tumhara node js mai jaata hai aur woh nodejs tumhara php mai store kar deta hai

    @POST("uploadpost")
    Call<GeneralResponse> uploadPost(@Body MultipartBody body);
    //multipartbody is used in uploading files in multipart / form data format and it Solve the problem of uploading large files.

    @POST("uploadpublicpost")
    Call<GeneralResponse> uploadPublicPost(@Body MultipartBody body);

    @POST("uploadImage")
    Call<GeneralResponse> uploadImage(@Body MultipartBody body);

    @GET("loadprofileinfo")
    Call<ProfileResponse> fetchProfileInfo(@QueryMap Map<String,String> params);

    @GET("search")
    Call<SearchResponse> search(@QueryMap Map<String,String> params);

    @GET("loadfriends")
    Call<FriendResponse> loadFriends(@Query("uid") String uid);

    @GET("getnewsfeed")
    Call<PostResponse> getNewsFeed(@QueryMap Map<String,String> params);

    @GET("getpublicposts")
    Call<PublicPostResponse> getPublicPost(@QueryMap Map<String,String> params);


    @GET("loadProfilePosts")
    Call<PostResponse> loadProfilePosts(@QueryMap Map<String,String> params);


    @POST("performaction")
    Call<GeneralResponse> performAction(@Body ProfileActivity.PerformAction performAction);

}
