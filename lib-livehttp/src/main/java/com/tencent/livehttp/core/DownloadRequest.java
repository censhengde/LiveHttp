package com.tencent.livehttp.core;

import android.content.Context;
import android.content.Intent;
import android.util.ArrayMap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import java.io.Serializable;
import java.util.Map;

/**
 * Author：岑胜德 on 2021/1/25 17:01
 *
 * 说明：
 */
public class DownloadRequest  implements Serializable {

    static final MutableLiveData<DownloadRequest> sRequestChannel = new MutableLiveData<>();
    String id;
    volatile DownloadStatus status;
    volatile int progress;
    String savePath;
    String fileName;

    public int getProgress() {
        return progress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DownloadStatus getStatus() {
        return status;
    }

    public void setStatus(DownloadStatus status) {
        this.status = status;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    @Nullable
    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(@Nullable Map<String, String> headers) {
        this.headers = headers;
    }

    @Nullable
    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(@Nullable Map<String, Object> params) {
        this.params = params;
    }

    @NonNull
    final String url;
    @Nullable
    private Map<String, String> headers;
    @Nullable
    private Map<String, Object> params;
    final MutableLiveData<DownloadRecord> recordChannel = new MutableLiveData<>();

    @NonNull
    public MutableLiveData<DownloadRecord> getRecordChannel() {
        return recordChannel;
    }

    DownloadRequest() {
        this("");
    }
    DownloadRequest(@NonNull String url) {
        this.url = url;
        if (LiveHttp.sCommonHeader != null) {
            headers = new ArrayMap<>(LiveHttp.sCommonHeader.size() + 4);
            headers.putAll(LiveHttp.sCommonHeader);
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

    public DownloadRequest savePath(@NonNull String path){
        savePath=path;
        return this;
    }
    public DownloadRequest fileName(@NonNull String name){
        this.fileName=fileName;
        return this;
    }

    public DownloadManager start(@NonNull Context context) {
        Intent intent = new Intent(context, DownloadService.class);

        this.id=this.id==null?url:this.id;
//        intent.putExtra(DownloadRecord.KEY,record);
        sRequestChannel.setValue(this);
        context.startService(intent);
        return new DownloadManager();
    }

    public DownloadRequest addListener(@NonNull LifecycleOwner owner, @NonNull final DownloadListener listener) {
        recordChannel.observe(owner, record -> {
            switch (record.status) {
                case ON_STARTED:
                    listener.onStarted();
                    break;
                case ON_LOADING:
                    listener.onLoading(record, record.progress);
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
        });
        return this;
    }

    public DownloadRequest addListener(@NonNull final DownloadListener listener) {
        recordChannel.observeForever(record -> {
            switch (record.status) {
                case ON_STARTED:
                    listener.onStarted();
                    break;
                case ON_LOADING:
                    listener.onLoading(record, record.progress);
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
        });
        return this;
    }

    final DownloadRecord asRecord(){
        final DownloadRecord record =new DownloadRecord();
        record.status=status;
        record.progress=progress;
        return record;
    }
}
