package com.example.yaari.feature.homepage.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.example.yaari.R;
import com.example.yaari.data.remote.ApiClient;
import com.example.yaari.feature.fullimage.FullImageActivity;
import com.example.yaari.feature.postupload.PostUploadActivity;
import com.example.yaari.feature.search.SearchActivity;
import com.example.yaari.model.GeneralResponse;
import com.example.yaari.model.Profile.ProfileResponse;
import com.example.yaari.model.post.PostResponse;
import com.example.yaari.model.post.PostsItem;
import com.example.yaari.model.publicpost.PublicPostsItem;
import com.example.yaari.utils.ViewModelFactory;
import com.example.yaari.utils.adapter.PostAdapter;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ProfileActivity extends AppCompatActivity implements DialogInterface.OnDismissListener, SwipeRefreshLayout.OnRefreshListener {
  /*  0=profile is still loading
    1=two peoples are friend(unfriend)
    2=we have sent friend request to that person(cancel request)
    3=we have received firend request  from that person(reject or accept)
    4=we are unknown(send request)
    5=our own profile*/

    private String uid = "", profileUrl = "", coverUrl = "";
    private int current_state = 0;
    private Button profileOptionButton;
    private ImageView profileImage;
    private ImageView coverImage;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    ProfileViewModel viewModel;
    Boolean isCoverImage = false;
    ProgressDialog progressDialog;
    PostAdapter postAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    List<PostsItem> postsItems;
    Boolean isFirstLoading=true;
    int limit=5;
    int offset=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileOptionButton = findViewById(R.id.profile_action_button);
        postsItems=new ArrayList<>();
        coverImage = findViewById(R.id.profilecover);
        toolbar = findViewById(R.id.toolbar);



        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("please wait");

        swipeRefreshLayout=findViewById(R.id.swipe);

        profileImage = findViewById(R.id.profile_image);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        recyclerView = findViewById(R.id.recyv_profile);
        progressBar = findViewById(R.id.progressbar);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        swipeRefreshLayout.setOnRefreshListener(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_button);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileActivity.super.onBackPressed();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                if(isItemReached()){
                    offset+=limit;
                    getProfilePosts();

                }
            }
        });



        viewModel = new ViewModelProvider(this, new ViewModelFactory()).get(ProfileViewModel.class);

        uid = getIntent().getStringExtra("uid");
        if (uid.equals(FirebaseAuth.getInstance().getUid())) {
            current_state = 5;
        } else {
            //find current state from backend
            profileOptionButton.setText("loading");
            profileOptionButton.setEnabled(false);
        }
        fetchprofileInfo();
    }

    private void fetchprofileInfo() {
        //we are going to call our api
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("userId", FirebaseAuth.getInstance().getUid());
        if (current_state == 5) {
            params.put("current_state", current_state + "");
        }else{
            params.put("profileId",uid);
        }
        viewModel.fetchProfileResponse(params).observe(this, new Observer<ProfileResponse>() {
            @Override
            public void onChanged(ProfileResponse profileResponse) {
                if (profileResponse.getStatus() == 200) {
                    progressDialog.hide();
                    collapsingToolbarLayout.setTitle(profileResponse.getProfile().getName());
                    profileUrl = profileResponse.getProfile().getProfileUrl();
                    coverUrl = profileResponse.getProfile().getCoverUrl();
                    current_state = Integer.parseInt(profileResponse.getProfile().getState());

                    if (!profileUrl.isEmpty()) {
                        Uri profileUri = Uri.parse(profileUrl);
                        if (profileUri.getAuthority() == null) {
                            profileUrl = ApiClient.BASE_URL + profileUrl;
                        }
                        Glide.with(ProfileActivity.this).load(profileUrl).into(profileImage);
                    }else{
                        profileUrl=R.drawable.default_profile_placeholder+"";
                    }
                    if (!coverUrl.isEmpty()) {
                        Uri coverUri = Uri.parse(coverUrl);
                        if (coverUri.getAuthority() == null) {
                            coverUrl = ApiClient.BASE_URL + coverUrl;
                        }
                        Glide.with(ProfileActivity.this).load(coverUrl).into(coverImage);
                    }else{
                        coverUrl=R.drawable.cover_picture_placeholder+"";
                    }
                    if (current_state == 0) {
                        profileOptionButton.setText("loading");
                        profileOptionButton.setEnabled(false);
                        return;
                    } else if (current_state == 1) {
                        profileOptionButton.setText("you are friends");
                    } else if (current_state == 2) {
                        profileOptionButton.setText("cancel requests");
                    } else if (current_state == 3) {
                        profileOptionButton.setText("accept requests");
                    } else if (current_state == 4) {
                        profileOptionButton.setText("send requests");
                    } else if (current_state == 5) {
                        profileOptionButton.setText("edit profile");
                    }
                    profileOptionButton.setEnabled(true);
                    loadProfileOptionButton();
                    getProfilePosts();
                } else {
                    Toast.makeText(ProfileActivity.this, profileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private boolean isItemReached() {
        LinearLayoutManager linearLayoutManager=(LinearLayoutManager)recyclerView.getLayoutManager();
        int position=linearLayoutManager.findLastCompletelyVisibleItemPosition();
        int numberofitems=postAdapter.getItemCount();
        return (position>=numberofitems-1);
    }

    private void getProfilePosts() {
        Map<String,String> params=new HashMap<>();
        params.put("uid", uid);
        params.put("limit",limit+"");
        params.put("offset",offset+"");
        params.put("current_state",current_state+"");
        progressBar.setVisibility(View.VISIBLE);
        viewModel.getProfilePosts(params).observe(this, new Observer<PostResponse>() {
            @Override
            public void onChanged(PostResponse postResponse) {
                progressBar.setVisibility(View.GONE);
                if(postResponse.getStatus()==200){

                    if(swipeRefreshLayout.isRefreshing()){
                        postsItems.clear();
                        postAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    postsItems.addAll(postResponse.getPosts());
                    if(isFirstLoading){
                        postAdapter=new PostAdapter(ProfileActivity.this,postsItems);
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
                    Toast.makeText(ProfileActivity.this, postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void loadProfileOptionButton() {
        profileOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileOptionButton.setEnabled(false);
                if (current_state == 5) {
                    CharSequence[] options = new CharSequence[]{"Change cover image", "Change Profile Image", "View Cover Image", "View Profile Image"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 0) {
                                isCoverImage = true;
                                selectImage();
                            } else if (i == 1) {
                                isCoverImage = false;
                                selectImage();

                            } else if (i == 2) {
                                viewFullImage(coverImage, coverUrl);

                            } else if (i == 3) {
                                viewFullImage(profileImage, profileUrl);
                            }

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                } else if (current_state == 4) {
                    CharSequence[] options = new CharSequence[]{"Send Friend Request"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 0) {
                                performAction();
                            }

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                } else if (current_state == 3) {
                    CharSequence[] options = new CharSequence[]{"Accept Friend Request"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 0) {
                                performAction();
                            }

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();

                } else if (current_state == 2) {
                    CharSequence[] options = new CharSequence[]{"Cancel Requests"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 0) {
                                performAction();
                            }

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();

                } else if (current_state == 1) {
                    CharSequence[] options = new CharSequence[]{"Unfriend"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 0) {
                                performAction();
                            }

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                }
            }
        });
    }

    private void performAction() {
        progressDialog.show();
        viewModel.performAction(new PerformAction(current_state+"",
                FirebaseAuth.getInstance().getUid(),
                uid)).observe(this, new Observer<GeneralResponse>() {
            @Override
            public void onChanged(GeneralResponse generalResponse) {
                progressDialog.hide();
                Toast.makeText(ProfileActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if(generalResponse.getStatus()==200){
                    profileOptionButton.setEnabled(true);
                    if(current_state==4){
                        current_state=2;
                        profileOptionButton.setText("Cancel Request");
                    }else if(current_state==3){
                        current_state=1;
                        profileOptionButton.setText("You are friends");
                    }
                    else if(current_state==2){
                        current_state=4;
                        profileOptionButton.setText("Send requests");
                    } else if(current_state==1){
                        current_state=4;
                        profileOptionButton.setText("Send requests");
                    }
                }else{
                    profileOptionButton.setEnabled(false);
                    profileOptionButton.setText("error");
                }
            }
        });
    }

    private void viewFullImage(ImageView profileImage, String profileUrl) {
        Intent intent=new Intent(ProfileActivity.this, FullImageActivity.class);
        intent.putExtra("imageUrl",profileUrl);
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP){
            Pair[] pairs=new Pair[1];
            pairs[0]=new Pair<View,String>(profileImage,profileUrl);
            ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this,pairs);
            startActivity(intent,activityOptions.toBundle());
        }else{
            startActivity(intent);
        }
    }

    private void selectImage() {
        ImagePicker.create(this).single().folderMode(true).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image selectedImage = ImagePicker.getFirstImageOrNull(data);
            try {
                File compressedImageFile = new Compressor(this).setQuality(75)
                        .compressToFile(new File(selectedImage.getPath()));
                uploadImage(compressedImageFile);

            } catch (IOException e) {
                Toast.makeText(this, "Image Picker Failed", Toast.LENGTH_SHORT).show();


            }
        }
    }

    private void uploadImage(File compressedImageFile) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("uid", FirebaseAuth.getInstance().getUid() + "");
        builder.addFormDataPart("isCoverImage", isCoverImage + "");
        builder.addFormDataPart("file", compressedImageFile.getName(),
                RequestBody.create(compressedImageFile, MediaType.parse("multipart/form-data")));
        progressDialog.show();

        viewModel.uploadPost(builder.build(),true).observe(this, new Observer<GeneralResponse>() {
            @Override
            public void onChanged(GeneralResponse generalResponse) {
                progressDialog.hide();
                Toast.makeText(ProfileActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if(isCoverImage){
                    Glide.with(ProfileActivity.this).load(ApiClient.BASE_URL+generalResponse.getExtra()).into(coverImage);
                    //ek baar woh php ka folder dekh le jaha coding kiya hai waha getExtra mai image ka location daala hai
                }else{
                    Glide.with(ProfileActivity.this).load(ApiClient.BASE_URL+generalResponse.getExtra()).into(profileImage);
                }
            }
        });


    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        profileOptionButton.setEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        offset=0;
        postsItems.clear();
        isFirstLoading=true;
    }

    @Override
    public void onRefresh() {
        offset=0;
        isFirstLoading=true;
        getProfilePosts();
    }

    public static class PerformAction{
        String operationType,uid,profileId;

        public PerformAction(String operationType, String uid, String profileId) {
            this.operationType = operationType;
            this.uid = uid;
            this.profileId = profileId;
        }
    }
}