package com.example.yaari.feature.postupload;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.example.yaari.R;
import com.example.yaari.feature.homepage.MainActivity;
import com.example.yaari.model.GeneralResponse;
import com.example.yaari.utils.ViewModelFactory;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PostUploadActivity extends AppCompatActivity {
    AppCompatSpinner spinner;
    PostUploadViewModel viewModel;
    TextView postTxt;
    TextInputEditText textInputEditText;
    private int privacylevel=0;
    ProgressDialog progressDialog;
    boolean isImageSelected=false;
    ImageView addImage,previewImage;
    File compressedImageFile=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_upload);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Uploading post please wait");

        addImage=findViewById(R.id.add_image);
        previewImage=findViewById(R.id.image_preview);

        addImage.setOnClickListener(view ->
            selectImage());
        previewImage.setOnClickListener(view ->
            selectImage()
        );

        postTxt=findViewById(R.id.postbtn);
        textInputEditText=findViewById(R.id.input_post);
        viewModel=new ViewModelProvider(this,new ViewModelFactory()).get(PostUploadViewModel.class);
        spinner=findViewById(R.id.spinner_privacy);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView selectedTextView=(TextView) view;
                if(selectedTextView!=null){
                    selectedTextView.setTextColor(Color.WHITE);
                    selectedTextView.setTypeface(null, Typeface.BOLD);
                }
                privacylevel=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                privacylevel=0;

            }
        });
        postTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadpost();
                if(privacylevel==2) {
                    uploadpublicpost();
                }
            }
        });

    }

    private void uploadpublicpost() {
        String status=textInputEditText.getText().toString();
        String userId= FirebaseAuth.getInstance().getUid();
        if(status.trim().length()>0 || isImageSelected){
            progressDialog.show();
            MultipartBody.Builder builder=new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart("post",status);
            builder.addFormDataPart("postUserId",userId);
            if(isImageSelected){
                builder.addFormDataPart("file",
                        compressedImageFile.getName(),
                        RequestBody.create(compressedImageFile, MediaType.parse("multipart/form-data")));

            }

            MultipartBody multipartBody=builder.build();

            viewModel.uploadPublicPost(multipartBody,false).observe(PostUploadActivity.this, new Observer<GeneralResponse>() {
                @Override
                public void onChanged(GeneralResponse generalResponse) {
                    progressDialog.hide();
                    Toast.makeText(PostUploadActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    if(generalResponse.getStatus()==200){
                        onBackPressed();
                    }

                }
            });
        }else{
            Toast.makeText(PostUploadActivity.this, "Please write your status", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadpost() {
        String status=textInputEditText.getText().toString();
        String userId= FirebaseAuth.getInstance().getUid();
        if(status.trim().length()>0 || isImageSelected){
            progressDialog.show();
            MultipartBody.Builder builder=new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart("post",status);
            builder.addFormDataPart("postUserId",userId);
            builder.addFormDataPart("privacy",privacylevel+"");
            if(isImageSelected){
                builder.addFormDataPart("file",
                        compressedImageFile.getName(),
                        RequestBody.create(compressedImageFile, MediaType.parse("multipart/form-data")));

            }

            MultipartBody multipartBody=builder.build();

            viewModel.uploadPost(multipartBody,false).observe(PostUploadActivity.this, new Observer<GeneralResponse>() {
                @Override
                public void onChanged(GeneralResponse generalResponse) {
                    progressDialog.hide();
                    Toast.makeText(PostUploadActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    if(generalResponse.getStatus()==200){
                        onBackPressed();
                    }

                }
            });
        }else{
            Toast.makeText(PostUploadActivity.this, "Please write your status", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImage() {
        ImagePicker.create(this).single().folderMode(true).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(ImagePicker.shouldHandle(requestCode,resultCode,data)){
            Image selectedImage=ImagePicker.getFirstImageOrNull(data);
            try {
                compressedImageFile=new Compressor(this).setQuality(75)
                        .compressToFile(new File(selectedImage.getPath()));
                isImageSelected=true;
                addImage.setVisibility(View.GONE);
                previewImage.setVisibility(View.VISIBLE);

                Glide.with(PostUploadActivity.this)
                        .load(selectedImage.getPath())
                        .error(R.drawable.cover_picture_placeholder)
                        .placeholder(R.drawable.cover_picture_placeholder)
                        .into(previewImage);

            }catch (IOException e){
                addImage.setVisibility(View.VISIBLE);
                previewImage.setVisibility(View.GONE);


            }
        }
    }
}