package com.votingsystem.db.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.PropertyName;

public class User implements Parcelable {

    String id, password;
    String name, image, gender, dob, address, state;
    boolean voted;

    public User() {
    }

    public User(String id, String password) {
        this.id = id;
        this.password = password;
    }

    protected User(Parcel in) {
        id = in.readString();
        password = in.readString();
        name = in.readString();
        image = in.readString();
        gender = in.readString();
        dob = in.readString();
        address = in.readString();
        state = in.readString();
        voted = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(password);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(gender);
        dest.writeString(dob);
        dest.writeString(address);
        dest.writeString(state);
        dest.writeByte((byte) (voted ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @PropertyName("is_voted")
    public boolean isVoted() {
        return voted;
    }
    @PropertyName("is_voted")
    public void setVoted(boolean voted) {
        this.voted = voted;
    }
}
