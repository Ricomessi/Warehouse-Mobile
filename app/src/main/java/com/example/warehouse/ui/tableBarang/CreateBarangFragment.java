package com.example.warehouse.ui.tableBarang;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.warehouse.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class CreateBarangFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextNamaBarang, editTextJenisBarang, editTextStock;
    private ImageView imageViewGambarBarang;
    private Button buttonCreate, buttonChooseImage;
    private Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_barang, container, false);

        editTextNamaBarang = view.findViewById(R.id.editTextNamaBarang);
        editTextJenisBarang = view.findViewById(R.id.editTextJenisBarang);
        editTextStock = view.findViewById(R.id.editTextStock);
        imageViewGambarBarang = view.findViewById(R.id.imageViewGambarBarang);
        buttonCreate = view.findViewById(R.id.buttonCreate);
        buttonChooseImage = view.findViewById(R.id.buttonChooseImage);

        buttonChooseImage.setOnClickListener(v -> openFileChooser());

        buttonCreate.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImage();
            } else {
                createBarang(null); // Jika tidak ada gambar yang dipilih
            }
        });

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewGambarBarang.setImageURI(imageUri);
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("barang");
            StorageReference fileReference = storageReference.child(UUID.randomUUID().toString());

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            createBarang(uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void createBarang(String imageUrl) {
        String namaBarang = editTextNamaBarang.getText().toString().trim();
        String jenisBarang = editTextJenisBarang.getText().toString().trim();
        String stock = editTextStock.getText().toString().trim();

        if (TextUtils.isEmpty(namaBarang) || TextUtils.isEmpty(jenisBarang) || TextUtils.isEmpty(stock)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("barang");
        String barangId = databaseReference.push().getKey();

        // Create a map to hold the data in the required structure
        Map<String, Object> barangData = new HashMap<>();
        barangData.put("nama_barang", namaBarang);
        barangData.put("jenis_barang", jenisBarang);
        barangData.put("stock", Integer.parseInt(stock));  // assuming stock is an integer
        barangData.put("gambar_barang", imageUrl);

        // Save the data to the database with the generated key
        databaseReference.child(barangId).setValue(barangData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Barang created", Toast.LENGTH_SHORT).show();
                        clearFields(); // Clear input fields after successful creation
                    } else {
                        Toast.makeText(getContext(), "Failed to create barang", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields() {
        editTextNamaBarang.setText("");
        editTextJenisBarang.setText("");
        editTextStock.setText("");
        imageViewGambarBarang.setImageResource(R.drawable.barang);
        imageUri = null;
    }
}
