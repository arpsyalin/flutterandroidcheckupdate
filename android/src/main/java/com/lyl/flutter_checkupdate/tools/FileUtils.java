package com.lyl.flutter_checkupdate.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyl on 2016/7/4.
 */
public class FileUtils {

    /**
     * sd卡的根目录
     */
    public static String SDROOTPATH = Environment
            .getExternalStorageDirectory().getPath();

    /**
     * 截屏
     *
     * @param activity
     * @return
     */
    public static Bitmap captureScreen(Activity activity) {
        activity.getWindow().getDecorView().setDrawingCacheEnabled(true);
        Bitmap bmp = activity.getWindow().getDecorView().getDrawingCache();
        return bmp;

    }

    public static Bitmap scaleBitmap(Bitmap origin, float ratiox, float ratioy) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratiox, ratioy);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
//        origin.recycle();
        return newBM;
    }

    public static Bitmap getPicFromBytes(byte[] bytes,
                                         BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                        opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    public static byte[] getBytesByBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        return data;
    }

    public static final Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    /**
     * 合成Bitmap
     *
     * @param bitmap1
     * @param bitmap2
     * @param x
     * @param y
     * @return
     */
    public static Bitmap compoundBitmap(Bitmap bitmap1, Bitmap bitmap2, int x, int y) {
        Bitmap bitmap3 = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), bitmap1.getConfig());
        Canvas canvas = new Canvas(bitmap3);
        canvas.drawBitmap(bitmap1, new Matrix(), null);
        canvas.drawBitmap(bitmap2, x, y, null);
        return bitmap3;
    }

    /**
     * 获得ViewBITMAP
     *
     * @param view
     * @return
     */
    public static Bitmap getViewBitmap(View view) {
        view.clearFocus();
        view.setPressed(false);

        boolean willNotCache = view.willNotCacheDrawing();
        view.setWillNotCacheDrawing(false);

        int color = view.getDrawingCacheBackgroundColor();
        view.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            view.destroyDrawingCache();
        }
        view.buildDrawingCache();
        Bitmap cacheBitmap = view.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        view.destroyDrawingCache();
        view.setWillNotCacheDrawing(willNotCache);
        view.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    /**
     * 保存bitmap
     *
     * @param fileName
     * @param bitmap
     * @throws IOException
     */
    public static String savaBitmap(String path, String fileName, Bitmap bitmap) throws IOException {
        if (bitmap == null) {
            return null;
        }
        File folderFile = new File(path);
        File sd = new File(Environment.getExternalStorageDirectory().toString());
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
        String filepath = path + File.separator + fileName;
        File file = new File(path + File.separator + fileName);
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
        return filepath;
    }

    /**
     * 获取bitmap
     *
     * @param fileName
     * @return
     */
    public static Bitmap getBitmap(String path, String fileName) {
        return BitmapFactory.decodeFile(path + File.separator
                + fileName);
    }

    /**
     * 文件是否存在
     *
     * @param fileName
     * @return
     */
    public static boolean isFileExists(String path, String fileName) {
        return new File(path + File.separator + fileName)
                .exists();
    }

    /**
     * 获取文件大小
     *
     * @param fileName
     * @return
     */
    public static long getFileSize(String path, String fileName) {
        return new File(path + File.separator + fileName)
                .length();
    }


    /**
     * 删除文件
     */
    public static void deleteFileDir(String path) {
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            return;
        }
        if (dirFile.isDirectory()) {
            String[] children = dirFile.list();
            for (int i = 0; i < children.length; i++) {
                new File(dirFile, children[i]).delete();
            }
        }
        dirFile.delete();
    }

    /**
     * 读取表情配置文件
     *
     * @param context
     * @return
     */
    public static List<String> getEmojiFile(Context context) {
        try {
            List<String> list = new ArrayList<String>();
            InputStream in = context.getResources().getAssets().open("emoji");
            BufferedReader br = new BufferedReader(new InputStreamReader(in,
                    "UTF-8"));
            String str = null;
            while ((str = br.readLine()) != null) {
                list.add(str);
            }

            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 判断是否有SD卡
    public static boolean isHasSdcard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 图片转成string * * @param bitmap * @return
     */
    public static String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);
    }

    /***
     * 创建APK文件
     */
    public static void createFile(String fileurl) {
        File file = new File(fileurl);
        if (!file.exists())
            file.mkdirs();
    }

    /***
     * 创建APK文件
     */
    public static void createFile(String saveDir, String name) {
        if (isHasSdcard()) {
//            makeRootDirectory(SDROOTPATH + "/" + saveDir);
            File updateDir = new File(SDROOTPATH + "/" + saveDir);

            if (!updateDir.exists()) {
                updateDir.mkdirs();
            }
            File updateFile = new File(updateDir.getPath() + "/" + name + ".apk");
            if (updateFile.exists()) {
                updateFile.delete();
                updateFile = new File(updateDir.getPath() + "/" + name + ".apk");
            }
            try {
                updateFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                updateFile.mkdir();
            }
        }
    }

    /***
     * 创建APK文件
     */
    public static void createDFile(String saveDir, String name) {
        if (isHasSdcard()) {
            File updateDir = new File(saveDir);
            if (!updateDir.exists()) {
                updateDir.mkdirs();
            }
            File updateFile = new File(updateDir + "/" + name);
            if (updateFile.exists()) {
                updateFile.delete();
                updateFile = new File(updateDir + "/" + name);
            }
            try {
                updateFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                updateFile.mkdir();
            }
        }
    }

    /**
     * 创建APK文件
     *
     * @param name
     * @return
     */
    public static String updataFilename(String apkdir, String name) {
        if (isHasSdcard()) {
            File updateDir = new File(SDROOTPATH + "/" + apkdir);
            if (!updateDir.exists()) {
                boolean istrue = updateDir.mkdirs();
                if (!istrue) {
                    //return null;
                }
            }
            File updateFile = new File(updateDir.getPath() + "/" + name + ".apk");
            if (updateFile.exists()) {
                return updateFile.getAbsolutePath();
            }
            try {
                updateFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
//                updateFile.mkdir();
            }
            return updateFile.getPath();

        }
        return null;
    }

    public static File getFilePath(String filePath,
                                   String fileName) {
        filePath = Environment.getExternalStoragePublicDirectory(
                filePath).getAbsolutePath() + "//";
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }

    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {

        }
    }

    public static File getCachePath(Context ctx, String path) {
        String rootpath = getDiskCacheDir(ctx);
        if (rootpath != null) {
            File file = new File(rootpath + path);
            if (file.exists()) {
                return file;
            } else {
                file.mkdirs();
                return file;
            }
        }
        return null;
    }

    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
//Environment.getExtemalStorageState() 获取SDcard的状态
//Environment.MEDIA_MOUNTED 手机装有SDCard,并且可以进行读写
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }
}
