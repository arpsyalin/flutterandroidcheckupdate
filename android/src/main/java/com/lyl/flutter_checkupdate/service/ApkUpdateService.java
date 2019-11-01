package com.lyl.flutter_checkupdate.service;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.lyl.flutter_checkupdate.R;
import com.lyl.flutter_checkupdate.cache.factory.CacheOptFactory;
import com.lyl.flutter_checkupdate.model.ApkUpdateModel;
import com.lyl.flutter_checkupdate.model.LayoutModel;
import com.lyl.flutter_checkupdate.receiver.AppInstallReceiver;
import com.lyl.flutter_checkupdate.receiver.ForceUpdateReceiver;
import com.lyl.flutter_checkupdate.tools.FileUtils;
import com.lyl.flutter_checkupdate.tools.PhoneFactory;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class ApkUpdateService extends Service {
    public static boolean isUpdata = false;
    public static final String PACKAGENAME = ApkUpdateService.class.getPackage().getName();
    public static final String ACTIONUPDATE = PACKAGENAME + "ACTIONUPDATE";
    public static final String APKUPDATEMODEL = PACKAGENAME + "APKUPDATEMODEL";
    public static final String LAYOUTMODEL = PACKAGENAME + "LAYOUTMODEL";

//    public static final String APPNAME = PACKAGENAME + "APPNAME";
//    public static final String VERSIONSTRING = PACKAGENAME + "VERSIONSTRING";
//    public static final String DOWNLOADAPKURL = PACKAGENAME + "DOWNLOADAPKURL";
//    public static final String FOREUPDATE = PACKAGENAME + "FOREUPDATE";
//    public static final String ACTIONUPDATE = PACKAGENAME + "ACTIONUPDATE";
//    public static final String SAVELOCALPATH = PACKAGENAME + "SAVELOCALPATH";
//    public static final String DOWNLOADERRORTEXT = PACKAGENAME + "DOWNLOADERRORTEXT";
//    public static final String DOWNLOADSUCCESSTEXT = PACKAGENAME + "DOWNLOADSUCCESSTEXT";
//    public static final String STARTDOWNLOADTEXT = PACKAGENAME + "STARTDOWNLOADTEXT";
//    public static final String DOWNLOADTOINSTALLTEXT = PACKAGENAME + "DOWNLOADTOINSTALLTEXT";
//    public static final String LOGOID = PACKAGENAME + "LOGOID";
//    public static final String LOGOICON = PACKAGENAME + "LOGOICON";

    private static final int TIMEOUT = 10 * 1000;// 超时
    private static final int DOWN_OK = 1;
    private static final int DOWN_ERROR = 0;
    private static final int mDownStep = 1;// 提示step
    private AppInstallReceiver mInstallReceiver;
    ApkUpdateModel mApkUpdateModel;
    LayoutModel mLayoutModel;
    private boolean isFore;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private Intent mUpdateIntent;
    private PendingIntent mPendingIntent;
    long mDownloadCount = 0;// 已经下载好的大小
    private int mNotificationId = 0;
    boolean isFirst = true;
    private String mFileName;
    Map<String, Object> mAppInfo = new HashMap();

    /**
     * 开始运行下载服务
     *
     * @param context
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static void startDownLoadService(Context context, ApkUpdateModel apkBean, LayoutModel layoutModel) {
        Intent intent = new Intent(context, ApkUpdateService.class);
        intent.setAction(ACTIONUPDATE);
        intent.putExtra(APKUPDATEMODEL, apkBean);
        intent.putExtra(LAYOUTMODEL, layoutModel);
        context.startService(intent);
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDownloadCount = 0;
        mAppInfo = PhoneFactory.appInfo(this);
        if (intent != null) {
            mApkUpdateModel = (ApkUpdateModel) intent.getSerializableExtra(APKUPDATEMODEL);
            mLayoutModel = (LayoutModel) intent.getSerializableExtra(LAYOUTMODEL);
            isFore = mApkUpdateModel.getForceUpdate() == 1;
            flags = START_STICKY;
            if (isUpdata == true) {
                return super.onStartCommand(intent, flags, startId);
            }
            mFileName = mAppInfo.get("appName").toString();
            // 创建文件
            FileUtils.createFile(mApkUpdateModel.getSavePath(), mFileName);
            createNotification();
            createThread();
        }

        return super.onStartCommand(intent,
                Service.START_REDELIVER_INTENT, startId);


    }

    /***
     * 开线程下载
     */
    public void createThread() {
        downloadThread(mDownloadCount, mFileName);
    }


    /***
     * 更新UI
     */
    Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_OK:
                    // 下载完成，点击安装
                    mDownloadCount = 0;
                    isUpdata = false;
                    IntentFilter filter = new IntentFilter(
                            ConnectivityManager.CONNECTIVITY_ACTION);
                    filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
                    filter.setPriority(Integer.MAX_VALUE);
                    filter.addDataScheme("package");
                    mInstallReceiver = new AppInstallReceiver(new AppInstallReceiver.InstallComplite() {
                        public void onFinish(String packageName) {
                            // TODO Auto-generated method stub
                            mNotificationManager.cancelAll();
                        }
                    });
                    registerReceiver(mInstallReceiver, filter);
                    String urlpath = FileUtils.updataFilename(mApkUpdateModel.getSavePath(), mFileName);
                    CacheOptFactory.saveDownloadUpdateData(getApplicationContext(), mApkUpdateModel.getVersion(), mApkUpdateModel.getVersionCode(), urlpath);
                    if (isFore) {
                        ForceUpdateReceiver.sendStop(getApplicationContext(), true);
                    }
                    CacheOptFactory.saveForce(getApplicationContext(), isFore);
                    sendSussceNotice(AppInstallReceiver.startInstallApk(getApplicationContext(), urlpath));
                    ApkUpdateService.this.stopSelf();
                    break;
                case DOWN_ERROR:
                    mNotification = getNotification(mApkUpdateModel.getDownloadErrorText(), mApkUpdateModel.getDownloadErrorText());
                    mNotification.flags = Notification.FLAG_AUTO_CANCEL;
                    mNotificationManager.notify(mNotificationId, mNotification);
                    CacheOptFactory.removeDownloadUpdateData(getApplicationContext());
                    isUpdata = false;
                    if (isFore) {
                        ForceUpdateReceiver.sendStop(getApplicationContext(), false);
                    }
                    break;
                default:
                    ApkUpdateService.this.stopSelf();
                    isUpdata = false;
                    if (isFore) {
                        ForceUpdateReceiver.sendStop(getApplicationContext(), false);
                    }
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    public Notification getNotification(String ticker, String contentTitle) {
        Drawable drawable = (Drawable) mAppInfo.get("logo");
        return new Notification.Builder(getApplicationContext())
                .setSmallIcon(Icon.createWithBitmap(FileUtils.drawableToBitmap(drawable)))
                .setTicker(ticker)
                .setContentTitle(contentTitle)
                .setAutoCancel(true)
                .setContentIntent(null)
                .build();

    }

    /**
     * 发送下载成功的通知
     *
     * @param intent
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendSussceNotice(Intent intent) {
        mPendingIntent = PendingIntent.getActivity(
                ApkUpdateService.this, 0, intent, 0);
        mNotification = getNotification(mApkUpdateModel.getDownloadSuccessText(), mApkUpdateModel.getDownloadToInstallText());
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(mNotificationId, mNotification);
    }


    public void downloadThread(final long startcount, final String filename) {
        new Thread(new DownLoadRunnable(startcount, filename)).start();
    }

    /**
     * 下载Runnable
     */
    class DownLoadRunnable implements Runnable {
        String fileName;
        final long mStartCount;

        public DownLoadRunnable(long startCount, String ifileName) {
            mStartCount = startCount;
            this.fileName = FileUtils.updataFilename(mApkUpdateModel.getSavePath(), ifileName);
        }

        @Override
        public void run() {
            try {
                long downloadSize = downloadUpdateFile(mStartCount, mApkUpdateModel.getUrl(), fileName);
                if (downloadSize > 0) {
                    // 下载成功
                    mHandler.obtainMessage(DOWN_OK).sendToTarget();
                } else {
                    mHandler.obtainMessage(DOWN_ERROR).sendToTarget();
                }
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.obtainMessage(DOWN_ERROR).sendToTarget();
            }
        }
    }

    /***
     * 创建通知栏
     */
    RemoteViews mContentView;

    /*
        创建通知栏
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createNotification() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (mNotification == null) {
            mNotification = new NotificationCompat.Builder(getApplicationContext()).build();
        }
        mNotification.icon = android.R.drawable.stat_sys_download; // 这个图标必须要设置，不然下面那个RemoteViews不起作用.
        // // 这个参数是通知提示闪出来的值.
        mNotification.tickerText = mApkUpdateModel.getStartDownloadText();
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
        mNotification.flags |= Notification.FLAG_NO_CLEAR;
        /***
         * 在这里我们用自定的view来显示Notification
         */
        if (mContentView == null) {
            mContentView = new RemoteViews(getPackageName(),
                    mLayoutModel.getLayoutId());
//            mContentView = new Notification.Builder(getApplicationContext()).createContentView();
            sendNotification(0);

        }
        if (isFore) {
            ForceUpdateReceiver.sendStart(getApplicationContext());
        }
        mNotification.contentView = mContentView;
        mNotificationManager.notify(mNotificationId, mNotification);

    }

    /***
     * 下载文件
     *
     * @return
     * @throws MalformedURLException
     */
    @SuppressWarnings("resource")
    public long downloadUpdateFile(long downloadStart, String down_url, String file)
            throws Exception {
        isUpdata = true;
        long totalSize;// 文件总大小
        int updateCount = 0;// 已经上传的文件大小
        InputStream inputStream;
        RandomAccessFile randomAccessFile = null;
        HttpURLConnection httpURLConnection = getDownloadHead(down_url, downloadStart);
        httpURLConnection.connect();
        // 获取下载文件的size
        totalSize = httpURLConnection.getContentLength() + downloadStart;
        if (httpURLConnection.getResponseCode() == 404) {
            throw new Exception("fail!");
        }
        inputStream = httpURLConnection.getInputStream();
        randomAccessFile = new RandomAccessFile(file, "rwd");
        randomAccessFile.seek(downloadStart);

        byte buffer[] = new byte[4096];
        long downsize = downloadStart;
        int readsize = 0;
        while ((readsize = inputStream.read(buffer)) != -1) {
            randomAccessFile.write(buffer, 0, readsize);
            downsize += readsize;// 时时获取下载到的大小
            if (((downsize * 100 / totalSize) - mDownStep) >= updateCount) {
                updateCount = (int) (downsize * 100 / totalSize);
                sendNotification(updateCount);
                if (isFore)
                    ForceUpdateReceiver.sendUpdate(getApplicationContext(), updateCount);
            }
        }

        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
        inputStream.close();
        randomAccessFile.close();
        if (totalSize != downsize) {
            return -1;
        }
        return downsize;

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        isUpdata = false;
        if (mInstallReceiver != null) {
            unregisterReceiver(mInstallReceiver);
        }


    }

    /**
     * 获取下载的头信息
     *
     * @param downUrl
     * @param startCount
     * @return
     * @throws Exception
     */
    public HttpURLConnection getDownloadHead(String downUrl, long startCount) throws Exception {
        // 设置文件开始的下载位置 使用 Range字段设置断点续传
        String start = "bytes=" + startCount + "-";
        URL url = new URL(downUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url
                .openConnection();
        httpURLConnection.setRequestProperty("User-Agent", "NetFox");
        httpURLConnection.setRequestProperty("RANGE", start);
        httpURLConnection.setConnectTimeout(TIMEOUT);
        httpURLConnection.setReadTimeout(TIMEOUT);
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setDoInput(true);
        return httpURLConnection;
    }

    /**
     * 设置通知栏内容
     *
     * @param updateCount
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void sendNotification(int updateCount) {
        Drawable drawable = (Drawable) mAppInfo.get("logo");
        mContentView.setImageViewBitmap(mLayoutModel.getLogoId(), FileUtils.drawableToBitmap(drawable));
        mContentView.setTextViewText(mLayoutModel.getTitleId(), mAppInfo.get("appName").toString());
        mContentView.setTextViewText(mLayoutModel.getProgressId(),
                updateCount + "%");
        mContentView.setProgressBar(mLayoutModel.getPbProgressId(), 100,
                updateCount, false);
        mNotificationManager.notify(mNotificationId, mNotification);
    }


}

