package com.dji.test.demo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.dji.test.demo.R;

import java.util.List;

import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;


/**
 * 数据适配器
 *
 * @author LiuTao
 */

public class MissionPointSetAdapter extends RecyclerView.Adapter<MissionPointSetAdapter.ItemViewHolder> {

    public List<WaypointAction> waypointActions;
    private Waypoint mWaypoint;
    private Context mContext;
    private LayoutInflater mInflater;
    private ItemClickListener mItemClickListener;
    private ItemOnLongClickListener mItemOnLongClickListener;
    private OnItemDeleteClickListener mOnItemDeleteClickListener = null;
    private OnItemTypeSelectClickListener mOnItemTypeSelectClickListener = null;
    private OnItemInputEditListener mOnItemInputEditClickListener = null;
    private PopupWindow mActionTypePW;

    //输入监听
    public void setOnItemInputEditClickListener(OnItemInputEditListener listener) {
        this.mOnItemInputEditClickListener = listener;
    }

    public interface OnItemInputEditListener {
        void onItemInputEditClick(ImageView view, int position);
    }

    //删除监听
    public void setmOnItemDeleteClickListener(OnItemDeleteClickListener listener) {
        this.mOnItemDeleteClickListener = listener;
    }

    public interface OnItemDeleteClickListener {
        void onItemDeleteClick(ImageView view, int position);
    }

    //动作类型添加
    public void setmOnItemTypeSelectClickListener(OnItemTypeSelectClickListener listener) {
        this.mOnItemTypeSelectClickListener = listener;
    }

    public interface OnItemTypeSelectClickListener {
        void onItemTypeSelectClick(TextView view, EditText editText, TextView textView, int position);
    }


    public void setOnItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setItemOnLongClickListener(ItemOnLongClickListener itemOnLongClickListener) {
        mItemOnLongClickListener = itemOnLongClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public interface ItemOnLongClickListener {
        void onItemOnLongClick(int position);
    }

    public MissionPointSetAdapter(Context context, Waypoint mWaypoint) {
        this.mContext = context;
        this.mWaypoint = mWaypoint;
        this.mInflater = LayoutInflater.from(mContext);
    }


    public void setWaypoint(Waypoint mWaypoint) {
        this.mWaypoint = mWaypoint;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_point_active, parent, false);
        ItemViewHolder holder = new ItemViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        final WaypointAction waypointAction = mWaypoint.getActionAtIndex(position);
        holder.itemView.setTag(position);
        holder.setIsRecyclable(false);
        Log.e("actionType", waypointAction.actionType.value() + "");
        int actionParam = waypointAction.actionParam;
        switch (waypointAction.actionType) {
            case STAY: //

                holder.mActiveIds.setText("停留");
                int value = actionParam / 1000;
                holder.mActiveValue.setText(value + "");
                holder.mActiveUnits.setText("秒");
                holder.mActiveValue.setEnabled(true);
                break;
            case START_TAKE_PHOTO:
                holder.mActiveIds.setText("拍照");
                holder.mActiveValue.setEnabled(true);
                holder.mActiveValue.setText("N/A");
                holder.mActiveUnits.setText("");
                break;
            case START_RECORD:
                holder.mActiveIds.setText("开始录像");
                holder.mActiveValue.setEnabled(false);
                holder.mActiveValue.setText("N/A");
                holder.mActiveUnits.setText("");
                break;
            case STOP_RECORD:
                holder.mActiveIds.setText("结束录像");
                holder.mActiveValue.setEnabled(false);
                holder.mActiveValue.setText("N/A");
                holder.mActiveUnits.setText("");
                break;
            case ROTATE_AIRCRAFT:
                holder.mActiveIds.setText("旋转飞机");
                holder.mActiveValue.setHint("偏航角度在[-180,180]之间");
                holder.mActiveValue.setEnabled(true);
                holder.mActiveValue.setText(actionParam + "");
                holder.mActiveUnits.setText("deg");
                break;
            case GIMBAL_PITCH:
                holder.mActiveIds.setText("控制相机俯仰角");
                holder.mActiveValue.setHint("俯仰角度在[-90,30]之间");
                holder.mActiveValue.setEnabled(true);
                holder.mActiveValue.setText(actionParam + "");
                holder.mActiveUnits.setText("deg");
                break;
            default:
                break;


        }

        holder.mActiveDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemDeleteClickListener != null) {
                    mOnItemDeleteClickListener.onItemDeleteClick((ImageView) v, position);
                }

            }
        });
        holder.mActiveIds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //动作类型
                if (mOnItemTypeSelectClickListener != null) {
                    mOnItemTypeSelectClickListener.onItemTypeSelectClick((TextView) v, holder.mActiveValue, holder.mActiveUnits, position);
                }
                final String[] activeTypes = new String[]{"停留", "拍照", "开始录像", "停止录像", "旋转飞机", "控制相机俯仰"};
                ActionTypeAdapter actionTypeAdapter = new ActionTypeAdapter(mContext, activeTypes);
                final RecyclerView recyclerView = new RecyclerView(mContext);
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                recyclerView.setAdapter(actionTypeAdapter);
                mActionTypePW = new PopupWindow(recyclerView, holder.mActiveIds.getWidth(), WindowManager.LayoutParams.WRAP_CONTENT, false);
                mActionTypePW.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mActionTypePW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                mActionTypePW.setOutsideTouchable(true);
                mActionTypePW.setFocusable(true);
                mActionTypePW.getContentView().measure(0, 0);
                mActionTypePW.showAsDropDown(holder.mActiveIds, 0, 0);
                //动作类型选择
                actionTypeAdapter.setOnItemClickListener(new ActionTypeAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (mActionTypePW != null) {
                            mActionTypePW.dismiss();
                        }
                        holder.mActiveIds.setText(activeTypes[position]);
                        switch (position) {
                            case 0:  //停留
                                waypointAction.actionType = WaypointActionType.STAY;
                                holder.mActiveUnits.setText("秒");
                                int value = waypointAction.actionParam / 1000;
                                holder.mActiveValue.setEnabled(true);
                                holder.mActiveValue.setText(value + "");
                                break;
                            case 1:  //拍照
                                waypointAction.actionType = WaypointActionType.START_TAKE_PHOTO;
                                holder.mActiveValue.setEnabled(false);
                                holder.mActiveValue.setText("N/A");
                                holder.mActiveUnits.setText("");
                                break;
                            case 2:  //开始录像
                                waypointAction.actionType = WaypointActionType.START_RECORD;
                                holder.mActiveValue.setEnabled(false);
                                holder.mActiveValue.setText("N/A");
                                holder.mActiveUnits.setText("");
                                break;
                            case 3:  //停止录像
                                waypointAction.actionType = WaypointActionType.STOP_RECORD;
                                holder.mActiveValue.setEnabled(false);
                                holder.mActiveValue.setText("N/A");
                                holder.mActiveUnits.setText("");
                                break;
                            case 4:  //旋转飞机
                                waypointAction.actionType = WaypointActionType.ROTATE_AIRCRAFT;
                                waypointAction.actionParam = 0;
                                holder.mActiveValue.setEnabled(true);
                                holder.mActiveValue.setText("0");
                                holder.mActiveValue.setHint("偏航角度在[-180,180]之间");
                                holder.mActiveUnits.setText("deg");
                                break;
                            case 5:  //控制相机俯仰角
                                waypointAction.actionType = WaypointActionType.GIMBAL_PITCH;
                                holder.mActiveValue.setEnabled(true);
                                holder.mActiveValue.setHint("俯仰角度在[-90,30]之间");
                                holder.mActiveValue.setText("0");
                                holder.mActiveUnits.setText("deg");
                                break;
                            default:
                                break;
                        }
                        //  mWaypoint.adjustActionAtIndex(position, waypointAction);
                    }
                });


            }
        });

        holder.mActiveValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (!TextUtils.isEmpty(holder.mActiveValue.getText())) {
                        String ed = holder.mActiveValue.getText().toString();
                        if (waypointAction.actionType.equals(WaypointActionType.STAY)) {
                            waypointAction.actionParam = Integer.parseInt(ed) * 1000;
                        } else {
                            waypointAction.actionParam = Integer.parseInt(ed);
                        }

                        // mWaypoint.adjustActionAtIndex(position, waypointAction);
                    } else {
                        waypointAction.actionParam = 0;
                    }
                } catch (NumberFormatException e) {
                    waypointAction.actionParam = 0;
                    e.printStackTrace();
                }
            }
        });

        /*//具体的值
        holder.mActiveValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        if (!TextUtils.isEmpty(holder.mActiveValue.getText())) {
                            String s = holder.mActiveValue.getText().toString();
                            if (waypointAction.actionType.equals(WaypointActionType.STAY)) {
                                waypointAction.actionParam = Integer.parseInt(s) * 1000;
                            } else {
                                waypointAction.actionParam = Integer.parseInt(s);
                            }

                            // mWaypoint.adjustActionAtIndex(position, waypointAction);
                        }
                    } catch (NumberFormatException e) {
                        waypointAction.actionParam = 0;
                        e.printStackTrace();
                    }
                }
            }
        });*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(position);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // mItemOnLongClickListener.onItemOnLongClick(position);
                return false;  //为true消耗事件，false不消耗事件 继续传递
            }
        });

    }

    @Override
    public int getItemCount() {
        return mWaypoint.waypointActions == null ? 0 : mWaypoint.waypointActions.size();

    }


    protected class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView mActiveIds;
        EditText mActiveValue;
        TextView mActiveUnits;
        ImageView mActiveDelete;

        public ItemViewHolder(View view) {
            super(view);
            mActiveIds = (TextView) view.findViewById(R.id.active_ids);
            mActiveValue = (EditText) view.findViewById(R.id.active_value);
            mActiveUnits = (TextView) view.findViewById(R.id.active_units);
            mActiveDelete = (ImageView) view.findViewById(R.id.active_delete);
        }
    }
}

