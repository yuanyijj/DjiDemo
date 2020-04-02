package com.dji.test.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.amap.api.maps.AMap;
import com.dji.mapkit.core.maps.DJIMap;
import com.dji.mapkit.core.models.DJILatLng;
import com.dji.mapkit.core.models.annotations.DJIMarker;
import com.dji.test.demo.base.MApplication;
import com.dji.test.demo.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import dji.common.flightcontroller.FlightControllerState;
import dji.sdk.base.BaseProduct;
import dji.ux.widget.FPVOverlayWidget;
import dji.ux.widget.FPVWidget;
import dji.ux.widget.MapWidget;

public class FPVActivity extends Activity {

    private MapWidget mapWidget;
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
    private List<DJIMarker> markerList;
    private TextView tv_test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpv);

        height = DensityUtil.dip2px(this, 130);
        width = DensityUtil.dip2px(this, 180);
        margin = DensityUtil.dip2px(this, 10);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;
        markerList=new ArrayList<>();
        mapWidget = findViewById(R.id.map_widget);
        mapWidget.setAutoFrameMap(true);
        mapWidget.initAMap(new MapWidget.OnMapReadyListener() {
            @Override
            public void onMapReady(@NonNull final DJIMap map) {
                map.setMapType(DJIMap.MapType.NORMAL);
                Log.v(TAG, "地图初始化完成。。。");
                map.setOnMapClickListener(new DJIMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(DJILatLng latLng) {
                        Log.v(TAG, "latLng:getLatitude()"+latLng.getLatitude());
                        Log.v(TAG, "latLng:getLongitude()"+latLng.getLongitude());
                        onViewClick(mapWidget);
                    }
                });





                map.setOnMarkerDragListener(new DJIMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(DJIMarker djiMarker) {
                        if (markerList.contains(djiMarker)) {
                            /*Toast.makeText(FPVActivity.this,
                                    "Marker " + markerList.indexOf(djiMarker) + " drag started",
                                    Toast.LENGTH_SHORT).show();*/
                        }
                    }

                    @Override
                    public void onMarkerDrag(DJIMarker djiMarker) {
                        // do nothing
                    }

                    @Override
                    public void onMarkerDragEnd(DJIMarker djiMarker) {
                        if (markerList.contains(djiMarker)) {
                            /*Toast.makeText(FPVActivity.this,
                                    "Marker " + markerList.indexOf(djiMarker) + " drag ended",
                                    Toast.LENGTH_SHORT).show();*/
                        }
                    }
                });
                mapWidget.setOnMarkerClickListener(new DJIMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(DJIMarker djiMarker) {
                        /*Toast.makeText(FPVActivity.this, "Marker " + markerList.indexOf(djiMarker) + " clicked",
                                Toast.LENGTH_SHORT).show();*/
                        return true;
                    }
                });

            }
        });
        mapWidget.onCreate(savedInstanceState);
        parentView = findViewById(R.id.root_view);
        fpvOverlayWidget = findViewById(R.id.fpv_overlay_widget);
        fpvWidget = findViewById(R.id.fpv_widget);
        fpvWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onViewClick(fpvWidget);

            }
        });
        tv_test = findViewById(R.id.tv_test);
        try {
            getFlightControllerState();
        } catch (Exception e) {
            Log.v(TAG,e.toString());
        }
        /*mapWidget.setHomeVisible(true);
        mapWidget.isCustomUnlockZonesVisible();
        mapWidget.showCustomUnlockZones(true);
        mapWidget.setMapCenterLock(MapWidget.MapCenterLock.AIRCRAFT);*/

    }


    private void onViewClick(View view) {
        if (view == fpvWidget && !isMapMini) {
            resizeFPVWidget(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, margin, 0);
            ResizeAnimation mapViewAnimation = new ResizeAnimation(mapWidget, deviceWidth, deviceHeight, width, height, margin);
            mapWidget.startAnimation(mapViewAnimation);
            isMapMini = true;
        } else if (view == mapWidget && isMapMini) {
            resizeFPVWidget(width, height, margin, 3);
            ResizeAnimation mapViewAnimation = new ResizeAnimation(mapWidget, width, height, deviceWidth, deviceHeight, 0);
            mapWidget.startAnimation(mapViewAnimation);
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
            fpvWidget.setAlpha(1f);
        } else {
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            fpvParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            fpvWidget.setAlpha(1f);
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
        mapWidget.onResume();
    }

    @Override
    protected void onPause() {
        mapWidget.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapWidget.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapWidget.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapWidget.onLowMemory();
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


    public void getFlightControllerState() {
        BaseProduct mProduct = MApplication.getProductInstance();
        if (mProduct == null||!mProduct.isConnected()) {
            Toast.makeText(getApplicationContext(),
                    "未连接",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (MApplication.getAircraftInstance() == null) {
            return;
        }
        MApplication.getAircraftInstance().getFlightController()
                .setStateCallback(new FlightControllerState.Callback() {
                    @Override
                    public void onUpdate(@NonNull final FlightControllerState state) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getUpdataFpvState(state);
                            }
                        });
                    }
                });

    }

    public double drone_lat;// 飞机纬度
    public double drone_log;// 飞机经度
    public float droneHeight;// 飞机相对高度
    StringBuffer mStringBuffer = new StringBuffer();

    private void getUpdataFpvState(FlightControllerState state) {
        mStringBuffer.setLength(0);
        drone_lat = state.getAircraftLocation().getLatitude();
        drone_log = state.getAircraftLocation().getLongitude();
        droneHeight = state.getAircraftLocation().getAltitude();
        mStringBuffer.append("经度:" + drone_log + "\n纬度:" + drone_lat + "\n高度:" + droneHeight);
        tv_test.setText(mStringBuffer);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

}
