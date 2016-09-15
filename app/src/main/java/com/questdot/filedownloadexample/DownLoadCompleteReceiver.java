package com.questdot.filedownloadexample;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by HP on 16/9/2016.
 */
class DownLoadCompleteReceiver extends BroadcastReceiver {
    private MainActivity mainActivity;

    public DownLoadCompleteReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            Toast.makeText(mainActivity, "Completed", Toast.LENGTH_SHORT).show();
        } else if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
            Toast.makeText(mainActivity, "Clicked", Toast.LENGTH_SHORT).show();
        }
    }
}
