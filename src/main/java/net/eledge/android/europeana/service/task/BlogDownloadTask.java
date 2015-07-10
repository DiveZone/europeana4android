/*
 * Copyright (c) 2013-2015 eLedge.net and the original author or authors.
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

package net.eledge.android.europeana.service.task;

import android.content.Context;
import android.content.SharedPreferences;

import com.squareup.otto.Subscribe;

import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.Preferences;
import net.eledge.android.europeana.db.dao.BlogArticleDao;
import net.eledge.android.europeana.db.model.BlogArticle;
import net.eledge.android.europeana.db.setup.DatabaseSetup;
import net.eledge.android.europeana.gui.notification.NewBlogNotification;
import net.eledge.android.europeana.service.event.BlogItemsLoadedEvent;
import net.eledge.android.europeana.tools.RssReader;
import net.eledge.android.europeana.tools.UriHelper;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BlogDownloadTask {

    private final Context mContext;

    public BlogDownloadTask(Context c) {
        mContext = c;
        EuropeanaApplication.bus.register(this);
    }

    public void execute() {
        DateTime lastViewed = DateTime.now();

        SharedPreferences settings = mContext.getSharedPreferences(Preferences.BLOG, 0);
        long time = settings.getLong(Preferences.BLOG_LAST_VIEW, -1);
        if (time != -1) {
            lastViewed = new DateTime(new Date(time));
        }
        RssReader mRssReaderTask = new RssReader(lastViewed);
        mRssReaderTask.execute(UriHelper.URL_BLOGFEED);
    }

    @Subscribe
    public void onRssReaderFinish(BlogItemsLoadedEvent event) {
        EuropeanaApplication.bus.unregister(this);
        processArticles(event.articles, mContext);
    }

    public static void processArticles(final List<BlogArticle> articles, Context context) {
        if (articles != null) {
            BlogArticleDao mBlogArticleDao = new BlogArticleDao(new DatabaseSetup(context));
            mBlogArticleDao.deleteAll();
            mBlogArticleDao.store(articles);
            mBlogArticleDao.close();
            SharedPreferences settings = context.getSharedPreferences(Preferences.BLOG, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(Preferences.BLOG_LAST_UPDATE, new Date().getTime());
            editor.apply();

            if (settings.getBoolean(Preferences.BLOG_NOTIFICATION_ENABLE, true)) {
                List<BlogArticle> newArticles = new ArrayList<>();
                for (BlogArticle item : articles) {
                    if (item.markedNew) {
                        newArticles.add(item);
                    }
                }
                NewBlogNotification.notify(context, newArticles);
            }
        }
    }

}
