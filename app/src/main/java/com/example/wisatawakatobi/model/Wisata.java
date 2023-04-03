package com.example.wisatawakatobi.model;

public class Wisata {
    private String nama, lokasi, deskripsi, rating;

    public Wisata(String nama, String lokasi, String deskripsi, String rating) {
        this.nama = nama;
        this.lokasi = lokasi;
        this.deskripsi = deskripsi;
        this.rating = rating;
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

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
