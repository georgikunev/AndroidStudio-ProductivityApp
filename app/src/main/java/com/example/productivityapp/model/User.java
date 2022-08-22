package com.example.productivityapp.model;

public class User {

    private String usersId;
    private String email;
    private String userName;
    private String userType;

    public User(){

    }

    public String getUsersId() {
        return usersId;
    }

    public void setUsersId(String usersId) {
        this.usersId = usersId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public User(String userId, String email, String userName, String userType) {
        this.usersId = userId;
        this.email = email;
        this.userName = userName;
        this.userType = userType;
    }
}
