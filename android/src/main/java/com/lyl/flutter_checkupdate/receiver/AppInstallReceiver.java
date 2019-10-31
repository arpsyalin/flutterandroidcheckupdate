package com.lyl.flutter_checkupdate.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.io.File;

import androidx.core.content.FileProvider;


public class AppInstallReceiver extends BroadcastReceiver {
    InstallComplite installComplite;

    public AppInstallReceiver(InstallComplite installComplite) {
        super();
        this.installComplite = installComplite;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            installComplite.onFinish(packageName);
        } else if (intent.getAction().equals(
                "android.net.conn.CONNECTIVITY_CHANGE")) {

        }

    }

    public interface InstallComplite {
        void onFinish(String packageName);
    }

    public static Intent startInstallApk(Context context, String url) {
        Uri uri = Uri.fromFile(new File(url));
        String packageName = context.getPackageName();
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, packageName + ".fileprovider", new File(url));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        return intent;
    }


    public static void toInstallApk(Context context, File file) {
        Uri uri = Uri.fromFile(file);
        String packageName = context.getPackageName();
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, packageName + ".fileprovider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
	/*
		intent.setDataAndType(uri,
				"application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);*/
    }

    public static void registerAppInstallReceiver(Context context, InstallComplite installComplite) {

    }
}