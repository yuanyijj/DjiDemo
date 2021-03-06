package com.dji.test.demo.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import dji.common.mission.waypoint.WaypointActionType;

@Entity
public class WaypointBean {
    @Id(autoincrement = true)
    Long id;

    @NotNull
    private String Uid;//航点id

    @NotNull
    private double Latitude;//纬度
    @NotNull
    private double Longitude;//经度
    @NotNull
    private float DestinationHeight;//高度

    private float LocationAltitude;//海拔高度

    private String HeadingModeString;//航点动作

    private String picPath;//图片路径


    @Generated(hash = 542461796)
    public WaypointBean(Long id, @NotNull String Uid, double Latitude,
            double Longitude, float DestinationHeight, float LocationAltitude,
            String HeadingModeString, String picPath) {
        this.id = id;
        this.Uid = Uid;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.DestinationHeight = DestinationHeight;
        this.LocationAltitude = LocationAltitude;
        this.HeadingModeString = HeadingModeString;
        this.picPath = picPath;
    }


    @Generated(hash = 1682917225)
    public WaypointBean() {
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getUid() {
        return this.Uid;
    }


    public void setUid(String Uid) {
        this.Uid = Uid;
    }


    public double getLatitude() {
        return this.Latitude;
    }


    public void setLatitude(double Latitude) {
        this.Latitude = Latitude;
    }


    public double getLongitude() {
        return this.Longitude;
    }


    public void setLongitude(double Longitude) {
        this.Longitude = Longitude;
    }


    public float getDestinationHeight() {
        return this.DestinationHeight;
    }


    public void setDestinationHeight(float DestinationHeight) {
        this.DestinationHeight = DestinationHeight;
    }


    public float getLocationAltitude() {
        return this.LocationAltitude;
    }


    public void setLocationAltitude(float LocationAltitude) {
        this.LocationAltitude = LocationAltitude;
    }


    public String getHeadingModeString() {
        return this.HeadingModeString;
    }


    public void setHeadingModeString(String HeadingModeString) {
        this.HeadingModeString = HeadingModeString;
    }


    public String getPicPath() {
        return this.picPath;
    }


    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }


    public static class MissionHeadingMode {
        private WaypointActionType Mode;
        private int Var;

        public WaypointActionType getMode() {
            return Mode;
        }

        public void setMode(WaypointActionType mode) {
            Mode = mode;
        }

        public int getVar() {
            return Var;
        }

        public void setVar(int var) {
            Var = var;
        }
    }


}

