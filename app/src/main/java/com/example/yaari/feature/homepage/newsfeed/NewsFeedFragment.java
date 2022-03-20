package com.example.yaari.feature.homepage.newsfeed;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.yaari.R;
import com.example.yaari.feature.homepage.MainActivity;
import com.example.yaari.feature.homepage.MainViewModel;
import com.example.yaari.feature.homepage.profile.ProfileActivity;
import com.example.yaari.feature.homepage.profile.ProfileViewModel;
import com.example.yaari.model.post.PostResponse;
import com.example.yaari.model.publicpost.PublicPostResponse;
import com.example.yaari.model.publicpost.PublicPostsItem;
import com.example.yaari.utils.ViewModelFactory;
import com.example.yaari.utils.adapter.PublicPostAdapter;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    MainViewModel viewModel;
    Context context;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    PublicPostAdapter postAdapter;
   // List<PostsItem> postsItems;
   List<PublicPostsItem> postsItems;
    Boolean isFirstLoading = true;
    int limit = 5;
    int offset = 0;
    private int current_state = 0;
    String uid;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider((FragmentActivity) context, new ViewModelFactory()).get(MainViewModel.class);
        //viewModel = new ViewModelProvider((FragmentActivity) context, new ViewModelFactory()).get(ProfileViewModel.class);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = view.findViewById(R.id.recyv_newsfeed);
        postsItems = new ArrayList<>();
        postAdapter = new PublicPostAdapter(context, postsItems);

        uid = getActivity().getIntent().getStringExtra("uid");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                if (isItemReached()) {
                    offset += limit;
                    fetchNews();

                }
            }
        });


    }

    private boolean isItemReached() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        int numberofitems = postAdapter.getItemCount();
        return (position >= numberofitems - 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchNews();
    }
    private void fetchNews() {
        Map<String,String> params=new HashMap<>();
        params.put("uid", FirebaseAuth.getInstance().getUid());
        params.put("limit",limit+"");
        params.put("offset",offset+"");
        ((MainActivity) getActivity()).showProgressBar();

        viewModel.getPublicPosts(params).observe(getViewLifecycleOwner(), new Observer<PublicPostResponse>() {
            @Override
            public void onChanged(PublicPostResponse postResponse) {
                ((MainActivity) getActivity()).hideProgressBar();
                if(postResponse.getStatus()==200){

                    if(swipeRefreshLayout.isRefreshing()){
                        postsItems.clear();
                        postAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    postsItems.addAll(postResponse.getPosts());
                    if(isFirstLoading){
                        postAdapter=new PublicPostAdapter(context,postsItems);
                        recyclerView.setAdapter(postAdapter);
                    }else{
                        postAdapter.notifyItemRangeChanged(postsItems.size(),postResponse.getPosts().size());
                    }
                    if(postResponse.getPosts().size()==0){
                        offset=offset-limit;
                    }
                    isFirstLoading=false;
                }else{
                    if(swipeRefreshLayout.isRefreshing()){
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    Toast.makeText(context, postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

/*        private void fetchNews() {
            Map<String,String> params=new HashMap<>();
            params.put("uid", FirebaseAuth.getInstance().getUid());
            params.put("limit",limit+"");
            params.put("offset",offset+"");
            ((MainActivity) getActivity()).showProgressBar();

            viewModel.getNewsFeed(params).observe(getViewLifecycleOwner(), new Observer<PostResponse>() {
                @Override
                public void onChanged(PostResponse postResponse) {
                    ((MainActivity) getActivity()).hideProgressBar();
                    if(postResponse.getStatus()==200){

                        if(swipeRefreshLayout.isRefreshing()){
                            postsItems.clear();
                            postAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        postsItems.addAll(postResponse.getPosts());
                        if(isFirstLoading){
                            postAdapter=new PublicPostAdapter(context,postsItems);
                            recyclerView.setAdapter(postAdapter);
                        }else{
                            postAdapter.notifyItemRangeChanged(postsItems.size(),postResponse.getPosts().size());
                        }
                        if(postResponse.getPosts().size()==0){
                            offset=offset-limit;
                        }
                        isFirstLoading=false;
                    }else{
                        if(swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(context, postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }*/
    @Override
    public void onStop() {
        super.onStop();
        offset = 0;
        postsItems.clear();
        isFirstLoading = true;
    }

    @Override
    public void onRefresh() {
        offset = 0;
        isFirstLoading = true;
        fetchNews();
    }
}

//limit matlab ek time par kitna data load karenge
//offset matlab kitna post ham skip kar rhe hai load karne ko
//e.g ke liye agar limit=5 hai matlab 5 post ek time par load ho rha hai toh offset=0 hoga
//offset=0 matlab ham ek bhi post skip nhi kar rhe hai
//ab pehle 5 load huaa toh next 5 load hoga toh offset =5 hojayega
//offset =5 matlab pehle jo post load huaa tha ab woh post skip hojayega
//aise hi ab uska next 5 post load hoga toh offset=10 hojayega aisa kaam hoga