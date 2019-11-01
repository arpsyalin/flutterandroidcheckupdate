# flutter_checkupdate

一款flutter更新的插件，只有Android端因为ios审核不允许包含版本更新不支持ios

## Getting Started
///根据VersionCode比对版本VersionCode从你需要从你的服务器获取后传入

var result =await FlutterCheckUpdate().checkVersionCode(versionCode: 2); 
    setState(() {
      if (result == FlutterCheckUpdate.noUpdate) {
        ///"没有版本需要更新";
      }
      if (result == FlutterCheckUpdate.haveUpdate) {
        /// "有新版本需要更新";
      }
      if (result == FlutterCheckUpdate.isDownloadInstall) {
           /// "新版本已经下载可以调用FlutterCheckUpdate().toInstallApk()";
        FlutterCheckUpdate().toInstallApk();
      }
     });
     
///根据VersionName比对版本VersionName从你需要从你的服务器获取后传入
///如果本来软件版本是1.0 传入1.0.1得到返回结果会是需要更新 如果软件版本是1.1，传入1.0.1返回结果将会是不需要更新

var result = await FlutterCheckUpdate().checkUpdate("1.0.1"); 
      setState(() {
        if (result == FlutterCheckUpdate.noUpdate) {
          ///"没有版本需要更新";
        }
        if (result == FlutterCheckUpdate.haveUpdate) {
          ///"有新版本需要更新";
        }
        if (result == FlutterCheckUpdate.isDownloadInstall) {
          ///"新版本已经下载可以调用FlutterCheckUpdate().toInstallApk()"; 
         FlutterCheckUpdate().toInstallApk();
        }   
       });
       
///去下载apk带进度，下载的apk需要跟自己apk签名包名相同才能覆盖安装成功
///force参数不为FlutterCheckUpdate.force 设置startCallback progressCallback endCallback将无返回值

FlutterCheckUpdate().toDownloadApk(
        "/lyl/checkupdate/","https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk",
        force: FlutterCheckUpdate.force,
        version: "1.0.1",
        versionCode: 2, 
        startCallback: () {
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
        }
       );
 
