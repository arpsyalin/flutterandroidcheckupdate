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

  String get channelName => "com.lyl.flutter_checkupdate";

  String get eventName => "com.lyl.flutter_checkupdate.download";

  MethodChannel _methodChannel;
  EventChannel _eventChannel;

  FlutterCheckUpdate() {
    _methodChannel = MethodChannel(channelName);
    _eventChannel = EventChannel(eventName);
  }

  needAutoCheckUpdate() async {
    if (Platform.isAndroid) {
      var result = await _methodChannel.invokeMethod("needAutoCheckUpdate");
      return result;
    }
  }

  checkVersionCode({versionCode = 0}) async {
    if (Platform.isAndroid) {
      var result = await _methodChannel.invokeMethod(
          "checkVersionCode", <String, dynamic>{"versionCode": versionCode});
      return result;
    }
  }

  checkUpdate(version) async {
    if (Platform.isAndroid) {
      var result = await _methodChannel
          .invokeMethod("checkUpdate", <String, dynamic>{"version": version});
      return result;
    }
  }

  toInstallApk() async {
    if (Platform.isAndroid) {
      var result =
          await _methodChannel.invokeMethod("toInstall", <String, dynamic>{});
      return result;
    }
  }

  toDownloadApk(saveDir, url, version, force,
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
