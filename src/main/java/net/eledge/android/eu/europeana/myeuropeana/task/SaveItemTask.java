package net.eledge.android.eu.europeana.myeuropeana.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import net.eledge.android.eu.europeana.search.RecordController;
import net.eledge.android.toolkit.async.ListenerNotifier;
import net.eledge.android.toolkit.async.listener.TaskListener;

import org.springframework.social.europeana.api.Europeana;
import org.springframework.social.europeana.api.model.UserModification;

public class SaveItemTask extends AsyncTask<Void, Void, UserModification> {
    private static final String TAG = SaveItemTask.class.getSimpleName();

    private final Activity mActivity;
    private final TaskListener<UserModification> mListener;
    private final Europeana mEuropeanaApi;

    public SaveItemTask(Activity activity, Europeana europeana, TaskListener<UserModification> listener) {
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
    protected UserModification doInBackground(Void... params) {
        try {
            return mEuropeanaApi.savedItemsOperations().saveItem(RecordController._instance.getCurrentRecordId());
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
