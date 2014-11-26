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

package net.eledge.android.eu.europeana.gui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.gui.adapter.RecordPagerAdapter;
import net.eledge.android.eu.europeana.gui.adapter.ResultAdapter;
import net.eledge.android.eu.europeana.gui.dialog.NameInputDialog;
import net.eledge.android.eu.europeana.gui.fragment.RecordDetailsFragment;
import net.eledge.android.eu.europeana.myeuropeana.task.CheckItemTask;
import net.eledge.android.eu.europeana.myeuropeana.task.RemoveItemTask;
import net.eledge.android.eu.europeana.myeuropeana.task.SaveItemTask;
import net.eledge.android.eu.europeana.myeuropeana.task.SaveTagTask;
import net.eledge.android.eu.europeana.search.RecordController;
import net.eledge.android.eu.europeana.search.SearchController;
import net.eledge.android.eu.europeana.search.model.record.RecordObject;
import net.eledge.android.eu.europeana.search.model.searchresults.Item;
import net.eledge.android.toolkit.async.listener.TaskListener;
import net.eledge.android.toolkit.gui.GuiUtils;
import net.eledge.android.toolkit.gui.annotations.ViewResource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.social.europeana.api.Europeana;
import org.springframework.social.europeana.api.model.UserModification;

import java.util.List;

import static net.eledge.android.toolkit.gui.ViewInjector.inject;

public class RecordActivity extends ActionBarActivity implements TabListener, TaskListener<RecordObject>, NameInputDialog.NameInputDialogListener {

    public static final String RECORD_ID = "RECORDID";

    private EuropeanaApplication mApplication;
    private Europeana mEuropeanaApi;

    // Controller
    private final SearchController searchController = SearchController._instance;
    private final RecordController recordController = RecordController._instance;

    // Fragments
    private RecordDetailsFragment mDetailsFragment;

    // Views
    @ViewResource(R.id.drawer_items)
    private RecyclerView mResultsList;
    @ViewResource(value = R.id.drawerlayout_activity_record, optional = true)
    private DrawerLayout mDrawerLayout;
    @ViewResource(R.id.activity_record_pager)
    private ViewPager mViewPager;
    // NavigationDrawer
    private ActionBarDrawerToggle mDrawerToggle;

    // ViewPager
    private RecordPagerAdapter mRecordPagerAdapter;

    public boolean mTwoColumns = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        mApplication = (EuropeanaApplication) getApplication();
        mEuropeanaApi = mApplication.getMyEuropeanaApi();
        mTwoColumns = getResources().getBoolean(R.bool.home_support_landscape);
        recordController.registerListener(RecordActivity.class, this);

        ResultAdapter mResultAdaptor = new ResultAdapter((EuropeanaApplication) getApplication(),
                searchController.getSearchItems(), new ResultAdapter.ResultAdaptorClickListener() {
            @Override
            public void click(int position, Item item) {
                searchController.setItemSelected(position);
                openRecord(item.id);
                if (mDrawerLayout != null) {
                    mDrawerLayout.closeDrawers();
                }
            }
        });
        mResultsList.setAdapter(mResultAdaptor);

        // ViewPager
        mRecordPagerAdapter = new RecordPagerAdapter(this, getSupportFragmentManager(), getApplicationContext());
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
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);

        if (searchController.hasResults()) {
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
        menu.findItem(R.id.action_new_label).setVisible(mApplication.isMyEuropeanaConnected());
        MenuItem item = menu.findItem(R.id.action_save_item);
        item.setVisible(mApplication.isMyEuropeanaConnected());
        item.setIcon(recordController.isCurrentRecordSelected() ? R.drawable.ic_action_important : R.drawable.ic_action_not_important);
        item.setTitle(recordController.isCurrentRecordSelected() ? R.string.action_remove_item : R.string.action_save_item);
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
    public void onTaskStart() {
        switchViews(true);
    }

    @Override
    public void onTaskFinished(RecordObject result) {
        switchViews(false);
    }

    private void switchViews(boolean showLoading) {
        findViewById(R.id.include_record_loading).setVisibility(showLoading ? View.VISIBLE : View.GONE);
        findViewById(R.id.activity_record_pager).setVisibility(!showLoading ? View.VISIBLE : View.GONE);
        if (mTwoColumns) {
            findViewById(R.id.activity_record_fragment_details).setVisibility(!showLoading ? View.VISIBLE : View.GONE);
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
            case R.id.action_share:
                startActivity(createShareIntent());
                break;
            case R.id.action_new_label:
                saveLabel();
                break;
            case R.id.action_save_item:
                if (recordController.isCurrentRecordSelected()) {
                    new RemoveItemTask(mEuropeanaApi).execute();
                } else {
                    new SaveItemTask(this, mEuropeanaApi, null).execute();
                }
                recordController.setCurrentRecordSelected(!recordController.isCurrentRecordSelected());
                supportInvalidateOptionsMenu();
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
                            getSupportActionBar().newTab().setText(mRecordPagerAdapter.labels.get(i))
                                    .setTabListener(RecordActivity.this)
                    );
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
                    id = StringUtils.join("/", collectionId, "/", recordId);
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
        if (mEuropeanaApi != null) {
            new CheckItemTask(this, mEuropeanaApi, new TaskListener<Boolean>() {
                @Override
                public void onTaskStart() {
                    // ignore
                }

                @Override
                public void onTaskFinished(Boolean result) {
                    recordController.setCurrentRecordSelected(result);
                    RecordActivity.this.supportInvalidateOptionsMenu();
                }
            }).execute();
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, recordController.getPortalUrl());
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this item on Europeana.eu!");
        return shareIntent;
    }

    private void saveLabel() {
        if (mApplication.isMyEuropeanaConnected()) {
            Bundle bundle = new Bundle();
            bundle.putInt(NameInputDialog.KEY_RESTITLE_INT, R.string.action_new_label);
            bundle.putInt(NameInputDialog.KEY_RESTEXT_INT, R.string.dialog_tag_saveas_text);
            bundle.putInt(NameInputDialog.KEY_RESINPUT_INT, R.string.dialog_tag_saveas_input);
            bundle.putInt(NameInputDialog.KEY_RESPOSBUTTON_INT, R.string.action_new_label);
            NameInputDialog dialog = new NameInputDialog();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "SaveAs");
        }
    }

    @Override
    public void positiveResponse(String input) {
        if (StringUtils.isNotBlank(input)) {
            new SaveTagTask(RecordActivity.this, mEuropeanaApi, new TaskListener<UserModification>() {
                @Override
                public void onTaskStart() {
                    // ignore
                }

                @Override
                public void onTaskFinished(UserModification result) {
                    if (result != null) {
                        if (result.isSuccess()) {
                            GuiUtils.toast(RecordActivity.this, R.string.msg_tag_saved);
                        } else {
                            GuiUtils.toast(RecordActivity.this, result.getError());
                        }
                    } else {
                        GuiUtils.toast(RecordActivity.this, R.string.msg_unknown_error);
                    }
                }
            }).execute(input);
        }
    }

    @Override
    public void negativeResponse() {
        // ignore
    }
}