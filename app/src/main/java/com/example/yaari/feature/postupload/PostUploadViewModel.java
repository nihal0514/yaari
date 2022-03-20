package com.example.yaari.feature.postupload;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.yaari.data.Repository;
import com.example.yaari.model.GeneralResponse;

import okhttp3.MultipartBody;

public class PostUploadViewModel extends ViewModel {
    private Repository repository;

    public PostUploadViewModel(Repository repository) {
        this.repository = repository;
    }
    public LiveData<GeneralResponse> uploadPost(MultipartBody multipartBody,Boolean isCoverOrProfileImage){
        return this.repository.uploadPost(multipartBody,isCoverOrProfileImage);
    }
    public LiveData<GeneralResponse> uploadPublicPost(MultipartBody multipartBody,Boolean isCoverOrProfileImage){
        return this.repository.uploadPublicPost(multipartBody,isCoverOrProfileImage);
    }
}
