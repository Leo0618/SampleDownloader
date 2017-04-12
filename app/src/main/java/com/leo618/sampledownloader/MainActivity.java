package com.leo618.sampledownloader;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leo618.downloader.Downloader;
import com.leo618.downloader.IDownloadCallback;
import com.leo618.utils.FileStorageUtil;
import com.leo618.utils.LogUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressImg;
    private TextView textImg;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.textImg = (TextView) findViewById(R.id.textImg);
        this.progressImg = (ProgressBar) findViewById(R.id.progressImg);
        this.img = (ImageView) findViewById(R.id.img);
    }

    private long downloadId = -1;

    public void cancelDownloadImg(View view) {
        Downloader.getInstance(getApplicationContext()).cancel(downloadId);
    }

    public void startDownloadImg(View view) {
        String url = "https://raw.githubusercontent.com/Leo0618/api/master/img_ad.png";
//        String url = "https://raw.githubusercontent.com/Leo0618/api/master/splash_video.mp4";
        String[] fileNameInUrl = getFileNameInUrl(url);
        String filePath = FileStorageUtil.getPictureDirPath() + fileNameInUrl[0] + fileNameInUrl[1];

        Downloader.Task downloadTask = new Downloader.Task()
                .setDownloadUrl(url)            //下载链接
                .setDownloadFilePath(filePath)  //文件全路径
                .setDownloadCallback(mCallback) //下载回调
                //以上三个参数必须设置
                .notDeleteExist()               //不删除已有旧文件
                .setNotificationVisibility(Downloader.NOTIFICATION_HIDDEN)//通知栏通知隐藏
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
            if (img != null) img.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        }

        @Override
        public void onProgress(long writeSize, long totalSize, String percent) {
            LogUtil.e("leo", "writeSize=" + writeSize + " ,totalSize=" + totalSize + " ,percent=" + percent);
            if (progressImg == null || textImg == null) return;
            progressImg.setMax((int) totalSize);
            progressImg.setProgress((int) writeSize);
            textImg.setText("downloaded: " + percent);
        }
    };

    private static String[] getFileNameInUrl(String url) {
        String[] result = new String[2];
        int lastIndexOfPoint = url.lastIndexOf(".");
        result[0] = url.substring(url.lastIndexOf("/") + 1, lastIndexOfPoint);
        result[1] = url.substring(lastIndexOfPoint, url.length());
        return result;
    }
}
