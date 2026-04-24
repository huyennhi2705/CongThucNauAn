package com.example.congthucnauan.models;

import java.io.Serializable;

public class Category implements Serializable {
    private String id;
    private String name;
    private String description;
    private String icon;
    private String type;

    public Category() {
    }

    public Category(String id, String name, String description, String icon, String type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.type = type;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}