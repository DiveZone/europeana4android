package net.eledge.android.eu.europeana.gui.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import com.google.analytics.tracking.android.EasyTracker;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.db.dao.SearchProfileDao;
import net.eledge.android.eu.europeana.db.model.SearchProfile;
import net.eledge.android.eu.europeana.db.setup.DatabaseSetup;
import net.eledge.android.eu.europeana.gui.adapter.SuggestionAdapter;
import net.eledge.android.eu.europeana.gui.dialog.MyEuropeanaDialog;
import net.eledge.android.eu.europeana.gui.dialog.MyEuropeanaOauthDialog;
import net.eledge.android.eu.europeana.gui.fragment.HomeBlogFragment;
import net.eledge.android.eu.europeana.search.SearchController;
import net.eledge.android.eu.europeana.search.model.suggestion.Item;
import net.eledge.android.toolkit.async.listener.TaskListener;
import net.eledge.android.toolkit.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends FragmentActivity implements TaskListener<Item[]>, OnItemClickListener {

    private EuropeanaApplication mApplication;

    private GridView mGridViewSuggestions;
    private SuggestionAdapter mSuggestionsAdaptor;

    private Spinner mSpinnerProfiles;
    private ArrayAdapter<SearchProfile> mProfilesAdapter;

	private HomeBlogFragment mBlogFragment;

	private final SearchController searchController = SearchController._instance;
	
	private boolean isLandscape;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mApplication = (EuropeanaApplication) getApplication();
		setContentView(R.layout.activity_home);

        PreferenceManager.setDefaultValues(this, R.xml.settings_locale, false);

		searchController.suggestionPagesize = getResources().getInteger(R.integer.home_suggestions_pagesize);
		isLandscape = getResources().getBoolean(R.bool.home_support_landscape);

		mSuggestionsAdaptor = new SuggestionAdapter(this, new ArrayList<Item>());
		mGridViewSuggestions = (GridView) findViewById(R.id.activity_home_gridview_suggestions);
		mGridViewSuggestions.setAdapter(mSuggestionsAdaptor);
		mGridViewSuggestions.setOnItemClickListener(this);

        EditText mEditTextQuery = (EditText) findViewById(R.id.activity_home_edittext_query);
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
        mSpinnerProfiles = (Spinner) findViewById(R.id.activity_home_spinner_profile);
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
        EasyTracker.getInstance(this).activityStart(this);
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
        for (SearchProfile sp: profiles) {
            mProfilesAdapter.add(sp);
        }
        mProfilesAdapter.notifyDataSetChanged();
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
        SearchProfile selected = (SearchProfile) mSpinnerProfiles.getSelectedItem();
        if ((selected != null) && (selected.facets != null)) {
            intent.putExtra(SearchManager.USER_QUERY, selected.facets);
        }
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
        case R.id.action_myeuropeana:
            if (!mApplication.isMyEuropeanaConnected()) {
                DialogFragment newFragment = new MyEuropeanaOauthDialog();
                newFragment.show(getSupportFragmentManager(), "login");
            } else {
                DialogFragment newFragment = new MyEuropeanaDialog(mApplication);
                newFragment.show(getSupportFragmentManager(), "profile");
            }
            break;
        case R.id.action_settings:
            Intent i = new Intent(this, SettingsActivity.class);
            startActivityForResult(i,1);
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
