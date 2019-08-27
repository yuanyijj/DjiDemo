package com.dji.test.demo.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class WaypointBean {
    @Id(autoincrement = true)
    Long id;

    @NotNull
    private String Uid;//航点id


    @NotNull
    private String Latitude;//纬度
    @NotNull
    private String Longitude;//经度
    @NotNull
    private String DestinationHeight;//高度

    private String LocationAltitude;//海拔高度

    private String HeadingModeString;//兴趣点学习

    @Transient
    List<MissionHeadingMode> tlist;

    @Generated(hash = 952474857)
    public WaypointBean(Long id, @NotNull String Uid, @NotNull String Latitude,
            @NotNull String Longitude, @NotNull String DestinationHeight,
            String LocationAltitude, String HeadingModeString) {
        this.id = id;
        this.Uid = Uid;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.DestinationHeight = DestinationHeight;
        this.LocationAltitude = LocationAltitude;
        this.HeadingModeString = HeadingModeString;
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


    public String getLatitude() {
        return this.Latitude;
    }

    public void setLatitude(String Latitude) {
        this.Latitude = Latitude;
    }

    public String getLongitude() {
        return this.Longitude;
    }

    public void setLongitude(String Longitude) {
        this.Longitude = Longitude;
    }

    public String getDestinationHeight() {
        return this.DestinationHeight;
    }

    public void setDestinationHeight(String DestinationHeight) {
        this.DestinationHeight = DestinationHeight;
    }

    public String getLocationAltitude() {
        return this.LocationAltitude;
    }

    public void setLocationAltitude(String LocationAltitude) {
        this.LocationAltitude = LocationAltitude;
    }

    public String getHeadingModeString() {
        return this.HeadingModeString;
    }

    public void setHeadingModeString(String HeadingModeString) {
        this.HeadingModeString = HeadingModeString;
    }

    public String getUid() {
        return this.Uid;
    }

    public void setUid(String Uid) {
        this.Uid = Uid;
    }

    class MissionHeadingMode {
        private String Mode;
        private String Var;
    }
}

