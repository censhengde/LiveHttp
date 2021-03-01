package com.tencent.livehttp.core;

import java.io.Serializable;

/**
 * Author：岑胜德 on 2021/1/25 18:01
 *
 * 说明：记录下载信息的实体类，类似于ActivityRecord。
 */

public class DownloadRecord extends DownloadRequest implements Serializable {

    static final String KEY = "DownloadRecord";
    String id;
    volatile DownloadStatus status;
    volatile int progress;

    public int getProgress() {
        return progress;
    }


}
