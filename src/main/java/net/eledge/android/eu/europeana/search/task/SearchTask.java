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

package net.eledge.android.eu.europeana.search.task;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.search.SearchController;
import net.eledge.android.eu.europeana.search.listeners.SearchTaskListener;
import net.eledge.android.eu.europeana.search.model.SearchItems;
import net.eledge.android.eu.europeana.tools.UriHelper;

import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

public class SearchTask extends AsyncTask<String, Void, SearchItems> {

    private int pageLoad = 1;

    private long startTime;

    private final SearchController searchController = SearchController._instance;
    private final Activity mActivity;

    public SearchTask(Activity activity, int pageLoad) {
        super();
        mActivity = activity;
        this.pageLoad = pageLoad;
    }

    @Override
    protected void onPreExecute() {
        for (SearchTaskListener l : searchController.listeners.values()) {
            if (isCancelled()) {
                return;
            }
            if (l != null) {
                l.onSearchStart(false);
            }
        }
        startTime = new Date().getTime();
    }

    @Override
    protected SearchItems doInBackground(String... terms) {
        String url = UriHelper.getSearchUrl(((EuropeanaApplication) mActivity.getApplication()).getEuropeanaPublicKey(), terms, pageLoad,
                searchController.searchPagesize);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        return restTemplate.getForObject(url, SearchItems.class);
    }

    @Override
    protected void onPostExecute(SearchItems result) {
        EasyTracker tracker = EasyTracker.getInstance(mActivity);
        tracker.send(MapBuilder
                .createTiming("Tasks",
                        new Date().getTime() - startTime,
                        "SearchTask",
                        null)
                .build());
        if (isCancelled()) {
            return;
        }
        mActivity.runOnUiThread(new ListenerNotifier(result));
    }

    private class ListenerNotifier implements Runnable {

        private final SearchItems result;

        public ListenerNotifier(SearchItems result) {
            this.result = result;
        }

        public void run() {
            searchController.onSearchFinish(result);
            for (SearchTaskListener l : searchController.listeners.values()) {
                if (l != null) {
                    l.onSearchItemsFinish(result);
                }
            }
        }
    }

}