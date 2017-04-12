package com.leo618.downloader;

import android.app.Application;

import com.leo618.utils.AndroidUtilsCore;

/**
 * function:
 *
 * <p></p>
 * Created by lzj on 2017/4/11.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidUtilsCore.install(this);
    }
}
