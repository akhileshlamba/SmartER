package com.example.akhileshlamba.smarter.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by akhileshlamba on 5/4/18.
 */

public class House implements Parcelable{
    private Integer resid;
    private String firstName;
    private String lastName;
    private Date dob;
    private String address;
    private int postcode;
    private String email;
    private long mobile;
    private int noOfOccupants;
    private String energyproviderName;

    public House() {
    }

    public House(Parcel parcel) {
        this.resid = parcel.readInt();
        this.firstName = parcel.readString();
        this.lastName = parcel.readString();
        this.dob = new Date(parcel.readString());
        this.address = parcel.readString();
        this.postcode = parcel.readInt();
        this.email = parcel.readString();
        this.mobile = parcel.readLong();
        this.noOfOccupants = parcel.readInt();
        this.energyproviderName = parcel.readString();
    }

    public Integer getResid() {
        return resid;
    }

    public void setResid(Integer resid) {
        this.resid = resid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPostcode() {
        return postcode;
    }

    public void setPostcode(int postcode) {
        this.postcode = postcode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getMobile() {
        return mobile;
    }

    public void setMobile(long mobile) {
        this.mobile = mobile;
    }

    public int getNoOfOccupants() {
        return noOfOccupants;
    }

    public void setNoOfOccupants(int noOfOccupants) {
        this.noOfOccupants = noOfOccupants;
    }

    public String getEnergyproviderName() {
        return energyproviderName;
    }

    public void setEnergyproviderName(String energyproviderName) {
        this.energyproviderName = energyproviderName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(resid);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(dob.toString());
        dest.writeString(address);
        dest.writeInt(postcode);
        dest.writeString(email);
        dest.writeLong(mobile);
        dest.writeInt(noOfOccupants);
        dest.writeString(energyproviderName);
    }

    public static final Creator<House> CREATOR
            = new Creator<House>() {
        public House createFromParcel(Parcel in) {
            return new House(in);
        }

        public House[] newArray(int size) {
            return new House[size];
        }
    };


}
