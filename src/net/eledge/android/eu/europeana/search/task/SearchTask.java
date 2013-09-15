package net.eledge.android.eu.europeana.search.task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import net.eledge.android.eu.europeana.Config;
import net.eledge.android.eu.europeana.search.SearchController;
import net.eledge.android.eu.europeana.search.listeners.SearchTaskListener;
import net.eledge.android.eu.europeana.search.model.SearchResult;
import net.eledge.android.eu.europeana.search.model.enums.DocType;
import net.eledge.android.eu.europeana.search.model.searchresults.BreadCrumb;
import net.eledge.android.eu.europeana.search.model.searchresults.Facet;
import net.eledge.android.eu.europeana.search.model.searchresults.Field;
import net.eledge.android.eu.europeana.search.model.searchresults.Item;
import net.eledge.android.eu.europeana.tools.UriHelper;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class SearchTask extends AsyncTask<String, Void, SearchResult> {
	private final static String TAG = "SearchTask";

	private int pageLoad = 1;

	private SearchController searchController = SearchController._instance;
	private Activity mActivity;

	public SearchTask(Activity activity, int pageLoad) {
		super();
		mActivity = activity;
		this.pageLoad = pageLoad;
	}

	@Override
	protected void onPreExecute() {
		for (SearchTaskListener l : searchController.listeners.values()) {
			if (isCancelled()) {
				return;
			}
			if (l != null) {
				l.onSearchStart();
			}
		}
	}

	@Override
	protected SearchResult doInBackground(String... terms) {
		URI url = UriHelper.getSearchURI(Config._instance.getEuropeanaPublicKey(mActivity), terms, pageLoad,
				searchController.searchPagesize);
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
				json.append(line);
				line = br.readLine();
			}
			JSONObject jsonObj = new JSONObject(json.toString());
			// check for error
			if (jsonObj.has("error")) {
				// report error
			} else {
				// get statistics
				SearchResult result = new SearchResult();
				result.totalResults = jsonObj.getInt("totalResults");
				if (result.totalResults > 0) {
					JSONArray items = jsonObj.getJSONArray("items");
					if ((items != null) && (items.length() > 0)) {
						for (int i = 0; i < items.length(); i++) {
							if (isCancelled()) {
								return null;
							}
							JSONObject item = items.getJSONObject(i);
							Item tmp = new Item();
							tmp.id = item.getString("id");
							if (item.has("title")) {
								tmp.title = item.getJSONArray("title").getString(0);
							}
							tmp.type = DocType.safeValueOf(item.getString("type"));
							if (item.has("edmPreview")) {
								tmp.thumbnail = item.getJSONArray("edmPreview").getString(0);
							}
							result.searchItems.add(tmp);
						}
					}
					if (jsonObj.has("breadCrumbs")) {
						items = jsonObj.getJSONArray("breadCrumbs");
						for (int i = 0; i < items.length(); i++) {
							if (isCancelled()) {
								return null;
							}
							JSONObject bcObject = items.getJSONObject(i);
							BreadCrumb bc = new BreadCrumb();
							bc.display = bcObject.getString("display");
							bc.href = bcObject.getString("href");
							bc.param = bcObject.getString("param");
							bc.value = bcObject.getString("value");
							bc.last = bcObject.getBoolean("last");
							result.breadcrumbs.add(bc);
						}
					}
					if (jsonObj.has("facets")) {
						result.facetUpdated = true;
						items = jsonObj.getJSONArray("facets");
						for (int i = 0; i < items.length(); i++) {
							if (isCancelled()) {
								break;
							}
							JSONObject facetObject = items.getJSONObject(i);
							Facet facet = new Facet();
							facet.name = facetObject.getString("name");
							JSONArray fields = facetObject.getJSONArray("fields");
							for (int j = 0; j < fields.length(); j++) {
								if (isCancelled()) {
									return null;
								}
								JSONObject fieldObject = fields.getJSONObject(j);
								Field field = new Field();
								field.label = fieldObject.getString("label");
								field.count = fieldObject.getLong("count");
								facet.fields.add(field);
							}
							result.facets.add(facet);
						}
					}
				}
				return result;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(isr);
		}
		return null;
	}

	@Override
	protected void onPostExecute(SearchResult result) {
		if (isCancelled()) {
			return;
		}

		mActivity.runOnUiThread(new ListenerNotifier(result));
	}

	private class ListenerNotifier implements Runnable {

		private SearchResult result;

		public ListenerNotifier(SearchResult result) {
			this.result = result;
		}

		public void run() {
			searchController.onSearchFinish(result);
			for (SearchTaskListener l : searchController.listeners.values()) {
				if (l != null) {
					l.onSearchFinish(result);
				}
			}
		}
	}

}