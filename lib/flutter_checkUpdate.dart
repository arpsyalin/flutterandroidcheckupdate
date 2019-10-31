import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

class FlutterCheckUpdate {
  ///已经下载了只需要去安装
  static const isDownloadInstall = 0;

  ///有新的版本需要更新
  static const haveUpdate = 1;

  ///版本号比当前的要低
  static const noUpdate = 2;

  ///versionCode或者versionName传入有问题
  static const versionError = 3;

  ///需要下载进度更新的添加
  static const force = 1;

  String get channelName => "com.lyl.flutter_checkupdate";

  String get eventName => "com.lyl.flutter_checkupdate.download";

  MethodChannel _methodChannel;
  EventChannel _eventChannel;

  FlutterCheckUpdate() {
    _methodChannel = MethodChannel(channelName);
    _eventChannel = EventChannel(eventName);
  }

  ///一天内只会返回一次true
  needAutoCheckUpdate() async {
    if (Platform.isAndroid) {
      var result = await _methodChannel.invokeMethod("needAutoCheckUpdate");
      return result;
    }
  }

  ///用versionCode去判断是否是需要更新 返回0，1，2，3
  checkVersionCode({versionCode = 0}) async {
    if (Platform.isAndroid) {
      var result = await _methodChannel.invokeMethod(
          "checkVersionCode", <String, dynamic>{"versionCode": versionCode});
      return result;
    }
  }

  ///用versionName去判断是否是需要更新 返回0，1，2，3
  checkUpdate(version) async {
    if (Platform.isAndroid) {
      var result = await _methodChannel
          .invokeMethod("checkUpdate", <String, dynamic>{"version": version});
      return result;
    }
  }

  ///已经下载好最新版本直接去安装
  toInstallApk() async {
    if (Platform.isAndroid) {
      var result =
          await _methodChannel.invokeMethod("toInstall", <String, dynamic>{});
      return result;
    }
  }

  ///去下载apk
  toDownloadApk(saveDir, url, version, versionCode, force,
      {loginId = 0,
      loginIcon,
      downloadToInstallText,
      startDownloadText,
      downloadSuccessText,
      downloadErrorText,
      pbProgressId,
      progressId,
      titleId,
      layoutId,
      startCallback,
      progressCallback,
      endCallback}) async {
    if (Platform.isAndroid) {
      _eventChannel.receiveBroadcastStream().listen((event) {
        if (event["status"] == 0) {
          if (startCallback != null) startCallback();
        }
        if (event["status"] == 1) {
          if (progressCallback != null) progressCallback(event["count"]);
        }
        if (event["status"] == 2) {
          if (endCallback != null) endCallback(event["result"]);
        }
      });
      var result =
          await _methodChannel.invokeMethod("toDownload", <String, dynamic>{
        "loginId": loginId,
        "loginIcon": loginIcon,
        "downloadToInstallText": downloadToInstallText,
        "startDownloadText": startDownloadText,
        "downloadSuccessText": downloadSuccessText,
        "downloadErrorText": downloadErrorText,
        'path': saveDir,
        "url": url,
        "version": version,
        "versionCode": versionCode,
        "forceUpdate": force,
        "layoutId": layoutId,
        "progressId": progressId,
        "titleId": titleId,
        "pbProgressId": pbProgressId
      });
      return result;
    }
  }
}
