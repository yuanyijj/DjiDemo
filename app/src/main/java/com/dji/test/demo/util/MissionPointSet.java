package com.dji.test.demo.util;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.api.maps2d.model.Marker;
import com.dji.test.demo.R;
import com.dji.test.demo.adapter.MissionPointSetAdapter;
import com.dji.test.demo.dialog.MissonWayPointDialogFragment;

import java.util.List;

import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;

/**
 * 航点相关设置
 */

public class MissionPointSet {

    private Context mContext;

    public MissionPointSet(Context mContext) {
        this.mContext = mContext;
        mMissonWayPointDialogFragment = new MissonWayPointDialogFragment(mContext);
    }

    /*** 单航点设置
     *
     * @param wayFpv1pointList
     * @param marker
     */

    private int sIdmarker;
    private int sWayPointId;//设置选择的是哪个航点
    //航点设置及动作
    private Waypoint mWaypoint;
    private MissonWayPointDialogFragment mMissonWayPointDialogFragment;
    private Dialog sDialog;

    public void showWayPointActionDialog(final List<Waypoint> waypointList, final List<Marker> markerList, final Marker marker) {
        int idmarker;
        try {
            idmarker = Integer.parseInt(marker.getSnippet());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            idmarker = 1;
        }
        sIdmarker = idmarker - 1;
        sWayPointId = idmarker;
        if (sDialog == null) {
            sDialog = new Dialog(mContext, R.style.BottomDialog);
        }

        if (sIdmarker >= 0 && sIdmarker < waypointList.size()) {
            mWaypoint = waypointList.get(sIdmarker);
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_input_waypoint, null);

        final TextView tv_message = view.findViewById(R.id.tv_message);
        tv_message.setText("纬度：" + marker.getPosition().latitude + ";经度：" + marker.getPosition().longitude);

        final EditText et_height = view.findViewById(R.id.et_height);
        et_height.setText(mWaypoint.altitude + "");

        et_height.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
                if (et_height.getText().length() > 0) {
                    mWaypoint.altitude = Float.parseFloat(et_height.getText().toString());
                }else{
                    mWaypoint.altitude=0;
                }
            }
        });

        final RecyclerView mActiveRecyclerview = (RecyclerView) view.findViewById(R.id.active_recyclerview);
        //动作列表
        final MissionPointSetAdapter missionPointSetAdapter =
                new MissionPointSetAdapter(mContext, mWaypoint);
        mActiveRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        mActiveRecyclerview.setAdapter(missionPointSetAdapter);
        mActiveRecyclerview.setHasFixedSize(true);
        mActiveRecyclerview.setItemAnimator(new DefaultItemAnimator());
        mActiveRecyclerview.setNestedScrollingEnabled(false);
        missionPointSetAdapter.setmOnItemDeleteClickListener(
                new MissionPointSetAdapter.OnItemDeleteClickListener() {
                    @Override
                    public void onItemDeleteClick(ImageView view, int position) {
                        Log.e("位置", position + "");
                        mWaypoint.waypointActions.remove(position);
                        missionPointSetAdapter.setWaypoint(mWaypoint);
                        missionPointSetAdapter.notifyDataSetChanged();
                    }
                });


        ImageView mAddViewBtn = (ImageView) view.findViewById(R.id.add_view_btn);
        // todo 曲线过弯时动作无效
        mAddViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWaypoint.waypointActions.size() > 14) {
                    LogUtil.v("mAddViewBtn", "");
                    return;
                }
                WaypointAction waypointAction = new WaypointAction(WaypointActionType.STAY, 2000);
                mWaypoint.waypointActions.add(waypointAction);//WaypointAction
                LogUtil.e("additem=", "mActionList.size():" + mWaypoint.waypointActions.size());
                missionPointSetAdapter.notifyDataSetChanged();
                mActiveRecyclerview.scrollToPosition(mWaypoint.waypointActions.size() - 1);
            }
        });

        Window dialogWindow = sDialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.drawable.mission_bg);
        WindowManager.LayoutParams windowParams = dialogWindow.getAttributes();
        windowParams.dimAmount = 0.0f;
        dialogWindow.setAttributes(windowParams);
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int mWindowWidth = (int) (displayMetrics.widthPixels * 0.6);
        int mWindowHeight = (int) (displayMetrics.heightPixels * 0.8);
        sDialog.setContentView(view, new ViewGroup.LayoutParams(mWindowWidth, mWindowHeight));
        sDialog.show();
    }

}
