package com.tencent.lib.http;

import android.content.Intent;
import android.util.ArrayMap;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import java.util.Map;

/**
 * Author：岑胜德 on 2021/1/25 17:02
 *
 * 说明：
 */
class DownloadService extends LifecycleService {

    private static final MutableLiveData<DownloadCommand> sCommandChannel = new MutableLiveData<>();
    @NonNull
    private final Map<String, DownloadRecord> downloadRecordMap = new ArrayMap<>();

    static void sendCommand(String key, DownloadCommand cmd) {
        sCommandChannel.setValue(cmd);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sCommandChannel.observe(this, new Observer<DownloadCommand>() {
            @Override
            public void onChanged(DownloadCommand downloadCommand) {

            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DownloadRecord record = (DownloadRecord) intent.getSerializableExtra(DownloadRecord.KEY);
        if (record != null) {
            downloadRecordMap.put(record.id, record);
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
