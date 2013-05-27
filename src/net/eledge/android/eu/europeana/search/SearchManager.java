package net.eledge.android.eu.europeana.search;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.eledge.android.eu.europeana.Config;
import net.eledge.android.eu.europeana.search.listeners.SearchTaskListener;
import net.eledge.android.eu.europeana.search.model.BreadCrumb;
import net.eledge.android.eu.europeana.search.model.Facet;
import net.eledge.android.eu.europeana.search.model.SearchResult;
import net.eledge.android.eu.europeana.search.model.submodel.Field;
import net.eledge.android.eu.europeana.tools.UriHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.database.Cursor;
import android.os.AsyncTask;

public class SearchManager implements SearchTaskListener {
	
	private static SearchManager instance = new SearchManager();
	
	private String query;
	private int pageLoad = 1;
	private long totalResults;
	
	private List<SearchResult> searchItems;
	private List<BreadCrumb> breadcrumbs;
	private List<Facet> facets;
	
	private SearchManager() {
		// Singleton
	}
	
	public static SearchManager getInstance() {
		return instance;
	}
	
	public void search(String query, SearchTaskListener listener) {
		this.query = query;
		this.pageLoad = 1;
	}
	
	@Override
	public void onStart() {}
	
	@Override
	public void onError(String message) {}
	
	@Override
	public void onFinish(List<SearchResult> results, List<Facet> facets,
			List<BreadCrumb> breadCrumbs) {
		this.searchItems = results;
		if (breadCrumbs != null) {
			this.breadcrumbs = breadCrumbs;
		}
		if (facets != null) {
			this.facets = facets;
		}
	}
	
	private class SearchTask extends AsyncTask<String, Void, Void> {
		
		private SearchTaskListener[] listeners;

		private List<SearchResult> searchItems;
		private List<BreadCrumb> breadcrumbs;
		private List<Facet> facets;
		
		public SearchTask(SearchTaskListener... listeners) {
			super();
			this.listeners = listeners;
		}

		@Override
		protected void onPreExecute() {
			// TODO: start wait animation...
		}

		@Override
		protected Void doInBackground(String... terms) {
			searchItems = new ArrayList<SearchResult>();
			URI url = UriHelper.getSearchURI(terms[0], pageLoad++);
			try {
				HttpResponse response = new DefaultHttpClient().execute(new HttpGet(url));
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
						Config.JSON_CHARSET));
				StringBuilder json = new StringBuilder();
				String line = reader.readLine();
				while (line != null) {
					json.append(line);
					line = reader.readLine();
				}
				JSONObject jsonObj = new JSONObject(json.toString());
				// check for error
				if (jsonObj.has("error")) {
					// report error
				} else {
					// get statistics
					totalResults = jsonObj.getInt("totalResults");
					if (totalResults == 0) {
						return null;
					}
					// get items
					JSONArray items = jsonObj.getJSONArray("items");
					if ((items != null) && (items.length() > 0)) {
						for (int i = 0; i < items.length(); i++) {
							if (isCancelled()) {
								break;
							}
							JSONObject item = items.getJSONObject(i);
							SearchResult tmp = new SearchResult();
							tmp.id = item.getString("id");
							tmp.link = item.getString("link");
							tmp.title = item.getString("title");
							tmp.type = item.getString("type");
							if (item.has("edmPreview")) {
								//tmp.thumbnail = String.format(Config.URL_API_IMAGE_BRIEF, item.getString("enclosure"));
							}
							searchItems.add(tmp);
						}
					}
					if (jsonObj.has("breadCrumbs")) {
						breadcrumbs = new ArrayList<BreadCrumb>();
						items = jsonObj.getJSONArray("breadCrumbs");
						for (int i = 0; i < items.length(); i++) {
							if (isCancelled()) {
								break;
							}
							JSONObject bcObject = items.getJSONObject(i);
							BreadCrumb bc = new BreadCrumb();
							bc.display = bcObject.getString("display");
							bc.href = bcObject.getString("href");
							bc.param = bcObject.getString("param");
							bc.value = bcObject.getString("value");
							bc.last = bcObject.getBoolean("display");
							breadcrumbs.add(bc);
						}
					}
					if (jsonObj.has("facets")) {
						facets = new ArrayList<Facet>();
						items = jsonObj.getJSONArray("facets");
						for (int i = 0; i < items.length(); i++) {
							if (isCancelled()) {
								break;
							}
							JSONObject facetObject = items.getJSONObject(i);
							Facet facet = new Facet();
							facet.name = facetObject.getString("name");
							JSONArray fields = facetObject.getJSONArray("fields");
							for (int j = 0; i < fields.length(); j++) {
								if (isCancelled()) {
									break;
								}
								JSONObject fieldObject = fields.getJSONObject(j);
								Field field = new Field();
								field.label = fieldObject.getString("label");
								field.count = fieldObject.getLong("count");
								facet.fields.add(field);
							}
							facets.add(facet);
						}
					}
				}
			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			for (SearchTaskListener l: listeners) {
				l.onFinish(searchItems, facets, breadcrumbs);
			}
		}

	}
	
}
