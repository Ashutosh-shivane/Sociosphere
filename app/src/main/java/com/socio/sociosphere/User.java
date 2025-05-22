package com.socio.sociosphere;

public class User {

    private String name, email, password, profession;
    private String coverPhoto;  // Field for cover photo URL
    private String profilePhoto;  // Field for profile photo URL

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    private int followerCount = 0;




    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    String userId;

    // Default constructor
    public User() {
    }

    // Constructor for initializing user details
    public User(String name, String profession, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profession = profession;
    }

    // Getter and setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and setter for profession
    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    // Getter and setter for cover photo URL
    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    // Getter and setter for profile photo URL
    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profession='" + profession + '\'' +
                ", followerCount=" + followerCount +
                ", coverPhoto='" + coverPhoto + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                '}';
    }

}
