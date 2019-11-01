package com.lyl.flutter_checkupdate;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.lyl.flutter_checkupdate.cache.factory.CacheOptFactory;
import com.lyl.flutter_checkupdate.model.ApkUpdateModel;
import com.lyl.flutter_checkupdate.model.LayoutModel;
import com.lyl.flutter_checkupdate.receiver.AppInstallReceiver;
import com.lyl.flutter_checkupdate.receiver.ForceUpdateReceiver;
import com.lyl.flutter_checkupdate.service.ApkUpdateService;
import com.lyl.flutter_checkupdate.tools.FileUtils;
import com.lyl.flutter_checkupdate.tools.PhoneFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterCheckupdatePlugin
 */
public class FlutterCheckupdatePlugin implements MethodCallHandler {

    PluginRegistry.Registrar mRegistrar;

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "com.lyl.flutter_checkupdate");
        channel.setMethodCallHandler(new FlutterCheckupdatePlugin(registrar));
    }

    final EventChannel.EventSink[] eventSink = {null};

    public FlutterCheckupdatePlugin(PluginRegistry.Registrar registrar) {
        this.mRegistrar = registrar;
        EventChannel eventChannel =
                new EventChannel(
                        mRegistrar.messenger(), "com.lyl.flutter_checkupdate.download");
        eventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink sink) {
                eventSink[0] = sink;
            }

            @Override
            public void onCancel(Object o) {
                eventSink[0] = null;
            }
        });
    }

    enum MethodFunction {
        checkVersionCode,
        needAutoCheckUpdate,
        checkUpdate,
        toDownload,
        toInstall,
    }

    @Override
    public void onMethodCall(MethodCall methodCall, Result result) {
        MethodFunction methodFunction = MethodFunction.valueOf(methodCall.method);
        switch (methodFunction) {
            case checkVersionCode:
                CacheOptFactory.saveAutoCheckUpdate(mRegistrar.context());
                Object versionCode = methodCall.argument("versionCode");
                if (versionCode != null) {
                    result.success(checkVersionCode(mRegistrar.context(), Integer.parseInt(versionCode.toString())));
                } else {
                    result.success(3);
                }
                break;
            case checkUpdate:
                CacheOptFactory.saveAutoCheckUpdate(mRegistrar.context());
                Object versionName = methodCall.argument("version");
                if (versionName != null) {
                    result.success(checkVersion(mRegistrar.context(), versionName.toString()));
                } else {
                    result.success(3);
                }
                break;
            case needAutoCheckUpdate:
                result.success(CacheOptFactory.needAutoCheckUpdate(mRegistrar.context()));
                break;
            case toInstall:
                String downloadPath = CacheOptFactory.getDownloadUpdateUrl(mRegistrar.context());
                if (!TextUtils.isEmpty(downloadPath)) {
                    AppInstallReceiver.toInstallApk(mRegistrar.context(), new File(downloadPath));
                    result.success(true);
                } else {
                    result.success(false);
                }
                break;
            case toDownload:
                ApkUpdateModel apkUpdateModel = new ApkUpdateModel();
                Object url = methodCall.argument("url");
                Object version = methodCall.argument("version");
                Object vcode = methodCall.argument("versionCode");
                Object forceUpdate = methodCall.argument("forceUpdate");
                Object path = methodCall.argument("path");
                Object downloadErrorText = methodCall.argument("downloadErrorText");
                Object downloadSuccessText = methodCall.argument("downloadSuccessText");
                Object startDownloadText = methodCall.argument("startDownloadText");
                Object downloadToInstallText = methodCall.argument("downloadToInstallText");
                Map map = PhoneFactory.appInfo(mRegistrar.context());
                if (map.get("appName") != null)
                    apkUpdateModel.setAppName(map.get("appName").toString());
                if (url != null)
                    apkUpdateModel.setUrl(url.toString());
                if (version != null)
                    apkUpdateModel.setVersion(version.toString());
                if (vcode != null) {
                    apkUpdateModel.setVersionCode((Integer) vcode);
                }
                if (forceUpdate != null)
                    apkUpdateModel.setForceUpdate((int) forceUpdate);
                if (path != null)
                    apkUpdateModel.setSavePath(path.toString());
                if (downloadErrorText != null)
                    apkUpdateModel.setDownloadErrorText(downloadErrorText.toString());
                if (downloadSuccessText != null)
                    apkUpdateModel.setDownloadSuccessText(downloadSuccessText.toString());
                if (startDownloadText != null)
                    apkUpdateModel.setDownloadToInstallText(startDownloadText.toString());
                if (downloadToInstallText != null)
                    apkUpdateModel.setStartDownloadText(downloadToInstallText.toString());
                LayoutModel mLayoutModel = new LayoutModel();
                Object layoutId = methodCall.argument("layoutId");
                Object titleId = methodCall.argument("titleId");
                Object progressId = methodCall.argument("progressId");
                Object pbProgressId = methodCall.argument("pbProgressId");
                if (layoutId != null) {
                    mLayoutModel.setLayoutId((Integer) layoutId);
                }
                if (titleId != null) {
                    mLayoutModel.setTitleId((Integer) titleId);
                }
                if (progressId != null) {
                    mLayoutModel.setProgressId((Integer) progressId);
                }
                if (pbProgressId != null) {
                    mLayoutModel.setPbProgressId((Integer) pbProgressId);
                }

                if (apkUpdateModel.getUrl().indexOf(".apk") > -1) {
                    ForceUpdateReceiver mForceUpdateReceiver = new ForceUpdateReceiver();
                    if (apkUpdateModel.getForceUpdate() == 1) {
                        mForceUpdateReceiver.setForceUpdateListener(new ForceUpdateReceiver.ForceUpdateListener() {
                            @Override
                            public void onUpdateStart() {
                                Map<String, Object> map = new HashMap<>();
                                map.put("status", 0);
                                eventSink[0].success(map);
                            }

                            @Override
                            public void onUpdating(int progress) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("status", 1);
                                map.put("progress", progress);
                                eventSink[0].success(map);
                            }

                            @Override
                            public void onStop(boolean isSuccess) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("status", 2);
                                map.put("result", isSuccess);
                                eventSink[0].success(map);
                            }
                        });
                    }
                    ForceUpdateReceiver.registerForceUpdateReceiver(mRegistrar.context(), map.get("packageName").toString(), mForceUpdateReceiver);
                    ApkUpdateService.startDownLoadService(mRegistrar.context(), apkUpdateModel, mLayoutModel);
                } else {
                    result.success(false);
                }
                break;
        }
    }


    public int checkVersion(Context context, String newVersion) {
        if (PhoneFactory.compareVersion(context, newVersion) > 0) {
            String dVersion = CacheOptFactory.getDownloadUpdateVersion(context);
            String downloadPath = CacheOptFactory.getDownloadUpdateUrl(context);
            File file = new File(downloadPath);
            if (!TextUtils.isEmpty(dVersion) && PhoneFactory.compareVersion(context, newVersion, dVersion) == 0 && file.exists()) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 2;
        }
    }

    public int checkVersionCode(Context context, int versionCode) {
        if (PhoneFactory.getAppVersionCode(context) < versionCode) {
            int dVersion = CacheOptFactory.getDownloadUpdateVersionCode(context);
            String downloadPath = CacheOptFactory.getDownloadUpdateUrl(context);
            File file = new File(downloadPath);
            if (dVersion != 0 && dVersion == versionCode && file.exists()) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 2;
        }
    }
}
