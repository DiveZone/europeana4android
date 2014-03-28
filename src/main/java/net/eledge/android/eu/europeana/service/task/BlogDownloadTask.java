package net.eledge.android.eu.europeana.service.task;

import android.content.Context;
import android.content.SharedPreferences;

import net.eledge.android.eu.europeana.Preferences;
import net.eledge.android.eu.europeana.db.dao.BlogArticleDao;
import net.eledge.android.eu.europeana.db.model.BlogArticle;
import net.eledge.android.eu.europeana.db.setup.DatabaseSetup;
import net.eledge.android.eu.europeana.gui.notification.NewBlogNotification;
import net.eledge.android.eu.europeana.tools.RssReader;
import net.eledge.android.eu.europeana.tools.UriHelper;
import net.eledge.android.toolkit.async.listener.TaskListener;

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
        Date lastViewed = new Date();

        SharedPreferences settings = mContext.getSharedPreferences(Preferences.BLOG, 0);
        long time = settings.getLong(Preferences.BLOG_LAST_VIEW, -1);
        if (time != -1) {
            lastViewed.setTime(time);
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

    public static void processArticles(List<BlogArticle> articles, Context context) {
        if (articles != null) {
            BlogArticleDao mBlogArticleDao = new BlogArticleDao(new DatabaseSetup(context));
            mBlogArticleDao.deleteAll();
            mBlogArticleDao.store(articles);
            mBlogArticleDao.close();
            SharedPreferences settings = context.getSharedPreferences(Preferences.BLOG, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(Preferences.BLOG_LAST_UPDATE, new Date().getTime());
            editor.commit();

            if (listener != null) {
                listener.updatedArticles(articles);
            } else {
                if (settings.getBoolean(Preferences.BLOG_NOTIFICATION_ENABLE, true)) {
                    for (BlogArticle item : articles) {
                        if (item.markedNew) {
                            NewBlogNotification.notify(context, item.title, item.description, item.guid);
                        }
                    }
                }
            }
        }
    }

    public interface BlogCheckerListener {

        void updatedArticles(List<BlogArticle> articles);

    }

}
