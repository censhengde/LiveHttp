package com.tencent.lib.http;

import java.io.Serializable;

/**
 * Author：岑胜德 on 2021/1/25 18:01
 *
 * 说明：记录下载信息的实体类，类似于ActivityRecord。
 */
public class DownloadRecord implements Serializable {

    static final String KEY = "DownloadRecord";
    String id;
    DownloadRequest request;
    DownloadStatus status;
    int progress;

    public DownloadRecord(DownloadRequest request) {
        this.request = request;
    }
}
