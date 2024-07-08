package com.example.warehouse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvName, tvEmail, tvRole;
    private ImageView ivProfilePicture;
    private Button btnEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUsername = findViewById(R.id.tvUsername);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvRole = findViewById(R.id.tvRole);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = sharedPreferences.getString("USERNAME", "");
        String name = sharedPreferences.getString("NAME", "");
        String email = sharedPreferences.getString("EMAIL", "");
        String role = sharedPreferences.getString("ROLE", "");
        String profileUrl = sharedPreferences.getString("PROFILE", "");

        tvUsername.setText(username);
        tvName.setText(name);
        tvEmail.setText(email);
        tvRole.setText(role);

        if (!profileUrl.isEmpty()) {
            Glide.with(this).load(profileUrl).into(ivProfilePicture);
        }

        btnEditProfile.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
    }
}
