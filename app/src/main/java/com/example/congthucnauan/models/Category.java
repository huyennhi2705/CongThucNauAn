package com.example.congthucnauan.models;

<<<<<<< HEAD
public class Category {

    public String id;
    public String name;
    public String description;
    public String icon;
=======
import java.io.Serializable;

public class Category implements Serializable {
    private String id;
    private String name;
    private String description;
    private String icon;
    private String type;
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4

    public Category() {
    }

<<<<<<< HEAD
    public Category(String id, String name, String description, String icon) {
=======
    public Category(String id, String name, String description, String icon, String type) {
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
<<<<<<< HEAD
=======
        this.type = type;
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
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
<<<<<<< HEAD
=======

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
}