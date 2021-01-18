package com.tencent.lib.http;

import android.os.Looper;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

/**
 * Author：岑胜德 on 2020/11/9 10:11
 * <p>
 * 说明：Http数据传输通道
 */
 final class HttpChannel {

    private final MutableLiveData<HttpMessage> mChannel;

     static HttpChannel newInstance() {
        return new HttpChannel();
    }

    //屏蔽构造器，防止用户new出匿名对象
    private HttpChannel() {
        mChannel = new MutableLiveData<>();
    }

    /**
     * 写数据端，可在任何线程调用
     *
     * @param data
     */
    void write(HttpMessage data) {
        //判断是否主线程
        if ( Looper.getMainLooper().equals(Looper.myLooper())) {
            mChannel.setValue(data);
        } else {
            mChannel.postValue(data);
        }
    }

    /**
     * 读数据端，只能在UI线程调用
     *
     * @param owner
     * @param callback
     */
    @MainThread
    void read(@NonNull LifecycleOwner owner, @NonNull final HttpCallback callback) {
        mChannel.observe(owner,
                new Observer<HttpMessage>() {
                    @Override
                    public void onChanged(HttpMessage httpMessage) {
                        if (httpMessage.isSucceed) {
                            callback.onSucceed(httpMessage);
                        } else {
                            callback.onFailure(httpMessage);
                        }
                        httpMessage.recycle();//回收
                    }
                }
        );
    }


     void readForever(final HttpCallback callback){
        mChannel.observeForever(new Observer<HttpMessage>() {
            @Override
            public void onChanged(HttpMessage httpMessage) {
                if (httpMessage.isSucceed) {
                    callback.onSucceed(httpMessage);
                } else {
                    callback.onFailure(httpMessage);
                }
                httpMessage.recycle();
            }
        });
    }


}
