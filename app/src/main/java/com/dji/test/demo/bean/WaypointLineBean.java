package com.dji.test.demo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class WaypointLineBean implements Parcelable {
    @Id(autoincrement = true)
    Long id;

    @NotNull
    private String lineName;//航线名称


    private String num;//航点数量


    private double   HomeLatitude;//起飞点纬度

    private double   HomeLongitude;//起飞点经度

    private float   LocationAltitude;//起飞点水平高度

    public double getHomeLatitude() {
        return HomeLatitude;
    }

    public void setHomeLatitude(double homeLatitude) {
        HomeLatitude = homeLatitude;
    }

    public double getHomeLongitude() {
        return HomeLongitude;
    }

    public void setHomeLongitude(double homeLongitude) {
        HomeLongitude = homeLongitude;
    }

    public float getLocationAltitude() {
        return LocationAltitude;
    }

    public void setLocationAltitude(float locationAltitude) {
        LocationAltitude = locationAltitude;
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


    @Override
    public String toString() {
        return "WaypointLineBean{" +
                "id=" + id +
                ", lineName='" + lineName + '\'' +
                ", HomeLatitude='" + HomeLatitude + '\'' +
                ", HomeLongitude='" + HomeLongitude + '\'' +
                '}';
    }

    public String getNum() {
        return this.num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.lineName);
        dest.writeString(this.num);
        dest.writeDouble(this.HomeLatitude);
        dest.writeDouble(this.HomeLongitude);
        dest.writeFloat(this.LocationAltitude);
    }

    protected WaypointLineBean(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.lineName = in.readString();
        this.num = in.readString();
        this.HomeLatitude = in.readDouble();
        this.HomeLongitude = in.readDouble();
        this.LocationAltitude = in.readFloat();
    }

    @Generated(hash = 1230736907)
    public WaypointLineBean(Long id, @NotNull String lineName, String num,
            double HomeLatitude, double HomeLongitude, float LocationAltitude) {
        this.id = id;
        this.lineName = lineName;
        this.num = num;
        this.HomeLatitude = HomeLatitude;
        this.HomeLongitude = HomeLongitude;
        this.LocationAltitude = LocationAltitude;
    }

    public static final Creator<WaypointLineBean> CREATOR = new Creator<WaypointLineBean>() {
        @Override
        public WaypointLineBean createFromParcel(Parcel source) {
            return new WaypointLineBean(source);
        }

        @Override
        public WaypointLineBean[] newArray(int size) {
            return new WaypointLineBean[size];
        }
    };
}
