import 'package:flutter/material.dart';
import 'package:flutter_checkupdate/flutter_checkUpdate.dart';
import 'package:permission_handler/permission_handler.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  ///传入的VersionName VersionCode都需要从自己的服务器获取
  var _checkVersionNameResult = "当前Apk的VersionName:1.0 传入VersionName的1.0.1";
  var _checkVersionCodeResult = "当前Apk的VersionCode是1 传入VersionCode是2";
  var _downloadApkProgress = "下载带下载进度";

  @override
  void initState() {
    super.initState();
    requestStoragePermission(context, () {
      ///必须要有存储文件的权限
      print("同意 ");
    }, () {
      ///如果不同意可以退出
      ///
      print("不同意 ");
    });
  }

  @override
  Widget build(BuildContext context) {
//    initPlatformState();
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: ListView(
          children: <Widget>[
            Text(_checkVersionNameResult),
            MaterialButton(
                color: Colors.blue,
                child: Text("通过VersionName检查是否需要更新"),
                onPressed: () async {
                  print("22222");
                  var result = await FlutterCheckUpdate().checkUpdate("1.0.1");
                  print(result);
                  setState(() {
                    if (result == FlutterCheckUpdate.noUpdate) {
                      _checkVersionNameResult = "没有版本需要更新";
                    }
                    if (result == FlutterCheckUpdate.haveUpdate) {
                      _checkVersionNameResult = "有新版本需要更新";
                    }

                    if (result == FlutterCheckUpdate.isDownloadInstall) {
                      _checkVersionNameResult =
                          "新版本已经下载可以调用FlutterCheckUpdate().toInstallApk()";
                    }
                  });
                }),
            Text(_checkVersionCodeResult),
            MaterialButton(
              color: Colors.blue,
              child: Text("通过VersionCode检查是否需要更新"),
              onPressed: () async {
                print("111");
                var result =
                    await FlutterCheckUpdate().checkVersionCode(versionCode: 2);
                print(result);
                setState(() {
                  if (result == FlutterCheckUpdate.noUpdate) {
                    _checkVersionCodeResult = "没有版本需要更新";
                  }
                  if (result == FlutterCheckUpdate.haveUpdate) {
                    _checkVersionCodeResult = "有新版本需要更新";
                  }

                  if (result == FlutterCheckUpdate.isDownloadInstall) {
                    _checkVersionCodeResult =
                        "新版本已经下载可以调用FlutterCheckUpdate().toInstallApk()";
                  }
                });
              },
            ),
            Text(_downloadApkProgress),
            MaterialButton(
              color: Colors.blue,
              child: Text("下载apk带进度，下载的apk需要跟自己apk签名包名相同才能安装成功"),
              onPressed: () {
                print("333");
                FlutterCheckUpdate().toDownloadApk("/lyl/checkupdate/",
                    "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk",
                    force: FlutterCheckUpdate.force,
                    version: "1.0.1",
                    versionCode: 2, startCallback: () {
                  setState(() {
                    _downloadApkProgress = "开始下载";
                  });
                }, progressCallback: (progress) {
                  setState(() {
                    _downloadApkProgress = progress.toString() + "%";
                  });
                }, endCallback: (result) {
                  setState(() {
                    _downloadApkProgress = result == true ? "下载成功" : "下载失败";
                  });
                });
              },
            )
          ],
        ),
      ),
    );
  }

  static requestStoragePermission(
      BuildContext context, okCallBack, cancelCallBack) async {
    // 申请权限
    if (Theme.of(context).platform == TargetPlatform.android) {
      await PermissionHandler()
          .requestPermissions([PermissionGroup.storage]);
      // 申请结果  权限检测
      PermissionStatus permission = await PermissionHandler()
          .checkPermissionStatus(PermissionGroup.storage);

      if (permission == PermissionStatus.granted) {
        okCallBack();
      } else {
        cancelCallBack();
      }
    }
  }
}
