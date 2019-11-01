package com.lyl.flutter_checkupdate.model;
////
//// Created by  lyl
//// on 2019/10/31 0031 

import com.lyl.flutter_checkupdate.R;

import java.io.Serializable;

public class LayoutModel implements Serializable {
    int layoutId = R.layout.notification_item;
    int logoId = R.id.iv_icon;
    int titleId = R.id.tv_title;
    int progressId = R.id.tv_progress;
    int pbProgressId = R.id.pb_progress;

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public int getTitleId() {
        return titleId;
    }

    public void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    public int getProgressId() {
        return progressId;
    }

    public void setProgressId(int progressId) {
        this.progressId = progressId;
    }

    public int getPbProgressId() {
        return pbProgressId;
    }

    public void setPbProgressId(int pbProgressId) {
        this.pbProgressId = pbProgressId;
    }

    public int getLogoId() {
        return logoId;
    }

    public void setLogoId(int logoId) {
        this.logoId = logoId;
    }
}
