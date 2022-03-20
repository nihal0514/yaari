package com.example.yaari.feature.homepage.friends;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yaari.R;
import com.example.yaari.feature.homepage.MainActivity;
import com.example.yaari.feature.homepage.MainViewModel;
import com.example.yaari.model.friend.Friend;
import com.example.yaari.model.friend.FriendResponse;
import com.example.yaari.model.friend.Request;
import com.example.yaari.utils.ViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {
    MainViewModel viewModel;
    Context context;
    RecyclerView friendRequestRecyv,friendsRecyv;
    TextView friendTitle,requestTitle,defaultTitle;
    FriendsAdapter friendsAdapter;
    FriendRequestAdapter friendRequestAdapter;
    List<Friend>friends=new ArrayList<>();
    List<Request>requests=new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        viewModel=new ViewModelProvider((FragmentActivity)context,new ViewModelFactory()).get(MainViewModel.class);

        friendRequestRecyv=view.findViewById(R.id.friend_request_recyv);

        friendTitle=view.findViewById(R.id.friend_title);
        requestTitle=view.findViewById(R.id.request_title);
        defaultTitle=view.findViewById(R.id.default_textview);

        friendsRecyv=view.findViewById(R.id.friend_recyv);

        friendsAdapter=new FriendsAdapter(context,friends);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
        friendsRecyv.setAdapter(friendsAdapter);
        friendsRecyv.setLayoutManager(linearLayoutManager);

        friendRequestAdapter=new FriendRequestAdapter(context,requests);
        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(context);
        friendRequestRecyv.setAdapter(friendRequestAdapter);
        friendRequestRecyv.setLayoutManager(linearLayoutManager1);




        loadFriends();
    }

    private void loadFriends() {
        ((MainActivity)getActivity()).showProgressBar();
          viewModel.loadFriends(FirebaseAuth.getInstance().getUid()).observe(this, new Observer<FriendResponse>() {
              @Override
              public void onChanged(FriendResponse friendResponse) {
                  ((MainActivity)getActivity()).hideProgressBar();
                  loadData(friendResponse);

              }
          });
    }

    private void loadData(FriendResponse friendResponse) {
        if(friendResponse.getStatus()==200){
            friends.clear();
            friends.addAll(friendResponse.getResult().getFriends());
            friendsAdapter.notifyDataSetChanged();

            requests.clear();
            requests.addAll(friendResponse.getResult().getRequests());
            friendRequestAdapter.notifyDataSetChanged();

            if(friendResponse.getResult().getFriends().size()>0){
                friendTitle.setVisibility(View.VISIBLE);
            }else{
                friendTitle.setVisibility(View.GONE);
            }

            if(friendResponse.getResult().getRequests().size()>0){
                requestTitle.setVisibility(View.VISIBLE);
            }else{
                requestTitle.setVisibility(View.GONE);
            }

            if(friendResponse.getResult().getFriends().size()==0 && friendResponse.getResult().getRequests().size()==0){
                defaultTitle.setVisibility(View.VISIBLE);

            }
        }else{
            Toast.makeText(context, friendResponse.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}