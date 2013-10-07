package net.eledge.android.eu.europeana.search.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import net.eledge.android.eu.europeana.Config;
import net.eledge.android.eu.europeana.search.RecordController;
import net.eledge.android.eu.europeana.search.model.Record;
import net.eledge.android.eu.europeana.search.model.record.RecordObject;
import net.eledge.android.eu.europeana.tools.UriHelper;
import net.eledge.android.toolkit.async.ListenerNotifier;

import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

public class RecordTask extends AsyncTask<String, Void, RecordObject> {

	private final RecordController recordController = RecordController._instance;

	private Activity mActivity;

    private String recordId;

    private long startTime;

	public RecordTask(Activity activity) {
		super();
		mActivity = activity;
	}

    @Override
    protected void onPreExecute() {
        startTime = new Date().getTime();
        mActivity.runOnUiThread(new ListenerNotifier<RecordObject>(recordController.listeners.values()));
    }

	@Override
	protected RecordObject doInBackground(String... params) {
		if (TextUtils.isEmpty(params[0])) {
			return null;
		}
        recordId = params[0];
		String url = UriHelper.getRecordUrl(Config._instance.getEuropeanaPublicKey(mActivity), recordId);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        return RecordObject.normalize(restTemplate.getForObject(url, Record.class).object);
	}

	@Override
	protected void onPostExecute(RecordObject result) {
        EasyTracker tracker = EasyTracker.getInstance(mActivity);
        tracker.send(MapBuilder
                .createTiming("Tasks",
                        new Date().getTime() - startTime,
                        "RecordTask",
                        recordId)
                .build());
		recordController.record = result;
		mActivity.runOnUiThread(new ListenerNotifier<RecordObject>(recordController.listeners.values(), result));
	}

}
