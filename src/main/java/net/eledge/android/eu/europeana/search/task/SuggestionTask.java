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

import net.eledge.android.eu.europeana.search.SearchController;
import net.eledge.android.eu.europeana.search.model.Suggestions;
import net.eledge.android.eu.europeana.search.model.suggestion.Item;
import net.eledge.android.eu.europeana.tools.UriHelper;
import net.eledge.android.toolkit.async.ListenerNotifier;
import net.eledge.android.toolkit.async.listener.TaskListener;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

public class SuggestionTask extends AsyncTask<String, Void, Item[]> {

    private final SearchController searchController = SearchController._instance;

    private final TaskListener<Item[]> listener;

    private String term;

    private long startTime;

    public SuggestionTask(TaskListener<Item[]> listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        startTime = new Date().getTime();
    }

    @Override
    protected Item[] doInBackground(String... params) {
        if (StringUtils.isBlank(params[0])) {
            return null;
        }
        term = params[0];
        String url = UriHelper.getSuggestionUrl(term, searchController.suggestionPageSize);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        return restTemplate.getForObject(url, Suggestions.class).items;
    }

    @Override
    protected void onPostExecute(Item[] suggestions) {
        searchController.cacheSuggestions(term, suggestions);
        if (listener instanceof Activity) {
            EasyTracker tracker = EasyTracker.getInstance((Activity) listener);
            tracker.send(MapBuilder
                    .createTiming("Tasks",
                            new Date().getTime() - startTime,
                            "SuggestionTask",
                            term)
                    .build());
            ((Activity) listener).runOnUiThread(new ListenerNotifier<>(listener, suggestions));
        } else {
            listener.onTaskFinished(suggestions);
        }
    }

}
