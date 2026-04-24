package com.example.congthucnauan.models;

import java.util.ArrayList;

public class Place {
    private  String id;
    private  String categoryId;
    private  String name;
    private String description;
    private String provice;
    private  String district;
    private ArrayList<String> images;
    private  double latitude;
    private  double langitude;
    private  ArrayList<String> tags;
    private  int likes;

    public Place() { }

    public Place(String id, String categoryId, String name, String description, String provice, String district, ArrayList<String> images, double latitude, double langitude, ArrayList<String> tags, int likes) {

        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.provice = provice;
        this.district = district;
        this.images = images;
        this.latitude = latitude;
        this.langitude = langitude;
        this.tags = tags;
        this.likes = likes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProvice() {
        return provice;
    }

    public void setProvice(String provice) {
        this.provice = provice;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLangitude() {
        return langitude;
    }

    public void setLangitude(double langitude) {
        this.langitude = langitude;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
