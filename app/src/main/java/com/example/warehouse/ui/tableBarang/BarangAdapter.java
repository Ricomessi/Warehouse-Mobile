package com.example.warehouse.ui.tableBarang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.warehouse.R;
import com.example.warehouse.model.Barang;

import java.util.List;

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.BarangViewHolder> {
    private Context context;
    private List<Barang> barangList;

    public BarangAdapter(Context context, List<Barang> barangList) {
        this.context = context;
        this.barangList = barangList;
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
    }

    @Override
    public int getItemCount() {
        return barangList.size();
    }

    public static class BarangViewHolder extends RecyclerView.ViewHolder {
        TextView namaBarang, jenisBarang, stock;
        ImageView gambarBarang;

        public BarangViewHolder(@NonNull View itemView) {
            super(itemView);
            namaBarang = itemView.findViewById(R.id.nama_barang);
            jenisBarang = itemView.findViewById(R.id.jenis_barang);
            stock = itemView.findViewById(R.id.stock);
            gambarBarang = itemView.findViewById(R.id.gambar_barang);
        }
    }
}
