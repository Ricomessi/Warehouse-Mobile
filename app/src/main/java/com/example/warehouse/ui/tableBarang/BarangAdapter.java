package com.example.warehouse.ui.tableBarang;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.warehouse.R;
import com.example.warehouse.model.Barang;

import java.util.List;

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.BarangViewHolder> {
    private Context context;
    private List<Barang> barangList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onUpdateClick(Barang barang);
        void onDeleteClick(String barangId);
    }

    public BarangAdapter(Context context, List<Barang> barangList, OnItemClickListener listener) {
        this.context = context;
        this.barangList = barangList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BarangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_barang, parent, false);
        return new BarangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarangViewHolder holder, int position) {
        Barang barang = barangList.get(position);
        holder.namaBarang.setText(barang.getNama_barang());
        holder.jenisBarang.setText(barang.getJenis_barang());
        holder.stock.setText(barang.getStockAsString());
        Glide.with(context).load(barang.getGambar_barang()).into(holder.gambarBarang);

        holder.itemView.setOnClickListener(v -> listener.onUpdateClick(barang));

        holder.btnDelete.setOnClickListener(v -> showDeleteDialog(barang.getId()));

        holder.buttonAmbil.setOnClickListener(v -> {
            Intent intent = new Intent(context, AmbilBarangActivity.class);
            intent.putExtra("barang", barang);
            intent.putExtra("barangId", barang.getId());
            context.startActivity(intent);
        });
    }

    private void showDeleteDialog(String barangId) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes, Delete", (dialog, which) -> listener.onDeleteClick(barangId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return barangList.size();
    }

    public static class BarangViewHolder extends RecyclerView.ViewHolder {
        TextView namaBarang, jenisBarang, stock;
        ImageView gambarBarang;
        ImageView btnDelete;
        Button buttonAmbil;

        public BarangViewHolder(@NonNull View itemView) {
            super(itemView);
            namaBarang = itemView.findViewById(R.id.nama_barang);
            jenisBarang = itemView.findViewById(R.id.jenis_barang);
            stock = itemView.findViewById(R.id.stock);
            gambarBarang = itemView.findViewById(R.id.gambar_barang);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            buttonAmbil = itemView.findViewById(R.id.buttonAmbil);
        }
    }
}
