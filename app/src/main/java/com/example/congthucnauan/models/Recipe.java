package com.example.congthucnauan.models;

import java.io.Serializable;

public class Recipe implements Serializable {
    private String id;
    private String name;
    private String imageUrl;
    private String steps;
    private String categoryId;
    public Recipe() {}

    public Recipe(String id, String name, String imageUrl, String steps, String categoryId) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.steps = steps;
        this.categoryId =categoryId;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}