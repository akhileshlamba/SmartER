package com.example.akhileshlamba.smarter.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by akhileshlamba on 5/4/18.
 */

public class ResCredientials implements Parcelable {
    private String userName;
    private String password;
    private Date regDate;
    private int resid;

    public ResCredientials() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public int getResid() {
        return resid;
    }

    public void setResid(int resid) {
        this.resid = resid;
    }

    public ResCredientials(Parcel parcel) {

        this.userName = parcel.readString();
        this.password = parcel.readString();
        this.regDate = new Date(parcel.readString());
        this.resid = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(password);
        dest.writeString(regDate.toString());
        dest.writeInt(resid);
    }

    public static final Creator<ResCredientials> CREATOR
            = new Creator<ResCredientials>() {
        public ResCredientials createFromParcel(Parcel in) {
            return new ResCredientials(in);
        }

        public ResCredientials[] newArray(int size) {
            return new ResCredientials[size];
        }
    };


}
