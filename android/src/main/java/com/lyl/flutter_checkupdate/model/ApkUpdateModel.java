package com.lyl.flutter_checkupdate.model;

import android.text.TextUtils;

import java.io.Serializable;

public class ApkUpdateModel implements Serializable {
    String id;
    String appName;
    String url;
    String size;
    String createTime;
    String description;
    String version;
    int versionCode;
    int forceUpdate;
    String downloadErrorText = "下载失败";
    String downloadSuccessText = "下载成功";
    String startDownloadText = "开始下载";
    String downloadToInstallText = "下载完成，点击安装";
    int logoId;
    byte[] logoIcon;
    String savePath;

    public String getAppName() {
        return appName;
    }

    public int getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(int forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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

    public void setAppName(String appName) {
        if (!TextUtils.isEmpty(appName))
            this.appName = appName;
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

    public int getLogoId() {
        return logoId;
    }

    public void setLogoId(int logoId) {
        if (logoId != 0)
            this.logoId = logoId;
    }

    public String getDownloadToInstallText() {
        return downloadToInstallText;
    }

    public void setDownloadToInstallText(String downloadToInstallText) {
        if (!TextUtils.isEmpty(downloadToInstallText))
            this.downloadToInstallText = downloadToInstallText;
    }

    public byte[] getLogoIcon() {
        return logoIcon;
    }

    public void setLogoIcon(byte[] logoIcon) {
        if (logoIcon != null)
            this.logoIcon = logoIcon;
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
