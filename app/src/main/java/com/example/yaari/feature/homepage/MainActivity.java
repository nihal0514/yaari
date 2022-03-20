package com.example.yaari.feature.homepage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.NestedScrollingChild;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.yaari.R;
import com.example.yaari.feature.homepage.friends.FriendRequestAdapter;
import com.example.yaari.feature.homepage.friends.FriendsFragment;
import com.example.yaari.feature.homepage.newsfeed.NewsFeedFragment;
import com.example.yaari.feature.homepage.profile.ProfileActivity;
import com.example.yaari.feature.postupload.PostUploadActivity;
import com.example.yaari.feature.search.SearchActivity;
import com.example.yaari.model.GeneralResponse;
import com.example.yaari.model.friend.FriendResponse;
import com.example.yaari.utils.ViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements FriendRequestAdapter.IPerformAction {
    private BottomNavigationView bottomNavigationView;
    private FriendsFragment friendsFragment;
    private NewsFeedFragment newsFeedFragment;
    private FloatingActionButton fab;

    private ImageView searchIcon;
    private ProgressBar progressBar;
    MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab=findViewById(R.id.fab);
        bottomNavigationView=findViewById(R.id.navigation);
        searchIcon=findViewById(R.id.toolbar_search);
        friendsFragment=new FriendsFragment();
        newsFeedFragment=new NewsFeedFragment();
        progressBar=findViewById(R.id.progressbar);

        viewModel=new ViewModelProvider(this,new ViewModelFactory()).get(MainViewModel.class);

        setFragment(newsFeedFragment);
        setBottomNavigationView();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, PostUploadActivity.class);
                startActivity(intent);
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

    }
    private void setBottomNavigationView(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.newsfeed_fragment:
                        setFragment(newsFeedFragment);
                        return true;

                    case R.id.friendfragment:
                        setFragment(friendsFragment);
                        return true;
                    case R.id.profile_fragment:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class).putExtra("uid", FirebaseAuth.getInstance().getUid()));
                        return false;


                }
                return true;
            }
        });
    }
    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout,fragment).commit();
    }
    public void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }
    public void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void performAction(int position, String profileId, int operationType) {
        showProgressBar();
        viewModel.performAction(new ProfileActivity.PerformAction(operationType+"",
                FirebaseAuth.getInstance().getUid(), profileId)).observe(this, new Observer<GeneralResponse>() {
            @Override
            public void onChanged(GeneralResponse generalResponse) {
                hideProgressBar();
                Toast.makeText(MainActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if(generalResponse.getStatus()==200){
                    FriendResponse friendresponse=viewModel.loadFriends(FirebaseAuth.getInstance().getUid()).getValue();
                    friendresponse.getResult().getRequests().remove(position);

                    
                }
            }
        });

    }
}