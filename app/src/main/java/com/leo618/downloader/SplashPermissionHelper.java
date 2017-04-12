package com.leo618.downloader;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;

import com.leo618.mpermission.AfterPermissionGranted;
import com.leo618.mpermission.MPermission;
import com.leo618.mpermission.MPermissionSettingsDialog;

import java.util.List;

/**
 * function:
 *
 * <p></p>
 * Created by lzj on 2017/4/11.
 */
@SuppressWarnings("ALL")
public class SplashPermissionHelper implements MPermission.PermissionCallbacks {
    private static final int CODE_REQ_INIT_PERS = 100;
    private SplashActivity mActivity;
    private String[] mPerms;
    private IPermissionCallback mIPermissionCallback;

    public SplashPermissionHelper(SplashActivity activity) {
        this.mActivity = activity;
    }

    public void run(String[] perms, IPermissionCallback callback) {
        mPerms = perms;
        mIPermissionCallback = callback;
        if (mIPermissionCallback == null) {
            throw new IllegalArgumentException("请设置回调");
        }
        if (mPerms == null || mPerms.length == 0) {
            mIPermissionCallback.onPassed();
            return;
        }
        checkInitPermissions();
    }


    /**
     * 在启动页添加进去app必要的权限确认
     */
    @AfterPermissionGranted(CODE_REQ_INIT_PERS)
    private void checkInitPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (MPermission.hasPermissions(mActivity, mPerms)) {
                mIPermissionCallback.onPassed();
            } else {
                MPermission.requestPermissions(mActivity, "请授权以获取更完善的体验", CODE_REQ_INIT_PERS, mPerms);
            }
        } else {
            mIPermissionCallback.onPassed();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (MPermission.somePermissionPermanentlyDenied(mActivity, perms)) {
            new MPermissionSettingsDialog.Builder(mActivity).build().show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //从设置应用详情页返回
        if (requestCode == MPermissionSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            checkInitPermissions();
        }
    }

    public interface IPermissionCallback {
        void onPassed();
    }
}
