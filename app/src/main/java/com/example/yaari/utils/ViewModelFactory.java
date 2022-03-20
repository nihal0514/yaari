package com.example.yaari.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.yaari.data.Repository;
import com.example.yaari.data.remote.ApiClient;
import com.example.yaari.data.remote.ApiService;
import com.example.yaari.feature.auth.LoginViewModel;
import com.example.yaari.feature.homepage.MainViewModel;
import com.example.yaari.feature.homepage.profile.ProfileViewModel;
import com.example.yaari.feature.postupload.PostUploadViewModel;
import com.example.yaari.feature.search.SearchViewModel;

import org.jetbrains.annotations.NotNull;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private Repository repository;
    public ViewModelFactory(){
        ApiService apiService= ApiClient.getRetrofit().create(ApiService.class);
        repository=Repository.getRepository(apiService);
    }
    @SuppressWarnings("unchecked")
    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(LoginViewModel.class)){
            return (T) new LoginViewModel(repository);

        }else if(modelClass.isAssignableFrom(ProfileViewModel.class)){
            return (T) new ProfileViewModel(repository);
        }
        else if(modelClass.isAssignableFrom(PostUploadViewModel.class)){
            return (T) new PostUploadViewModel(repository);
        }else if(modelClass.isAssignableFrom(SearchViewModel.class)){
            return (T) new SearchViewModel(repository);
        }else if(modelClass.isAssignableFrom(MainViewModel.class)){
            return (T) new MainViewModel(repository);
        }
        throw new IllegalArgumentException("View Model not found!");

    }
}


//yeh viewmodel ke saat gift aata hai hame use karna hi padega
