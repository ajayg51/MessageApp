package com.example.just4fun;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.LogTime;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class splashScreen extends AppCompatActivity {
    private static final String TAG = "main";
    TextView textView, loginbutton2;
    Button loginbutton;
    int seconds, FIREBASE_AUTH_CODE = 1234;
    boolean connected = false;
    ConstraintLayout constraintLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        textView = findViewById(R.id.SplashText);
        loginbutton = findViewById(R.id.loginbutton);
        loginbutton2 = findViewById(R.id.loginbutton2);
        constraintLayout = findViewById(R.id.SplashconstraintLayout);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            loginbutton.setAlpha(1);
            loginbutton2.setAlpha(1);
            textView.setAlpha(0);
            loginbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isConnected()) {
                        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.EmailBuilder().build()
//                                new AuthUI.IdpConfig.PhoneBuilder().build()
                        );

                        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setLogo(R.drawable.iconaj)
                                .build();

                        startActivityForResult(intent, FIREBASE_AUTH_CODE);

                    }
                    else{
                        Snackbar.make(constraintLayout, Html.fromHtml("<font color='yellow'>Connection Lost</font>"),Snackbar.LENGTH_LONG)
                                .setAction(Html.fromHtml("<font color='yellow'>Connect</font>"), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        openSettings();
                                    }
                                })
                                .setDuration(3000)
                                .show();

                    }
                }
            });

        }
        else {
            loginbutton.setAlpha(0);
            loginbutton2.setAlpha(0);
            runAnimation();
            seconds=500;
             new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(splashScreen.this,MainActivity.class));
                        finish();
                    }
                },seconds);
        }
    }
    private  void runAnimation(){
        Animation abc= AnimationUtils.loadAnimation(this,R.anim.fui_slide_in_right);
        abc.setRepeatMode(Animation.REVERSE);
        abc.setRepeatCount(1);
        textView.clearAnimation();
        textView.startAnimation(abc);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FIREBASE_AUTH_CODE) {
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(splashScreen.this, MainActivity.class));
                this.finish();
            }
        } else {
            Toast.makeText(this, "Some Error Occured", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isConnected(){
        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(Objects.requireNonNull(cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState())==NetworkInfo.State.CONNECTED ||
           Objects.requireNonNull(cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState())==NetworkInfo.State.CONNECTED )
            connected=true;
        else connected=false;

    return connected;
    }
    private void openSettings(){
        startActivityForResult(new Intent(Settings.ACTION_SETTINGS),0);
    }



}
