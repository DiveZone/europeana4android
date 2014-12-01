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

package net.eledge.android.eu.europeana.gui.fragment;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.Preferences;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.db.dao.BlogArticleDao;
import net.eledge.android.eu.europeana.db.model.BlogArticle;
import net.eledge.android.eu.europeana.db.setup.DatabaseSetup;
import net.eledge.android.eu.europeana.gui.adapter.BlogAdapter;
import net.eledge.android.eu.europeana.service.receiver.BlogCheckerReceiver;
import net.eledge.android.eu.europeana.service.task.BlogDownloadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeBlogFragment extends Fragment implements BlogDownloadTask.BlogCheckerListener {

    private BlogAdapter mBlogAdapter;

    private BlogArticleDao mBlogArticleDao;

    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBlogAdapter = new BlogAdapter(getActivity(), new ArrayList<BlogArticle>());
        BlogDownloadTask.listener = this;


        if (PendingIntent.getBroadcast(getActivity(), 0,
                new Intent(new Intent(getActivity(), BlogCheckerReceiver.class)),
                PendingIntent.FLAG_NO_CREATE) == null) {
            new BlogCheckerReceiver().enableBlogChecker(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_blog, null);
        mListView = (ListView) root.findViewById(R.id.fragment_home_blog_listview);
        mListView.setAdapter(mBlogAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BlogArticle article = mBlogAdapter.getItem(position);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.guid));
                Tracker tracker = ((EuropeanaApplication) getActivity().getApplication()).getAnalyticsTracker();
                tracker.send(new HitBuilders.EventBuilder().setCategory("blog").setAction("click").setLabel(article.guid).build());
                startActivity(browserIntent);
            }
        });
        loadFromDatabase();

        return root;
    }

    @Override
    public void onDestroy() {
        BlogDownloadTask.listener = null;
        super.onDestroy();
    }

    private void loadFromDatabase() {
        mBlogArticleDao = new BlogArticleDao(new DatabaseSetup(getActivity()));
        List<BlogArticle> articles = mBlogArticleDao.findAll();
        mBlogArticleDao.close();
        if ((articles == null) || articles.isEmpty()) {
            BlogDownloadTask.getInstance(this.getActivity()).execute();
        } else {
            updatedArticles(articles);
        }
    }

    @Override
    public void updatedArticles(List<BlogArticle> articles) {
        if (articles != null) {
            mBlogAdapter.clear();
            for (BlogArticle article : articles) {
                mBlogAdapter.add(article);
            }
            mBlogAdapter.notifyDataSetChanged();

            SharedPreferences settings = getActivity().getSharedPreferences(Preferences.BLOG, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(Preferences.BLOG_LAST_VIEW, new Date().getTime());
            editor.apply();
        }
    }
}
