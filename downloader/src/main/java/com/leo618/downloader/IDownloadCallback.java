package com.leo618.downloader;

import java.io.File;

/**
 * function:下载回调
 *
 * <br/>
 * Created by lzj on 2016/3/31.
 */
@SuppressWarnings("ALL")
public interface IDownloadCallback {

    /**
     * 下载开始
     */
    void onStart(Downloader.Task task);

    /**
     * 下载失败
     */
    void onFailure(Downloader.Task task, Exception e);

    /**
     * 下载成功
     */
    void onSuccess(File file);

    /**
     * 下载进度更新
     */
    void onProgressUpdate(long writeSize, long totalSize, boolean completed);
}
