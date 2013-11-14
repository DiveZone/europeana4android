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
