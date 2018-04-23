package com.leo618.downloader;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.URLUtil;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

/**
 * function:简易下载器
 *
 * <p></p>
 * Created by lzj on 2017/4/12.
 */
@SuppressWarnings("ALL")
public final class Downloader {
    private static volatile AtomicReference<Downloader> INSTANCE = new AtomicReference<>();
    private        Context         mContext;
    private static DownloadManager mDownloadManager;
    private Map<Long, Task> mTaskMap = new HashMap<>();

    public static Downloader getInstance(Context context) {
        for(; ; ) {
            Downloader manager = INSTANCE.get();
            if(manager != null) return manager;
            manager = new Downloader(context);
            if(INSTANCE.compareAndSet(null, manager)) return manager;
        }
    }

    private Downloader(Context context) {
        mContext = context;
        mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    /**
     * 是否开启打印log,默认开启
     */
    public static void debug(boolean debug) {
        DownloadLog.config(debug);
    }

    /**
     * 下载一个任务
     *
     * @param task see {@link Task}
     */
    public void download(Task task) {
        if(task == null) return;
        DownloadLog.d("downloadURL="+task.downloadURL);
        DownloadLog.d("downloadFilePath="+task.downloadFilePath);
        if(!URLUtil.isNetworkUrl(task.downloadURL)) {
            task.mIDownloadCallback.onFailure(task, new IllegalArgumentException("downloadURL illegal."));
            return;
        }
        if(TextUtils.isEmpty(task.downloadFilePath)) {
            task.mIDownloadCallback.onFailure(task, new IllegalArgumentException("downloadFilePath illegal."));
            return;
        }
        task.startMe();
        mTaskMap.put(task.getDownloadId(), task);
    }

    /**
     * 下载多个任务
     *
     * @param tasks see {@link Task}
     */
    public void download(Task[] tasks) {
        if(tasks == null) return;
        for(Task task : tasks) {
            download(task);
        }
    }

    /**
     * 取消一个下载任务
     *
     * @param downloadId 下载任务的ID
     */
    public void cancel(long downloadId) {
        if(downloadId == -1) {
            DownloadLog.i("downloadId is -1");
            return;
        }
        Task task = mTaskMap.get(downloadId);
        if(!mTaskMap.containsKey(downloadId) || task == null) {
            DownloadLog.i("no download at this downloadId");
            return;
        }
        mTaskMap.remove(downloadId);
        task.cancelMe();
        DownloadLog.d("has canceled task. downloadId="+downloadId);
    }

    /**
     * 取消多个下载任务
     *
     * @param downloadIds 下载任务的ID数组
     */
    public void cancel(long[] downloadIds) {
        if(downloadIds == null) return;
        for(long downloadId : downloadIds) {
            cancel(downloadId);
        }
    }

    /**
     * 一个下载任务<br/>
     * 必须至少设置{@link Task#downloadURL}、{@link Task#downloadFilePath}、{@link Task#mIDownloadCallback}
     */
    public static class Task {
        /** 下载链接 */
        String            downloadURL;
        /** 下载完成后文件存放地址 */
        String            downloadFilePath;
        /** 下载回调 */
        IDownloadCallback mIDownloadCallback;
        long downloadId = -1;

        private boolean             allowScan               = true;
        private int                 mNotificationVisibility = NOTIFICATION_VISIBLE;
        private Map<String, String> headersMap              = new HashMap<>();
        private CharSequence mTitle, mDescription;
        private String mMimeType;
        private boolean onlyInWifiDownload, mIsVisibleInDownloadsUi, deleteExist = true;

        /**
         * 设置下载链接
         */
        public Downloader.Task setDownloadUrl(String url) {
            this.downloadURL = url;
            return this;
        }

        /**
         * 设置下载文件本地存放全路径
         */
        public Downloader.Task setDownloadFilePath(String downloadFilePath) {
            this.downloadFilePath = downloadFilePath;
            return this;
        }

        /**
         * 设置下载回调
         */
        public Downloader.Task setDownloadCallback(IDownloadCallback callback) {
            this.mIDownloadCallback = callback;
            return this;
        }

        /**
         * 是否允许被系统扫描到
         */
        public Downloader.Task setAllowScan(boolean allowScan) {
            this.allowScan = allowScan;
            return this;
        }

        /**
         * 通知栏对下载任务的显示设置，可使用以下几个状态：<br/>
         * 仅下载时 可见：{@link Downloader#NOTIFICATION_VISIBLE}<br/>
         * 下载中、下载完成 均可见：{@link Downloader#NOTIFICATION_VISIBLE_NOTIFY_COMPLETED}<br/>
         * 不可见：{@link Downloader#NOTIFICATION_HIDDEN} , 该状态需要添加权限： android.permission.DOWNLOAD_WITHOUT_NOTIFICATION<br/>
         * 仅下载完成后：{@link Downloader#NOTIFICATION_VISIBLE_NOTIFY_ONLY_COMPLETION}<br/>
         * 默认是 仅下载时可见：{@link Downloader#NOTIFICATION_VISIBLE}<br/>
         */
        public Downloader.Task setNotificationVisibility(int visibility) {
            mNotificationVisibility = visibility;
            return this;
        }

        /**
         * 添加头信息
         */
        public Downloader.Task addHeader(String header, String value) {
            headersMap.put(header, value);
            return this;
        }

        /**
         * 设置下载时通知栏通知的标题
         */
        public Downloader.Task setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        /**
         * 设置下载时通知栏通知的描述
         */
        public Downloader.Task setDescription(CharSequence description) {
            mDescription = description;
            return this;
        }

        /**
         * 设置下载文件的mime类型<br/>
         * 举例：如果是下载apk文件，设置mimeType为：application/vnd.android.package-archive
         */
        public Downloader.Task setMimeType(String mimeType) {
            mMimeType = mimeType;
            return this;
        }

        /**
         * 仅允许在WiFi网络下进行下载
         */
        public Downloader.Task setOnlyInWifiDownload() {
            onlyInWifiDownload = true;
            return this;
        }

        /**
         * 在系统下载列表中显示任务
         */
        public Downloader.Task showInDownloadsUi() {
            mIsVisibleInDownloadsUi = true;
            return this;
        }

        /**
         * 不删除下载过的已有文件
         */
        public Downloader.Task notDeleteExist() {
            this.deleteExist = false;
            return this;
        }

        /**
         * 当前下载任务的下载ID，需要在开启下载后获取，即调用 {@link Downloader#download(com.leo618.downloader.Downloader.Task)}后
         */
        public long getDownloadId() {
            return downloadId;
        }

        private Timer     mTimer;
        private TimerTask mTimerTask;
        private android.os.Handler mHandler = new Handler(android.os.Looper.getMainLooper());
        private volatile long downloadedSize, totalSize;

        @SuppressWarnings("ResultOfMethodCallIgnored")
        DownloadManager.Request create() {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadURL));
            File                    file    = new File(downloadFilePath);
            if(deleteExist) file.delete();
            request.setDestinationUri(Uri.fromFile(file));
            if(allowScan) request.allowScanningByMediaScanner();
            request.setNotificationVisibility(mNotificationVisibility);
            for(Map.Entry<String, String> entry : headersMap.entrySet()) {
                request.addRequestHeader(entry.getKey(), entry.getValue());
            }
            request.setTitle(mTitle);
            request.setDescription(mDescription);
            request.setMimeType(mMimeType);
            if(onlyInWifiDownload) request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            request.setVisibleInDownloadsUi(mIsVisibleInDownloadsUi);
            DownloadLog.i("has created a download request.");
            return request;
        }

        void startMe() {
            try {
                downloadId = mDownloadManager.enqueue(create());
                DownloadLog.i("start a task. id="+downloadId);
                mIDownloadCallback.onStart(this);
                final DownloadManager.Query query = new DownloadManager.Query();
                mTimer = new Timer();
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        Cursor cursor = mDownloadManager.query(query.setFilterById(getDownloadId()));
                        if(cursor != null && cursor.moveToFirst()) {
                            if(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                mTimerTask.cancel();
                                mTimer.purge();
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        DownloadLog.d("the download has successfully completed. id="+downloadId);
                                        mIDownloadCallback.onProgress(totalSize, totalSize, "100%");
                                        mIDownloadCallback.onSuccess(new File(downloadFilePath));
                                        mHandler.removeCallbacksAndMessages(mProgressUpdateRunnable);
                                    }
                                });
                                return;
                            }
                            downloadedSize = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            totalSize = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                            if(totalSize > 0) mHandler.post(mProgressUpdateRunnable);
                        }
                        if(cursor != null) cursor.close();
                    }
                };
                mTimer.schedule(mTimerTask, 0, 1000);
            } catch(Exception e) {
                mIDownloadCallback.onFailure(this, e);
                DownloadLog.e("startme has Exception: "+e.getMessage());
            }
        }

        private Runnable mProgressUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                String percent = String.valueOf(downloadedSize*100/totalSize)+"%";
                DownloadLog.d("download progress update : downloadedSize="+downloadedSize+" ,totalSize="+totalSize+" ,percent="+percent);
                mIDownloadCallback.onProgress(downloadedSize, totalSize, percent);
            }
        };

        void cancelMe() {
            mDownloadManager.remove(getDownloadId());
        }
    }


    /**
     * 仅下载时 可见
     */
    public static final int NOTIFICATION_VISIBLE = 0;

    /**
     * 下载中、下载完成 可见
     */
    public static final int NOTIFICATION_VISIBLE_NOTIFY_COMPLETED = 1;

    /**
     * 不可见 ,需要添加权限   android.permission.DOWNLOAD_WITHOUT_NOTIFICATION
     */
    public static final int NOTIFICATION_HIDDEN = 2;

    /**
     * 仅下载完成后 可见
     */
    public static final int NOTIFICATION_VISIBLE_NOTIFY_ONLY_COMPLETION = 3;

}
