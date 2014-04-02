package net.eledge.android.eu.europeana.gui.notification.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import net.eledge.android.eu.europeana.Config;

import org.apache.commons.lang3.StringUtils;

public class UrlButtonReceiver extends BroadcastReceiver {

    public static final String PARAM_NOTIFICATIONID = "notificationId";
    public static final String PARAM_URL = "url";

    @Override
    public void onReceive(Context context, Intent intent) {

        int notificationId = intent.getIntExtra(PARAM_NOTIFICATIONID, Config.NOTIFICATION_NEWBLOG);
        String url = intent.getStringExtra(PARAM_URL);

        if (StringUtils.isNoneBlank(url)) {
            try {
                PendingIntent.getActivity(
                        context,
                        0,
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)),
                        PendingIntent.FLAG_UPDATE_CURRENT).send();
            } catch (PendingIntent.CanceledException e) {
                Log.e(UrlButtonReceiver.class.getSimpleName(), e.getMessage(), e);
            }
        }

        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(notificationId);
    }
}