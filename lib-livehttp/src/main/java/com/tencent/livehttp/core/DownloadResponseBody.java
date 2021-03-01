package com.tencent.livehttp.core;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Author：岑胜德 on 2021/2/3 17:31
 *
 * 说明：
 */
public class DownloadResponseBody extends ResponseBody {

    final ResponseBody delegate;

    public DownloadResponseBody(ResponseBody delegate) {
        this.delegate = delegate;
    }

    @Override
    public long contentLength() {
        return delegate.contentLength();
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return null;
    }

    @NotNull
    @Override
    public BufferedSource source() {
        return null;
    }
}
