package com.tencent.livehttp.core;

import android.util.Log;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

/**
 * Author：岑胜德 on 2021/2/1 16:21
 *
 * 说明：
 */
public class LoggingInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
      final   Request request=chain.request();
        Log.e("","url="+request.url());


        return chain.proceed(request);
    }
}
