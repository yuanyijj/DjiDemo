package com.dji.test.demo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.dji.test.demo.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 数据适配器
 *
 * @author LiuTao
 */

public class ActionTypeAdapter extends RecyclerView.Adapter {

    private String[] mStrings;

    private Context mContext;
    private LayoutInflater mInflater;
    private ItemClickListener mItemClickListener;
    private ItemOnLongClickListener mItemOnLongClickListener;

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

    public ActionTypeAdapter(Context context, String[] strings) {
        this.mStrings = strings;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item, parent, false);
        ItemViewHolder holder = new ItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        viewHolder.tvName.setText(mStrings[position]);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mItemClickListener.onItemClick(position);
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mStrings == null ? 0 : mStrings.length;

    }


    protected class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        View itemView;

        public ItemViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.text_item);
            itemView = view;
        }
    }


}

