package com.example.warehouse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etEmail, etPassword;
    private ImageView ivProfilePicture;
    private Button btnSaveProfile, btnChangeProfilePicture;
    private Uri imageUri;
    private DatabaseReference database;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnChangeProfilePicture = findViewById(R.id.btnChangeProfilePicture);

        // Set up the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Edit Profile");
            actionBar.setDisplayHomeAsUpEnabled(true); // Show the back button
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarBackground)));
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24); // Ensure you have a black back button icon
        }

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = sharedPreferences.getString("USERNAME", "");
        String name = sharedPreferences.getString("NAME", "");
        String email = sharedPreferences.getString("EMAIL", "");
        String role = sharedPreferences.getString("ROLE", "");
        String profileUrl = sharedPreferences.getString("PROFILE", "");

        etName.setText(name);
        etEmail.setText(email);

        if (!profileUrl.isEmpty()) {
            Glide.with(this).load(profileUrl).into(ivProfilePicture);
        }

        btnChangeProfilePicture.setOnClickListener(view -> openFileChooser());

        btnSaveProfile.setOnClickListener(view -> saveProfileChanges(username));
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

    private void saveProfileChanges(String username) {
        String newName = etName.getText().toString();
        String newEmail = etEmail.getText().toString();
        String newPassword = etPassword.getText().toString();

        if (newName.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(getApplicationContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        database = FirebaseDatabase.getInstance().getReference("users").child(username);
        database.child("nama").setValue(newName);
        database.child("email").setValue(newEmail);
        database.child("password").setValue(newPassword); // Updating password

        if (imageUri != null) {
            storageReference = FirebaseStorage.getInstance().getReference("profile_pictures/" + username + ".jpg");
            UploadTask uploadTask = storageReference.putFile(imageUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String profileUrl = uri.toString();
                database.child("profile").setValue(profileUrl);
                editor.putString("PROFILE", profileUrl);
                editor.apply();
                Toast.makeText(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();

                // Navigate back to ProfileActivity
                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                intent.putExtra("NAME", newName);
                intent.putExtra("EMAIL", newEmail);
                startActivity(intent);
                finish(); // Finish EditProfileActivity

            })).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();

            // Navigate back to ProfileActivity
            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
            intent.putExtra("NAME", newName);
            intent.putExtra("EMAIL", newEmail);
            startActivity(intent);
            finish(); // Finish EditProfileActivity
        }

        editor.putString("NAME", newName);
        editor.putString("EMAIL", newEmail);
        editor.putString("PASSWORD", newPassword); // Storing updated password
        editor.apply();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button click here
            finish(); // This will close the current activity and return to the previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
