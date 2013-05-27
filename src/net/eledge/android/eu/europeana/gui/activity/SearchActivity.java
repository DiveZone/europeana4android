package net.eledge.android.eu.europeana.gui.activity;

import net.eledge.android.eu.europeana.Config;
import net.eledge.android.eu.europeana.R;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.ShareActionProvider;

public class SearchActivity extends FragmentActivity {
	
	private ShareActionProvider mShareActionProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		if (Config.DEBUGMODE) {
			StrictMode.enableDefaults();
		}
		handleIntent(getIntent());
		
//		mSearchFragment = (SearchResultsFragment) getFragmentManager().findFragmentById(R.id.fragment_search_results);
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
	
	private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "www.europeana.eu");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this search on Europeana.eu!");
        return shareIntent;
    }

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
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
				// mQuery = query.toLowerCase();
				// mSearchFragment.search(mQuery);
			}
		}
	}

}
