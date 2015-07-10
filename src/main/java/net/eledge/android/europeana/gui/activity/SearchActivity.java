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

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.squareup.otto.Subscribe;

import net.eledge.android.europeana.Config;
import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.R;
import net.eledge.android.europeana.gui.adapter.FacetAdapter;
import net.eledge.android.europeana.gui.fragment.SearchResultsFragment;
import net.eledge.android.europeana.search.SearchController;
import net.eledge.android.europeana.search.event.SearchFacetsLoadedEvent;
import net.eledge.android.europeana.search.event.SearchStartedEvent;
import net.eledge.android.europeana.search.model.facets.enums.FacetItemType;
import net.eledge.android.europeana.search.model.searchresults.FacetItem;
import net.eledge.android.toolkit.StringArrayUtils;
import net.eledge.android.toolkit.gui.GuiUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements FacetAdapter.FacetAdaptorClickListener {

    // Controller
    private final SearchController searchController = SearchController._instance;

    // Fragments
    private SearchResultsFragment mSearchFragment;

    // Views
    @Nullable
    @Bind(R.id.drawerlayout_activity_search)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.drawer_facets)
    RecyclerView mFacetsList;

    private ActionBarDrawerToggle mDrawerToggle;

    // Adapters
    private FacetAdapter mFacetsAdaptor;

    private String runningSearch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        EuropeanaApplication.bus.register(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        searchController.searchPageSize = getResources().getInteger(R.integer.search_result_pagesize);

        mFacetsAdaptor = new FacetAdapter(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mFacetsList.setAdapter(mFacetsAdaptor);
        mFacetsList.setLayoutManager(mLayoutManager);

        // enable ActionBar app icon to behave as action to toggle nav drawer
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);

        if (mDrawerLayout != null) {
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                    R.string.drawer_facets_open, R.string.drawer_facets_close) {
                public void onDrawerClosed(View view) {
                    // getActionBar().setTitle(mTitle);
                    supportInvalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView) {
                    // getActionBar().setTitle(mDrawerTitle);
                    supportInvalidateOptionsMenu();
                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
        }

        if (Config.DEBUGMODE) {
            StrictMode.enableDefaults();
        }
        createResultFragment();
        if (savedInstanceState != null && searchController.hasResults()) {
            updateFacetDrawer();
            return;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            createNdefPushMessageCallback();
        }
        handleIntent(getIntent());
    }

    @TargetApi(14)
    private void createNdefPushMessageCallback() {
        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter != null) {
            mNfcAdapter.setNdefPushMessageCallback(new CreateNdefMessageCallback() {
                @Override
                public NdefMessage createNdefMessage(NfcEvent event) {
                    return new NdefMessage(new NdefRecord[]{
                            new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                                    "application/vnd.net.eledge.android.europeana.search".getBytes(), new byte[0],
                                    searchController.getPortalUrl().getBytes()),
                            NdefRecord.createApplicationRecord(getPackageName())});
                }
            }, this);
        }
    }

    @Override
    protected void onDestroy() {
        searchController.cancelSearch();
        EuropeanaApplication.bus.register(this);
        super.onDestroy();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(searchController.getSearchTitle(this));
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            handleIntent(getIntent());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mDrawerLayout != null) {
            boolean drawerOpen = mDrawerLayout.isDrawerOpen(mFacetsList);
            menu.findItem(R.id.action_search).setVisible(!drawerOpen);
            menu.findItem(R.id.action_share).setVisible(!drawerOpen);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ((mDrawerLayout != null) && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_share:
                startActivity(createShareIntent());
                break;
            case R.id.action_search:
                GuiUtils.startTopActivity(this, HomeActivity.class);
                break;
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivityForResult(i, 1);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerLayout != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        searchController.searchPageSize = getResources().getInteger(R.integer.search_result_pagesize);
        if (mDrawerLayout != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Subscribe
    public void onSearchStart(SearchStartedEvent event) {
        if (event.facetSearch) {
            mFacetsAdaptor.clear();
            if (mFacetsAdaptor.isEmpty()) {
                createBreadcrumbs();
            }
        }
    }

    @Subscribe
    public void onSearchFacetFinish(SearchFacetsLoadedEvent event) {
        updateFacetDrawer();
        runningSearch = null;
    }

    private void closeSearchActivity() {
        GuiUtils.startTopActivity(this, HomeActivity.class);
        finish();
    }

    private void createResultFragment() {
        if (mSearchFragment == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (mSearchFragment == null) {
                mSearchFragment = new SearchResultsFragment();
            }
            fragmentTransaction.replace(R.id.content_frame_searchgrid, mSearchFragment);
            fragmentTransaction.commit();
        }
    }

    private void createBreadcrumbs() {
        FacetItem facetSection = new FacetItem();
        facetSection.itemType = FacetItemType.SECTION;
        facetSection.labelResource = R.string.drawer_facets_section_breadcrumbs;
        mFacetsAdaptor.add(facetSection);
        for (FacetItem item : searchController.getBreadcrumbs(this)) {
            mFacetsAdaptor.add(item);
        }
        if (!searchController.hasFacets()) {
            facetSection = new FacetItem();
            facetSection.itemType = FacetItemType.SECTION;
            facetSection.labelResource = R.string.drawer_facets_section_refine_loading;
            facetSection.last = true;
            mFacetsAdaptor.add(facetSection);
        }
        mFacetsAdaptor.notifyDataSetChanged();
        if (mDrawerLayout != null) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        }
    }

    private void updateFacetDrawer() {
        List<FacetItem> facetList = searchController.getFacetList(this);
        if (facetList != null) {
            mFacetsAdaptor.clear();
            createBreadcrumbs();
            FacetItem facetSection = new FacetItem();
            facetSection.itemType = FacetItemType.SECTION;
            facetSection.labelResource = R.string.drawer_facets_section_refine;
            mFacetsAdaptor.add(facetSection);
            for (FacetItem item : facetList) {
                mFacetsAdaptor.add(item);
            }
            mFacetsAdaptor.notifyDataSetChanged();
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, searchController.getPortalUrl());
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this search on Europeana.eu!");
        return shareIntent;
    }

    private String[] splitFacets(String facets) {
        if (StringUtils.isNotBlank(facets)) {
            facets = StringUtils.substringAfter(facets, "qf=");
            return StringUtils.split(facets, "&qf=");
        }
        return null;
    }

    private void handleIntent(Intent intent) {
        String query = null;
        String[] qf = null;
        if (intent != null) {

            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                query = intent.getStringExtra(SearchManager.QUERY);
                qf = splitFacets(intent.getStringExtra(SearchManager.USER_QUERY));
            } else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
                Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                NdefMessage msg = (NdefMessage) parcelables[0];
                Uri uri = Uri.parse(new String(msg.getRecords()[0].getPayload()));
                query = uri.getQueryParameter("query");
                qf = StringArrayUtils.toArray(uri.getQueryParameters("qf"));
            } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                query = intent.getDataString();
                if (!TextUtils.isEmpty(query)) {
                    if (StringUtils.contains(query, "europeana.eu/")) {
                        Uri uri = Uri.parse(query);
                        query = uri.getQueryParameter("query");
                        qf = StringArrayUtils.toArray(uri.getQueryParameters("qf"));
                    }
                }
            } else {
                // no search action recognized? end this activity...
                closeSearchActivity();
            }
            if (!TextUtils.isEmpty(query) && !TextUtils.equals(runningSearch, query)) {
                runningSearch = query;
                if (StringArrayUtils.isNotBlank(qf)) {
                    searchController.newSearch(query, qf);
                } else {
                    searchController.newSearch(query);
                }
                getSupportActionBar().setTitle(searchController.getSearchTitle(this));
            }
        }
    }

    @Override
    public void click(FacetItem item) {
        switch (item.itemType) {
            case SECTION:
                // ignore, is disabled.
                break;
            case BREADCRUMB:
                if (!searchController.removeRefineSearch(item.facet)) {
                    closeSearchActivity();
                } else if (mDrawerLayout != null) {
                    mDrawerLayout.closeDrawers();
                }
                break;
            case CATEGORY:
                searchController.setCurrentFacetType(item.facetType);
                updateFacetDrawer();
                break;
            case CATEGORY_OPENED:
                searchController.setCurrentFacetType(null);
                updateFacetDrawer();
                break;
            case ITEM:
                searchController.refineSearch(item.facet);
                if (mDrawerLayout != null) {
                    mDrawerLayout.closeDrawers();
                }
                break;
            case ITEM_SELECTED:
                searchController.removeRefineSearch(item.facet);
                if (mDrawerLayout != null) {
                    mDrawerLayout.closeDrawers();
                }
                break;
        }
    }

}
