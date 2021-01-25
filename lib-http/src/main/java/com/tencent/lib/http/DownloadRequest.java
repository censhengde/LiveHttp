package com.tencent.lib.http;

import android.content.Context;
import android.content.Intent;
import android.util.ArrayMap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import java.util.Map;

/**
 * Author：岑胜德 on 2021/1/25 17:01
 *
 * 说明：
 */
public final class DownloadRequest {


    @NonNull
    final String url;
    @Nullable
    private Map<String, String> headers;
    @Nullable
    private Map<String, Object> params;
    final MutableLiveData<DownloadRecord> downloadRecordChannel = new MutableLiveData<>();

    DownloadRequest(@NonNull String url) {
        this.url = url;
        if (HttpService.sCommonHeader != null) {
            headers = new ArrayMap<>(HttpService.sCommonHeader.size() + 4);
            headers.putAll(HttpService.sCommonHeader);
        }
    }

    public DownloadRequest addHeader(@NonNull String key, @NonNull String value) {
        if (headers == null) {
            headers = new ArrayMap<>(4);
        }
        headers.put(key, value);
        return this;
    }

    public DownloadRequest addParam(@NonNull String key, @NonNull Object value) {
        if (params == null) {
            params = new ArrayMap<>(5);
        }
        params.put(key, value);
        return this;
    }

    public DownloadManager start(@NonNull Context context, @NonNull LifecycleOwner owner,
            @NonNull final OnDownloadListener listener) {
        Intent intent = new Intent(context, DownloadService.class);
        DownloadRecord record = new DownloadRecord(this);
        downloadRecordChannel.observe(owner, new Observer<DownloadRecord>() {
            @Override
            public void onChanged(DownloadRecord record) {
                switch (record.status) {
                    case ON_STARTED:
                        listener.onStarted();
                        break;
                    case ON_LOADING:
                        listener.onDownloading(record,record.progress);
                        break;
                    case ON_FINISHED:
                        listener.onFinished(record);
                        break;
                    case ON_ERROR:
                        listener.onError(record);
                        break;
                    default:
                        break;
                }
            }
        });
        record.id=record.id==null?url:record.id;
        intent.putExtra(DownloadRecord.KEY,record);
        context.startService(intent);
        return new DownloadManager(record);
    }

}
