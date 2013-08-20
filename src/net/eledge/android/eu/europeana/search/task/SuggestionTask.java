package net.eledge.android.eu.europeana.search.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.eledge.android.eu.europeana.Config;
import net.eledge.android.eu.europeana.search.SearchController;
import net.eledge.android.eu.europeana.search.model.Suggestion;
import net.eledge.android.eu.europeana.tools.UriHelper;
import net.eledge.android.toolkit.async.ListenerNotifier;
import net.eledge.android.toolkit.async.listener.TaskListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

public class SuggestionTask extends AsyncTask<String, Void, Suggestion[]> {
	
	private SearchController searchController = SearchController._instance;
	
	private TaskListener<Suggestion[]> listener;
	
	public SuggestionTask(TaskListener<Suggestion[]> listener) {
		this.listener = listener;
	}
	
	@Override
	protected Suggestion[] doInBackground(String... params) {
		if (TextUtils.isEmpty(params[0])) {
			return null;
		}
		URI url = UriHelper.getSuggestionURI(params[0], searchController.suggestionPagesize);
		try {
			HttpResponse response = new DefaultHttpClient().execute(new HttpGet(url));
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
					Config.JSON_CHARSET));
			StringBuilder json = new StringBuilder();
			String line = reader.readLine();
			while (line != null) {
				if (isCancelled()) {
					return null;
				}
				json.append(line);
				line = reader.readLine();
			}
			JSONObject jsonObj = new JSONObject(json.toString());
			JSONArray array = jsonObj.getJSONArray("items");
			if ((array != null) && (array.length() > 0)) {
				List<Suggestion> results = new ArrayList<Suggestion>();
				for (int i = 0; i < array.length(); i++) {
					if (isCancelled()) {
						return null;
					}
					JSONObject item = array.getJSONObject(i);
					Suggestion suggestion = new Suggestion();
					suggestion.term = item.getString("term");
					suggestion.field = item.getString("field");
					suggestion.freq = item.getLong("frequency");
					suggestion.query = item.getString("query");
					results.add(suggestion);
				}
				return results.toArray(new Suggestion[results.size()]);
			}
		} catch (IOException e) {
			// ignore
		} catch (JSONException e) {
			// ignore
		}
		return null;	
	}
	
	@Override
	protected void onPostExecute(Suggestion[] result) {
		if (listener instanceof Activity) {
			((Activity)listener).runOnUiThread(new ListenerNotifier<Suggestion[]>(listener, result));
		} else {
			listener.onTaskFinished(result);
		}
	}

}
