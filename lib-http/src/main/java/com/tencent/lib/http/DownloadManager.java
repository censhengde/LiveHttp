package com.tencent.lib.http;

/**
 * Author：岑胜德 on 2021/1/25 17:00
 *
 * 说明：
 */
public class DownloadManager {

    private final DownloadRecord record;


    DownloadManager(DownloadRecord record) {
        this.record = record;
    }

    public void pause() {
        DownloadService.sendCommand(record.id, DownloadCommand.PAUSE);
    }

    public void resume() {
        DownloadService.sendCommand(record.id, DownloadCommand.RESUME);
    }

    public void cancel() {
        DownloadService.sendCommand(record.id, DownloadCommand.CANCEL);
    }


}
