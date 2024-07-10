package com.example.warehouse.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.warehouse.EditProfileActivity;
import com.example.warehouse.R;

public class MenuProfile extends Fragment {

    private TextView tvUsername, tvName, tvEmail, tvRole;
    private ImageView ivProfilePicture;
    private Button btnEditProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_profile, container, false);

        tvUsername = view.findViewById(R.id.tvUsername);
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvRole = view.findViewById(R.id.tvRole);
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserSession", getActivity().MODE_PRIVATE);
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

        btnEditProfile.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
