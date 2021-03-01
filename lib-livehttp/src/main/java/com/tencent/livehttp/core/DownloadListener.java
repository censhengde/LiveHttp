package com.tencent.livehttp.core;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

/**
 * Author：岑胜德 on 2021/1/25 17:57
 *
 * 说明：
 */
public interface DownloadListener {
    @UiThread
    void onStarted();
    @UiThread
    void onLoading(@NonNull DownloadRecord record,int progress);
    @UiThread
    void onFinished(@NonNull DownloadRecord record);
    @UiThread
    void onError(@NonNull DownloadRecord record);

}
