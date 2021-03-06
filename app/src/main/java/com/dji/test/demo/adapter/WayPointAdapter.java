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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dji.test.demo.R;
import com.dji.test.demo.bean.WaypointBean;
import com.dji.test.demo.bean.WaypointLineBean;

import java.util.List;

import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;


/**
 * 数据适配器
 *
 * @author LiuTao
 */

public class WayPointAdapter extends RecyclerView.Adapter<WayPointAdapter.ItemViewHolder> {

    public List<WaypointLineBean> waypointActions;
    private Context mContext;
    private LayoutInflater mInflater;
    private ItemClickListener mItemClickListener;
    private ItemOnLongClickListener mItemOnLongClickListener;
    private OnItemDeleteClickListener mOnItemDeleteClickListener;
    private OnDownLoadClickListener mOnDownLoadClickListener;
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
        void onItemDeleteClick(int position, List<WaypointLineBean> list);
    }


    //下载
    public void setmDownLoadClickListener(OnDownLoadClickListener listener) {
        this.mOnDownLoadClickListener = listener;
    }

    public interface OnDownLoadClickListener {
        void onDownLoadClick(long position, List<WaypointLineBean> id);
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
        void onItemClick(int position, List<WaypointLineBean> waypointActions);
    }

    public interface ItemOnLongClickListener {
        void onItemOnLongClick(int position);
    }

    public WayPointAdapter(Context context, List<WaypointLineBean> waypointActions) {
        this.mContext = context;
        this.waypointActions = waypointActions;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public void setWaypointActions(List<WaypointLineBean> waypointActions){
        this.waypointActions = waypointActions;
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_waypoint, parent, false);
        ItemViewHolder holder = new ItemViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        holder.tv_name.setText("航线名称：" + waypointActions.get(position).getLineName());
        StringBuffer tvNum = new StringBuffer();
        if (TextUtils.isEmpty(waypointActions.get(position).getNum())) {
            tvNum.append("航点数量：0");
        } else {
            tvNum.append("航点数量：" + waypointActions.get(position).getNum());
        }
        tvNum.append("\n复飞次数：" + waypointActions.get(position).getFlyNum());
        holder.tv_num.setText(tvNum.toString());
        holder.ll_waypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(position, waypointActions);
            }
        });
        holder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemDeleteClickListener.onItemDeleteClick(position, waypointActions);
            }
        });

        holder.tv_dowon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnDownLoadClickListener.onDownLoadClick(position, waypointActions);
            }
        });
    }

    @Override
    public int getItemCount() {
        return waypointActions == null ? 0 : waypointActions.size();

    }


    protected class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_num;
        TextView tv_name;
        TextView tv_delete;
        TextView tv_dowon;
        LinearLayout ll_waypoint;

        public ItemViewHolder(View view) {
            super(view);
            ll_waypoint = view.findViewById(R.id.ll_waypoint);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_num = (TextView) view.findViewById(R.id.tv_num);
            tv_delete = (TextView) view.findViewById(R.id.tv_delete);
            tv_dowon = (TextView) view.findViewById(R.id.tv_dowon);
        }
    }
}

