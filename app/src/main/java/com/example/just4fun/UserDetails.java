package com.example.just4fun;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class UserDetails extends AppCompatActivity {
    ImageView userPicture;
    TextView usernamefield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        userPicture = findViewById(R.id.otherUserPicture);
        usernamefield = findViewById(R.id.otherUserName);
        String userPicString = getIntent().getStringExtra("userPic");
        String username = getIntent().getStringExtra("username");
        if (userPicString.length() == 0)
            Glide.with(userPicture).load(R.drawable.person).into(userPicture);
        else
            Glide.with(userPicture).load(userPicString).into(userPicture);
        usernamefield.setText(username);
    }
}