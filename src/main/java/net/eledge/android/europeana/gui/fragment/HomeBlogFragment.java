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

package net.eledge.android.europeana.gui.fragment;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Subscribe;

import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.Preferences;
import net.eledge.android.europeana.R;
import net.eledge.android.europeana.db.dao.BlogArticleDao;
import net.eledge.android.europeana.db.model.BlogArticle;
import net.eledge.android.europeana.db.setup.DatabaseSetup;
import net.eledge.android.europeana.gui.adapter.BlogAdapter;
import net.eledge.android.europeana.gui.adapter.events.BlogItemClicked;
import net.eledge.android.europeana.service.event.BlogItemsLoadedEvent;
import net.eledge.android.europeana.service.receiver.BlogCheckerReceiver;
import net.eledge.android.europeana.service.task.BlogDownloadTask;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeBlogFragment extends Fragment {

    private BlogAdapter mBlogAdapter;
    private LinearLayoutManager mLayoutManager;

    @Bind(R.id.fragment_home_blog_recyclerView)
    RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EuropeanaApplication.bus.register(this);
        mBlogAdapter = new BlogAdapter(getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity());

        if (PendingIntent.getBroadcast(getActivity(), 0,
                new Intent(new Intent(getActivity(), BlogCheckerReceiver.class)),
                PendingIntent.FLAG_NO_CREATE) == null) {
            new BlogCheckerReceiver().enableBlogChecker(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_blog, container, false);
        ButterKnife.bind(this, root);
        mRecyclerView.setAdapter(mBlogAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        loadFromDatabase();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EuropeanaApplication.bus.unregister(this);
    }

    private void loadFromDatabase() {
        BlogArticleDao mBlogArticleDao = new BlogArticleDao(new DatabaseSetup(getActivity()));
        List<BlogArticle> articles = mBlogArticleDao.findAll();
        mBlogArticleDao.close();
        if ((articles == null) || articles.isEmpty()) {
            new BlogDownloadTask(this.getActivity()).execute();
        } else {
            updatedArticles(articles);
        }
    }

    @Subscribe
    public void onBlogItemsLoadedEvent(BlogItemsLoadedEvent event) {
        updatedArticles(event.articles);
    }


    @Subscribe
    public void onBlogItemClicked(BlogItemClicked event) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.article.guid));
        Tracker tracker = EuropeanaApplication._instance.getAnalyticsTracker();
        tracker.send(new HitBuilders
                .EventBuilder()
                .setCategory("blog")
                .setAction("click")
                .setLabel(event.article.guid)
                .build());
        startActivity(browserIntent);
    }

    private void updatedArticles(List<BlogArticle> articles) {
        if (articles != null) {
            mBlogAdapter.articles.clear();
            mBlogAdapter.articles.addAll(articles);
            mBlogAdapter.notifyDataSetChanged();

            SharedPreferences settings = getActivity().getSharedPreferences(Preferences.BLOG, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(Preferences.BLOG_LAST_VIEW, new Date().getTime());
            editor.apply();
        }
    }
}
