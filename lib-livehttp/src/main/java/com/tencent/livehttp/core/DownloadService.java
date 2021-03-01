package com.tencent.livehttp.core;

import android.content.Intent;
import android.util.ArrayMap;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleService;
import java.util.Map;

/**
 * Author：岑胜德 on 2021/1/25 17:02
 *
 * 说明：
 */
public final class DownloadService extends LifecycleService {


    @NonNull
    private static final Map<String, DownloadRecord> sRecordMap = new ArrayMap<>();

    static void sendCommand(String recordId, DownloadCommand cmd) {
//        sCommandChannel.setValue(cmd);
        sRecordMap.get(recordId).status=DownloadStatus.ON_ERROR;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    /*每次开启一个任务*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sRecordMap.clear();
    }
}
