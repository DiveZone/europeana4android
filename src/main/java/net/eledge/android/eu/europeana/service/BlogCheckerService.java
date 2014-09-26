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

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.Preferences;
import net.eledge.android.eu.europeana.db.model.BlogArticle;
import net.eledge.android.eu.europeana.service.task.BlogDownloadTask;
import net.eledge.android.eu.europeana.tools.RssReader;
import net.eledge.android.eu.europeana.tools.UriHelper;

import org.joda.time.DateTime;

import java.util.List;

public class BlogCheckerService extends IntentService {

    public BlogCheckerService() {
        super("BlogCheckerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        EuropeanaApplication application = (EuropeanaApplication) getApplication();
        if (application.isConnected()) {
            SharedPreferences settings = this.getSharedPreferences(Preferences.BLOG, 0);
            long time = settings.getLong(Preferences.BLOG_LAST_VIEW, -1);
            DateTime lastViewed = (time != -1 ? new DateTime(time) : DateTime.now());
            List<BlogArticle> articles = RssReader.readFeed(UriHelper.URL_BLOGFEED, lastViewed);
            BlogDownloadTask.processArticles(articles, this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
