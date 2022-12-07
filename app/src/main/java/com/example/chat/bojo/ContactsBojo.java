package com.example.chat.bojo;

public class ContactsBojo {
    private String uid;
    private String name;
    private   String status;
   String profileImage;

    public ContactsBojo() {
    }

    public ContactsBojo(String name, String status, String uid,String profileImage) {
        this.name = name;
        this.status = status;
        this.uid=uid;
        this.profileImage = profileImage;
    }

    public ContactsBojo(String name, String status, String uid) {
        this.name = name;
        this.status = status;
        this.uid=uid;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getId() {
        return uid;
    }

    public void setId(String uid ) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

//    public String getImage() {
//        return image;
//    }
//
//    public void setImage(String image) {
//        this.image = image;
//    }










}
