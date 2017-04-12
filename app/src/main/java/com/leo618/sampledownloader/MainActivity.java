package com.leo618.sampledownloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leo618.downloader.Downloader;
import com.leo618.downloader.IDownloadCallback;
import com.leo618.utils.FileStorageUtil;
import com.leo618.utils.LogUtil;
import com.leo618.utils.SignUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressImg;
    private TextView textImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.textImg = (TextView) findViewById(R.id.textImg);
        this.progressImg = (ProgressBar) findViewById(R.id.progressImg);
    }

    public void cancelDownloadImg(View view) {
        Downloader.getInstance(getApplicationContext()).cancel(downloadId);
    }

    private long downloadId = -1;

    public void startDownloadImg(View view) {
//        String url = "https://raw.githubusercontent.com/Leo0618/api/master/img_ad.png";
        String url = "https://raw.githubusercontent.com/Leo0618/api/master/splash_video.mp4";
        String filePath = FileStorageUtil.getPictureDirPath() + SignUtil.md5(url) + ".mp4";

        Downloader.Task downloadTask = new Downloader.Task()
                .setDownloadUrl(url)            //下载链接
                .setDownloadFilePath(filePath)  //文件全路径
                .setDownloadCallback(mCallback) //下载回调
                .notDeleteExist()               //不删除已有旧文件
                .setNotificationVisibility(Downloader.NOTIFICATION_VISIBLE_NOTIFY_COMPLETED)//通知栏通知隐藏
                .setTitle("下载标题")            //通知栏显示通知的标题
                .setDescription("下载描述内容")  //通知栏显示通知的描述
                .setAllowScan(true)             //允许被系统外部扫描到
                .showInDownloadsUi();           //在系统下载列表中显示
        Downloader.getInstance(getApplicationContext()).download(downloadTask);
        downloadId = downloadTask.getDownloadId();
    }

    private IDownloadCallback mCallback = new IDownloadCallback() {
        @Override
        public void onStart(Downloader.Task task) {
            LogUtil.e("leo", "start download task:" + task);
        }

        @Override
        public void onFailure(Downloader.Task task, Exception e) {
            LogUtil.e("leo", "e:" + (e == null ? "null" : e.getMessage()));
        }

        @Override
        public void onSuccess(File file) {
            LogUtil.e("leo", "file=" + file.getAbsolutePath());
        }

        @Override
        public void onProgressUpdate(long writeSize, long totalSize, boolean completed) {
            LogUtil.e("leo", "writeSize=" + writeSize + " ,totalSize=" + totalSize + " ,completed=" + completed);
        }
    };

}
