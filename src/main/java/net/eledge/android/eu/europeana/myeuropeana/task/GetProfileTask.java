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

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import net.eledge.android.toolkit.async.ListenerNotifier;
import net.eledge.android.toolkit.async.listener.TaskListener;

import org.springframework.http.HttpStatus;
import org.springframework.social.europeana.api.Europeana;
import org.springframework.social.europeana.api.model.Profile;
import org.springframework.web.client.HttpClientErrorException;

public class GetProfileTask extends AsyncTask<Void, Void, Profile> {
    private static final String TAG = GetProfileTask.class.getSimpleName();

    private final Activity mActivity;
    private final TaskListener<Profile> mListener;
    private final Europeana mEuropeanaApi;

    public GetProfileTask(Activity activity, Europeana europeana, TaskListener<Profile> listener) {
        mActivity = activity;
        mEuropeanaApi = europeana;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        mActivity.runOnUiThread(new ListenerNotifier<>(mListener));
    }

    @Override
    protected Profile doInBackground(Void... params) {
        try {
            return mEuropeanaApi.profileOperations().getProfile();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                // TODO: what??
            }
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Profile profile) {
        mActivity.runOnUiThread(new ListenerNotifier<>(mListener, profile));
    }

}
