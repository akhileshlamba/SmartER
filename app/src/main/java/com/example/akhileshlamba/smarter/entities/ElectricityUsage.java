package com.example.akhileshlamba.smarter.entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by akhileshlamba on 5/4/18.
 */

public class ElectricityUsage implements Serializable {

    private Integer usageid;
    private Date currentdate;
    private int dayHour;
    private double fridgeUsage;
    private double airconditionerUsage;
    private double washingmachineUsage;
    private double temperature;
    private int resid;

    public Integer getUsageid() {
        return usageid;
    }

    public void setUsageid(Integer usageid) {
        this.usageid = usageid;
    }

    public Date getCurrentdate() {
        return currentdate;
    }

    public void setCurrentdate(Date currentdate) {
        this.currentdate = currentdate;
    }

    public int getDayHour() {
        return dayHour;
    }

    public void setDayHour(int dayHour) {
        this.dayHour = dayHour;
    }

    public double getFridgeUsage() {
        return fridgeUsage;
    }

    public void setFridgeUsage(double fridgeUsage) {
        this.fridgeUsage = fridgeUsage;
    }

    public double getAirconditionerUsage() {
        return airconditionerUsage;
    }

    public void setAirconditionerUsage(double airconditionerUsage) {
        this.airconditionerUsage = airconditionerUsage;
    }

    public double getWashingmachineUsage() {
        return washingmachineUsage;
    }

    public void setWashingmachineUsage(double washingmachineUsage) {
        this.washingmachineUsage = washingmachineUsage;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getResid() {
        return resid;
    }

    public void setResid(int resid) {
        this.resid = resid;
    }
}
