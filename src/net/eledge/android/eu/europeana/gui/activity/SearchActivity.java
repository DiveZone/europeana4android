package net.eledge.android.eu.europeana.gui.activity;

import java.util.List;

import net.eledge.android.eu.europeana.Config;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.gui.fragments.SearchResultsFragment;
import net.eledge.android.eu.europeana.search.SearchController;
import net.eledge.android.eu.europeana.search.listeners.SearchTaskListener;
import net.eledge.android.eu.europeana.search.model.SearchResult;
import net.eledge.android.eu.europeana.search.model.searchresults.Facet;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.ShareActionProvider;

public class SearchActivity extends FragmentActivity implements SearchTaskListener {

	private ShareActionProvider mShareActionProvider;

	private SearchResultsFragment mSearchFragment;
	private DrawerLayout mDrawerLayout;
	private ListView mFacetsList;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		if (Config.DEBUGMODE) {
			StrictMode.enableDefaults();
		}
		handleIntent(getIntent());

		mSearchFragment = (SearchResultsFragment) getSupportFragmentManager().findFragmentById(
				R.id.fragment_search_results);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.layout_activity_search);
		mFacetsList = (ListView) findViewById(R.id.drawer_facets);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		// mFacetsList.setAdapter(new ArrayAdapter<String>(this,
		// R.layout.drawer_list_item, mPlanetTitles));
		mFacetsList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_facets_open, /*
									 * "open drawer" description for accessibility
									 */
		R.string.drawer_facets_close /*
									 * "close drawer" description for accessibility
									 */
		) {
			public void onDrawerClosed(View view) {
				// getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				// getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

		MenuItem share = menu.findItem(R.id.action_share);
		// Fetch and store ShareActionProvider
		mShareActionProvider = (ShareActionProvider) share.getActionProvider();
		if (mShareActionProvider != null) {
			mShareActionProvider.setShareIntent(createShareIntent());
		}

		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	@Override
	public void onSearchStart() {
		// TODO Search animation
	}

	@Override
	public void onSearchError(String message) {
		// TODO Report error
	}

	@Override
	public void onSearchFinish(SearchResult results) {
		// TODO Add results to fragment
	}

	@Override
	public void onSearchFacetsUpdate(List<Facet> facets) {
		// TODO Auto-generated method stub
	}

	private Intent createShareIntent() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, "www.europeana.eu");
		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this search on Europeana.eu!");
		return shareIntent;
	}

	private void handleIntent(Intent intent) {
		String query = null;
		if (intent != null) {
			if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
				query = intent.getStringExtra(SearchManager.QUERY);
			} else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
				query = intent.getDataString();
			} else {
				onSearchRequested();
			}
			if (!TextUtils.isEmpty(query)) {
				SearchController.getInstance().newSearch(this, query);
			}
		}
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// selectItem(position);
		}
	}

}
