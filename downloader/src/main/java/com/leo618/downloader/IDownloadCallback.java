package com.leo618.downloader;

import java.io.File;

/**
 * function:下载回调
 *
 * <br/>
 * Created by lzj on 2016/3/31.
 */
@SuppressWarnings("ALL")
public abstract class IDownloadCallback {

    /**
     * 下载开始
     */
    public void onStart() {
    }

    /**
     * 下载失败
     */
    public abstract void onFailure(Exception e);

    /**
     * 下载成功
     */
    public abstract void onSuccess(File file);

    /**
     * 下载进度更新
     */
    public void onProgressUpdate(long writeSize, long totalSize, boolean completed) {
    }
}
