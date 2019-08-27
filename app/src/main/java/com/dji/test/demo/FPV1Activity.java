package com.dji.test.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.dji.test.demo.util.DensityUtil;

import dji.ux.widget.FPVOverlayWidget;
import dji.ux.widget.FPVWidget;

public class FPV1Activity extends Activity {

    private ViewGroup parentView;
    private FPVWidget fpvWidget;
    private FPVOverlayWidget fpvOverlayWidget;
    private boolean isMapMini = true;

    private int height;
    private int width;
    private int margin;
    private int deviceWidth;
    private int deviceHeight;
    private String TAG = "FPVActivity";
    public AMap aMap;// 高德地图
    MapView mMapView = null;
    private UiSettings mUiSettings;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    public AMapLocation aMapLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpv1);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map_widget);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);


        height = DensityUtil.dip2px(this, 100);
        width = DensityUtil.dip2px(this, 150);
        margin = DensityUtil.dip2px(this, 12);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;


        parentView = findViewById(R.id.root_view);
        fpvOverlayWidget = findViewById(R.id.fpv_overlay_widget);
        fpvWidget = findViewById(R.id.fpv_widget);
        fpvWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onViewClick(fpvWidget);
            }
        });

        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);
            mUiSettings = aMap.getUiSettings();
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            // 设置小红点的图标
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.weizhi);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(DensityUtil.dip2px(this, 26), DensityUtil.dip2px(this, 26)));
            BitmapDescriptor markerIcon = BitmapDescriptorFactory
                    .fromView(imageView);
            myLocationStyle.myLocationIcon(markerIcon);
            myLocationStyle.radiusFillColor(android.R.color.transparent);
            myLocationStyle.strokeColor(android.R.color.transparent);
            aMap.setMyLocationStyle(myLocationStyle);
            // aMap.setLocationSource(FPV1Activity.this);// 设置定位监听
            //alert("开始设置位置监听", Toast.LENGTH_SHORT);
            aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
            mUiSettings.setZoomControlsEnabled(false);
            mUiSettings.setMyLocationButtonEnabled(false);

            aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    return false;
                }
            });

            aMap.setLocationSource(new LocationSource() {
                @Override
                public void activate(OnLocationChangedListener listener) {
                    mListener = listener;
                    if (mlocationClient == null) {
                        mlocationClient = new AMapLocationClient(FPV1Activity.this);
                        mLocationOption = new AMapLocationClientOption();
                        mLocationOption.setMockEnable(false);
                        //设置定位监听
                        mlocationClient.setLocationListener(new AMapLocationListener() {
                            @Override
                            public void onLocationChanged(AMapLocation location) {
                                if (mListener != null && location != null) {
                                    if (location.getErrorCode() == 0) {
                                        aMapLocation = location;
                                        mListener.onLocationChanged(location);// 显示系统小蓝点
                                        //设置缩放级别
                                        aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
                                        //将地图移动到定位点
                                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                                    }
                                }
                            }
                        });
                        //设置为高精度定位模式
                        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                        mLocationOption.setInterval(3000);
                        //设置定位参数
                        mlocationClient.setLocationOption(mLocationOption);
                        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
                        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
                        // 在定位结束后，在合适的生命周期调用onDestroy()方法
                        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
                        mlocationClient.startLocation();
                    }
                }

                @Override
                public void deactivate() {

                }
            });// 设置定位监听

            aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng lng) {
                    onViewClick(mMapView);
                }
            });

        }


    }


    private void onViewClick(View view) {
        if (view == fpvWidget && !isMapMini) {
            resizeFPVWidget(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, margin, 0);
            ResizeAnimation mapViewAnimation = new ResizeAnimation(mMapView, deviceWidth, deviceHeight, width, height, margin);
            mMapView.startAnimation(mapViewAnimation);
            isMapMini = true;
        } else if (view == mMapView && isMapMini) {
            resizeFPVWidget(width, height, margin, 3);
            ResizeAnimation mapViewAnimation = new ResizeAnimation(mMapView, width, height, deviceWidth, deviceHeight, 0);
            mMapView.startAnimation(mapViewAnimation);
            isMapMini = false;
        }
    }


    private void resizeFPVWidget(int width, int height, int margin, int fpvInsertPosition) {
        RelativeLayout.LayoutParams fpvParams = (RelativeLayout.LayoutParams) fpvWidget.getLayoutParams();
        fpvParams.height = height;
        fpvParams.width = width;
        fpvParams.rightMargin = margin;
        fpvParams.bottomMargin = margin;
        if (isMapMini) {
            fpvParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        } else {
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            fpvParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        }

        parentView.removeView(fpvWidget);
        fpvWidget.setLayoutParams(fpvParams);
        parentView.addView(fpvWidget, fpvInsertPosition);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Hide both the navigation bar and the status bar.
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    private class ResizeAnimation extends Animation {

        private View mView;
        private int mToHeight;
        private int mFromHeight;

        private int mToWidth;
        private int mFromWidth;
        private int mMargin;

        private ResizeAnimation(View v, int fromWidth, int fromHeight, int toWidth, int toHeight, int margin) {
            mToHeight = toHeight;
            mToWidth = toWidth;
            mFromHeight = fromHeight;
            mFromWidth = fromWidth;
            mView = v;
            mMargin = margin;
            setDuration(300);
            setFillAfter(true);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float height = (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
            float width = (mToWidth - mFromWidth) * interpolatedTime + mFromWidth;
            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) mView.getLayoutParams();
            p.height = (int) height;
            p.width = (int) width;
            p.leftMargin = mMargin;
            p.bottomMargin = mMargin;
            mView.requestLayout();
        }
    }

}
