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

package net.eledge.android.europeana.search.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.search.RecordController;
import net.eledge.android.europeana.search.model.Record;
import net.eledge.android.europeana.search.model.record.RecordObject;
import net.eledge.android.europeana.tools.UriHelper;
import net.eledge.android.toolkit.async.ListenerNotifier;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RecordTask extends AsyncTask<String, Void, RecordObject> {

    private final RecordController recordController = RecordController._instance;

    private final Activity mActivity;

    private String recordId;

    private DateTime startTime;

    public RecordTask(Activity activity) {
        super();
        mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        startTime = DateTime.now();
        mActivity.runOnUiThread(new ListenerNotifier<>(recordController.listeners.values()));
    }

    @Override
    protected RecordObject doInBackground(String... params) {
        if (TextUtils.isEmpty(params[0])) {
            return null;
        }
        recordId = params[0];
        String url = UriHelper.getRecordUrl(((EuropeanaApplication) mActivity.getApplication()).getEuropeanaPublicKey(), recordId);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return RecordObject.normalize(restTemplate.getForObject(url, Record.class).object);
    }

    @Override
    protected void onPostExecute(RecordObject result) {
        DateTime endTime = DateTime.now();
        Interval interval = new Interval(startTime, endTime);
        Tracker tracker = ((EuropeanaApplication) mActivity.getApplication()).getAnalyticsTracker();
        tracker.send(new HitBuilders.TimingBuilder()
                .setCategory("Tasks")
                .setValue(interval.toDurationMillis())
                .setVariable("RecordTask")
                .setLabel(recordId).build());
        recordController.record = result;
        mActivity.runOnUiThread(new ListenerNotifier<>(recordController.listeners.values(), result));
    }

}
