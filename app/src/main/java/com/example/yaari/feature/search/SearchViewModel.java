package com.example.yaari.feature.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.yaari.data.Repository;
import com.example.yaari.model.GeneralResponse;
import com.example.yaari.model.Profile.ProfileResponse;
import com.example.yaari.model.search.SearchResponse;

import java.util.Map;

import okhttp3.MultipartBody;

public class SearchViewModel extends ViewModel {
    private Repository repository;

    public SearchViewModel(Repository repository) {
        this.repository = repository;
    }
    public LiveData<SearchResponse> search(Map<String,String> params){
        return repository.search(params);

    }
}
