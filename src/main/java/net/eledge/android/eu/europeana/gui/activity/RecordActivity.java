package net.eledge.android.eu.europeana.gui.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.analytics.tracking.android.EasyTracker;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.gui.adapter.RecordPagerAdapter;
import net.eledge.android.eu.europeana.gui.adapter.ResultAdapter;
import net.eledge.android.eu.europeana.gui.dialog.AboutDialog;
import net.eledge.android.eu.europeana.gui.fragments.RecordDetailsFragment;
import net.eledge.android.eu.europeana.search.RecordController;
import net.eledge.android.eu.europeana.search.SearchController;
import net.eledge.android.eu.europeana.search.model.record.RecordObject;
import net.eledge.android.toolkit.async.listener.TaskListener;
import net.eledge.android.toolkit.gui.GuiUtils;

import org.apache.commons.lang.StringUtils;

import java.util.List;

public class RecordActivity extends ActionBarActivity implements TabListener, TaskListener<RecordObject> {

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

    private RecordDetailsFragment mDetailsFragment;
    public boolean mTwoColumns = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        mTwoColumns = getResources().getBoolean(R.bool.home_support_landscape);
        recordController.registerListener(RecordActivity.class, this);

        mResultsList = (ListView) findViewById(R.id.drawer_items);
        mResultAdaptor = new ResultAdapter((EuropeanaApplication) getApplication(), this,
                searchController.getSearchItems());
        mResultsList.setAdapter(mResultAdaptor);
        mResultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchController.setItemSelected(position);
                openRecord(searchController.getSearchItems().get(position).id);
                if (mDrawerLayout != null) {
                    mDrawerLayout.closeDrawers();
                }
            }
        });

        // ViewPager
        mRecordPagerAdapter = new RecordPagerAdapter(this, getSupportFragmentManager(), getApplicationContext());
        mViewPager = (ViewPager) findViewById(R.id.activity_record_pager);
        mViewPager.setAdapter(mRecordPagerAdapter);

        if (mTwoColumns) {
            // Record details for tablets.
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (mDetailsFragment == null) {
                mDetailsFragment = new RecordDetailsFragment();
            }
            fragmentTransaction.replace(R.id.activity_record_fragment_details, mDetailsFragment);
            fragmentTransaction.commit();
        } else {
            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    getSupportActionBar().setSelectedNavigationItem(position);
                }
            });
        }

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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            createNdefPushMessageCallback();
        }
        handleIntent(getIntent());
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void createNdefPushMessageCallback() {
        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter != null) {
            mNfcAdapter.setNdefPushMessageCallback(new CreateNdefMessageCallback() {
                @Override
                public NdefMessage createNdefMessage(NfcEvent event) {
                    return new NdefMessage(new NdefRecord[]{
                            new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                                    "application/vnd.net.eledge.android.eu.europeana.record".getBytes(), new byte[0],
                                    recordController.getPortalUrl().getBytes()),
                            NdefRecord.createApplicationRecord(getPackageName())});
                }
            }, this);
        }
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
        menu.findItem(R.id.action_previous).setVisible(searchController.hasPrevious());
        menu.findItem(R.id.action_next).setVisible(searchController.hasNext());
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
    protected void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onDestroy() {
        recordController.unregister(RecordActivity.class);
        super.onDestroy();
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
    public void onTaskStart() {
        switchViews(true);
    }

    @Override
    public void onTaskFinished(RecordObject result) {
        switchViews(false);
    }

    private void switchViews(boolean showLoading) {
        findViewById(R.id.include_record_loading).setVisibility(showLoading?View.VISIBLE:View.GONE);
        findViewById(R.id.activity_record_pager).setVisibility(!showLoading?View.VISIBLE:View.GONE);
        if (mTwoColumns) {
            findViewById(R.id.activity_record_fragment_details).setVisibility(!showLoading?View.VISIBLE:View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ((mDrawerLayout != null) && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_previous:
                int pos = searchController.getItemSelected() - 1;
                searchController.setItemSelected(pos);
                openRecord(searchController.getSearchItems().get(pos).id);
                break;
            case R.id.action_next:
                pos = searchController.getItemSelected() + 1;
                searchController.setItemSelected(pos);
                openRecord(searchController.getSearchItems().get(pos).id);
                break;
            case R.id.action_about:
                try {
                    Dialog dialog = new AboutDialog(this, (EuropeanaApplication) getApplication(), getPackageManager()
                            .getPackageInfo(getPackageName(), 0));
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                } catch (NameNotFoundException e) {
                    // ignore
                }
                break;
            case R.id.action_share:
                startActivity(createShareIntent());
                break;
            case R.id.action_search:
                GuiUtils.startTopActivity(this, HomeActivity.class);
                break;
            case android.R.id.home:
                if (!searchController.hasResults()) {
                    GuiUtils.startTopActivity(this, HomeActivity.class);
                    this.finish();
                    return true;
                }
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
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
        mTwoColumns = getResources().getBoolean(R.bool.home_support_landscape);
        if (mDrawerLayout != null) {
            mDrawerToggle.syncState();
        }
    }

    public void updateTabs() {
        if (!mTwoColumns) {
            getSupportActionBar().removeAllTabs();
            if (mRecordPagerAdapter.getCount() > 0) {
                for (int i = 0; i < mRecordPagerAdapter.getCount(); i++) {
                    getSupportActionBar().addTab(
                            getSupportActionBar().newTab().setText(mRecordPagerAdapter.labels.get(i).intValue())
                                    .setTabListener(RecordActivity.this));
                }
            }
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
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
                Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                // only one message sent during the beam
                NdefMessage msg = (NdefMessage) rawMsgs[0];
                // record 0 contains the MIME type, record 1 is the AAR, if present
                id = new String(msg.getRecords()[0].getPayload());
            } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                id = StringUtils.defaultIfBlank(intent.getDataString(), intent.getStringExtra(RECORD_ID));
            }
            if (StringUtils.contains(id, "europeana.eu/")) {
                Uri uri = Uri.parse(id);
                List<String> paths = uri.getPathSegments();
                if ((paths != null) && (paths.size() == 4)) {
                    String collectionId = paths.get(paths.size() - 2);
                    String recordId = StringUtils.removeEnd(paths.get(paths.size() - 1), ".html");
                    id = StringUtils.join(new String[]{"/", collectionId, "/", recordId});
                } else {
                    // invalid url/id, cancel opening record
                    id = null;
                }
            }
            if (StringUtils.isNotBlank(id)) {
                openRecord(id);
            }
        }
    }

    private void openRecord(String id) {
        supportInvalidateOptionsMenu();
        recordController.readRecord(this, id);
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, recordController.getPortalUrl());
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this item on Europeana.eu!");
        return shareIntent;
    }

}
