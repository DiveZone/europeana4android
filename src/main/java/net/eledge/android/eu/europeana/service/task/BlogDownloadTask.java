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

package net.eledge.android.eu.europeana.service.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

import net.eledge.android.eu.europeana.Preferences;
import net.eledge.android.eu.europeana.db.dao.BlogArticleDao;
import net.eledge.android.eu.europeana.db.model.BlogArticle;
import net.eledge.android.eu.europeana.db.setup.DatabaseSetup;
import net.eledge.android.eu.europeana.gui.notification.NewBlogNotification;
import net.eledge.android.eu.europeana.tools.RssReader;
import net.eledge.android.eu.europeana.tools.UriHelper;
import net.eledge.android.toolkit.async.listener.TaskListener;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BlogDownloadTask implements TaskListener<List<BlogArticle>> {

    public static BlogCheckerListener listener;

    private RssReader mRssReaderTask;

    private Context mContext;

    private static BlogDownloadTask _instance;

    public static BlogDownloadTask getInstance(Context c) {
        if (_instance == null) {
            _instance = new BlogDownloadTask(c);
        }
        return _instance;
    }

    private BlogDownloadTask(Context c) {
        mContext = c;
    }

    public void execute() {
        DateTime lastViewed = DateTime.now();

        SharedPreferences settings = mContext.getSharedPreferences(Preferences.BLOG, 0);
        long time = settings.getLong(Preferences.BLOG_LAST_VIEW, -1);
        if (time != -1) {
            lastViewed = new DateTime(new Date(time));
        }
        mRssReaderTask = new RssReader(lastViewed, this);
        mRssReaderTask.execute(UriHelper.URL_BLOGFEED);
    }

    public void cancel() {
        if (mRssReaderTask != null) {
            mRssReaderTask.cancel(true);
        }
    }

    @Override
    public void onTaskStart() {
        // left empty on purpose
    }

    @Override
    public void onTaskFinished(List<BlogArticle> articles) {
        processArticles(articles, mContext);
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
            editor.commit();

            if ((listener != null) && listener instanceof Fragment) {
                ((Fragment) listener).getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.updatedArticles(articles);
                    }
                });
            } else {
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

    public interface BlogCheckerListener {

        void updatedArticles(List<BlogArticle> articles);

    }

}
