package com.lyl.flutter_checkupdate.tools;

import android.content.Context;
import android.content.SharedPreferences;


import java.lang.reflect.Type;

/**
 * 缓存处理类
 *
 * @author yalin
 */

public class StorePrferences {
    public static String getFromText(Context context, String key, String subKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(key,
                Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(subKey, "");
        return value;
    }

    public static boolean getFromBoolean(Context context, String key,
                                         String subKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(key,
                Context.MODE_PRIVATE);
        boolean value = sharedPreferences.getBoolean(subKey, false);

        return value;
    }

    public static void saveToText(Context context, String key, String subKey,
                                  String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(key,
                Context.MODE_PRIVATE).edit();
        editor.putString(subKey, value);
        editor.commit();
    }

    public static void saveToBoolean(Context context, String key,
                                     String subKey, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(key,
                Context.MODE_PRIVATE).edit();
        editor.putBoolean(subKey, value);
        editor.commit();
    }

    public static void removeKey(Context context, String key) {
        SharedPreferences.Editor editor = context.getSharedPreferences(key,
                Context.MODE_PRIVATE).edit();
        if (editor != null) {
            editor.clear();
            editor.commit();
        }
    }

}