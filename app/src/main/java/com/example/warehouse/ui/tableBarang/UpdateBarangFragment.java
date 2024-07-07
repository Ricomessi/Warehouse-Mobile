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

import com.bumptech.glide.Glide;
import com.example.warehouse.R;
import com.example.warehouse.model.Barang;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class UpdateBarangFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextNamaBarang, editTextJenisBarang, editTextStock;
    private ImageView imageViewGambarBarang;
    private Button buttonUpdate, buttonChooseImage;
    private Uri imageUri;
    private Barang barang;
    private String barangId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_barang, container, false);

        editTextNamaBarang = view.findViewById(R.id.editTextNamaBarang);
        editTextJenisBarang = view.findViewById(R.id.editTextJenisBarang);
        editTextStock = view.findViewById(R.id.editTextStock);
        imageViewGambarBarang = view.findViewById(R.id.imageViewGambarBarang);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
        buttonChooseImage = view.findViewById(R.id.buttonChooseImage);

        if (getArguments() != null) {
            barang = (Barang) getArguments().getSerializable("barang");
            barangId = getArguments().getString("barangId");

            if (barang != null) {
                editTextNamaBarang.setText(barang.getNama_barang());
                editTextJenisBarang.setText(barang.getJenis_barang());
                editTextStock.setText(barang.getStockAsString());
                // Load the image using Glide or any image loading library
                Glide.with(this).load(barang.getGambar_barang()).into(imageViewGambarBarang);
            }
        }

        buttonChooseImage.setOnClickListener(v -> openFileChooser());

        buttonUpdate.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImage();
            } else {
                updateBarang(barang.getGambar_barang());
            }
        });

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
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
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");
            StorageReference fileReference = storageReference.child(UUID.randomUUID().toString());

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    updateBarang(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBarang(String imageUrl) {
        String namaBarang = editTextNamaBarang.getText().toString().trim();
        String jenisBarang = editTextJenisBarang.getText().toString().trim();
        String stock = editTextStock.getText().toString().trim();

        if (TextUtils.isEmpty(namaBarang) || TextUtils.isEmpty(jenisBarang) || TextUtils.isEmpty(stock)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("barang").child(barangId);
        Barang updatedBarang = new Barang();
        updatedBarang.setGambar_barang(imageUrl);
        updatedBarang.setJenis_barang(jenisBarang);
        updatedBarang.setNama_barang(namaBarang);
        updatedBarang.setStock(stock);

        databaseReference.setValue(updatedBarang)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Barang updated", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Failed to update barang", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
