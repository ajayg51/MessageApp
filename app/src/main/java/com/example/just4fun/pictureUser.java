package com.example.just4fun;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class pictureUser extends AppCompatActivity {
    Button userPicButton, uploadPicButton;
    ImageView userPicture;
    Bitmap userPic;
    TextView usernamefield;
    boolean connected;
    ConstraintLayout constraintLayout;
    private static final String TAG = "pictureUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_user);

        userPicture = findViewById(R.id.userPicture);
        usernamefield = findViewById(R.id.usernamefield);
        userPicButton = findViewById(R.id.takePic);
        uploadPicButton = findViewById(R.id.uploadPic);
        constraintLayout = findViewById(R.id.picture_user_constraintlayout);
        uploadPicButton.setAlpha(0);

        String userPicString = getIntent().getStringExtra("userPic");
        String username = getIntent().getStringExtra("username");
        if (userPicString.length() == 0)
            Glide.with(userPicture).load(R.drawable.person).into(userPicture);
        else
            Glide.with(userPicture).load(userPicString).into(userPicture);
        usernamefield.setText(username);

    }

    public void takePic(View view) {
        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (resultCode == RESULT_OK && data != null) {
                Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                userPicture.setImageBitmap(selectedImage);
                userPic = selectedImage;
                uploadPicButton.setAlpha(1);
            }
        }
    }

    public void uploadUserPic(View view) {
//        Toast.makeText(this, "Will Upload Soon!", Toast.LENGTH_SHORT).show();
        if (isConnected()) {
            if (FirebaseAuth.getInstance().getUid() != null) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference reference = storage.getReference();
                StorageReference person = reference.child(userPic.toString());
                Bitmap perBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.person);
                ByteArrayOutputStream boas = new ByteArrayOutputStream();
                perBitmap.compress(Bitmap.CompressFormat.JPEG, 100, boas);
                person.putBytes(boas.toByteArray())
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                handleUpload(userPic);
                                Toast.makeText(pictureUser.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(pictureUser.this, "Some error occured", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Please Log in using a valid email id.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(constraintLayout, Html.fromHtml("<font color='yellow'>Connection Lost</font>"), Snackbar.LENGTH_LONG)
                    .setAction(Html.fromHtml("<font color='yellow'>Connect</font>"), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openSettings();
                        }
                    })
                    .setDuration(2000)
                    .show();

        }

    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, boas);
        String uid = FirebaseAuth.getInstance().getUid().toString();
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child(uid)
                .child("profile Images")
                .child("beglobal")
                .child(uid + ".jpeg");
        reference.putBytes(boas.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(reference);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(pictureUser.this, "Upload Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        userProfileUrl(uri);
                    }
                });
    }

    private void userProfileUrl(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(pictureUser.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(pictureUser.this, "Some error occured", Toast.LENGTH_SHORT).show();
            }
        });
        Glide.with(userPicture).load(uri).into(userPicture);
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Objects.requireNonNull(cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()) == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()) == NetworkInfo.State.CONNECTED)
            connected = true;
        else connected = false;

        return connected;
    }

    private void openSettings() {
        startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
    }
}
