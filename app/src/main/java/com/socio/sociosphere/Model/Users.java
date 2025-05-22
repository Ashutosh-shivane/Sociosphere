package com.socio.sociosphere.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Users implements Parcelable {

    private String name, email, password, profession;
    private String coverPhoto;  // Field for cover photo URL
    private String profilePhoto;  // Field for profile photo URL
    private String userId;
    private int followerCount = 0;

    // Default constructor
    public Users() {
    }

    // Constructor for initializing user details
    public Users(String name, String profession, String email, String password, String userId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profession = profession;
        this.userId = userId;
    }

    // Getter and setter methods
    public String getUsername() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getUser_id() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(profession);
        dest.writeString(coverPhoto);
        dest.writeString(profilePhoto);
        dest.writeString(userId);
        dest.writeInt(followerCount);
    }

    // Constructor to recreate the object from Parcel
    protected Users(Parcel in) {
        name = in.readString();
        email = in.readString();
        password = in.readString();
        profession = in.readString();
        coverPhoto = in.readString();
        profilePhoto = in.readString();
        userId = in.readString();
        followerCount = in.readInt();
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel in) {
            return new Users(in);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };
}
