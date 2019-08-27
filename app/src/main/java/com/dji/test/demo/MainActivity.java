package com.dji.test.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.dji.test.demo.util.ViewClickHookUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.useraccount.UserAccountManager;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final String LAST_USED_BRIDGE_IP = "bridgeip";
    private AtomicBoolean isRegistrationInProgress = new AtomicBoolean(false);
    private static boolean isAppStarted = false;
    private StringBuffer strbuff = new StringBuffer();
    public static Handler mFpvHandler;


    private DJISDKManager.SDKManagerCallback registrationCallback = new DJISDKManager.SDKManagerCallback() {

        @Override
        public void onRegister(DJIError error) {
            isRegistrationInProgress.set(false);
            upUI(error.toString());

            if (error == DJISDKError.REGISTRATION_SUCCESS) {
                upUI("REGISTRATION_SUCCESS");
                //loginAccount();
                //DJILog.e("App registration", DJISDKError.REGISTRATION_SUCCESS.getDescription());
                DJISDKManager.getInstance().startConnectionToProduct();

               // checkDIJUserStatus(UserAccountManager.getInstance().getUserAccountState());
                Toast.makeText(getApplicationContext(), "SDK registration succeeded!", Toast.LENGTH_LONG).show();
            } else {
                upUI("未登录");
                Toast.makeText(getApplicationContext(),
                        "SDK registration failed, check network and retry!",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onProductDisconnect() {
            upUI("product disconnect!");
            /*Toast.makeText(getApplicationContext(),
                    "product disconnect!",
                    Toast.LENGTH_LONG).show();*/
        }

        @Override
        public void onProductConnect(BaseProduct product) {
            upUI("设备连接成功");
            /*Toast.makeText(getApplicationContext(),
                    "product connect!",
                    Toast.LENGTH_LONG).show();*/
        }

        @Override
        public void onComponentChange(BaseProduct.ComponentKey key,
                                      BaseComponent oldComponent,
                                      BaseComponent newComponent) {
            upUI(key.toString() + " changed");
           /* Toast.makeText(getApplicationContext(),
                    key.toString() + " changed",
                    Toast.LENGTH_LONG).show();*/

        }

        @Override
        public void onInitProcess(DJISDKInitEvent event, int totalProcess) {

        }
    };

    private void checkDIJUserStatus(UserAccountState checkDIJUserStatus) {

        switch (checkDIJUserStatus) {
            case NOT_LOGGED_IN:
                upUI("用户未登录。用户需要登录才能检索当前已解锁的用户，并解锁授权区域。");
                break;
            case NOT_AUTHORIZED:
                upUI("用户已登录但尚未获得授权解锁授权区域的权限。");
                break;
            case AUTHORIZED:
                upUI("用户已登录并已被授权解锁授权区域。");
                break;
            case TOKEN_OUT_OF_DATE:
                upUI("用户帐户的令牌已过期。");
                break;
            default:
                upUI("未知");
                break;
        }
        getDJIUserAccountName();
    }


    private void loginAccount() {

        UserAccountManager.getInstance().logIntoDJIUserAccount(this,
                new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
                    @Override
                    public void onSuccess(final UserAccountState userAccountState) {
                        /*Toast.makeText(getApplicationContext(),
                                "Login Success!",
                                Toast.LENGTH_LONG).show();*/
                        upUI("Login Success!");
                        checkDIJUserStatus(userAccountState);
                        //startActivity(new Intent(MainActivity.this, FPVActivity.class));
                    }

                    @Override
                    public void onFailure(DJIError error) {
                        upUI("Login Error!");
                        upUI("登录失败");
                        /*Toast.makeText(getApplicationContext(),
                                "Login Error!",
                                Toast.LENGTH_LONG).show();*/
                    }
                });
    }

    public static boolean isStarted() {
        return isAppStarted;
    }

    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO
    };
    private static final int REQUEST_PERMISSION_CODE = 12345;
    private List<String> missingPermission = new ArrayList<>();
    private EditText bridgeModeEditText;

    private TextView tv_login;
    private TextView tv_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置窗体为没有标题的模式
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_main);
        mFpvHandler = new Handler(getMainLooper());
        isAppStarted = true;
        strbuff = new StringBuffer();
        checkAndRequestPermissions();
        tv_message = findViewById(R.id.tv_message);
        tv_login = findViewById(R.id.tv_login);
        ViewClickHookUtils.HookViewClick(tv_login);
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAccount();
            }
        });
        findViewById(R.id.tv_login1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FPVActivity.class));
                finish();
            }
        });
        permissionForM();
        //init();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
    }

    @Override
    protected void onDestroy() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
        mLocationClient = null;
        isAppStarted = false;
        super.onDestroy();
    }

    /**
     * Checks if there is any missing permissions, and
     * requests runtime permission if needed.
     */
    private void checkAndRequestPermissions() {
        // Check for permissions
        for (String eachPermission : REQUIRED_PERMISSION_LIST) {
            if (ContextCompat.checkSelfPermission(this, eachPermission) != PackageManager.PERMISSION_GRANTED) {
                missingPermission.add(eachPermission);
            }
        }
        // Request for missing permissions
        if (missingPermission.isEmpty()) {
            startSDKRegistration();
        } else {
            ActivityCompat.requestPermissions(this,
                    missingPermission.toArray(new String[missingPermission.size()]),
                    REQUEST_PERMISSION_CODE);
        }
    }

    /**
     * Result of runtime permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check for granted permission and remove from missing list
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = grantResults.length - 1; i >= 0; i--) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    missingPermission.remove(permissions[i]);
                }
            }
        }
        // If there is enough permission, we will start the registration
        if (missingPermission.isEmpty()) {
            startSDKRegistration();
        } else {
            //Toast.makeText(getApplicationContext(), "Missing permissions! Will not register SDK to connect to aircraft.", Toast.LENGTH_LONG).show();
        }
    }

    private void startSDKRegistration() {
        if (isRegistrationInProgress.compareAndSet(false, true)) {
            upUI("startSdk");
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    DJISDKManager.getInstance().registerApp(MainActivity.this, registrationCallback);
                }
            });
        }
    }

    private void getDJIUserAccountName() {
        mFpvHandler.post(new Runnable() {
            @Override
            public void run() {
                UserAccountManager.getInstance().getLoggedInDJIUserAccountName(
                        new CommonCallbacks.CompletionCallbackWith<String>() {
                            @Override
                            public void onSuccess(String s) {
                                upUI(s + "");
                                startActivity(new Intent(MainActivity.this, FPV2Activity.class));
                                finish();
                            }

                            @Override
                            public void onFailure(final DJIError djiError) {
                                upUI(djiError.toString());
                            }
                        });
            }
        });

    }


    public void upUI(String message) {
        strbuff.append(message + "\n");
        mFpvHandler.post(new Runnable() {
            @Override
            public void run() {
                tv_message.setText(strbuff.toString());
            }
        });
    }

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    private void init() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        mLocationOption.setOnceLocation(false);
        mLocationOption.setOnceLocationLatest(false);
        mLocationOption.setMockEnable(false);
        //mLocationOption.setInterval(3000);
        mLocationOption.setHttpTimeOut(20000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.stopLocation();
        //启动定位
        mLocationClient.startLocation();
    }

    /**
     * @description 兼容手机6.0权限管理
     * @author ldm
     * @time 2016/5/24 14:59
     */
    private void permissionForM() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE},
                    1);
        }
    }

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。

                    StringBuffer buffer = new StringBuffer();
                    buffer.append("当前定位结果来源(类型)" + amapLocation.getLocationType() + "\n;获取纬度:");//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    buffer.append(amapLocation.getLatitude() + ";\n获取经度:");//获取纬度
                    buffer.append(amapLocation.getLongitude() + ";\n获取精度信息:");//获取经度
                    buffer.append(amapLocation.getAccuracy() + ";\n地址:");//获取精度信息
                    buffer.append(amapLocation.getAddress() + ";\n国家信息:");//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    buffer.append(amapLocation.getCountry() + ";\n省信息:");//国家信息
                    buffer.append(amapLocation.getProvince() + ";\n城市信息:");//省信息
                    buffer.append(amapLocation.getCity() + ";\n城区信息:");//城市信息
                    buffer.append(amapLocation.getDistrict() + ";\n街道信息:");//城区信息
                    buffer.append(amapLocation.getStreet() + ";\n街道门牌号信息:");//街道信息
                    buffer.append(amapLocation.getStreetNum() + ";\n城市编码:");//街道门牌号信息
                    buffer.append(amapLocation.getCityCode() + ";\n地区编码:");//城市编码
                    buffer.append(amapLocation.getAdCode() + ";\n获取当前定位点的AOI信息:");//地区编码
                    buffer.append(amapLocation.getAoiName() + ";\n获取当前室内定位的建筑物Id:");//获取当前定位点的AOI信息
                    buffer.append(amapLocation.getBuildingId() + ";\n获取当前室内定位的楼层:");//获取当前室内定位的建筑物Id
                    buffer.append(amapLocation.getFloor() + ";\n获取GPS的当前状态:");//获取当前室内定位的楼层
                    buffer.append(amapLocation.getGpsAccuracyStatus() + ";\n时间:");//获取GPS的当前状态
                    //获取定位时间
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    buffer.append(df.format(date) + ";");
                    Log.v("TAG", buffer.toString());
                    upUI(buffer.toString());
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };
}
