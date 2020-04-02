package com.dji.test.demo.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.dji.test.demo.R;


/**
 * @author yuanyi
 * @date on 2019/7/2 上午09:00
 * @describe 航点属性设置
 */

@SuppressLint("ValidFragment")
public class MissonWayPointDialogFragment extends DialogFragment {


    private Context mContext;


    private TextView tv_name;
    private TextView tv_all;

    public MissonWayPointDialogFragment(Context mContezt) {
        this.mContext = mContezt;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_input_waypoint, container);




        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全透明
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomDialog);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(String string, String name);
    }


}
