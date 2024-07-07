package com.example.warehouse.model;

public class Barang {
    private String gambar_barang;
    private String jenis_barang;
    private String nama_barang;
    private Object stock;  // Changed to Object

    public Barang() {
        // Default constructor required for calls to DataSnapshot.getValue(Barang.class)
    }

    public Barang(String gambar_barang, String jenis_barang, String nama_barang, Object stock) {
        this.gambar_barang = gambar_barang;
        this.jenis_barang = jenis_barang;
        this.nama_barang = nama_barang;
        this.stock = stock;
    }

    public String getGambar_barang() {
        return gambar_barang;
    }

    public void setGambar_barang(String gambar_barang) {
        this.gambar_barang = gambar_barang;
    }

    public String getJenis_barang() {
        return jenis_barang;
    }

    public void setJenis_barang(String jenis_barang) {
        this.jenis_barang = jenis_barang;
    }

    public String getNama_barang() {
        return nama_barang;
    }

    public void setNama_barang(String nama_barang) {
        this.nama_barang = nama_barang;
    }

    public Object getStock() {
        return stock;
    }

    public void setStock(Object stock) {
        this.stock = stock;
    }

    // Helper method to get stock as a String
    public String getStockAsString() {
        if (stock instanceof String) {
            return (String) stock;
        } else if (stock instanceof Long) {
            return Long.toString((Long) stock);
        } else {
            return "0";  // Default value or handle appropriately
        }
    }
}