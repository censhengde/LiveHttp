package com.tencent.example

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tencent.livehttp.core.DownloadRecord
import com.tencent.livehttp.core.LiveHttp
import com.tencent.livehttp.core.DownloadListener
import kotlinx.android.synthetic.main.activity_download.*

class DownloadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
    }

    fun download(view: View) {
        if (btn_download.text == "开始下载") {
            btn_download.text = "下载中..."
            btn_download.isEnabled = false
            LiveHttp.download("http")
                    .addListener(this, object : DownloadListener {
                        override fun onLoading(record: DownloadRecord, progress: Int) {
                            tv_progress.text="进度：$progress%"
                        }

                        override fun onFinished(record: DownloadRecord) {
                            btn_download.text = "开始下载"
                            btn_download.isEnabled = true
                            tv_progress.text="下载完成：${record.progress}%"

                        }

                        override fun onStarted() {

                        }

                        override fun onError(record: DownloadRecord) {
                        }

                    }).start(this)
        }


    }
}
