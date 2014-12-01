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

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.search.SearchController;
import net.eledge.android.eu.europeana.search.listeners.SearchTaskListener;
import net.eledge.android.eu.europeana.search.model.SearchFacets;
import net.eledge.android.eu.europeana.tools.UriHelper;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

public class SearchFacetTask extends AsyncTask<String, Void, SearchFacets> {

    private final SearchController searchController = SearchController._instance;
    private final Activity mActivity;

    private long startTime;

    public SearchFacetTask(Activity activity) {
        super();
        mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        for (SearchTaskListener l : searchController.listeners.values()) {
            if (isCancelled()) {
                return;
            }
            if (l != null) {
                l.onSearchStart(true);
            }
        }
        startTime = new Date().getTime();
    }

    @Override
    protected SearchFacets doInBackground(String... terms) {
        String url = UriHelper.getSearchUrl(((EuropeanaApplication) mActivity.getApplication()).getEuropeanaPublicKey(), terms, 1, 1);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate.getForObject(url, SearchFacets.class);
    }

    @Override
    protected void onPostExecute(SearchFacets result) {
        Tracker tracker = ((EuropeanaApplication) mActivity.getApplication()).getAnalyticsTracker();
        tracker.send(new HitBuilders.TimingBuilder().setCategory("Tasks").setValue(new Date().getTime() - startTime).setVariable("SearchFacetTask").build());
        if (isCancelled()) {
            return;
        }

        mActivity.runOnUiThread(new ListenerNotifier(result));
    }

    private class ListenerNotifier implements Runnable {

        private final SearchFacets result;

        public ListenerNotifier(SearchFacets result) {
            this.result = result;
        }

        public void run() {
            searchController.onSearchFacetFinish(result);
            for (SearchTaskListener l : searchController.listeners.values()) {
                if (l != null) {
                    l.onSearchFacetFinish();
                }
            }
        }
    }

}