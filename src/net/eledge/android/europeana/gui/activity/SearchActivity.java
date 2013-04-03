package net.eledge.android.europeana.gui.activity;

import net.eledge.android.europeana.Config;
import net.eledge.android.europeana.R;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.Menu;

public class SearchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		if (Config.DEBUGMODE) {
			StrictMode.enableDefaults();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		String query = null;
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
