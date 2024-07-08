package com.example.warehouse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etUsername, etName, etEmail, etRole;
    private ImageView ivProfilePicture;
    private Button btnSaveProfile, btnChangeProfilePicture;
    private Uri imageUri;
    private DatabaseReference database;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etUsername = findViewById(R.id.etUsername);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etRole = findViewById(R.id.etRole);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnChangeProfilePicture = findViewById(R.id.btnChangeProfilePicture);

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = sharedPreferences.getString("USERNAME", "");
        String name = sharedPreferences.getString("NAME", "");
        String email = sharedPreferences.getString("EMAIL", "");
        String role = sharedPreferences.getString("ROLE", "");
        String profileUrl = sharedPreferences.getString("PROFILE", "");

        etUsername.setText(username);
        etName.setText(name);
        etEmail.setText(email);
        etRole.setText(role);

        if (!profileUrl.isEmpty()) {
            Glide.with(this).load(profileUrl).into(ivProfilePicture);
        }

        btnChangeProfilePicture.setOnClickListener(view -> openFileChooser());

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfileChanges();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(ivProfilePicture);
        }
    }

    private void saveProfileChanges() {
        String newUsername = etUsername.getText().toString();
        String newName = etName.getText().toString();
        String newEmail = etEmail.getText().toString();
        String newRole = etRole.getText().toString();

        if (newUsername.isEmpty() || newName.isEmpty() || newEmail.isEmpty() || newRole.isEmpty()) {
            Toast.makeText(getApplicationContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        database = FirebaseDatabase.getInstance().getReference("users").child(newUsername);
        database.child("nama").setValue(newName);
        database.child("email").setValue(newEmail);
        database.child("role").setValue(newRole);

        if (imageUri != null) {
            storageReference = FirebaseStorage.getInstance().getReference("profile_pictures/" + newUsername + ".jpg");
            UploadTask uploadTask = storageReference.putFile(imageUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String profileUrl = uri.toString();
                database.child("profile").setValue(profileUrl);
                editor.putString("PROFILE", profileUrl);
                editor.apply();
                Toast.makeText(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            })).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        }

        editor.putString("USERNAME", newUsername);
        editor.putString("NAME", newName);
        editor.putString("EMAIL", newEmail);
        editor.putString("ROLE", newRole);
        editor.apply();
    }
}
