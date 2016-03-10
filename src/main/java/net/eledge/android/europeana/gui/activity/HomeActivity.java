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

package net.eledge.android.europeana.gui.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Subscribe;

import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.Preferences;
import net.eledge.android.europeana.R;
import net.eledge.android.europeana.db.model.BlogArticle;
import net.eledge.android.europeana.gui.adapter.BlogAdapter;
import net.eledge.android.europeana.gui.adapter.events.BlogItemClicked;
import net.eledge.android.europeana.gui.adapter.events.SuggestionClicked;
import net.eledge.android.europeana.search.ApiTasks;
import net.eledge.android.europeana.service.event.BlogItemsLoadedEvent;
import net.eledge.android.europeana.service.task.BlogDownloadTask;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class HomeActivity extends AppCompatActivity {

    // Views
    @Bind(R.id.toolbar_searchform_edittext_query)
    EditText mEditTextQuery;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.activity_home_blog_recyclerView)
    RecyclerView mRecyclerView;

    private boolean isLandscape;
    private BlogAdapter mBlogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        EuropeanaApplication.bus.register(this);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowCustomEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
        }

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        ApiTasks.getInstance().suggestionPageSize = getResources().getInteger(R.integer.home_suggestions_pagesize);
        isLandscape = getResources().getBoolean(R.bool.home_support_landscape);

        // setup BLOG

        mBlogAdapter = new BlogAdapter(this);
        mRecyclerView.setAdapter(mBlogAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadFromDatabase();

        mEditTextQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_SEARCH) || (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    performSearch(v.getText().toString());
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EuropeanaApplication.bus.unregister(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isLandscape = getResources().getBoolean(R.bool.home_support_landscape);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (StringUtils.isNoneEmpty(mEditTextQuery.getText())) {
                mEditTextQuery.setText(null);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void performSearch(String query) {
        final Intent intent = new Intent(this, SearchActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        this.startActivity(intent);
    }

    @Subscribe
    public void onSuggestionClicked(SuggestionClicked event) {
        performSearch(event.suggestion.query);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivityForResult(i, 1);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void switchBlogSuggestions(boolean showSuggestions) {
//        if (!isLandscape) {
//            mGridViewSuggestions.setVisibility(showSuggestions ? View.VISIBLE : View.GONE);
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            if (showSuggestions) {
//                fragmentTransaction.hide(mBlogFragment);
//            } else {
//                fragmentTransaction.show(mBlogFragment);
//            }
//            fragmentTransaction.commitAllowingStateLoss();
//        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        getToolbar();
    }

    protected Toolbar getToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        return mToolbar;
    }

    public void setLocale(String lang) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = new Locale(lang);
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, HomeActivity.class);
        startActivity(refresh);
    }

    private void loadFromDatabase() {
        RealmResults<BlogArticle> articles = Realm.getDefaultInstance().allObjects(BlogArticle.class);
        if ((articles == null) || articles.isEmpty()) {
            new BlogDownloadTask(this).execute();
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
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.article.getGuid()));
        Tracker tracker = EuropeanaApplication._instance.getAnalyticsTracker();
        tracker.send(new HitBuilders
                .EventBuilder()
                .setCategory("blog")
                .setAction("click")
                .setLabel(event.article.getGuid())
                .build());
        startActivity(browserIntent);
    }

    private void updatedArticles(List<BlogArticle> articles) {
        if (articles != null) {
            mBlogAdapter.articles.clear();
            mBlogAdapter.articles.addAll(articles);
            mBlogAdapter.notifyDataSetChanged();

            SharedPreferences settings = getSharedPreferences(Preferences.BLOG, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(Preferences.BLOG_LAST_VIEW, new Date().getTime());
            editor.apply();
        }
    }


}
