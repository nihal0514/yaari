package com.example.yaari.feature.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.yaari.R;
import com.example.yaari.feature.homepage.MainActivity;
import com.example.yaari.model.auth.AuthResponse;
import com.example.yaari.utils.ViewModelFactory;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;


public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN=50;
    private static final String TAG = "LoginActivity";
    private SignInButton signInButton;
    private Button normalSignIn;

    LoginViewModel viewModel;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signInButton=findViewById(R.id.btn_signin);
      //  normalSignIn= findViewById(R.id.btn_normal_signin);
        progressDialog=new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading..");
        progressDialog.setMessage("Signing you in");
        viewModel=new ViewModelProvider(this,new ViewModelFactory()).get(LoginViewModel.class);

        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id1))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
       /* normalSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendusertonormalsigninActivity();
            }
        });*/

    }

    private void sendusertonormalsigninActivity() {
        startActivity(new Intent(LoginActivity.this,NormalSignInActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void firebaseAuthWithGoogle(String idToken) {
        progressDialog.show();
        signInButton.setVisibility(View.GONE);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseInstallations.getInstance().getToken(true).addOnSuccessListener(new OnSuccessListener<InstallationTokenResult>() {
                                @Override
                                public void onSuccess(InstallationTokenResult installationTokenResult) {
                                    FirebaseUser user=mAuth.getCurrentUser();
                                    viewModel.login(new UserInfo(
                                            user.getUid(),
                                            user.getDisplayName(),
                                            user.getEmail(),
                                            user.getPhotoUrl().toString(),
                                            "",
                                            installationTokenResult.getToken()


                                    ))
                                    .observe(LoginActivity.this, authResponse -> {
                                        progressDialog.hide();
                                        Toast.makeText(LoginActivity.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                        if(authResponse.getAuth()!=null){
                                            updateUI(user);

                                        }else{
                                            FirebaseAuth.getInstance().signOut();
                                            mGoogleSignInClient.signOut();
                                            updateUI(null);
                                        }
                                    });
                                    ;
                                }
                            });
                           // FirebaseUser user = mAuth.getCurrentUser();
                         //   updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    private void updateUI(FirebaseUser currentUser) {
        if(currentUser!=null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }else{
            signInButton.setVisibility(View.VISIBLE);
        }

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



//live data ko read karne ke liye .observe use karte hai

//live data 2 baar call hota hai jab ek baar app kholte hai tab aur dusre baar jab koi data update hota hai tab


//pehle login karenge
//fir u