package com.tencent.example;

import android.app.Application;
import com.tencent.example.http.OkHttpEngine;
import com.tencent.livehttp.core.LiveHttp;

/**
 * Author：岑胜德 on 2021/3/1 14:46
 *
 * 说明：
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LiveHttp.init(new OkHttpEngine());
    }
}
