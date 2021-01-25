package com.tencent.lib.http;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

/**
 * Author：岑胜德 on 2021/1/25 17:57
 *
 * 说明：
 */
public interface OnDownloadListener {
    @UiThread
    void onStarted();
    @UiThread
    void onDownloading(DownloadRecord record,int progress);
    @UiThread
    void onFinished(@NonNull DownloadRecord record);
    @UiThread
    void onError(@NonNull DownloadRecord record);

}
