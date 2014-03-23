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
import net.eledge.android.eu.europeana.db.dao.BlogArticleDao;
import net.eledge.android.eu.europeana.db.model.BlogArticle;
import net.eledge.android.eu.europeana.db.setup.DatabaseSetup;
import net.eledge.android.eu.europeana.gui.notification.NewBlogNotification;
import net.eledge.android.eu.europeana.service.receiver.BlogCheckerReceiver;
import net.eledge.android.eu.europeana.tools.RssReader;
import net.eledge.android.eu.europeana.tools.UriHelper;
import net.eledge.android.toolkit.async.listener.TaskListener;

import java.util.Date;
import java.util.List;

public class BlogCheckerService extends IntentService implements TaskListener<List<BlogArticle>> {

    public static BlogCheckerListener listener;

    private RssReader mRssReaderTask;

    public BlogCheckerService() {
        super("BlogCheckerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Date lastViewed = new Date();

        SharedPreferences settings = this.getSharedPreferences(Preferences.BLOG, 0);
        long time = settings.getLong(Preferences.BLOG_LAST_VIEW, -1);
        if (time != -1) {
            lastViewed.setTime(time);
        }

        mRssReaderTask = new RssReader(lastViewed, this);
        mRssReaderTask.execute(UriHelper.URL_BLOGFEED);

        BlogCheckerReceiver.completeWakefulIntent(intent);
    }

    @Override
    public void onTaskStart() {
        // left empty on purpose
    }

    @Override
    public void onTaskFinished(List<BlogArticle> articles) {
        if (articles != null) {
            BlogArticleDao mBlogArticleDao = new BlogArticleDao(new DatabaseSetup(this));
            mBlogArticleDao.deleteAll();
            mBlogArticleDao.store(articles);
            mBlogArticleDao.close();
            SharedPreferences settings = getSharedPreferences(Preferences.BLOG, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(Preferences.BLOG_LAST_UPDATE, new Date().getTime());
            editor.commit();

            for (BlogArticle item : articles) {
                if (item.markedNew) {
                    NewBlogNotification.notify(this, item.title, item.description, item.guid);
                }
            }

            if (listener != null) {
                listener.updatedArticles(articles);
            }
        }
    }

    public interface BlogCheckerListener {

        void updatedArticles(List<BlogArticle> articles);

    }

}
