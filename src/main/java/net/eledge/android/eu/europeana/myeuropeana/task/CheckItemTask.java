package net.eledge.android.eu.europeana.myeuropeana.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import net.eledge.android.eu.europeana.search.RecordController;
import net.eledge.android.toolkit.async.ListenerNotifier;
import net.eledge.android.toolkit.async.listener.TaskListener;

import org.springframework.social.europeana.api.Europeana;
import org.springframework.social.europeana.api.model.SavedItemResults;

public class CheckItemTask extends AsyncTask<Void, Void, SavedItemResults> {
    private static final String TAG = CheckItemTask.class.getSimpleName();

    private final Activity mActivity;
    private final TaskListener<Boolean> mListener;
    private final Europeana mEuropeanaApi;

    public CheckItemTask(Activity activity, Europeana europeana, TaskListener<Boolean> listener) {
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
    protected SavedItemResults doInBackground(Void... params) {
        try {
            return mEuropeanaApi.savedItemsOperations().getSavedItemByEuropeanaId(RecordController._instance.getCurrentRecordId());
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(SavedItemResults results) {
        if ((mListener != null) && (results != null)) {
            mActivity.runOnUiThread(new ListenerNotifier<>(mListener, results.getItemsCount() == 1));
        }
    }

}
