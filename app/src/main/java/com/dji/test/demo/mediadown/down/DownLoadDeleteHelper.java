package com.dji.test.demo.mediadown.down;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.dji.test.demo.R;
import com.dji.test.demo.base.MApplication;
import com.dji.test.demo.bean.DownMediaFile;
import com.dji.test.demo.bean.MediaCheckBean;
import com.dji.test.demo.bean.MediaRecyBean;
import com.dji.test.demo.bean.WaypointBean;
import com.dji.test.demo.bean.WaypointLineBean;
import com.dji.test.demo.util.Constant;
import com.dji.test.demo.util.DJIModuleVerificationUtil;
import com.dji.test.demo.util.FileUtils;
import com.dji.test.demo.util.GreendaoUtils;
import com.dji.test.demo.util.LogUtil;
import com.dji.test.demo.util.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dji.common.error.DJICameraError;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;

import dji.sdk.media.DownloadListener;
import dji.sdk.media.MediaFile;
import dji.sdk.media.MediaManager;
import dji.thirdparty.io.reactivex.Observable;
import dji.thirdparty.io.reactivex.ObservableEmitter;
import dji.thirdparty.io.reactivex.ObservableOnSubscribe;
import dji.thirdparty.io.reactivex.android.schedulers.AndroidSchedulers;
import dji.thirdparty.io.reactivex.functions.Consumer;
import dji.thirdparty.io.reactivex.schedulers.Schedulers;

/**
 * $activityName
 *
 * @author yuanyi
 * @date 2019/09/03
 */


public class DownLoadDeleteHelper {
    private TextView mDownloadSize;
    private TextView mNetSpeed;
    private TextView mDiaLogTvTitle;
    private TextView mTvProgress;
    private Button mBtnInstall;
    private ProgressBar mPbProgress1;
    private ProgressBar mPbProgress2;
    private Dialog mAlertDialog;
    private RelativeLayout mRll_date;
    //下载的id索
    int downIndex = 0;
    public boolean idDownMediaIng = false;
    private String TAG = "DownLoadDeleteHelper";
    public List<MediaFile> mThumMediaBenas;
    private DownloadMediaHandler mDownloadMediaHandler;
    WaypointLineBean mWaypointLineBean;
    List<WaypointBean> mWaypointBeanList;
    private DownLoadListener mDownLoadListener = null;
    private DeleteMediaListener mDeleteMediaListener = null;
    private AppCompatActivity mActivity;

    public interface DownLoadListener {
        void onNotifyRefreshUIChange();
    }

    public interface DeleteMediaListener {
        void onNotifyDeleteUIChange(List<MediaFile> seleteMediaFiles);
    }

    public void setDownLoadListener(DownLoadListener downLoadListener) {
        mDownLoadListener = downLoadListener;
    }

    public DownLoadDeleteHelper(AppCompatActivity acticity, List<MediaFile> thumMediaBenas, WaypointLineBean mWaypointLineBean, List<WaypointBean> mWaypointBeanList, DownLoadListener downLoadListener) {
        this.mActivity = acticity;
        this.mWaypointLineBean = mWaypointLineBean;
        this.mThumMediaBenas = thumMediaBenas;
        this.mWaypointBeanList = mWaypointBeanList;
        this.mDownLoadListener = downLoadListener;
    }


    /**
     * 下载
     */

    public void downLoadMediaFiles() {
        createDialogProgress("正在下载照片..", 1);
        mDiaLogTvTitle.setText("正在下载照片");
        mTvProgress.setVisibility(View.INVISIBLE);
        //下载
        if (mThumMediaBenas.size() > 0) {
            downIndex = 0;
            //下载
            mDownloadMediaHandler = new DownloadMediaHandler(mThumMediaBenas);
            MediaFile mediaFile = mThumMediaBenas.get(downIndex);
            idDownMediaIng = true;
            if (idDownMediaIng) {

                downLoadMediaFile(mediaFile, mThumMediaBenas.size(), mDownloadMediaHandler);
            }
        } else {
            ToastUtils.showToast("已下载");
        }
    }


    private void downLoadMediaFile(MediaFile media, final int progressMax, DownloadMediaHandler downloadMediaHandler) {
        LogUtil.v(TAG, "progressMax:" + progressMax);
        if (!idDownMediaIng) {
            return;
        }
        final int idx = downIndex + 1;

        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                e.onNext("");
            }
        }).observeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mDiaLogTvTitle.setText("正在下载 " + idx + "/" + progressMax);
                    }
                });


        if (DJIModuleVerificationUtil.isCameraModuleAvailable() && media != null) {
            //文件夹路径
            String[] strings = media.getDateCreated().split(" ");
            File destDir = new File(FileUtils.getDir(Constant.mediaOrgCach));
            WaypointBean waypointBean = mWaypointBeanList.get(downIndex);
//            media.fetchFileData(destDir, "org_" + strings[0] + "_" + strings[1].replace(":", "-") + "_", downloadMediaHandler);
            String path = mWaypointLineBean.getLineName() + "_" + waypointBean.getId() + "_";
            waypointBean.setPicPath(path);
            GreendaoUtils.getInstance().updataWaypoint(waypointBean);
            media.fetchFileData(destDir, path, downloadMediaHandler);
        }
    }

    // 下载媒体
    public class DownloadMediaHandler implements DownloadListener<String> {
        List<MediaFile> mMediaFiles;

        private DownloadMediaHandler(List<MediaFile> mediaFiles) {
            this.mMediaFiles = mediaFiles;
        }

        @Override
        public void onStart() {
            final int idx = downIndex + 1;
            MApplication.mFpvHandler.post(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    mDiaLogTvTitle.setText("开始下载 " + idx + "/" + mMediaFiles.size());
                }
            });
        }

        @Override
        public void onRateUpdate(final long total, final long current, final long persize) {
            final int idx = downIndex + 1;
            final String downloadLength = Formatter.formatFileSize(MApplication.getContext(), current);

            final String totalLength = Formatter.formatFileSize(MApplication.getContext(), total);
            final String speed = Formatter.formatFileSize(MApplication.getContext(), persize);
            MApplication.mFpvHandler.post(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    mDiaLogTvTitle.setText("正在下载 " + idx + "/" + mMediaFiles.size());
                    mNetSpeed.setText(String.format("%s/s", speed));
                    mTvProgress.setText(((current / total) * 100) + "%");
                    mDownloadSize.setText(downloadLength + "/" + totalLength);
                    mPbProgress2.setProgress((int) current);
                    mPbProgress2.setMax((int) total);
                }
            });

        }

        @Override
        public void onProgress(final long total, final long current) {
            final int idx = downIndex + 1;
            final String downloadLength = Formatter.formatFileSize(MApplication.getContext(), current);
            final String totalLength = Formatter.formatFileSize(MApplication.getContext(), total);
            MApplication.mFpvHandler.post(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    mDiaLogTvTitle.setText("正在下载 " + idx + "/" + mMediaFiles.size());
                    mTvProgress.setText(((current / total) * 100) + "%");
                    mDownloadSize.setText(downloadLength + "/" + totalLength);
                    mPbProgress2.setProgress((int) current);
                    mPbProgress2.setMax((int) total);
                }
            });
        }

        @Override
        public void onSuccess(String s) {


            downIndex++;
            if (downIndex == mMediaFiles.size()) {
                MApplication.mFpvHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        idDownMediaIng = false;
                        mDiaLogTvTitle.setText("下载完成");
                        ToastUtils.showToast("完成");
                        mAlertDialog.dismiss();
                        MApplication.mFpvHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mDownLoadListener.onNotifyRefreshUIChange();

                            }
                        });
                    }
                });
            } else if (idDownMediaIng) {
                LogUtil.v(TAG, "onSuccess" + mThumMediaBenas.size());
                final MediaFile mediaFile = mMediaFiles.get(downIndex);
                downLoadMediaFile(mediaFile, mMediaFiles.size(), mDownloadMediaHandler);
            }
        }

        @Override
        public void onFailure(final DJIError djiError) {
            MApplication.mFpvHandler.post(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    mDiaLogTvTitle.setText(djiError.toString() + "下载失败：" + downIndex);
                    mDownLoadListener.onNotifyRefreshUIChange();
                }
            });
            if (idDownMediaIng) {
                final MediaFile mediaFile = mMediaFiles.get(downIndex);
                downLoadMediaFile(mediaFile, mMediaFiles.size(), mDownloadMediaHandler);
            }
        }
    }

    //
    private void createDialogProgress(String msgTitle, int style) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = LayoutInflater.from(mActivity).inflate(R.layout.wanpoint_progress_dialog, null);
        mDownloadSize = view.findViewById(R.id.downloadSize);
        mNetSpeed = view.findViewById(R.id.netSpeed);
        mDiaLogTvTitle = view.findViewById(R.id.tv_title);
        mTvProgress = view.findViewById(R.id.tvProgress);
        mBtnInstall = view.findViewById(R.id.btn_install);
        mRll_date = view.findViewById(R.id.rll_date);
        mDiaLogTvTitle.setText(msgTitle);
        mPbProgress1 = view.findViewById(R.id.progress_load_list1);
        mPbProgress2 = view.findViewById(R.id.progress_load_list2);
        if (style == 0) {
            mRll_date.setVisibility(View.GONE);
            mPbProgress1.setVisibility(View.VISIBLE);
            mPbProgress2.setVisibility(View.GONE);
        }
        //进度条
        if (style == 1) {
            mRll_date.setVisibility(View.VISIBLE);
            mPbProgress1.setVisibility(View.GONE);
            mPbProgress2.setVisibility(View.VISIBLE);
        }
        builder.setView(view);
        builder.setCancelable(false);
        mBtnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAlertDialog != null) {
                    mAlertDialog.dismiss();
                }
                idDownMediaIng = false;
                //取消下载
            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }
}

