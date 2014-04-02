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

package net.eledge.android.eu.europeana.gui.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;

import net.eledge.android.eu.europeana.Config;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.db.model.BlogArticle;
import net.eledge.android.eu.europeana.gui.activity.HomeActivity;
import net.eledge.android.eu.europeana.gui.notification.receiver.UrlButtonReceiver;
import net.eledge.android.eu.europeana.tools.UriHelper;

import java.util.List;

public class NewBlogNotification {

    public static void notify(final Context context, List<BlogArticle> articles) {
        if ((articles == null) || articles.isEmpty()) {
            return;
        }
        // cancel earlier notification
        cancel(context);
        final Resources res = context.getResources();

        String blogUrl = UriHelper.URL_BLOG;
        String title = res.getString(
                R.string.new_blog_notification_title_multiple, articles.size());

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_stat_new_blog)
                .setContentText(res.getText(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setTicker(title)
                .setAutoCancel(true);
        if (articles.size() == 1) {
            BlogArticle item = articles.get(0);
            blogUrl = item.guid;
            title = res.getString(
                    R.string.new_blog_notification_title_single, item.title);

        } else {
            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle();
            for (BlogArticle item : articles) {
                inboxStyle.addLine(item.title);
            }
            builder.setStyle(inboxStyle);
        }

        Intent buttonIntent = new Intent(context, UrlButtonReceiver.class);
        buttonIntent.putExtra(UrlButtonReceiver.PARAM_NOTIFICATIONID, Config.NOTIFICATION_NEWBLOG);
        buttonIntent.putExtra(UrlButtonReceiver.PARAM_URL, blogUrl);
        PendingIntent openUrl = PendingIntent.getBroadcast(context, 0, buttonIntent, 0);

        PendingIntent openApp = PendingIntent.getActivity(
                context,
                0,
                new Intent(context, HomeActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(openApp)
                .addAction(0, res.getString(R.string.action_open_browser), openUrl)
                .addAction(0, res.getString(R.string.action_open_app), openApp)
                .setContentTitle(title);

        notify(context, builder.build());
    }

    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(Config.NOTIFICATION_NEWBLOG, notification);
    }

    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(Config.NOTIFICATION_NEWBLOG);
    }
}