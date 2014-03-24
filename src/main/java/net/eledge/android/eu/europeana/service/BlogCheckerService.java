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

package net.eledge.android.eu.europeana.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import net.eledge.android.eu.europeana.Preferences;
import net.eledge.android.eu.europeana.service.receiver.BlogCheckerReceiver;
import net.eledge.android.eu.europeana.service.task.BlogDownloadTask;

import java.util.Date;

public class BlogCheckerService extends IntentService {

    public BlogCheckerService() {
        super("BlogCheckerService");
    }

    BlogDownloadTask mTask;

    @Override
    protected void onHandleIntent(Intent intent) {
        Date lastViewed = new Date();

        SharedPreferences settings = this.getSharedPreferences(Preferences.BLOG, 0);
        long time = settings.getLong(Preferences.BLOG_LAST_VIEW, -1);
        if (time != -1) {
            lastViewed.setTime(time);
        }

        if (mTask == null) {
            mTask = BlogDownloadTask.getInstance(this);
        }
        mTask.execute();

        BlogCheckerReceiver.completeWakefulIntent(intent);
    }

    @Override
    public void onDestroy() {
        if (mTask != null) {
            mTask.cancel();
        }
        super.onDestroy();
    }

}
