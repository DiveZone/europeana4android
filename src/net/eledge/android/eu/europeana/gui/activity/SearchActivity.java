package net.eledge.android.eu.europeana.gui.activity;

import java.util.ArrayList;
import java.util.List;

import net.eledge.android.eu.europeana.Config;
import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.gui.adapter.FacetsAdapter;
import net.eledge.android.eu.europeana.gui.dialog.AboutDialog;
import net.eledge.android.eu.europeana.gui.fragments.SearchResultsFragment;
import net.eledge.android.eu.europeana.search.SearchController;
import net.eledge.android.eu.europeana.search.listeners.SearchTaskListener;
import net.eledge.android.eu.europeana.search.model.SearchResult;
import net.eledge.android.eu.europeana.search.model.facets.enums.FacetItemType;
import net.eledge.android.eu.europeana.search.model.searchresults.FacetItem;
import net.eledge.android.toolkit.StringArrayUtils;
import net.eledge.android.toolkit.gui.GuiUtils;

import org.apache.commons.lang.StringUtils;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SearchActivity extends ActionBarActivity implements SearchTaskListener {
	
	private SearchResultsFragment mSearchFragment;

	// NavigationDrawer
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mFacetsList;
	private FacetsAdapter mFacetsAdaptor;
	
	// Controller
	private SearchController searchController = SearchController._instance;
	
	private String runningSearch = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		searchController.registerListener(SearchActivity.class, this);
		searchController.searchPagesize = getResources().getInteger(R.integer.search_result_pagesize);

		mFacetsAdaptor = new FacetsAdapter((EuropeanaApplication) getApplication(), this, new ArrayList<FacetItem>());

		mFacetsList = (ListView) findViewById(R.id.drawer_facets);
		mFacetsList.setAdapter(mFacetsAdaptor);
		mFacetsList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_activity_search);
		if (mDrawerLayout != null) {
			mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
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
		handleIntent(getIntent());
	}
	
	@Override
	protected void onDestroy() {
		searchController.cancelSearch();
		searchController.unregister(SearchActivity.class);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

		return true;
	}

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
		case R.id.action_about:
			try {
				Dialog dialog = new AboutDialog(this, (EuropeanaApplication) getApplication(), getPackageManager().getPackageInfo(getPackageName(), 0));
				dialog.show();
			} catch (NameNotFoundException e) {}
			break;
		case R.id.action_share:
			startActivity(createShareIntent());
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
		searchController.searchPagesize = getResources().getInteger(R.integer.search_result_pagesize);
		if (mDrawerLayout != null) {
			mDrawerToggle.syncState();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	@Override
	public void onSearchStart(boolean isFacetSearch) {
		if (isFacetSearch) {
			mFacetsAdaptor.clear();
			if (mFacetsAdaptor.isEmpty()) {
				createBreadcrumbs();
			}
		}
	}

	@Override
	public void onSearchError(String message) {
		GuiUtils.toast(this, message);
	}

	@Override
	public void onSearchFinish(SearchResult results) {
		if (results.facetUpdated) {
			updateFacetDrawer();
		}
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
		for (FacetItem item: searchController.getBreadcrumbs(this)) {
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
			for (FacetItem item: facetList) {
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

	private void handleIntent(Intent intent) {
		String query = null;
		List<String> qf = null;
		if (intent != null) {
			if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
				query = intent.getStringExtra(SearchManager.QUERY);
			} else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
				query = intent.getDataString();
				if (!TextUtils.isEmpty(query)) {
					if (StringUtils.contains(query, "europeana.eu/")) {
						Uri uri = Uri.parse(query);
						query = uri.getQueryParameter("query");
						qf = uri.getQueryParameters("qf");
					}
				}
			} else {
				onSearchRequested();
			}
			if (!TextUtils.isEmpty(query) && !TextUtils.equals(runningSearch, query)) {
				runningSearch = query;
				if ( (qf != null) && !qf.isEmpty()) {
					searchController.newSearch(this, query, StringArrayUtils.toArray(qf));
				} else {
					searchController.newSearch(this, query);
				}
				
				getSupportActionBar().setTitle(searchController.getSearchTitle(this));
			}
		}
	}

	/* The click listener for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			mFacetsList.setItemChecked(position, false);
			FacetItem item = mFacetsAdaptor.getItem(position);
			switch (item.itemType) {
			case SECTION:
				// ignore, is disabled.
				break;
			case BREADCRUMB:
				if (!searchController.removeRefineSearch(SearchActivity.this, item.facet)) {
					closeSearchActivity();
				} else
				if (mDrawerLayout != null) {
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
				searchController.refineSearch(SearchActivity.this, item.facet);
				if (mDrawerLayout != null) {
					mDrawerLayout.closeDrawers();
				}
				break;
			case ITEM_SELECTED:
				searchController.removeRefineSearch(SearchActivity.this, item.facet);
				if (mDrawerLayout != null) {
					mDrawerLayout.closeDrawers();
				}
				break;
			}
		}
	}

}
