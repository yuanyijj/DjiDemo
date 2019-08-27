package com.dji.test.demo.util;

import android.graphics.Point;
import com.amap.api.maps.model.LatLng;
import com.dji.test.demo.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 算法
 *
 * @author ${LiuTao}
 * @date 2017/12/30/030
 */

public class RouteUtlis {
    private static String TAG = "RouteUtlis";
    private static double sSlope;

    /**
     * 求两点之间的距离
     */

    public static double disTancePoint(Point p1, Point p2) {
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }


    /**
     * 已知两点求直线方程 等分点
     * 等分点
     *
     * @param p1     起点
     * @param p2     终点
     * @param divDis 等分长度
     * @return 等分点
     */
    public static List<Point> getLinePoint(Point p1, Point p2, double divDis) {//divdis =1
        int disNum;
        List<Point> list = new ArrayList<>();
        double dis = disTancePoint(p1, p2);
        if (divDis > dis / 2) {
            disNum = 1;
            divDis = dis / 2;
        } else {
            disNum = (int) (dis / divDis); //等分个数
        }
        if (p1.equals(p2)) { //同一个点
            list.add(p1);
        }
        if (p1.x == p2.x) {
            int divStep = (int) ((p1.y > p2.y) ? -divDis : divDis);//divstep = 1
            if (p1.y >= p2.y) {
                for (int i = p1.y + divStep; i >= p2.y; i += divStep) {
                    list.add(new Point(p1.x, i));
                }
            } else {
                for (int i = p1.y + divStep; i <= p2.y; i += divStep) {
                    list.add(new Point(p1.x, i));
                }
            }

        } else if (p1.y == p2.y) {
            int divStep = (int) ((p1.x > p2.x) ? -divDis : divDis);//divstep = 1
            if (p1.x >= p2.x) { // divstep = -1
                for (int i = p1.x + divStep; i >= p2.x; i += divStep) {
                    list.add(new Point(i, p1.y));
                }
            } else { // divstep = 1
                for (int i = p1.x + divStep; i <= p2.x; i += divStep) {
                    list.add(new Point(i, p1.y));
                }
            }
        } else {
            double s1 = p2.y - p1.y;
            double s2 = p2.x - p1.x;
            double slope = s1 / s2;
            if (p2.x > p1.x) {
                for (int i = 0; i < disNum; i++) {
                    double everyDis = (divDis + divDis * i);
                    Point p0 = new Point();
                    //已知斜率 slope, 和 点（p2.x,p2.y） 直线 y = slope*x +p2.y-slope*p2.x;
                    double x = (everyDis * (p2.x - p1.x) / dis) + p1.x;
                    p0.x = (int) x;
                    p0.y = (int) (slope * x + p2.y - slope * p2.x);
                    list.add(p0);
                }
            }
            if (p2.x < p1.x) {
                for (int i = 0; i < disNum; i++) {
                    double everyDis = (divDis + divDis * i);
                    Point p0 = new Point();
                    double x = p1.x - (everyDis * (p1.x - p2.x) / dis);
                    double y = slope * x + p2.y - slope * p2.x;
                    p0.x = (int) x;
                    p0.y = (int) y;
                    list.add(p0);
                }
            }
        }
        return list;
    }

    /**
     * @param edgPointList 所有的范围点
     * @param flag         x,y轴 方向
     * @return
     */
    public static Point getMaxDisPonit(Point startPoint, List<Point> edgPointList, String flag) {
        int index = 0;
        if (flag == null) {
            flag = "x";
        }
        double maxX = Math.abs(edgPointList.get(0).x - startPoint.x);
        double maxY = Math.abs(edgPointList.get(0).y - startPoint.y);
        double maxDis = disTancePoint(startPoint, edgPointList.get(0));
        switch (flag) {
            case "x":
                for (int i = 0; i < edgPointList.size(); i++) {
                    double num = Math.abs(edgPointList.get(i).x - startPoint.x);
                    if (maxX < num) {
                        maxX = num;
                        index = i;
                    }
                }
                break;
            case "y":
                for (int i = 0; i < edgPointList.size(); i++) {
                    double num = Math.abs(edgPointList.get(i).y - startPoint.y);
                    if (maxY < num) {
                        maxY = num;
                        index = i;
                    }
                }
                break;
            case "m":
                //求中点
                if ((edgPointList.size() - 1) % 2 == 0) {  //偶数
                    int x = (edgPointList.size() - 1) / 2;
                    Point p1 = edgPointList.get(x);
                    Point p2 = edgPointList.get(x + 1);
                    int dis1 = (int) disTancePoint(startPoint, p1);
                    int dis2 = (int) disTancePoint(startPoint, p2);
                    if (dis1 > dis2) {
                        index = x;
                    } else {
                        index = x + 1;
                    }
                } else {//奇数
                    index = edgPointList.size() / 2;
                }
                break;
            case "d":
                for (int i = 0; i < edgPointList.size(); i++) {
                    double num = disTancePoint(startPoint, edgPointList.get(i));
                    if (maxDis < num) {
                        maxDis = num;
                        index = i;
                    }
                }
                break;
        }
        return edgPointList.get(index);
    }

    /**
     * 得到目标点的斜率
     * 首尾相连
     *
     * @param tartgetPoint
     */
    public static List<Double> getSlope(Point startPoint, Point endPoint, List<Point> tartgetPoint) {

        for (int i = 0; i < tartgetPoint.size(); i++) {
            //double slope = ((double) (endPoint.y - startPoint.y)) / ((double) (endPoint.x - startPoint.x));
        }
        return null;
    }


    /**
     * 得到交点
     * 行列式
     *
     * @param aa 直线点
     * @param bb 直线点
     * @param cc 线段点
     * @param dd 线段点
     * @return
     */
    public static Point getIntersectionPoint(Point aa, Point bb, Point cc, Point dd) {
        Point point = new Point();
        double delta = determinant(bb.x - aa.x, cc.x - dd.x, bb.y - aa.y, cc.y - dd.y);
        if (delta > (1e-6) || delta < -(1e-6))  // delta=0，排除两线段重合或平行的情况
        {
            //
            double namenda = determinant(cc.x - aa.x, cc.x - dd.x, cc.y - aa.y, cc.y - dd.y) / delta;
            double miu = determinant(bb.x - aa.x, cc.x - aa.x, bb.y - aa.y, cc.y - aa.y) / delta;
            //求交点
            double x = aa.x + namenda * (bb.x - aa.x);
            double y = aa.y + namenda * (bb.y - aa.y);
            System.out.println("X:" + x + ",Y:" + y);
            if (cc.x <= dd.x) {
                if (x >= cc.x && x <= dd.x) {
                    if (x != aa.x && y != aa.y) {
                        point.x = (int) x;
                        point.y = (int) y;
                    }

                }
            } else if (cc.x > dd.x) {
                if (x >= dd.x && x <= cc.x) {
                    if (x != aa.x && y != aa.y) {
                        point.x = (int) x;
                        point.y = (int) y;
                    }
                }
            }
            System.out.println("getX:" + point.x + ",getY:" + point.y);
        }
        return point;
    }

    public static double determinant(double v1, double v2, double v3, double v4)  // 行列式
    {
        return (v1 * v4 - v2 * v3);
    }

    /**
     * 计算边长
     *
     * @return
     */
//    public static double getLineLength(int x, List<LatLng> latLngs) {
//        if (latLngs.size() < 2) {
//            return 0;
//        }
//        double sum = 0;
//        int j = 0;
//        for (int i = x; i < latLngs.size(); i++) {
//            double a = DJIUtils.getDistance(
//                    latLngs.get(i).latitude,
//                    latLngs.get(i).longitude, latLngs
//                            .get(i + 1).latitude, latLngs
//                            .get(i + 1).longitude) / 1000;
//            sum += a;
//            j = i + 1;
//            if (j >= latLngs.size() - 1) {
//                break;
//            }
//        }
//        return sum;
//    }

    public static boolean checkGpsCoordinate(double latitude, double longitude) {
        return (latitude > -90 && latitude < 90 && longitude > -180 && longitude < 180) && (latitude != 0f && longitude != 0f);
    }

    /**
     * 我的点
     *
     * @param lat_a 纬度1
     * @param lng_a 经度1
     *
     *              <p>
     *              <p>
     *              对着的 目标点/兴趣点
     * @param lat_b 纬度2
     * @param lng_b 经度2
     *              <p>
     *              逆时针
     * @return
     */
    public static double getAngle1(double lat_a, double lng_a, double lat_b, double lng_b) {
//        LatLng latLnga = LatLngUtils.latlngToWGS84(lat_a, lng_a);
//        LatLng latLngb = LatLngUtils.latlngToWGS84(lat_b, lng_b);
//        lat_a = latLnga.latitude;
//        lng_a = latLnga.longitude;
//        lat_b = latLngb.latitude;
//        lng_b = latLngb.longitude;

//        double y = Math.sin(lng_b - lng_a) * Math.cos(lat_b);
//        double x = Math.cos(lat_a) * Math.sin(lat_b) - Math.sin(lat_a) * Math.cos(lat_b) * Math.cos(lng_b - lng_a);
//        double brng = Math.atan2(y, x);
//        LogUtil.e("计算角度值 1", brng + "");
//        brng = Math.toDegrees(brng);
//        LogUtil.e("计算角度值 2", brng + "");
//        if (brng < 0) {
//            brng = brng + 360;
//        }
//        LogUtil.e("计算角度值 3", brng + "");
        return computeAzimuth(new LatLng(lat_a,lng_a),new LatLng(lat_b,lng_b));
    }

    private static double computeAzimuth(LatLng la1, LatLng la2) {
        double lat1 = la1.latitude, lon1 = la1.longitude, lat2 = la2.latitude,
                lon2 = la2.longitude;
        double result = 0.0;

        int ilat1 = (int) (0.50 + lat1 * 360000.0);
        int ilat2 = (int) (0.50 + lat2 * 360000.0);
        int ilon1 = (int) (0.50 + lon1 * 360000.0);
        int ilon2 = (int) (0.50 + lon2 * 360000.0);

        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        if ((ilat1 == ilat2) && (ilon1 == ilon2)) {
            return result;
        } else if (ilon1 == ilon2) {
            if (ilat1 > ilat2) {
                result = 180.0;
            }
        } else {
            double c = Math
                    .acos(Math.sin(lat2) * Math.sin(lat1) + Math.cos(lat2)
                            * Math.cos(lat1) * Math.cos((lon2 - lon1)));
            double A = Math.asin(Math.cos(lat2) * Math.sin((lon2 - lon1))
                    / Math.sin(c));
            result = Math.toDegrees(A);
            if ((ilat2 > ilat1) && (ilon2 > ilon1)) {
            } else if ((ilat2 < ilat1) && (ilon2 < ilon1)) {
                result = 180.0 - result;
            } else if ((ilat2 < ilat1) && (ilon2 > ilon1)) {
                result = 180.0 - result;
            } else if ((ilat2 > ilat1) && (ilon2 < ilon1)) {
                result += 360.0;
            }
        }
        LogUtil.e("计算角度值 1", result + "");
        return result;
    }

    /**
     * [180~0~（-180)]->[（-180~0~（180））]
     *
     * @param value
     */
    public static double parsefu180Tozh180(double value) {

        return -value;
    }

    /**
     * 正北为0 飞机方位角（-180~0~（180））to 高德地图方位角度0-360（逆时针的）
     * 角度转换-180~0~（180）转成0~360 (逆时针)
     * *正南-180和180交界
     * =====90 ->270
     * =======-90->90
     *
     * @param value
     * @return
     */
    public static double parseYawTo360(double value) {
        double yaw = 0;
        try {
            if (value < 0) {
                yaw = -value;
            } else if (value > 0) {
                yaw = 360 - value;
            } else {
                yaw = value;
            }
        } catch (Exception e) {
            e.printStackTrace();
           LogUtil.e(TAG,e.toString());
        }
        LogUtil.e("计算角度值 转地图角度", yaw + "");
        return yaw;
    }



    /**
     * -180~0~（180）转逆时针方位角0-360
     * <p>
     * -90  -> 90
     * 90   -> 270
     *
     * @param value
     * @return
     */
    public static double parseYawToAntiClockwise360(double value) {
        double yaw = 0;
        try {
            if (value < 0) {
                yaw = -value;
            } else if (value > 0) {
                yaw = 360 - value;
            } else {
                yaw = value;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG,e.toString());
        }
        return yaw;
    }


    /**
     * 顺时针360度 =逆时针360度 转换
     *
     * @param value
     * @return
     */
    public static double parseYawAnticlockwise360(double value) {
        return 360 - value;
    }

    /**
     * 角度转换0~360（逆时针）转成飞机方位角-180~ 180
     * 90-> -90
     * 270 ->90
     *
     * @param value -90   90
     * @return
     */
    public static double parseYawTo180(double value) {
        double yaw = 0;
        if (value > 180) {
            yaw = 360 - value;
        } else if (value < 180) {
            yaw = -value;
        } else {
            yaw = value;
        }
        LogUtil.e("计算角度值 机头3", yaw + "");
        return yaw;
    }

    /**
     * 角度转换0~360（顺时针针）转成飞机方位角-180~ 180
     * 90-> 90
     * 270 ->-90
     *
     * @param value -90   90
     * @return
     */
    public static double parseYawToClockwise180(double value) {
        double yaw = 0;
        if (value > 180) {
            yaw = value - 360;
        } else if (value < 180) {
            yaw = value;
        } else {
            yaw = value;
        }
        LogUtil.e("计算角度值 机头3", yaw + "");
        return yaw;
    }
    /**
     * 飞机方位角度转顺时针方向
     * 角度转换-180~0~（180）转成0~360 (顺时针方向)
     * *正南-180和180交界
     * =====90 ->270
     * =======-90->90
     *
     * @param value
     * @return
     */
    public static double parseYawToClockwise360(double value) {
        double yaw = 0;
        try {
            if (value < 0) {
                yaw = 360 + value;
            } else if (value > 0) {
                yaw = value;
            } else {
                yaw = value;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG,e.toString());
        }
        LogUtil.e("计算角度值 转地图角度", yaw + "");

        return yaw;
    }
}
