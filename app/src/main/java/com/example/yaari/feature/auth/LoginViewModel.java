package com.example.yaari.feature.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.yaari.data.Repository;
import com.example.yaari.model.auth.AuthResponse;

public class LoginViewModel extends ViewModel {
    private Repository repository;

    public LoginViewModel(Repository repository) {
        this.repository = repository;
    }
    public LiveData<AuthResponse> login(LoginActivity.UserInfo userInfo){
        return repository.login(userInfo);
    }
}
