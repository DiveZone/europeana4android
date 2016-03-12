package net.eledge.android.europeana.search;

import android.os.AsyncTask;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import net.eledge.android.europeana.Config;
import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.search.event.RecordLoadedEvent;
import net.eledge.android.europeana.search.event.RecordLoadingEvent;
import net.eledge.android.europeana.search.event.SearchFacetsLoadedEvent;
import net.eledge.android.europeana.search.event.SearchItemsLoadedEvent;
import net.eledge.android.europeana.search.event.SearchStartedEvent;
import net.eledge.android.europeana.search.model.SearchFacets;
import net.eledge.android.europeana.search.model.SearchItems;
import net.eledge.android.europeana.search.model.record.RecordObject;
import net.eledge.android.europeana.tools.UriHelper;
import net.eledge.android.toolkit.net.JsonUtils;
import net.eledge.android.toolkit.net.abstracts.AsyncLoaderListener;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class ApiTasks {

    private static ApiTasks _instance;

    private AsyncTask<String, Void, SearchItems> mSearchTask;
    private AsyncTask<String, Void, SearchFacets> mSearchFacetTask;
    private AsyncTask<String, Void, RecordObject> mRecordTask;

    private ApiTasks() {

    }

    public static ApiTasks getInstance() {
        if (_instance == null) {
            _instance = new ApiTasks();
        }
        return _instance;
    }

    void runSearch(String[] terms, int page, int pageSize) {
        cancelSearchTasks();
        if (ArrayUtils.isNotEmpty(terms)) {
            EuropeanaApplication.bus.post(new SearchStartedEvent(false));
            mSearchTask = JsonUtils.parseJson(SearchItems.class,
                    new AsyncLoaderListener<SearchItems>() {
                        @Override
                        public void onFinished(SearchItems result, int httpStatus, long timing) {
                            Tracker tracker = EuropeanaApplication._instance.getAnalyticsTracker();
                            tracker.send(new HitBuilders.TimingBuilder()
                                    .setCategory("Tasks")
                                    .setValue(timing)
                                    .setVariable("SearchTask").build());
                            if (!mSearchTask.isCancelled()) {
                                EuropeanaApplication.bus.post(new SearchItemsLoadedEvent(result));
                            }
                        }
                    }, UriHelper.getSearchUrl(terms, page, pageSize), Config.JSON_CHARSET);
        }
    }

    void runSearchFacets(String[] terms) {
        if (mSearchFacetTask != null) {
            mSearchFacetTask.cancel(true);
        }
        if (ArrayUtils.isNotEmpty(terms)) {
            mSearchFacetTask = JsonUtils.parseJson(SearchFacets.class,
                    new AsyncLoaderListener<SearchFacets>() {
                        @Override
                        public void onFinished(SearchFacets result, int httpStatus, long timing) {
                            Tracker tracker = EuropeanaApplication._instance.getAnalyticsTracker();
                            tracker.send(new HitBuilders.TimingBuilder()
                                    .setCategory("Tasks")
                                    .setValue(timing)
                                    .setVariable("SearchFacetTask").build());
                            if (!mSearchFacetTask.isCancelled()) {
                                EuropeanaApplication.bus.post(new SearchFacetsLoadedEvent(result));
                            }

                        }
                    }, UriHelper.getSearchUrl(terms, 1, 1), Config.JSON_CHARSET);
        }
    }

    void loadRecord(final String recordId) {
        if (StringUtils.isNoneBlank(recordId)) {
            if (mRecordTask != null) {
                mRecordTask.cancel(true);
            }
            EuropeanaApplication.bus.post(new RecordLoadingEvent());
            mRecordTask = JsonUtils.parseJson(RecordObject.class,
                    new AsyncLoaderListener<RecordObject>() {
                        @Override
                        public void onFinished(RecordObject result, int httpStatus, long timing) {
                            Tracker tracker = EuropeanaApplication._instance.getAnalyticsTracker();
                            tracker.send(new HitBuilders.TimingBuilder()
                                    .setCategory("Tasks")
                                    .setValue(timing)
                                    .setVariable("RecordTask")
                                    .setLabel(recordId).build());
                            EuropeanaApplication.bus.post(new RecordLoadedEvent(result));
                        }
                    }, UriHelper.getRecordUrl(recordId), Config.JSON_CHARSET);
        }
    }

    void cancelSearchTasks() {
        if (mSearchTask != null) {
            mSearchTask.cancel(true);
        }
        if (mSearchFacetTask != null) {
            mSearchFacetTask.cancel(true);
        }
    }

    public boolean isSearching() {
        return (mSearchTask != null) && (mSearchTask.getStatus() != AsyncTask.Status.FINISHED);
    }
}
