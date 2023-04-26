package com.example.wisatawakatobi.model;

public class Wisata implements Comparable<Wisata> {
    private String id, nama, lokasi, deskripsi, jumlah, foto, kategori, harga;
    private double JWValue;

    public Wisata(String nama, String lokasi, String deskripsi, String jumlah, String foto, String kategori, String harga) {
        this.nama = nama;
        this.lokasi = lokasi;
        this.deskripsi = deskripsi;
        this.jumlah = jumlah;
        this.foto = foto;
        this.kategori = kategori;
        this.harga = harga;
    }

    public double getJWValue() {
        return JWValue;
    }

    public void setJWValue(double JWValue) {
        this.JWValue = JWValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    @Override
    public int compareTo(Wisata wisata) {
        return this.nama.compareTo(wisata.getNama());
    }
}
