package com.example.yaari.feature.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yaari.R;
import com.example.yaari.feature.homepage.MainActivity;
import com.example.yaari.utils.ViewModelFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;

public class NormalRegisterActivity extends AppCompatActivity {

    private EditText registeremail,registerpassword,registerconfirmpassword,registername;
    private Button registercreateaccount;
    private FirebaseAuth mAuth;
    LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_register);

        registeremail=findViewById(R.id.register_email);
        registerpassword=findViewById(R.id.register_password);
        registerconfirmpassword=findViewById(R.id.register_confirmpassword);
        registercreateaccount=findViewById(R.id.btn_register);
        registername =findViewById(R.id.register_name);

        viewModel=new ViewModelProvider(this,new ViewModelFactory()).get(LoginViewModel.class);


        mAuth= FirebaseAuth.getInstance();

        registercreateaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createnewAccount();
            }
        });
    }

    private void createnewAccount() {
        String name= registername.getText().toString();
        String email=registeremail.getText().toString();
        String password=registerpassword.getText().toString();
        String confirmpassword=registerconfirmpassword.getText().toString();
        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(name)){
            Toast.makeText(this, "please write email", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "please write password", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(confirmpassword)){
            Toast.makeText(this, "please write confirm password", Toast.LENGTH_SHORT).show();
        }else if(!password.equals(confirmpassword)){
            Toast.makeText(this, "password not match", Toast.LENGTH_SHORT).show();
        }else{

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                sendUserToMainActivity();
                                FirebaseInstallations.getInstance().getToken(true).addOnSuccessListener(new OnSuccessListener<InstallationTokenResult>() {
                                    @Override
                                    public void onSuccess(InstallationTokenResult installationTokenResult) {
                                        FirebaseUser user=mAuth.getCurrentUser();
                                        viewModel.login(new LoginActivity.UserInfo(
                                                user.getUid(),
                                                name,
                                                email,
                                                "https://firebasestorage.googleapis.com/v0/b/yaari-53c45.appspot.com/o/avatar1.png?alt=media&token=30373a45-7ede-42a8-8308-e2726be49b47",
                                                "https://firebasestorage.googleapis.com/v0/b/yaari-53c45.appspot.com/o/avatar2.jpg?alt=media&token=36be1488-85eb-4a86-a852-969c099d8f52",
                                                installationTokenResult.getToken()


                                        ));
                                    }
                                });
                                Toast.makeText(NormalRegisterActivity.this, "registered Successfully", Toast.LENGTH_SHORT).show();
                            }else{
                                String message=task.getException().getMessage();
                                Toast.makeText(NormalRegisterActivity.this, "Error Occured"+ message, Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            sendUserToMainActivity();

        }


    }

    private void sendUserToMainActivity() {
        Intent mainIntent=new Intent(NormalRegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);

        finish();
    }
    public static class UserInfo{
        String uid,name,email,profileUrl,coverUrl,userToken;

        public UserInfo(String uid, String name, String email, String profileUrl, String coverUrl, String userToken) {
            this.uid = uid;
            this.name = name;
            this.email = email;
            this.profileUrl = profileUrl;
            this.coverUrl = coverUrl;
            this.userToken = userToken;
        }
    }


}