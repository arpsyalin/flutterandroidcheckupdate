package com.lyl.flutter_checkupdate.model;

import android.text.TextUtils;

import java.io.Serializable;

public class ApkUpdateModel implements Serializable {
    String url;
    String version;
    int versionCode;
    int forceUpdate;
    String downloadErrorText = "下载失败";
    String downloadSuccessText = "下载成功";
    String startDownloadText = "开始下载";
    String downloadToInstallText = "下载完成，点击安装";
    String savePath;

    public int getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(int forceUpdate) {
        this.forceUpdate = forceUpdate;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        if (!TextUtils.isEmpty(url))
            this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public String getDownloadErrorText() {
        return downloadErrorText;
    }

    public void setDownloadErrorText(String downloadErrorText) {
        if (!TextUtils.isEmpty(downloadErrorText))
            this.downloadErrorText = downloadErrorText;
    }

    public String getDownloadSuccessText() {
        return downloadSuccessText;
    }

    public void setDownloadSuccessText(String downloadSuccessText) {
        if (!TextUtils.isEmpty(downloadSuccessText))
            this.downloadSuccessText = downloadSuccessText;
    }

    public String getStartDownloadText() {
        return startDownloadText;
    }

    public void setStartDownloadText(String startDownloadText) {
        if (!TextUtils.isEmpty(startDownloadText))
            this.startDownloadText = startDownloadText;
    }

    public String getDownloadToInstallText() {
        return downloadToInstallText;
    }

    public void setDownloadToInstallText(String downloadToInstallText) {
        if (!TextUtils.isEmpty(downloadToInstallText))
            this.downloadToInstallText = downloadToInstallText;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        if (!TextUtils.isEmpty(savePath))
            this.savePath = savePath;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
}
