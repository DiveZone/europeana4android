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
