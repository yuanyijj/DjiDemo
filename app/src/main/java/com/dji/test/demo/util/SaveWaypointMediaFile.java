package com.dji.test.demo.util;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import dji.sdk.media.MediaFile;

public class SaveWaypointMediaFile {
    private static String TAG="SaveWaypointMediaFile";
    public static void saveWaypointFiles(List<MediaFile> list, String fileName) {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            File file = new File(FileUtils.getDir(Constant.mMediaFileCach) + fileName);
            //存入数据
            /*File file = new File(Environment.getExternalStorageDirectory().toString()
                    + File.separator + "Test" + File.separator + "data.txt");*/
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            fileOutputStream = new FileOutputStream(file.toString());
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(list);
            LogUtil.v(TAG,"数据写入成功");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null!=objectOutputStream ) {
                    objectOutputStream.close();
                    LogUtil.v(TAG,"ObjectOutputStream流关闭成功");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                    LogUtil.v(TAG,"FileOutputStream流关闭成功");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<MediaFile> putWaypointFiles(String fileName) {
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            File file = new File(FileUtils.getDir(Constant.mMediaFileCach) + fileName);
            //取出数据
            fileInputStream = new FileInputStream(file.toString());
            objectInputStream = new ObjectInputStream(fileInputStream);
            ArrayList<MediaFile> savedArrayList = (ArrayList<MediaFile>) objectInputStream.readObject();
            LogUtil.v(TAG,"数据取出"+savedArrayList.size());
            return savedArrayList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }finally {
            try {
                if(null!=objectInputStream){
                    objectInputStream.close();
                    LogUtil.v(TAG,"ObjectInputStream流关闭");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(null!=fileInputStream){
                    fileInputStream.close();
                    LogUtil.v(TAG,"FileInputStream流关闭");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
