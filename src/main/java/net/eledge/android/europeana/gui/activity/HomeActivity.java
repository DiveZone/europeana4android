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
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.R;
import net.eledge.android.europeana.gui.adapter.SuggestionAdapter;
import net.eledge.android.europeana.gui.adapter.events.SuggestionClicked;
import net.eledge.android.europeana.gui.fragment.HomeBlogFragment;
import net.eledge.android.europeana.gui.notification.NewBlogNotification;
import net.eledge.android.europeana.search.ApiTasks;
import net.eledge.android.europeana.search.SearchController;
import net.eledge.android.europeana.search.event.SuggestionsLoadedEvent;
import net.eledge.android.europeana.search.model.suggestion.Suggestion;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    // Controllers
    private final SearchController searchController = SearchController._instance;

    // Fragments
    private HomeBlogFragment mBlogFragment;

    // Views
    @Bind(R.id.activity_home_recyclerview_suggestions)
    RecyclerView mGridViewSuggestions;

    @Bind(R.id.toolbar_searchform_edittext_query)
    EditText mEditTextQuery;

    @Bind(R.id.my_toolbar)
    Toolbar mToolbar;

    // Adapters
    private SuggestionAdapter _suggestionAdaptor;

    private boolean isLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        EuropeanaApplication.bus.register(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NewBlogNotification.cancel(this);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        ApiTasks.getInstance().suggestionPageSize = getResources().getInteger(R.integer.home_suggestions_pagesize);
        isLandscape = getResources().getBoolean(R.bool.home_support_landscape);

        _suggestionAdaptor = new SuggestionAdapter();
        mGridViewSuggestions.setAdapter(_suggestionAdaptor);

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
        mEditTextQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    if (mGridViewSuggestions.isShown()) {
                        _suggestionAdaptor.suggestions.clear();
                        _suggestionAdaptor.notifyDataSetChanged();
                    }
                    searchController.suggestions(s.toString());
                } else {
                    updateSuggestions(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                // ignore
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (mBlogFragment == null) {
            mBlogFragment = new HomeBlogFragment();
        }
        fragmentTransaction.replace(R.id.activity_home_fragment_blog, mBlogFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        if (!isLandscape) {
            mGridViewSuggestions.setVisibility(showSuggestions ? View.VISIBLE : View.GONE);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (showSuggestions) {
                fragmentTransaction.hide(mBlogFragment);
            } else {
                fragmentTransaction.show(mBlogFragment);
            }
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    @Subscribe
    public void onSuggestionsLoadedEvent(SuggestionsLoadedEvent event) {
        updateSuggestions(event.suggestions);
    }

    private void updateSuggestions(Suggestion[] suggestions) {
        _suggestionAdaptor.suggestions.clear();
        if (ArrayUtils.isNotEmpty(suggestions)) {
            Collections.addAll(_suggestionAdaptor.suggestions, suggestions);
            _suggestionAdaptor.notifyDataSetChanged();
            switchBlogSuggestions(true);
        } else {
            _suggestionAdaptor.notifyDataSetChanged();
            switchBlogSuggestions(false);
        }
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

}
