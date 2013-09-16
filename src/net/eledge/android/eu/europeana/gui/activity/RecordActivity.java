package net.eledge.android.eu.europeana.gui.activity;

import java.util.List;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.gui.adapter.RecordPagerAdapter;
import net.eledge.android.eu.europeana.gui.adapter.ResultAdapter;
import net.eledge.android.eu.europeana.gui.dialog.AboutDialog;
import net.eledge.android.eu.europeana.search.RecordController;
import net.eledge.android.eu.europeana.search.SearchController;
import net.eledge.android.eu.europeana.search.model.record.Record;
import net.eledge.android.toolkit.async.listener.TaskListener;
import net.eledge.android.toolkit.gui.GuiUtils;

import org.apache.commons.lang.StringUtils;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class RecordActivity extends ActionBarActivity implements TaskListener<Record>, TabListener {
	
	public static final String RECORD_ID = "RECORDID";
	
	// Controller
	private SearchController searchController = SearchController._instance;
	private RecordController recordController = RecordController._instance;
	
	// ViewPager
	private RecordPagerAdapter mRecordPagerAdapter;
    private ViewPager mViewPager;
	
	// NavigationDrawer
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mResultsList;
	private ResultAdapter mResultAdaptor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		recordController.registerListener(RecordActivity.class, this);
		
		mResultsList = (ListView) findViewById(R.id.drawer_items);
		mResultAdaptor = new ResultAdapter((EuropeanaApplication) getApplication(), this, searchController.getSearchItems());
		mResultsList.setAdapter(mResultAdaptor);

		// ViewPager
		mRecordPagerAdapter = new RecordPagerAdapter(this, getSupportFragmentManager(), getApplicationContext());
        mViewPager = (ViewPager) findViewById(R.id.activity_record_pager);
        mViewPager.setAdapter(mRecordPagerAdapter);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                    	getSupportActionBar().setSelectedNavigationItem(position);
                    }
                });
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Drawer layout
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (searchController.hasResults()) {
	        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_activity_record);
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
			}
        }
		
		handleIntent(getIntent());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.record, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (mDrawerLayout != null) {
			boolean drawerOpen = mDrawerLayout.isDrawerOpen(mResultsList);
			menu.findItem(R.id.action_share).setVisible(!drawerOpen);
			if (drawerOpen && (searchController.getItemSelected() != -1)) {
				mResultsList.smoothScrollToPosition(searchController.getItemSelected());
			}
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}
	
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
	
	@Override
	protected void onDestroy() {
		recordController.unregister(Record.class);
		super.onDestroy();
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
		case android.R.id.home:
	        if (!searchController.hasResults()) {
	        	GuiUtils.startTopActivity(this, HomeActivity.class);
	        	this.finish();
	        	return true;
	        }
	        Intent upIntent = NavUtils.getParentActivityIntent(this);
	        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
	            TaskStackBuilder.create(this)
	                    .addNextIntentWithParentStack(upIntent)
	                    .startActivities();
	        } else {
	            NavUtils.navigateUpTo(this, upIntent);
	        }
	        return true;
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
		if (mDrawerLayout != null) {
			mDrawerToggle.syncState();
		}
	}

	public void updateTabs() {
		getSupportActionBar().removeAllTabs();
		for (int i = 0; i < mRecordPagerAdapter.getCount(); i++) {
			getSupportActionBar().addTab(
					getSupportActionBar().newTab()
	                        .setText(mRecordPagerAdapter.labels.get(i).intValue())
	                        .setTabListener(RecordActivity.this));
	    }
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		String id = null;
		if (intent != null) {
			if (Intent.ACTION_VIEW.equals(intent.getAction())) {
				id = intent.getDataString();
				if (!TextUtils.isEmpty(id)) {
					if (StringUtils.contains(id, "europeana.eu/")) {
						Uri uri = Uri.parse(id);
						List<String> paths = uri.getPathSegments();
						if ( (paths != null) && (paths.size() == 4)) {
							String collectionId = paths.get(paths.size()-2);
							String recordId = StringUtils.removeEnd(paths.get(paths.size()-1), ".html");
							id = StringUtils.join(new String[] {"/", collectionId ,"/", recordId});
						} else {
							// invalid url/id, cancel opening record
							id = null;
						}
					}
				} else {
					id = intent.getStringExtra(RECORD_ID);
				}
			}
			if (StringUtils.isNotBlank(id)) {
				openRecord(id);
			}
		}
	}
	
	private void openRecord(String id) {
		recordController.readRecord(this, id);
	}
	
	@Override
	public void onTaskFinished(Record record) {
	}
	
	private Intent createShareIntent() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, recordController.getPortalUrl());
		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this item on Europeana.eu!");
		return shareIntent;
	}

}
