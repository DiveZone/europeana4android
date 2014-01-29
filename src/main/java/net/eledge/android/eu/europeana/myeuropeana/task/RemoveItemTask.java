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

package net.eledge.android.eu.europeana.myeuropeana.task;

import android.os.AsyncTask;
import android.util.Log;

import net.eledge.android.eu.europeana.search.RecordController;

import org.springframework.social.europeana.api.Europeana;

public class RemoveItemTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = RemoveItemTask.class.getSimpleName();

    private final Europeana mEuropeanaApi;

    public RemoveItemTask(Europeana europeana) {
        mEuropeanaApi = europeana;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            mEuropeanaApi.savedItemsOperations().deleteByEuropeanaId(RecordController._instance.getCurrentRecordId());
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
        return null;
    }

}
