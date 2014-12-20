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

package net.eledge.android.europeana.gui.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import net.eledge.android.europeana.R;
import net.eledge.android.europeana.db.dao.SearchProfileDao;
import net.eledge.android.europeana.db.model.SearchProfile;
import net.eledge.android.europeana.db.setup.DatabaseSetup;
import net.eledge.android.europeana.gui.adapter.SuggestionAdapter;
import net.eledge.android.europeana.gui.fragment.HomeBlogFragment;
import net.eledge.android.europeana.gui.notification.NewBlogNotification;
import net.eledge.android.europeana.search.SearchController;
import net.eledge.android.europeana.search.model.suggestion.Item;
import net.eledge.android.toolkit.async.listener.TaskListener;
import net.eledge.android.toolkit.gui.GuiUtils;
import net.eledge.android.toolkit.gui.annotations.ViewResource;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static net.eledge.android.toolkit.gui.ViewInjector.inject;

public class HomeActivity extends ActionBarActivity implements TaskListener<Item[]>, OnItemClickListener {

    // Controllers
    private final SearchController searchController = SearchController._instance;

    // Fragments
    private HomeBlogFragment mBlogFragment;

    // Views
    @ViewResource(R.id.activity_home_gridview_suggestions)
    private GridView mGridViewSuggestions;
    @ViewResource(R.id.toolbar_searchform_edittext_query)
    private EditText mEditTextQuery;
    @ViewResource(R.id.activity_home_spinner_profile)
    private Spinner mSpinnerProfiles;
    @ViewResource(R.id.toolbar_actionbar)
    private Toolbar mActionBarToolbar;

    // adapters
    private SuggestionAdapter mSuggestionsAdaptor;
    private ArrayAdapter<SearchProfile> mProfilesAdapter;

    private boolean isLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NewBlogNotification.cancel(this);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        searchController.suggestionPageSize = getResources().getInteger(R.integer.home_suggestions_pagesize);
        isLandscape = getResources().getBoolean(R.bool.home_support_landscape);

        mSuggestionsAdaptor = new SuggestionAdapter(this, new ArrayList<Item>());
        mGridViewSuggestions.setAdapter(mSuggestionsAdaptor);
        mGridViewSuggestions.setOnItemClickListener(this);

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
                        mSuggestionsAdaptor.clear();
                        mSuggestionsAdaptor.notifyDataSetChanged();
                    }
                    searchController.suggestions(HomeActivity.this, s.toString());
                } else {
                    onTaskFinished(null);
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

        mProfilesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        mSpinnerProfiles.setAdapter(mProfilesAdapter);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (mBlogFragment == null) {
            mBlogFragment = new HomeBlogFragment();
        }
        fragmentTransaction.replace(R.id.activity_home_fragment_blog, mBlogFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // load/refresh search profiles...
        mProfilesAdapter.clear();
        SearchProfileDao searchProfileDao = new SearchProfileDao(new DatabaseSetup(this));
        List<SearchProfile> profiles = searchProfileDao.findAll();
        searchProfileDao.close();
        mProfilesAdapter.add(new SearchProfile(GuiUtils.getString(this, R.string.form_search_profile_default), null));
        for (SearchProfile sp : profiles) {
            mProfilesAdapter.add(sp);
        }
        mProfilesAdapter.notifyDataSetChanged();
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
        SearchProfile selected = (SearchProfile) mSpinnerProfiles.getSelectedItem();
        if ((selected != null) && (selected.facets != null)) {
            intent.putExtra(SearchManager.USER_QUERY, selected.facets);
        }
        this.startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item suggestion = mSuggestionsAdaptor.getItem(position);
        searchController.suggestionPageSize = getResources().getInteger(R.integer.home_suggestions_pagesize);
        performSearch(suggestion.query);
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

    @Override
    public void onTaskStart() {
        // ignore
    }

    @Override
    public void onTaskFinished(Item[] suggestions) {
        mSuggestionsAdaptor.clear();
        if ((suggestions != null) && (suggestions.length > 0)) {
            for (Item s : suggestions) {
                mSuggestionsAdaptor.add(s);
            }
            mSuggestionsAdaptor.notifyDataSetChanged();
            switchBlogSuggestions(true);
        } else {
            mSuggestionsAdaptor.clear();
            mSuggestionsAdaptor.notifyDataSetChanged();
            switchBlogSuggestions(false);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        inject(this);
        getActionBarToolbar();
    }

    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar != null) {
            setSupportActionBar(mActionBarToolbar);
        }
        return mActionBarToolbar;
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
