package net.eledge.android.eu.europeana.search.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import net.eledge.android.eu.europeana.Config;
import net.eledge.android.eu.europeana.search.RecordController;
import net.eledge.android.eu.europeana.search.model.Record;
import net.eledge.android.eu.europeana.search.model.record.RecordObject;
import net.eledge.android.eu.europeana.tools.UriHelper;
import net.eledge.android.toolkit.async.ListenerNotifier;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RecordTask extends AsyncTask<String, Void, RecordObject> {

	private final RecordController recordController = RecordController._instance;

	private Activity mActivity;

	public RecordTask(Activity activity) {
		super();
		mActivity = activity;
	}

	@Override
	protected RecordObject doInBackground(String... params) {
		if (TextUtils.isEmpty(params[0])) {
			return null;
		}
		String url = UriHelper.getRecordUrl(Config._instance.getEuropeanaPublicKey(mActivity), params[0]);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return RecordObject.normalize(restTemplate.getForObject(url, Record.class).object);
	}

	@Override
	protected void onPostExecute(RecordObject result) {
		recordController.record = result;
		mActivity.runOnUiThread(new ListenerNotifier<RecordObject>(recordController.listeners.values(), result));
	}

}
