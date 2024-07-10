package com.example.warehouse.ui.home;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.warehouse.databinding.FragmentHomeBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    private PieChart pieChart;
    private Map<String, Integer> categoryCounts = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        final TextView welcomeTextView = binding.welcome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), welcomeTextView::setText);

        pieChart = binding.piechart;
        setupPieChart();
        loadDataFromFirebase();

        // Fetch username from session and update welcome message
        fetchUsernameFromSession();

        return root;
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(android.R.color.white);
        pieChart.setTransparentCircleRadius(61f);

        // Enable entry labels (names inside the pie chart)
        pieChart.setDrawEntryLabels(true);

        // Add value selection listener to display values on click
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e instanceof PieEntry) {
                    PieEntry pe = (PieEntry) e;
                    Toast.makeText(getContext(), pe.getLabel() + ": " + (int) pe.getValue(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected() {
                // Do nothing
            }
        });
    }

    private void loadDataFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("barang");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryCounts.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String jenisBarang = snapshot.child("jenis_barang").getValue(String.class);
                    if (jenisBarang != null) {
                        int count = categoryCounts.getOrDefault(jenisBarang, 0);
                        categoryCounts.put(jenisBarang, count + 1);
                    }
                }
                updatePieChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void updatePieChart() {
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Barang Categories");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        // Hide values initially
        dataSet.setDrawValues(false);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(0f); // Initially hide values on the pie slices
        data.setValueTextColor(Color.BLACK); // Change text color for better readability

        pieChart.setData(data);
        pieChart.invalidate(); // refresh

        // Enable value selection for showing values on slice click
        pieChart.setHighlightPerTapEnabled(true);
    }

    private void fetchUsernameFromSession() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = sharedPreferences.getString("USERNAME", "");

        if (!username.isEmpty()) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(username).child("nama");
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String nama = dataSnapshot.getValue(String.class);
                        homeViewModel.setTextWithUsername(nama);
                        // Gunakan nilai nama di sini, misalnya untuk mengatur teks welcomeTextView
//                        welcomeTextView.setText("Welcome, " + nama);
                    } else {
                        Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Failed to fetch user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Failed to fetch username", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
