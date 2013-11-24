package net.eledge.android.eu.europeana.myeuropeana.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import net.eledge.android.eu.europeana.search.RecordController;
import net.eledge.android.toolkit.async.ListenerNotifier;
import net.eledge.android.toolkit.async.listener.TaskListener;

import org.springframework.social.europeana.api.Europeana;
import org.springframework.social.europeana.api.model.UserModification;

public class SaveTagTask extends AsyncTask<String, Void, UserModification> {
    private static final String TAG = SaveTagTask.class.getSimpleName();

    private final Activity mActivity;
    private final TaskListener<UserModification> mListener;
    private final Europeana mEuropeanaApi;

    public SaveTagTask(Activity activity, Europeana europeana, TaskListener<UserModification> listener) {
        mActivity = activity;
        mEuropeanaApi = europeana;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        if (mListener != null) {
            mActivity.runOnUiThread(new ListenerNotifier<>(mListener));
        }
    }

    @Override
    protected UserModification doInBackground(String... params) {
        try {
            return mEuropeanaApi.socialTagsOperations().saveSocialTag(RecordController._instance.getCurrentRecordId(), params[0]);
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(UserModification userModification) {
        if (mListener != null) {
            mActivity.runOnUiThread(new ListenerNotifier<>(mListener, userModification));
        }
    }

}
