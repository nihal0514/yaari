package com.example.yaari.feature.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yaari.R;
import com.example.yaari.model.search.SearchResponse;
import com.example.yaari.model.search.User;
import com.example.yaari.utils.ViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    Toolbar toolbar;
    SearchView searchView;
    RecyclerView recyclerView;
    TextView defaultText;
    SearchViewModel viewModel;
    private SearchAdapter searchAdapter;
    private List<User> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar=findViewById(R.id.toolbar);
        searchView=findViewById(R.id.search);
        recyclerView=findViewById(R.id.search_recyv);
        defaultText=findViewById(R.id.default_text);
        viewModel=new ViewModelProvider(this,new ViewModelFactory()).get(SearchViewModel.class);

        userList=new ArrayList<>();
        searchAdapter=new SearchAdapter(this,userList);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(searchAdapter);

        searchView.setQueryHint("Search People");


        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_button);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.super.onBackPressed();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchDb(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()>2){
                    searchDb(newText);
                }else{
                    defaultText.setVisibility(View.VISIBLE);
                    userList.clear();
                    searchAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });
    }

    private void searchDb(String query) {
        Map<String, String> params = new HashMap<>();
        params.put("keyword",query);
        viewModel.search(params).observe(SearchActivity.this, new Observer<SearchResponse>() {
            @Override
            public void onChanged(SearchResponse searchResponse) {
                if(searchResponse.getStatus()==200){
                    defaultText.setVisibility(View.GONE);
                    userList.clear();
                    userList.addAll(searchResponse.getUser());
                    searchAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(SearchActivity.this, searchResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    defaultText.setVisibility(View.VISIBLE);
                }
            }
        });
    }













}