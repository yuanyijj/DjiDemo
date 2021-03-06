package com.dji.test.demo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.dji.test.demo.adapter.WayPointAdapter;
import com.dji.test.demo.base.MApplication;
import com.dji.test.demo.bean.WaypointBean;
import com.dji.test.demo.bean.WaypointLineBean;
import com.dji.test.demo.bean.WaypointMode;
import com.dji.test.demo.mediadown.down.DownLoadDeleteHelper;
import com.dji.test.demo.util.DJIModuleVerificationUtil;
import com.dji.test.demo.util.DensityUtil;
import com.dji.test.demo.util.GreendaoUtils;
import com.dji.test.demo.util.GsonUtil;
import com.dji.test.demo.util.LatLngUtils;
import com.dji.test.demo.util.LogUtil;
import com.dji.test.demo.util.MissionPointSet;
import com.dji.test.demo.util.RouteUtlis;
import com.dji.test.demo.util.SPUtil;
import com.dji.test.demo.util.SaveWaypointMediaFile;
import com.dji.test.demo.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.gimbal.GimbalState;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionDownloadEvent;
import dji.common.mission.waypoint.WaypointMissionExecutionEvent;
import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;
import dji.common.mission.waypoint.WaypointMissionUploadEvent;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.media.MediaFile;
import dji.sdk.media.MediaManager;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.ux.widget.FPVOverlayWidget;
import dji.ux.widget.FPVWidget;

public class FPV2Activity extends AppCompatActivity implements AMap.OnMarkerClickListener, AMap.OnMapClickListener {

    private String TAG = "FPVActivity";
    public AMap aMap;// 高德地图
    MapView mMapView = null;
    FrameLayout fl2_container_little;
    private FPVWidget fpvWidget;
    private FPVOverlayWidget fpvOverlayWidget;
    private boolean isMapMini = true;
    //地图布局
    private View viewInflateMap;
    private int height;
    private int width;
    private int margin;
    private int deviceWidth;
    private int deviceHeight;
    private ViewGroup parentView;
    private TextView tv_test;
    private TextView tv_gimbal;

    private TextView tv_progress;
    private TextView tv_lin_details;
    private ProgressBar progressBar;
    private TextView tv_progress_all;

    private Button btn_unlock_map;
    private Context mContext;
    private List<Marker> markerList = new ArrayList<>();
    private HashMap<String, String> map = new HashMap<>();
    Gson gson;
    boolean isType = true;
    String m_Ok;
    String m_clear;
    MissionPointSet missionPointSet;
    private MediaManager mediaManager = null;
    private WaypointLineBean mWaypointLineBean;
    private int WaypointNum = 0;

    private StringBuffer WaypointLinebuff = new StringBuffer();//航点详情

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpv2);
        //获取地图控件引用
        mContext = this;
        initMapView();
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);
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
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.setOnMapClickListener(this);
            aMap.setOnMarkerClickListener(this);
        }
        height = DensityUtil.dip2px(this, 130);
        width = DensityUtil.dip2px(this, 180);
        LogUtil.v(TAG,"height:"+height+";width="+width);
        margin = DensityUtil.dip2px(this, 10);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;
        parentView = findViewById(R.id.root_view);
        fl2_container_little = findViewById(R.id.fl2_container_little);
        fpvOverlayWidget = findViewById(R.id.fpv_overlay_widget);
        fpvWidget = findViewById(R.id.fpv_widget);
        fpvWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onViewClick(fpvWidget);
            }
        });
        fl2_container_little.addView(viewInflateMap);
        tv_test = findViewById(R.id.tv_test);
        tv_gimbal = findViewById(R.id.tv_gimbal);

        try {
            getFlightControllerState();
        } catch (Exception e) {
            Log.v(TAG, e.toString());
        }
        mMediaFiles = new ArrayList<>();

        markerList = new ArrayList<>();


        btnInitView();

        gson = new Gson();
        missionPointSet = new MissionPointSet(mContext);
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
        removeListener();
        super.onDestroy();
    }

    private boolean unlock_map = false;

    public void btnInitView() {
        tv_progress = findViewById(R.id.tv_progress);
        tv_lin_details = findViewById(R.id.tv_lin_details);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(99);
        tv_progress_all = findViewById(R.id.tv_progress_all);
        btn_unlock_map = findViewById(R.id.btn_unlock_map);
        btn_unlock_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (unlock_map) {
                    unlock_map = false;
                    btn_unlock_map.setText("解锁地图");
                } else {
                    btn_unlock_map.setText("锁定地图");
                    unlock_map = true;
                }
            }
        });
        findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogText("0");
            }
        });
        findViewById(R.id.btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogText("2");
               /* String value2 = SPUtil.getInstance().getString("3", "");
                if (TextUtils.isEmpty(value2)) {
                    map.put("latitude", drone_lat + "");
                    map.put("longitude", drone_log + "");
                    map.put("height", droneHeight + "");
                    Gson gson = new Gson();
                    SPUtil.getInstance().putString("2", gson.toJson(map));
                }*/
            }
        });
        findViewById(R.id.btn_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogText("3");
                /*String value3 = SPUtil.getInstance().getString("3", "");
                if (TextUtils.isEmpty(value3)) {
                    map.put("latitude", drone_lat + "");
                    map.put("longitude", drone_log + "");
                    map.put("height", droneHeight + "");
                    Gson gson = new Gson();
                    SPUtil.getInstance().putString("3", gson.toJson(map));
                }*/
            }
        });
        findViewById(R.id.btn_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogText("4");
               /* String value4 = SPUtil.getInstance().getString("4", "");
                if (TextUtils.isEmpty(value4)) {
                    map.put("latitude", drone_lat + "");
                    map.put("longitude", drone_log + "");
                    map.put("height", droneHeight + "");
                    Gson gson = new Gson();
                    SPUtil.getInstance().putString("4", gson.toJson(map));
                }*/
            }
        });
        findViewById(R.id.btn_logcation).setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                if (mHomeMarker != null) {
                    setHomeImage(mHomeLatitude, mHomeLongitude);
                }
            }
        });
        isUpdateBackPoint = false;
        istask = false;
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//清除
                // aMap.clear();
                if (!markerList.isEmpty()) {
                    for (Marker marker : markerList) {
                        marker.remove();
                    }
                    markerList.clear();
                }
                if (!markerList.isEmpty()) {
                    for (Marker marker : markerList) {
                        marker.remove();
                    }
                    markerList.clear();
                }

                mMediaFiles.clear();
                WaypointLineNum = -1;
                waypointList.clear();
                istask = false;
                isUpdateBackPoint = false;
                mWaypointLineBean = null;
                tv_lin_details.setText("");

            }
        });
        //添加飞行任务
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (waypointMissionBuilder == null) {
                    Toast.makeText(mContext, "请设置航点", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (waypointMissionBuilder.getWaypointList().size() < 2) {
                    Toast.makeText(mContext, "请最少设置一个航点", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (WaypointLineNum < 0) {
                    setResultToToast("请先选择航线");
                    return;
                }
                List<WaypointLineBean> listLine =
                        GreendaoUtils.getInstance().queryWaypointLine(WaypointLineNum + "");
                if (listLine.size() > 0) {
                    double diatas = AMapUtils.calculateLineDistance(
                            LatLngUtils.Gps84ToAmap(new LatLng(listLine.get(0).getHomeLatitude(),
                                    listLine.get(0).getHomeLongitude())),
                            homeLatLng);
                    float diatal = listLine.get(0).getLocationAltitude() - takeoffLocationAltitude;
                    setResultToToast("两次起飞点的水平位置相差：" + diatas + "米，海平面高度相差：" + diatal + "米请谨慎飞行！");
                    unlock_map = false;
                    btn_unlock_map.setText("解锁地图");
                    showSettingDialog(diatas, diatal);

                }
                //configWayPointMission();
            }
        });

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWaypointMission();
            }
        });
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopWaypointMission();
            }
        });

        /**
         * 撤销
         * */
        findViewById(R.id.btn_repeal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (markerList.size() > 0) {
                    if (waypointMissionBuilder != null) {
                        waypointMissionBuilder.getWaypointList().remove(markerList.size() - 1);
                        waypointMissionBuilder.waypointCount(waypointMissionBuilder.getWaypointList().size());
                        markerList.get(markerList.size() - 1).destroy();
                        markerList.remove(markerList.size() - 1);
                        //waypointList.remove(waypointList.size() - 1);
                        WaypointLinebuff.setLength(0);
                        WaypointNum = WaypointNum - 1;
                        WaypointLinebuff.append("航线名称：" + mWaypointLineBean.getLineName());
                        WaypointLinebuff.append("\n起飞点经度：" + mWaypointLineBean.getHomeLongitude());
                        WaypointLinebuff.append("\n起飞点纬度：" + mWaypointLineBean.getHomeLatitude());
                        WaypointLinebuff.append("\n起飞点海平面高度：" + mWaypointLineBean.getLocationAltitude());
                        WaypointLinebuff.append("\n当前航点数：" + WaypointNum);
                        tv_progress.setText(WaypointNum + "");
                        progressBar.setProgress(WaypointNum);
                        tv_lin_details.setText(WaypointLinebuff.toString());
                    } else {
                        setResultToToast("撤销失败");
                    }
                    // waypointList.get(waypointList.size()-1).removeAction()
                } else {
                    setResultToToast("撤销失败,无航点信息");
                }
            }
        });

        findViewById(R.id.add_waypoint).setOnClickListener(new View.OnClickListener() {//手动添加航点
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("目标点");
                //通过布局填充器获login_layout
                View view = getLayoutInflater().inflate(R.layout.dialog_input_gps, null);
                final LinearLayout ll_input = (LinearLayout) view.findViewById(R.id.ll_input);
                final EditText et_latitude = (EditText) view.findViewById(R.id.et_latitude);
                final EditText et_longitude = (EditText) view.findViewById(R.id.et_longitude);

                et_latitude.setText(drone_lat + "");
                et_longitude.setText(drone_log + "");


                final EditText et_height = (EditText) view.findViewById(R.id.et_height);
                final TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
                if (TextUtils.isEmpty(SPUtil.getInstance().getString("1", ""))) {
                    //获取两个文本编辑框（密码这里不做登陆实现，仅演示）
                    ll_input.setVisibility(View.VISIBLE);
                    tv_message.setVisibility(View.GONE);
                    isType = true;
                    m_Ok = "确认";
                    m_clear = "取消";
                } else {
                    isType = false;
                    ll_input.setVisibility(View.GONE);
                    tv_message.setVisibility(View.VISIBLE);


                    hashList = new Gson().fromJson(SPUtil.getInstance().getString("1", ""), new TypeToken<HashMap<String, Object>>() {
                    }.getType());
                    String z = "经度:" + hashList.get("longitude") + "\n纬度:" + hashList.get("latitude") + "\n高度:" + hashList.get("height");
                    tv_message.setText(z);
                    m_Ok = "飞向该点";
                    m_clear = "清除";
                }
                builder.setView(view);//设置login_layout为对话提示框
                builder.setCancelable(true);//设置为不可取消
                //设置正面按钮，并做事件处理
                builder.setPositiveButton(m_Ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isType) {
                            String sLatitude = et_latitude.getText().toString().trim();
                            String sLongitude = et_longitude.getText().toString().trim();
                            String sHeight = et_height.getText().toString().trim();
                            map.put("latitude", sLatitude);
                            map.put("longitude", sLongitude);
                            map.put("height", sHeight);
                            SPUtil.getInstance().putString("1", gson.toJson(map));
                        } else {//飞向该点
                            if (MApplication.getProductInstance() == null || !MApplication.getProductInstance().isConnected()) {
                                Toast.makeText(getApplicationContext(),
                                        "未连接,请先连接飞机在进行操作。",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (!markerList.isEmpty() && markerList.size() > 0) {
                                for (Marker marker : markerList) {
                                    marker.remove();
                                }
                                markerList.clear();
                            }
                            waypointList.clear();
                            istask = false;
                            isUpdateBackPoint = false;
                            markerList.add(aircraftMarker);
                            HashMap<String, Double> GpsLatLng = LatLngUtils.delta(aircraftMarker.getPosition().latitude, aircraftMarker.getPosition().longitude);
                            if (waypointMissionBuilder != null) {
                                waypointList.add(new Waypoint(GpsLatLng.get("lat"), GpsLatLng.get("lon"), Math.round(droneHeight)));
                                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
                            } else {
                                waypointMissionBuilder = new WaypointMission.Builder();
                                waypointList.add(new Waypoint(GpsLatLng.get("lat"), GpsLatLng.get("lon"), Math.round(droneHeight)));
                                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
                            }
                            mark = new MarkerOptions();
                            mark.position(LatLngUtils.Gps84ToAmap(
                                    new LatLng(Double.parseDouble(hashList.get("latitude") + ""),
                                            Double.parseDouble(hashList.get("longitude") + "")))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            markerList.add(aMap.addMarker(mark));


                            if (waypointMissionBuilder != null) {
                                waypointList.add(new Waypoint(Double.parseDouble(hashList.get("latitude") + ""), Double.parseDouble(hashList.get("longitude") + ""), Float.parseFloat(hashList.get("height") + "")));
                                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
                            } else {
                                waypointMissionBuilder = new WaypointMission.Builder();
                                waypointList.add(new Waypoint(Double.parseDouble(hashList.get("latitude") + ""), Double.parseDouble(hashList.get("longitude") + ""), Float.parseFloat(hashList.get("height") + "")));
                                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
                            }
                        }

                    }
                });
                //设置反面按钮，并做事件处理
                builder.setNegativeButton(m_clear, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!isType) {
                            SPUtil.getInstance().putString("1", "");
                        }
                    }
                });
                builder.show();//显示Dialog对话框
            }
        });

        //新建航线
        findViewById(R.id.route).setOnClickListener(new View.OnClickListener() {//新建航线
            @Override
            public void onClick(View v) {
                final Dialog sInputDialog = new Dialog(mContext, R.style.BottomDialog);
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View view = inflater.inflate(R.layout.input_waypoint_dialog, null);
                final EditText setInfo = (EditText) view.findViewById(R.id.set_info);
                final TextView settitle = (TextView) view.findViewById(R.id.set_dialog_title);
                final TextView cancel = (TextView) view.findViewById(R.id.cancel);
                final TextView ok = (TextView) view.findViewById(R.id.ok);
                settitle.setText("新建航线");
                sInputDialog.setCancelable(true);
                sInputDialog.setCanceledOnTouchOutside(true);
                sInputDialog.setTitle("");
                Window dialogWindow = sInputDialog.getWindow();
                dialogWindow.setGravity(Gravity.CENTER);
                WindowManager.LayoutParams windowParams = dialogWindow.getAttributes();
                windowParams.dimAmount = 0.0f;
                dialogWindow.setAttributes(windowParams);
                DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
                int mWindowWidth = (int) (displayMetrics.widthPixels * 0.5);
                sInputDialog.setContentView(view, new ViewGroup.LayoutParams(mWindowWidth,
                        ViewGroup.MarginLayoutParams.WRAP_CONTENT));
                sInputDialog.show();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sInputDialog.dismiss();
                    }
                });

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (setInfo.getText().length() > 0) {
                            WaypointLineBean waypointLine = new WaypointLineBean();
                            waypointLine.setLineName(
                                    setInfo.getText().toString());
                            waypointLine.setHomeLatitude(mHomeLatitude);
                            waypointLine.setHomeLongitude(mHomeLongitude);
                            waypointLine.setLocationAltitude(takeoffLocationAltitude);
                            GreendaoUtils.getInstance().saveWaypointLine(waypointLine);
                            sInputDialog.dismiss();
                        } else {
                            ToastUtils.showToast("请输入航线名称");
                        }
                    }
                });
            }
        });

        /**添加航点*/
        findViewById(R.id.add_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WaypointLineNum < 0) {
                    setResultToToast("请先选择航线..");
                    return;
                }
                if (null != waypointMissionBuilder && waypointMissionBuilder.getWaypointList().size() > 0) {
                    List<Waypoint> waypointList = waypointMissionBuilder.getWaypointList();
                    if (waypointList.size() > 0) {
                        GreendaoUtils.getInstance().delectWaypoint(
                                GreendaoUtils.getInstance().getWaypointlisr(WaypointLineNum + ""));
                    }

                        for (Waypoint way : waypointList) {
                            WaypointBean waypointBean = new WaypointBean();
                            List<WaypointBean.MissionHeadingMode> tlist = new ArrayList<>();
                            waypointBean.setLongitude(way.coordinate.getLongitude());
                            waypointBean.setUid(WaypointLineNum + "");
                            waypointBean.setLatitude(way.coordinate.getLatitude());
                            waypointBean.setDestinationHeight(way.altitude);
                            for (WaypointAction ways : way.waypointActions) {
                                WaypointBean.MissionHeadingMode mode = new WaypointBean.MissionHeadingMode();
                                mode.setMode(ways.actionType);
                                mode.setVar(ways.actionParam);
                                tlist.add(mode);
                            }
                            waypointBean.setHeadingModeString(GsonUtil.toJson(tlist));
                            GreendaoUtils.getInstance().saveWaypoint(waypointBean);
                        }
                    List<WaypointLineBean> waylinlist = GreendaoUtils.getInstance().queryWaypointLine(WaypointLineNum + "");
                    if (waylinlist.size() > 0) {
                        WaypointLineBean waylin = waylinlist.get(0);
                        waylin.setNum(waypointMissionBuilder.getWaypointCount() + "");
                        GreendaoUtils.getInstance().updataWaypointLine(waylin);
                    }
                    setResultToToast("添加成功");

                }
            }
        });

        /**
         * 航线列表*/
        findViewById(R.id.routelist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<WaypointLineBean> tlist =
                        GreendaoUtils.getInstance().getWaypointLinelisr();
                LogUtil.v(TAG, tlist.toString());
                final Dialog sInputDialog = new Dialog(mContext, R.style.BottomDialog);
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View view = inflater.inflate(R.layout.dialog_waypoint, null);
                sInputDialog.setCancelable(true);
                sInputDialog.setCanceledOnTouchOutside(true);
                sInputDialog.setTitle("");
                Window dialogWindow = sInputDialog.getWindow();
                dialogWindow.setGravity(Gravity.CENTER);
                WindowManager.LayoutParams windowParams = dialogWindow.getAttributes();
                windowParams.dimAmount = 0.0f;
                dialogWindow.setAttributes(windowParams);
                DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
                int mWindowWidth = (int) (displayMetrics.widthPixels * 0.5);
                sInputDialog.setContentView(view, new ViewGroup.LayoutParams(mWindowWidth,
                        ViewGroup.MarginLayoutParams.WRAP_CONTENT));
                sInputDialog.show();
                final RecyclerView active_recyclerview = (RecyclerView) view.findViewById(R.id.active_recyclerview);

                final WayPointAdapter missionPointSetAdapter =
                        new WayPointAdapter(mContext, tlist);
                active_recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
                active_recyclerview.setAdapter(missionPointSetAdapter);

                missionPointSetAdapter.setmOnItemDeleteClickListener(new WayPointAdapter.OnItemDeleteClickListener() {
                    @Override
                    public void onItemDeleteClick(int position, List<WaypointLineBean> list) {
                        LogUtil.v(TAG, "onItemDeleteClick:position=" + position + ";list:" + list.get(position).toString());
                        sInputDialog.dismiss();
                        GreendaoUtils.getInstance().delectWaypoint(
                                GreendaoUtils.getInstance().getWaypointlisr(list.get((int) position).getId() + "")
                        );
                        GreendaoUtils.getInstance().delectWaypointLine(
                                GreendaoUtils.getInstance().queryWaypointLine(list.get((int) position).getId() + "").get(0)
                        );
                        /*tlist.remove(position);
                        missionPointSetAdapter.setWaypointActions(tlist);
                        missionPointSetAdapter.notifyItemRemoved(position);*/
                    }
                });

                missionPointSetAdapter.setmDownLoadClickListener(new WayPointAdapter.OnDownLoadClickListener() {
                    @Override
                    public void onDownLoadClick(long position, List<WaypointLineBean> list) {
                        sInputDialog.dismiss();
                        ArrayList<MediaFile> thumMediaBenas = SaveWaypointMediaFile.putWaypointFiles(list.get((int) position).getLineName());
                        if (null != thumMediaBenas && thumMediaBenas.size() > 0) {
                            mDownLoadHelper = new DownLoadDeleteHelper(FPV2Activity.this,
                                    list.get((int) position),
                                    thumMediaBenas,
                                    GreendaoUtils.getInstance().getWaypointlisr(list.get((int) position).getId() + ""),
                                    new DownLoadDeleteHelper.DownLoadListener() {
                                        @Override
                                        public void onNotifyRefreshUIChange() {


                                        }
                                    });
                            mDownLoadHelper.downLoadMediaFiles();
                        } else {
                            setResultToToast("无数据");
                        }
                    }
                });

                missionPointSetAdapter.setOnItemClickListener(new WayPointAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(int position, List<WaypointLineBean> waypointActions) {
                        sInputDialog.dismiss();
                        mWaypointLineBean = waypointActions.get(position);
                        WaypointLineNum = mWaypointLineBean.getId();
                        List<WaypointBean> zlist =
                                GreendaoUtils.getInstance().getWaypointlisr(WaypointLineNum + "");
                        LogUtil.v(TAG, zlist.toString());
                        WaypointLinebuff.setLength(0);
                        if (!TextUtils.isEmpty(mWaypointLineBean.getNum())) {
                            WaypointNum = Integer.parseInt(mWaypointLineBean.getNum());
                        } else {
                            WaypointNum = 0;
                        }
                        progressBar.setProgress(WaypointNum);
                        tv_progress.setText(WaypointNum + "");
                        WaypointLinebuff.append("航线名称：" + mWaypointLineBean.getLineName());
                        WaypointLinebuff.append("\n起飞点经度：" + mWaypointLineBean.getHomeLongitude());
                        WaypointLinebuff.append("\n起飞点纬度：" + mWaypointLineBean.getHomeLatitude());
                        WaypointLinebuff.append("\n起飞点海平面高度：" + mWaypointLineBean.getLocationAltitude());
                        WaypointLinebuff.append("\n当前航点数：" + WaypointNum);
                        tv_lin_details.setText(WaypointLinebuff.toString());
                        if (zlist.size() > 0) {
                            if (!markerList.isEmpty()) {
                                for (Marker marker : markerList) {
                                    marker.remove();
                                }
                                markerList.clear();
                            }

                            waypointList.clear();
                            istask = false;
                            isUpdateBackPoint = false;
                            for (WaypointBean way : zlist) {
                                markerList.add(getMarker(LatLngUtils.Gps84ToAmap(new LatLng(way.getLatitude(), way.getLongitude())), way));
                                //markerList.add(getMarker(new LatLng(way.getLatitude(), way.getLongitude())));
                            }

                        } else {
                            setResultToToast("航线无航点");
                        }
                    }
                });
            }
        });

        findViewById(R.id.add_waypoints).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * public double AttitudeYaw;//机头角度
                 *     public float getPitch;//云台俯仰角度
                 *     public double drone_lat;// 飞机纬度
                 *     public double drone_log;// 飞机经度
                 *     public float droneHeight;// 飞机相对高度/飞机高度
                 * */
                if (WaypointLineNum > 0) {
                    BaseProduct mProduct = MApplication.getProductInstance();
                    if (mProduct == null || !mProduct.isConnected()) {
                        Toast.makeText(getApplicationContext(),
                                "未连接",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (MApplication.getAircraftInstance() == null) {
                        return;
                    }

                    if (SatelliteCount <= 10) {
                        setResultToToast("请注意卫星数量少于10颗");
                    }
                    if (waypointList.size() > 0) {
                        if (waypointList.size() > 0) {
                            double diatas = AMapUtils.calculateLineDistance(
                                    LatLngUtils.Gps84ToAmap(new LatLng(drone_lat, drone_log)),
                                    LatLngUtils.Gps84ToAmap(new LatLng(waypointList.get(waypointList.size() - 1).coordinate.getLatitude(),
                                            waypointList.get(waypointList.size() - 1).coordinate.getLongitude())));
                            float a = droneHeight - waypointList.get(waypointList.size() - 1).altitude;
                            if (diatas > 0.5 || Math.abs(a) > 0.5) {
                            } else {
                                setResultToToast("航点之间的距离不能少于0.5m");
                                return;
                            }
                        }
                    }
                    WaypointBean waypointBean = new WaypointBean();
                    waypointBean.setUid(WaypointLineNum + "");
                    waypointBean.setLatitude(drone_lat);
                    waypointBean.setLongitude(drone_log);
                    waypointBean.setDestinationHeight(droneHeight);
                    waypointBean.setLocationAltitude(takeoffLocationAltitude + droneHeight);
                    List<WaypointBean.MissionHeadingMode> tlist = new ArrayList<>();
                    WaypointBean.MissionHeadingMode mode = new WaypointBean.MissionHeadingMode();
                    mode.setMode(WaypointActionType.STAY);
                    mode.setVar(1000);
                    tlist.add(mode);

                    mode = new WaypointBean.MissionHeadingMode();
                    mode.setMode(WaypointActionType.ROTATE_AIRCRAFT);
                    mode.setVar((int) AttitudeYaw);
                    tlist.add(mode);

                    mode = new WaypointBean.MissionHeadingMode();
                    mode.setMode(WaypointActionType.GIMBAL_PITCH);
                    mode.setVar((int) getPitch);
                    tlist.add(mode);

                    mode = new WaypointBean.MissionHeadingMode();
                    mode.setMode(WaypointActionType.STAY);
                    mode.setVar(1000);
                    tlist.add(mode);

                    mode = new WaypointBean.MissionHeadingMode();
                    mode.setMode(WaypointActionType.START_TAKE_PHOTO);
                    mode.setVar(1000);
                    tlist.add(mode);

                    mode = new WaypointBean.MissionHeadingMode();
                    mode.setMode(WaypointActionType.STAY);
                    mode.setVar(1000);
                    tlist.add(mode);

                    waypointBean.setHeadingModeString(GsonUtil.toJson(tlist));
                    LogUtil.v(TAG, waypointBean.toString());

                    //航线描述
                    WaypointLinebuff.setLength(0);
                    WaypointNum = WaypointNum + 1;
                    WaypointLinebuff.append("航线名称：" + mWaypointLineBean.getLineName());
                    WaypointLinebuff.append("\n起飞点经度：" + mWaypointLineBean.getHomeLongitude());
                    WaypointLinebuff.append("\n起飞点纬度：" + mWaypointLineBean.getHomeLatitude());
                    WaypointLinebuff.append("\n起飞点海平面高度：" + mWaypointLineBean.getLocationAltitude());
                    WaypointLinebuff.append("\n当前航点数：" + WaypointNum);
                    tv_progress.setText(WaypointNum + "");
                    progressBar.setProgress(WaypointNum);
                    tv_lin_details.setText(WaypointLinebuff.toString());


                    markerList.add(getMarker(aircraftLatLng, waypointBean));
                    setResultToToast("添加第" + (markerList.size()) + "个航点成功");
                } else {
                    setResultToToast("请先选择航线");
                }
            }
        });

    }


    long WaypointLineNum = -1;
    Map<String, Object> hashList;

    public void getDialogText(final String test) {
        String value1 = SPUtil.getInstance().getString(test, "");
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);

        //通过布局填充器获login_layout
        View view = getLayoutInflater().inflate(R.layout.dialog_input_gps, null);
        final LinearLayout ll_input = (LinearLayout) view.findViewById(R.id.ll_input);
        final TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
        ll_input.setVisibility(View.GONE);
        if (TextUtils.isEmpty(value1)) {
            builder.setTitle("当前飞机位置");
            tv_message.setVisibility(View.VISIBLE);
            String z = "经度:" + drone_log + "\n纬度:" + drone_lat + "\n高度:" + Math.round(droneHeight);
            tv_message.setText(z);
            isType = true;
            m_Ok = "保存";
            m_clear = "取消";
        } else {
            builder.setTitle("保存点坐标");
            isType = false;
            tv_message.setVisibility(View.VISIBLE);
            hashList = new Gson().fromJson(value1, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            String z = "经度:" + hashList.get("longitude") + "\n纬度:" + hashList.get("latitude") + "\n高度:" + hashList.get("height");
            tv_message.setText(z);
            m_Ok = "飞向该点";
            m_clear = "清除";
        }
        builder.setView(view);//设置login_layout为对话提示框
        builder.setCancelable(true);//设置为不可取消
        //设置正面按钮，并做事件处理
        builder.setPositiveButton(m_Ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isType) {
                    map.clear();
                    map.put("latitude", drone_lat + "");
                    map.put("longitude", drone_log + "");
                    map.put("height", Math.round(droneHeight) + "");
                    SPUtil.getInstance().putString(test, gson.toJson(map));
                } else {//飞向该点
                    if (MApplication.getProductInstance() == null || !MApplication.getProductInstance().isConnected()) {
                        Toast.makeText(getApplicationContext(),
                                "未连接,请先连接飞机在进行操作。",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!markerList.isEmpty() && markerList.size() > 0) {
                        for (Marker marker : markerList) {
                            marker.remove();
                        }
                        markerList.clear();
                    }
                    waypointList.clear();
                    istask = false;
                    isUpdateBackPoint = false;
                    markerList.add(aircraftMarker);
                    HashMap<String, Double> GpsLatLng = LatLngUtils.delta(aircraftMarker.getPosition().latitude, aircraftMarker.getPosition().longitude);
                    if (waypointMissionBuilder == null) {
                        waypointMissionBuilder = new WaypointMission.Builder();

                    }
                    waypointList.add(new Waypoint(GpsLatLng.get("lat"), GpsLatLng.get("lon"), Math.round(droneHeight)));
                    waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
                    aircraftLatLng = LatLngUtils.Gps84ToAmap(
                            new LatLng(Double.parseDouble(hashList.get("latitude") + ""),
                                    Double.parseDouble(hashList.get("longitude") + "")));
                    mark = new MarkerOptions();
                    mark.position(aircraftLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    markerList.add(aMap.addMarker(mark));
                    if (waypointMissionBuilder != null) {
                        waypointList.add(new Waypoint(Double.parseDouble(hashList.get("latitude") + ""), Double.parseDouble(hashList.get("longitude") + ""),
                                Float.parseFloat(hashList.get("height") + "")));
                        waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
                    } else {
                        waypointMissionBuilder = new WaypointMission.Builder();
                        waypointList.add(new Waypoint(Double.parseDouble(hashList.get("latitude") + ""), Double.parseDouble(hashList.get("longitude") + ""),
                                Float.parseFloat(hashList.get("height") + "")));
                        waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
                    }
                }
            }
        });
        //设置反面按钮，并做事件处理
        builder.setNegativeButton(m_clear, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!isType) {
                    SPUtil.getInstance().putString(test, "");
                }
            }
        });
        builder.show();//显示Dialog对话框
    }


    //Add Listener for WaypointMissionOperator
    private void addListener() {
        if (getWaypointMissionOperator() != null) {
            getWaypointMissionOperator().addListener(eventNotificationListener);
        }
    }

    private void removeListener() {
        if (getWaypointMissionOperator() != null) {
            getWaypointMissionOperator().removeListener(eventNotificationListener);
        }
    }

    private WaypointMissionOperatorListener eventNotificationListener = new WaypointMissionOperatorListener() {
        @Override
        public void onDownloadUpdate(WaypointMissionDownloadEvent downloadEvent) {//下载事件发生时调用。
            LogUtil.v(TAG, "onDownloadUpdate：" + downloadEvent.getProgress());
        }

        //上传航点
        @Override
        public void onUploadUpdate(final WaypointMissionUploadEvent uploadEvent) {//上传
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (null != uploadEvent.getProgress()) {
                        int upindex = uploadEvent.getProgress().uploadedWaypointIndex + 1;
                        LogUtil.v(TAG, "uploadedWaypointIndex=" + upindex + "");
                        tv_progress.setText("航点上传中：" + upindex + "");
                        progressBar.setProgress(upindex);
                        if (uploadEvent.getProgress().isSummaryUploaded
                                && uploadEvent.getProgress().uploadedWaypointIndex
                                == (waypointMissionBuilder.getWaypointList().size() - 1)) {
                            startWaypointMission();
                            tv_progress.setText("执行中：");
                        }
                        //LogUtil.v(TAG, "onUploadUpdate：" + uploadEvent.getProgress().totalWaypointCount);
                    }
                }
            });

        }

        //执行进度 正在执行任务
        @Override
        public void onExecutionUpdate(final WaypointMissionExecutionEvent onExecutionUpdate) {//在航点任务操作员执行更新时调用。
            LogUtil.v(TAG, "onExecutionUpdate：" + onExecutionUpdate.getProgress());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_progress.setText("航点执行中：" + (onExecutionUpdate.getProgress().targetWaypointIndex + 1) + "");
                    progressBar.setProgress(onExecutionUpdate.getProgress().targetWaypointIndex + 1);

                }
            });
        }

        @Override
        public void onExecutionStart() {//在航点任务开始时调用。
            LogUtil.v(TAG, "onExecutionStart");
        }

        @Override
        public void onExecutionFinish(@Nullable final DJIError error) {//在航点任务结束时调用。
            if (error == null && isdownLoadMediaFile) {
                isdownLoadMediaFile = false;
                waypointList.clear();
                waypointMissionBuilder = null;
                waypointList.clear();
                setResultToToast("Execution finished: " + (error == null ? "Success!" : error.getDescription()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Execution finished: " + (error == null ? "Success!" : error.getDescription()), Toast.LENGTH_SHORT).show();
                        LogUtil.v(TAG, "Execution finished: " + (error == null ? "Success!" : error.getDescription()));
                        if (mMediaFiles == null) {
                            return;
                        }
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        LogUtil.v(TAG, "mMediaFiles.size():" + mMediaFiles.size());
                        List<WaypointLineBean> waylinlist = GreendaoUtils.getInstance().queryWaypointLine(WaypointLineNum + "");
                        WaypointLineBean wayBean = waylinlist.get(0);
                        wayBean.setFlyNum(wayBean.getFlyNum() + 1);
                        GreendaoUtils.getInstance().updataWaypointLine(wayBean);
                        SaveWaypointMediaFile.saveWaypointFiles(mMediaFiles, waylinlist.get(0).getLineName());


                        /*mDownLoadHelper = new DownLoadDeleteHelper(FPV2Activity.this, mMediaFiles, waylinlist.get(0), GreendaoUtils.getInstance().getWaypointlisr(waylinlist.get(0).getId() + ""),
                                new DownLoadDeleteHelper.DownLoadListener() {
                                    @Override
                                    public void onNotifyRefreshUIChange() {
                                        isdownLoadMediaFile = false;
                                        mMediaFiles.clear();
                                    }
                                });
                        mDownLoadHelper.downLoadMediaFiles();*/

                    }
                });
            }
        }
    };
    public DownLoadDeleteHelper mDownLoadHelper;
    private boolean isdownLoadMediaFile = false;

    private void onViewClick(View view) {
        if (view == fpvWidget && !isMapMini) {
            resizeFPVWidget(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, margin, 0);
            ResizeAnimation mapViewAnimation = new ResizeAnimation(fl2_container_little, deviceWidth, deviceHeight, width, height, margin);
            fl2_container_little.startAnimation(mapViewAnimation);
            isMapMini = true;
            tv_test.setTextColor(getResources().getColor(R.color.white));
            tv_gimbal.setTextColor(getResources().getColor(R.color.white));
        } else if (view == fl2_container_little && isMapMini) {
            resizeFPVWidget(width, height, margin, 3);
            ResizeAnimation mapViewAnimation = new ResizeAnimation(fl2_container_little, width, height, deviceWidth, deviceHeight, 0);
            fl2_container_little.startAnimation(mapViewAnimation);
            isMapMini = false;
            tv_test.setTextColor(getResources().getColor(R.color.black));
            tv_gimbal.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private void initMapView() {
        //地图
        viewInflateMap = LayoutInflater.from(this).inflate(R.layout.view_map, null);
        mMapView = (MapView) viewInflateMap.findViewById(R.id.mapView);
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
        try {
            getFlightControllerState();
        } catch (Exception e) {
            Log.v(TAG, e.toString());
        }
        parentView.removeView(fpvWidget);
        fpvWidget.setLayoutParams(fpvParams);
        parentView.addView(fpvWidget, fpvInsertPosition);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.hideInfoWindow();
        switch (marker.getTitle()) {
            case "WAY_POINT":
                missionPointSet.showWayPointActionDialog(waypointMissionBuilder.getWaypointList(), markerList, marker);
                break;
        }
        return true;
    }


    @Override
    public void onMapClick(LatLng lng) {
        if (isMapMini) {
            onViewClick(fl2_container_little);
        } else if (mWaypointLineBean == null) {
            setResultToToast("请选择航线");
        } else if (unlock_map) {
            WaypointLinebuff.setLength(0);
            WaypointNum = WaypointNum + 1;
            WaypointLinebuff.append("航线名称：" + mWaypointLineBean.getLineName());
            WaypointLinebuff.append("\n起飞点经度：" + mWaypointLineBean.getHomeLongitude());
            WaypointLinebuff.append("\n起飞点纬度：" + mWaypointLineBean.getHomeLatitude());
            WaypointLinebuff.append("\n起飞点海平面高度：" + mWaypointLineBean.getLocationAltitude());
            WaypointLinebuff.append("\n当前航点数：" + WaypointNum);
            tv_progress.setText(WaypointNum + "");
            progressBar.setProgress(WaypointNum);
            tv_lin_details.setText(WaypointLinebuff.toString());
            markerList.add(getMarker(lng));
        } else if (!unlock_map) {
            setResultToToast("请先解锁地图");
        }
    }

    MarkerOptions mark;


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
            setDuration(10);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mMapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        mMapView.onLowMemory();
        super.onLowMemory();
    }

    public void getFlightControllerState() {
        BaseProduct mProduct = MApplication.getProductInstance();
        if (mProduct == null || !mProduct.isConnected()) {
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
        addListener();
        MApplication.getAircraftInstance().getGimbal().setStateCallback(new GimbalState.Callback() {
            @Override
            public void onUpdate(@NonNull final GimbalState state) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getPitch = state.getAttitudeInDegrees().getPitch();
                        tv_gimbal.setText("云台俯仰角度:" + state.getAttitudeInDegrees().getPitch());
                        //state.getAttitudeInDegrees().
                    }
                });
            }
        });
        MApplication.getProductInstance().getGimbal()
                .setPitchRangeExtensionEnabled(true, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (null != djiError) {
                            LogUtil.v(TAG, djiError.getDescription());
                        } else {
                            LogUtil.v(TAG, "setPitchRangeExtensionEnabled:true设置成功");
                        }
                    }
                });

        MApplication.getProductInstance().getCamera().setMediaFileCallback(new MediaFile.Callback() {
            @Override
            public void onNewFile(@NonNull MediaFile file) {
                mMediaFiles.add(file);

            }
        });

        if (DJIModuleVerificationUtil.isCameraModuleAvailable() && MApplication.getProductInstance().getCamera().isMediaDownloadModeSupported()) {
            mediaManager = MApplication.getProductInstance().getCamera().getMediaManager();

        }
    }

    public double mHomeLatitude;// 飞机返航点纬度
    public double mHomeLongitude;// 飞机返航点经度
    public float takeoffLocationAltitude;// 飞机返航点海平面高度
    public double DestinationLatitude;//终点纬度
    public double DestinationLongitude;// 终点经度
    public float DestinationHeight;// 终点高度
    public double AttitudeYaw;//机头角度
    public float getPitch;//云台俯仰角度
    public double drone_lat;// 飞机纬度
    public double drone_log;// 飞机经度
    public float droneHeight;// 飞机相对高度/飞机高度
    public int SatelliteCount = 0;
    public List<MediaFile> mMediaFiles = new ArrayList<>();
    StringBuffer mStringBuffer = new StringBuffer();

    private void getUpdataFpvState(FlightControllerState state) {
        mStringBuffer.setLength(0);
        mHomeLatitude = state.getHomeLocation().getLatitude();
        mHomeLongitude = state.getHomeLocation().getLongitude();
        drone_lat = state.getAircraftLocation().getLatitude();
        drone_log = state.getAircraftLocation().getLongitude();
        droneHeight = state.getAircraftLocation().getAltitude();
        SatelliteCount = state.getSatelliteCount();
        yaw = state.getAttitude().yaw;
        takeoffLocationAltitude = state.getTakeoffLocationAltitude();
        //double lat1, double lng1, double lat2,double lng2
        mStringBuffer.append("目标点坐标：" + "\n经度:" + DestinationLongitude + "\n纬度:  " + DestinationLatitude + "\n高度:" + DestinationHeight);
        mStringBuffer.append("\n当前坐标：" + "\n经度:" + drone_log + "\n纬度:  " + drone_lat + "\n高度:" + droneHeight+"\n海平面高度:"+takeoffLocationAltitude);
        // LogUtil.v(TAG, mStringBuffer.toString());
        String distance;
        if (DestinationLatitude > 0 && DestinationLongitude > 0) {
            distance = String.format("%.2f", LatLngUtils.getDistance(DestinationLatitude, DestinationLongitude, drone_lat, drone_log));
        } else {
            distance = "0";
        }
        //mStringBuffer.append("\n海拔高度：" + takeoffLocationAltitude);
        // mStringBuffer.append("\n距离：" + distance + "米");
        mStringBuffer.append("\n机头角度：" + state.getAttitude().yaw);
        AttitudeYaw = state.getAttitude().yaw;
        //mStringBuffer.append("\n机头俯仰角度：" + state.getAttitude().pitch);
        //mStringBuffer.append("\n机头滚动角度：" + state.getAttitude().roll);
        tv_test.setText(mStringBuffer);
        if (mHomeLatitude > 0 && mHomeLongitude > 0 && !isUpdateBackPoint) {
            setHomeImage(mHomeLatitude, mHomeLongitude);
            isUpdateBackPoint = true;
        }
        if (drone_lat > 0 && drone_log > 0) {
            showAircraftImage(drone_lat, drone_log, yaw);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public boolean isUpdateBackPoint = false;// 是否更新返航点坐标
    Marker mHomeMarker, aircraftMarker;

    private LatLng homeLatLng;//家转换后的经纬度
    private LatLng aircraftLatLng;//飞机转换后的经纬度
    public double yaw = 0;// 飞机方位角

    private void setHomeImage(double lat, double lng) {
        try {
            if (mHomeMarker != null) {
                mHomeMarker.destroy();
            }
            LogUtil.v(TAG, "设置家的图标");
            homeLatLng = LatLngUtils.Gps84ToAmap(new LatLng(lat, lng));
            if (aMap != null) {
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(R.drawable.home);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(DensityUtil.dip2px(this, 26), DensityUtil.dip2px(this, 26)));
                BitmapDescriptor markerIcon = BitmapDescriptorFactory
                        .fromView(imageView);
                mHomeMarker = aMap.addMarker(new MarkerOptions().position(homeLatLng).
                        icon(markerIcon));
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(homeLatLng));
                mHomeMarker.setAnchor(0.5f, 0.5f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示飞机图片
     */
    private void showAircraftImage(double lat, double lng, double yaw) {
        try {
            if (aircraftMarker != null) {
                aircraftMarker.destroy();
            }
            //LogUtil.v(TAG, "设置飞机图标");
            aircraftLatLng = LatLngUtils.Gps84ToAmap(new LatLng(lat, lng));
            if (aMap != null) {
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(R.drawable.fly_yaw_min);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(DensityUtil.dip2px(this, 26), DensityUtil.dip2px(this, 26)));
                BitmapDescriptor markerIcon = BitmapDescriptorFactory
                        .fromView(imageView);
                //飞机位置
                aircraftMarker = aMap.addMarker(new MarkerOptions().position(aircraftLatLng).
                        icon(markerIcon));
                // 设置当前marker的锚点 锚点是定位图标接触地图平面的点。
                // 图标的左顶点为（0,0）点，右底点为（1,1）点。
                // 默认情况下，锚点为（0.5,1.0）。
                aircraftMarker.setAnchor(0.5f, 0.4f);
                aircraftMarker.setRotateAngle((float) RouteUtlis.parseYawToAntiClockwise360(yaw));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private float altitude = 100.0f;
    private float mSpeed = 10.0f;
    private List<Waypoint> waypointList = new ArrayList<>();
    public static WaypointMission.Builder waypointMissionBuilder;
    private WaypointMissionOperator instance;//用于设置任务
    private WaypointMissionFinishedAction mFinishedAction = WaypointMissionFinishedAction.NO_ACTION;
    private WaypointMissionHeadingMode mHeadingMode = WaypointMissionHeadingMode.AUTO;


    private void showSettingDialog(double diatas, float diatal) {


        LinearLayout wayPointSettings = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_waypointsetting, null);
        RadioGroup speed_RG = wayPointSettings.findViewById(R.id.speed);
        RadioGroup actionAfterFinished_RG = wayPointSettings.findViewById(R.id.actionAfterFinished);
        RadioGroup heading_RG = wayPointSettings.findViewById(R.id.heading);
        TextView tv_test = wayPointSettings.findViewById(R.id.tv_test);
        tv_test.setText("两次起飞点的水平位置相差：" + diatas + "米\n海平面高度相差：" + diatal + "米");
        final CheckBox rb_absolute = wayPointSettings.findViewById(R.id.rb_absolute);
        /*final CheckBox cb_shoot = wayPointSettings.findViewById(R.id.cb_shoot);
        final CheckBox cb_rotate = wayPointSettings.findViewById(R.id.cb_rotate);
        final CheckBox cb_gimbal = wayPointSettings.findViewById(R.id.cb_gimbal);
        final CheckBox cb_rotate_mode = wayPointSettings.findViewById(R.id.cb_rotate_mode);

        final EditText et_gimbal = wayPointSettings.findViewById(R.id.et_gimbal);
        final EditText et_rotate = wayPointSettings.findViewById(R.id.et_rotate);*/

        mHeadingMode = WaypointMissionHeadingMode.AUTO;
        speed_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.lowSpeed) {
                    mSpeed = 3.0f;
                } else if (checkedId == R.id.MidSpeed) {
                    mSpeed = 5.0f;
                } else if (checkedId == R.id.HighSpeed) {
                    mSpeed = 10.0f;
                }
            }

        });

        actionAfterFinished_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                LogUtil.v(TAG, "Select finish action");
                if (checkedId == R.id.finishNone) {
                    mFinishedAction = WaypointMissionFinishedAction.NO_ACTION;
                } else if (checkedId == R.id.finishGoHome) {
                    mFinishedAction = WaypointMissionFinishedAction.GO_HOME;
                } else if (checkedId == R.id.finishAutoLanding) {
                    mFinishedAction = WaypointMissionFinishedAction.AUTO_LAND;
                } else if (checkedId == R.id.finishToFirst) {
                    mFinishedAction = WaypointMissionFinishedAction.GO_FIRST_WAYPOINT;
                }
            }
        });

        heading_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                LogUtil.v(TAG, "Select heading");
                if (checkedId == R.id.headingNext) {
                    mHeadingMode = WaypointMissionHeadingMode.AUTO;
                } else if (checkedId == R.id.headingInitDirec) {
                    mHeadingMode = WaypointMissionHeadingMode.USING_INITIAL_DIRECTION;
                } else if (checkedId == R.id.headingRC) {
                    mHeadingMode = WaypointMissionHeadingMode.CONTROL_BY_REMOTE_CONTROLLER;
                } else if (checkedId == R.id.headingWP) {
                    mHeadingMode = WaypointMissionHeadingMode.USING_WAYPOINT_HEADING;
                }
            }
        });


        new AlertDialog.Builder(this)
                .setTitle("")
                .setView(wayPointSettings)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (rb_absolute.isChecked()) {
                            configWayPointMission(true);
                        } else {
                            configWayPointMission(false);
                        }
                    }

                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }


    private void configWayPointMission(boolean rb_absolute) {
        if (waypointMissionBuilder == null) {
            waypointMissionBuilder = new WaypointMission.Builder().finishedAction(mFinishedAction)
                    .headingMode(mHeadingMode)
                    .autoFlightSpeed(mSpeed)
                    .maxFlightSpeed(mSpeed)
                    .flightPathMode(WaypointMissionFlightPathMode.NORMAL);
        } else {
            waypointMissionBuilder.finishedAction(mFinishedAction)
                    .headingMode(mHeadingMode)
                    .autoFlightSpeed(mSpeed)
                    .maxFlightSpeed(mSpeed)
                    .flightPathMode(WaypointMissionFlightPathMode.NORMAL);
        }

        LogUtil.v(TAG, "航点个数：" + waypointMissionBuilder.getWaypointList().size());
       /* List<WaypointBean> zlist =
                GreendaoUtils.getInstance().getWaypointlisr(WaypointLineNum + "");*/
        if (waypointMissionBuilder.getWaypointList().size() > 0) {
            for (int i = 0; i < waypointMissionBuilder.getWaypointList().size(); i++) {
                waypointMissionBuilder.getWaypointList().get(i).heading = 90;
                //takeoffLocationAltitude  当前海拔高度
                if(rb_absolute){
                    float alt = mWaypointLineBean.getLocationAltitude()
                            -takeoffLocationAltitude
                            + waypointMissionBuilder.getWaypointList().get(i).altitude;
                    LogUtil.v(TAG,"当前航点当前高度："+waypointMissionBuilder.getWaypointList().get(i).altitude);
                    LogUtil.v(TAG,"当前航线起飞点海拔："+mWaypointLineBean.getLocationAltitude());
                    LogUtil.v(TAG,"当前起飞点海拔高度："+takeoffLocationAltitude);
                    LogUtil.v(TAG, "计算的绝对高度:绝对高度" + alt);
                    waypointMissionBuilder.getWaypointList().get(i).altitude = alt;
                }else{
                    LogUtil.v(TAG, "alt:相对高度" + waypointMissionBuilder.getWaypointList().get(i).altitude);
                }

            }
            DestinationLatitude = waypointMissionBuilder.getWaypointList().get(waypointMissionBuilder.getWaypointList().size() - 1).coordinate.getLatitude();//终点纬度
            DestinationLongitude = waypointMissionBuilder.getWaypointList().get(waypointMissionBuilder.getWaypointList().size() - 1).coordinate.getLongitude();// 终点经度
            DestinationHeight = waypointMissionBuilder.getWaypointList().get(waypointMissionBuilder.getWaypointList().size() - 1).altitude;// 终点高度
            setResultToToast("设置航点状态成功");
        }
        /*for (int i = 0; i < waypointMissionBuilder.getWaypointList().size(); i++) {
            LogUtil.v(TAG, "-----------------------");
            LogUtil.v(TAG, "Latitude:" + waypointMissionBuilder.getWaypointList().get(i).coordinate.getLatitude() + ";Longitude()"
                    + waypointMissionBuilder.getWaypointList().get(i).coordinate.getLongitude() + ";高度："
                    + waypointMissionBuilder.getWaypointList().get(i).altitude + ";"
            );

            for (WaypointAction Action : waypointMissionBuilder.getWaypointList().get(i).waypointActions) {
                LogUtil.v(TAG, "actionParam:" + Action.actionParam + ";actionType:"
                        + Action.actionType + ";");

            }
        }*/
        tv_progress_all.setText(waypointMissionBuilder.getWaypointList().size() + "");
        DJIError error = getWaypointMissionOperator().loadMission(waypointMissionBuilder.build());
        if (error == null) {
            setResultToToast("加载路径成功");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    uploadWayPointMission();
                }
            });
        } else {
            setResultToToast("加载路径失败 " + error.getDescription());
        }
    }

    public WaypointMissionOperator getWaypointMissionOperator() {
        if (instance == null) {
            instance = DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator();
        }
        return instance;
    }

    private void setResultToToast(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
                LogUtil.v(TAG, string);
            }
        });
    }

    private boolean istask = false;

    private void uploadWayPointMission() {
        getWaypointMissionOperator().uploadMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                if (error == null) {
                    istask = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setMax(markerList.size());
                            tv_progress.setText("1");
                            tv_progress_all.setText(markerList.size() + "");
                        }
                    });
                    setResultToToast("任务上传成功!");
                } else {
                    setResultToToast("任务上传失败, error: " + error.getDescription() + " 重试中...");
                    getWaypointMissionOperator().retryUploadMission(null);
                }
            }
        });
    }

    private void startWaypointMission() {
        getWaypointMissionOperator().startMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                isdownLoadMediaFile = true;
                mMediaFiles.clear();
                setResultToToast("任务开始: " + (error == null ? "成功" : error.getDescription()));
            }
        });
    }

    private void stopWaypointMission() {
        getWaypointMissionOperator().stopMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                setResultToToast("任务停止: " + (error == null ? "成功" : error.getDescription()));
            }
        });
    }


    protected View getInputLatlngView(String pm_val) {
        View view = getLayoutInflater().inflate(R.layout.way_point_input_latlng_marker, null);
        TextView markerIndexNumber = (TextView) view.findViewById(R.id.marker_index_number);
        markerIndexNumber.setText(pm_val);
        return view;
    }

    //航点Marker
    private Marker getMarker(LatLng latLng) {
        View view = getInputLatlngView(String.valueOf(markerList.size() + 1));
        BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromView(view);
        final MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(markerIcon).zIndex(2);
        Marker marker = aMap.addMarker(markerOptions);
        marker.setAnchor(0.5f, 0.71100f);
        marker.setDraggable(false);
        marker.setSnippet(String.valueOf(markerList.size() + 1));
        marker.setTitle("WAY_POINT");
        HashMap<String, Double> GpsLatLng = LatLngUtils.delta(latLng.latitude, latLng.longitude);
        if (waypointMissionBuilder == null) {
            waypointMissionBuilder = new WaypointMission.Builder();
        }
        waypointList.add(new Waypoint(GpsLatLng.get("lat"), GpsLatLng.get("lon"), Math.round(droneHeight)));
        waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
        return marker;
    }

    //航点Marker
    private Marker getMarker(LatLng latLng, WaypointBean way) {
        View view = getInputLatlngView(String.valueOf(markerList.size() + 1));
        BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromView(view);
        final MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(markerIcon).zIndex(2);
        Marker marker = aMap.addMarker(markerOptions);
        marker.setAnchor(0.5f, 0.71100f);
        marker.setDraggable(true);
        marker.setSnippet(String.valueOf(markerList.size() + 1));
        marker.setTitle("WAY_POINT");
        if (waypointMissionBuilder == null) {
            waypointMissionBuilder = new WaypointMission.Builder();
        }
        waypointList.add(new Waypoint(way.getLatitude(), way.getLongitude(), Math.round(way.getDestinationHeight())));
        try {
            JSONArray jsonArray = new JSONArray(way.getHeadingModeString());
            for (int z = 0; z < jsonArray.length(); z++) {
                WaypointMode mode = GsonUtil.fromJson(jsonArray.opt(z).toString(), WaypointMode.class);
                waypointList.get(waypointList.size() - 1).addAction(new WaypointAction(mode.getMode(), mode.getVar()));
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
        return marker;
    }
}
