package net.eledge.android.eu.europeana.search.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import net.eledge.android.eu.europeana.Config;
import net.eledge.android.eu.europeana.search.RecordController;
import net.eledge.android.eu.europeana.search.model.record.Record;
import net.eledge.android.eu.europeana.tools.UriHelper;
import net.eledge.android.toolkit.async.ListenerNotifier;
import net.eledge.android.toolkit.json.exception.JsonParserException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

public class RecordTask extends AsyncTask<String, Void, Record> {

	private final RecordController recordController = RecordController._instance;

	private Activity mActivity;

	public RecordTask(Activity activity) {
		super();
		mActivity = activity;
	}

	@Override
	protected Record doInBackground(String... params) {
		if (TextUtils.isEmpty(params[0])) {
			return null;
		}
		URI url = UriHelper.getRecordURI(Config._instance.getEuropeanaPublicKey(mActivity), params[0]);
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			HttpGet request = new HttpGet(url);
			AndroidHttpClient.modifyRequestToAcceptGzipResponse(request);
			HttpResponse response = new DefaultHttpClient().execute(request);
			isr = new InputStreamReader(AndroidHttpClient.getUngzippedContent(response.getEntity()), Config.JSON_CHARSET);
			br = new BufferedReader(isr);
			StringBuilder json = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				if (isCancelled()) {
					return null;
				}
				json.append(line);
				line = br.readLine();
			}
			JSONObject jsonObj = new JSONObject(json.toString());
			JSONObject object = jsonObj.getJSONObject("object");
			Record record = new Record();
			recordController.jsonParser.parseToObject(object, record);
			return record;
		} catch (IOException e) {
			// ignore
		} catch (JSONException e) {
			// ignore
		} catch (JsonParserException e) {
			Log.e(RecordTask.class.getSimpleName(), e.getMessage());
			Log.e(RecordTask.class.getSimpleName(), e.getCause().getMessage());
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(isr);
		}
		return null;	
	}

	@Override
	protected void onPostExecute(Record result) {
		recordController.record = result;
		mActivity.runOnUiThread(new ListenerNotifier<Record>(recordController.listeners.values(), result));
	}

}
