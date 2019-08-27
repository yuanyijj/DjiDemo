package com.dji.test.demo.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class WaypointLineBean {
    @Id(autoincrement = true)
    Long id;

    @NotNull
    private String lineName;//航线名称

    @NotNull
    private String HomeLatitude;//起飞点纬度
    @NotNull
    private String HomeLongitude;//起飞点经度

    @Generated(hash = 395298666)
    public WaypointLineBean(Long id, @NotNull String lineName,
            @NotNull String HomeLatitude, @NotNull String HomeLongitude) {
        this.id = id;
        this.lineName = lineName;
        this.HomeLatitude = HomeLatitude;
        this.HomeLongitude = HomeLongitude;
    }

    @Generated(hash = 1610417246)
    public WaypointLineBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLineName() {
        return this.lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getHomeLatitude() {
        return this.HomeLatitude;
    }

    public void setHomeLatitude(String HomeLatitude) {
        this.HomeLatitude = HomeLatitude;
    }

    public String getHomeLongitude() {
        return this.HomeLongitude;
    }

    public void setHomeLongitude(String HomeLongitude) {
        this.HomeLongitude = HomeLongitude;
    }
}
