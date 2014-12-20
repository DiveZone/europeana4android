/*
 * Copyright (c) 2014 eLedge.net and the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.eledge.android.europeana.gui.notification.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import net.eledge.android.europeana.Config;

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