package com.lyl.flutter_checkupdate.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lyl on 2016/7/5.
 */
public class PhoneFactory {

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        int versionCode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versionCode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {

        }
        return versionName;
    }

    public static int getAppVersionCode(Context context) {
        int versionCode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
            Log.e("", "versionCode:" + versionCode);
        } catch (Exception e) {

        }
        return versionCode;
    }

    public static int compareVersion(Context context, String version) {
        String oldVersion = getAppVersionName(context);
        return compareVersion(context, version, oldVersion);
    }

    public static int compareVersion(Context context, String version, String oldVersion) {
        String[] vs = version.split("\\.");
        String[] ovs = oldVersion.split("\\.");
        int result = 0;
        if (vs.length == ovs.length) {
            int count = vs.length;
            for (int i = 0; i < count; i++) {
                result = compareString(vs[i], ovs[i]);
                if (result != 0) {
                    return result;
                }
            }
        } else {
            int count = vs.length < ovs.length ? vs.length : ovs.length;
            for (int i = 0; i < count; i++) {
                result = compareString(vs[i], ovs[i]);
                if (result != 0) {
                    return result;
                }
            }
            if (result == 0) {
                return vs.length < ovs.length ? -1 : 1;
            }
        }
        return result;
    }

    private static int compareString(String v, String ov) {
        int iv = Integer.parseInt(v);
        int iov = Integer.parseInt(ov);
        if (iv == iov) {
            return 0;
        } else if (iv > iov) {
            return 1;
        } else {
            return -1;
        }
    }

    public static Map<String, Object> appInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        Map<String, Object> map = new HashMap<>();
        PackageInfo info = null;
        try {
            info = pm.getPackageInfo(context.getPackageName(), 0);
            map.put("appName", info.applicationInfo.loadLabel(pm).toString());
            map.put("logo", info.applicationInfo.loadIcon(pm));
            map.put("packageName", context.getPackageName());
            map.put("version", info.versionName);
            map.put("buildNumber", String.valueOf(getLongVersionCode(info)));
            return map;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return map;
    }

    private static long getLongVersionCode(PackageInfo info) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return info.getLongVersionCode();
        }
        return info.versionCode;
    }
}
