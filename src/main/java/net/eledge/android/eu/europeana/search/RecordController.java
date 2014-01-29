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

package net.eledge.android.eu.europeana.search;

import android.app.Activity;

import net.eledge.android.eu.europeana.search.model.record.RecordObject;
import net.eledge.android.eu.europeana.search.task.RecordTask;
import net.eledge.android.eu.europeana.tools.UriHelper;
import net.eledge.android.toolkit.async.ListenerNotifier;
import net.eledge.android.toolkit.async.listener.TaskListener;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class RecordController {

    public final static RecordController _instance = new RecordController();

    private String currentRecordId;
    private boolean currentRecordSelected = false;

    public RecordObject record;

    private RecordTask mRecordTask;

    public final Map<String, TaskListener<RecordObject>> listeners = new HashMap<>();

    public void readRecord(Activity activity, String id) {
        if (StringUtils.isNotBlank(id)) {
            if ((record != null) && StringUtils.equals(id, record.about)) {
                // don't load the same record twice but do notify listeners!;
                activity.runOnUiThread(new ListenerNotifier<>(listeners.values(), record));
                return;
            }
            record = null;
            currentRecordId = id;
            currentRecordSelected = false;
            if (mRecordTask != null) {
                mRecordTask.cancel(true);
            }
            mRecordTask = new RecordTask(activity);
            mRecordTask.execute(id);
        }
    }

    public String getCurrentRecordId() {
        return currentRecordId;
    }

    public boolean isCurrentRecordSelected() {
        return currentRecordSelected;
    }

    public void setCurrentRecordSelected(boolean selected) {
        currentRecordSelected = selected;
    }

    public void registerListener(Class<?> clazz, TaskListener<RecordObject> listener) {
        listeners.put(clazz.getName(), listener);
    }

    public void unregister(Class<?> clazz) {
        listeners.remove(clazz.getName());
    }

    public String getPortalUrl() {
        return UriHelper.getPortalRecordUrl(currentRecordId);
    }

}
