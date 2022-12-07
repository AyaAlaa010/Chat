package com.example.chat.bojo;

public class ChatUserBojo {
    private String uid;
    private String name;
    private   String status;



    String profileImage;
    private UserStateBojo userStateBojo;

    public ChatUserBojo(String uid, String name, String status, UserStateBojo userStateBojo, String profileImage) {
        this.uid = uid;
        this.name = name;
        this.status = status;
        this.userStateBojo = userStateBojo;
          this.profileImage = profileImage;

    }


    public ChatUserBojo() {


    }

    public ChatUserBojo(String uid, String name, String status, UserStateBojo userStateBojo) {

        this.uid = uid;
        this.name = name;
        this.status = status;
        this.userStateBojo = userStateBojo;

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


    public UserStateBojo getUserStateBojo() {
        return userStateBojo;
    }

    public void setUserStateBojo(UserStateBojo userStateBojo) {
        this.userStateBojo = userStateBojo;
    }

//    public String getImage() {
//        return image;
//    }
//
//    public void setImage(String image) {
//        this.image = image;
//    }










}
