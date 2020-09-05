package com.snehasisdebbarman.chatapp;

public class ModelUser {
    String name, email, phone, details, search, image,uid;

    public ModelUser() {

    }

    public ModelUser(String name, String email, String phone, String details, String search, String image, String uid) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.details = details;
        this.search = search;
        this.image = image;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}