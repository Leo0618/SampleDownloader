package com.leo618.downloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leo618.utils.UIUtil;

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

    private int progress;

    public void startDownloadImg(View view) {
        UIUtil.post(new Runnable() {
            @Override
            public void run() {
                if (progress == 100) {
                    UIUtil.removeCallbacksFromMainLooper(this);
                    return;
                }
                progressImg.setProgress(++progress);
                textImg.setText("downloaded: " + progressImg.getProgress() + "%");
                UIUtil.postDelayed(this, 300);
            }
        });

    }
}
