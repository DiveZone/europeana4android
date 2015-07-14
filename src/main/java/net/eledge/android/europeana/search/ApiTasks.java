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
import net.eledge.android.europeana.search.event.SuggestionsLoadedEvent;
import net.eledge.android.europeana.search.model.SearchFacets;
import net.eledge.android.europeana.search.model.SearchItems;
import net.eledge.android.europeana.search.model.Suggestions;
import net.eledge.android.europeana.search.model.record.RecordObject;
import net.eledge.android.europeana.search.model.suggestion.Suggestion;
import net.eledge.android.europeana.tools.UriHelper;
import net.eledge.android.toolkit.net.JsonUtils;
import net.eledge.android.toolkit.net.abstracts.AsyncLoaderListener;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ApiTasks {

    private static ApiTasks _instance;

    private final Map<String, Suggestion[]> suggestionCache = new HashMap<>();

    public int suggestionPageSize = 12;

    private AsyncTask<String, Void, Suggestions> mSuggestionTask;
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
            mSearchTask = JsonUtils.parseJson(
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

    void runSuggestions(final String query) {
        if (mSuggestionTask != null) {
            mSuggestionTask.cancel(true);
        }
        if (query != null) {
            if (suggestionCache.containsKey(query)) {
                EuropeanaApplication.bus.post(new SuggestionsLoadedEvent(suggestionCache.get(query)));
            } else {
                String url = UriHelper.getSuggestionUrl(query, suggestionPageSize);
                if (url != null) {
                    mSuggestionTask = JsonUtils.parseJson(
                            new AsyncLoaderListener<Suggestions>() {
                                @Override
                                public void onFinished(Suggestions suggestions, int httpStatus, long timing) {
                                    if (suggestions != null) {
                                        suggestionCache.put(query, suggestions.items);
                                        Tracker tracker = EuropeanaApplication._instance.getAnalyticsTracker();
                                        tracker.send(new HitBuilders.TimingBuilder()
                                                .setCategory("Tasks")
                                                .setValue(timing)
                                                .setVariable("SuggestionTask")
                                                .setLabel(query).build());
                                        EuropeanaApplication.bus
                                                .post(new SuggestionsLoadedEvent(suggestions.items));
                                    }
                                }
                            },
                            url, Config.JSON_CHARSET);
                }
            }
        }
    }

    void runSearchFacets(String[] terms) {
        if (mSearchFacetTask != null) {
            mSearchFacetTask.cancel(true);
        }
        if (ArrayUtils.isNotEmpty(terms)) {
            mSearchFacetTask = JsonUtils.parseJson(
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
            mRecordTask = JsonUtils.parseJson(
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
