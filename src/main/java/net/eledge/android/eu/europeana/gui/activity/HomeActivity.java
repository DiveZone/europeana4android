package net.eledge.android.eu.europeana.gui.activity;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.gui.adapter.SuggestionAdapter;
import net.eledge.android.eu.europeana.gui.dialog.AboutDialog;
import net.eledge.android.eu.europeana.gui.fragments.HomeBlogFragment;
import net.eledge.android.eu.europeana.search.SearchController;
import net.eledge.android.eu.europeana.search.model.suggestion.Item;
import net.eledge.android.toolkit.async.listener.TaskListener;

import java.util.ArrayList;

public class HomeActivity extends FragmentActivity implements TaskListener<Item[]>, OnItemClickListener {

	private SuggestionAdapter mSuggestionsAdaptor;

	private EditText mEditTextQuery;
	private GridView mGridViewSuggestions;

	private HomeBlogFragment mBlogFragment;

	private SearchController searchController = SearchController._instance;
	
	private boolean isLandscape;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		searchController.suggestionPagesize = getResources().getInteger(R.integer.home_suggestions_pagesize);
		isLandscape = getResources().getBoolean(R.bool.home_support_landscape);

		mSuggestionsAdaptor = new SuggestionAdapter(this, new ArrayList<Item>());

		mGridViewSuggestions = (GridView) findViewById(R.id.activity_home_gridview_suggestions);
		mGridViewSuggestions.setAdapter(mSuggestionsAdaptor);
		mGridViewSuggestions.setOnItemClickListener(this);

		mEditTextQuery = (EditText) findViewById(R.id.activity_home_edittext_query);
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
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		isLandscape = getResources().getBoolean(R.bool.home_support_landscape);
	}

	private void performSearch(String query) {
		final Intent intent = new Intent(this, SearchActivity.class);
		intent.setAction(Intent.ACTION_SEARCH);
		intent.putExtra(SearchManager.QUERY, query);
		this.startActivity(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Item suggestion = mSuggestionsAdaptor.getItem(position);
		searchController.suggestionPagesize = getResources().getInteger(R.integer.home_suggestions_pagesize);
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
		case R.id.action_about:
			try {
				Dialog dialog = new AboutDialog(this, (EuropeanaApplication) getApplication(), getPackageManager()
						.getPackageInfo(getPackageName(), 0));
                dialog.setCanceledOnTouchOutside(true);
				dialog.show();
			} catch (NameNotFoundException e) {
			}
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
			for (Item s: suggestions) {
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

}
