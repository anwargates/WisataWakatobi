package com.example.wisatawakatobi.model;

import java.util.ArrayList;

public class User {
    String Uid;
    ArrayList<String> Favorite = new ArrayList<String>();

    public ArrayList<String> getFavorite() {
        return Favorite;
    }

    public void setFavorite(ArrayList<String> favorite) {
        Favorite = favorite;
    }

    public User(String uid, ArrayList<String> favorite) {
        Uid = uid;
        Favorite = favorite;
    }

    public User() {

    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

}
