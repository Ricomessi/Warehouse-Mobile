package com.example.warehouse.ui.tableBarang;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warehouse.R;
import com.example.warehouse.databinding.FragmentGalleryBinding;
import com.example.warehouse.model.Barang;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TableBarangFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private RecyclerView recyclerView;
    private BarangAdapter adapter;
    private List<Barang> barangList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        barangList = new ArrayList<>();
        adapter = new BarangAdapter(getContext(), barangList, new BarangAdapter.OnItemClickListener() {
            @Override
            public void onUpdateClick(Barang barang) {
                openUpdateFragment(barang);
            }

            @Override
            public void onDeleteClick(String barangId) {
                deleteBarang(barangId);
            }
        });
        recyclerView.setAdapter(adapter);

        fetchBarangData();

        FloatingActionButton fabAddItem = binding.fabAddItem;
        fabAddItem.setOnClickListener(view -> {
            openCreateFragment();
        });


        return root;
    }

    private void openUpdateFragment(Barang barang) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("barang", barang);
        bundle.putString("barangId", barang.getId());

        UpdateBarangFragment updateFragment = new UpdateBarangFragment();
        updateFragment.setArguments(bundle);

        // Menggantikan fragment di dalam fragment_container dengan UpdateBarangFragment
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, updateFragment)
                .addToBackStack(null)
                .commit();
    }

    private void openCreateFragment() {
        CreateBarangFragment createFragment = new CreateBarangFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, createFragment)
                .addToBackStack(null)
                .commit();
    }



    private void deleteBarang(String barangId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("barang").child(barangId);
        databaseReference.removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Barang deleted", Toast.LENGTH_SHORT).show();
                        fetchBarangData();
                    } else {
                        Toast.makeText(getContext(), "Failed to delete barang", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchBarangData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("barang");
        Query query = databaseReference.orderByKey().limitToLast(50); // Adjust limit as needed
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                barangList.clear();
                List<Barang> tempList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Barang barang = dataSnapshot.getValue(Barang.class);
                    if (barang != null) {
                        barang.setId(dataSnapshot.getKey()); // Ensure the ID is set
                        tempList.add(barang);
                    }
                }
                // Add items from tempList to barangList in reverse order
                for (int i = tempList.size() - 1; i >= 0; i--) {
                    barangList.add(tempList.get(i));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
