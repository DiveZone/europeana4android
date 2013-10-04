package net.eledge.android.eu.europeana.search.task;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import net.eledge.android.eu.europeana.Config;
import net.eledge.android.eu.europeana.search.SearchController;
import net.eledge.android.eu.europeana.search.listeners.SearchTaskListener;
import net.eledge.android.eu.europeana.search.model.SearchFacets;
import net.eledge.android.eu.europeana.tools.UriHelper;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

public class SearchFacetTask extends AsyncTask<String, Void, SearchFacets> {

	private SearchController searchController = SearchController._instance;
	private Activity mActivity;

    private long startTime;

	public SearchFacetTask(Activity activity) {
		super();
		mActivity = activity;
	}

	@Override
	protected void onPreExecute() {
		for (SearchTaskListener l : searchController.listeners.values()) {
			if (isCancelled()) {
				return;
			}
			if (l != null) {
				l.onSearchStart(true);
			}
		}
        startTime = new Date().getTime();
	}

	@Override
	protected SearchFacets doInBackground(String... terms) {
        String url = UriHelper.getSearchUrl(Config._instance.getEuropeanaPublicKey(mActivity), terms, 1, 1);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate.getForObject(url, SearchFacets.class);
	}

	@Override
	protected void onPostExecute(SearchFacets result) {
        EasyTracker tracker = EasyTracker.getInstance(mActivity);
        tracker.send(MapBuilder
                .createTiming("Tasks",
                        new Date().getTime() - startTime,
                        "SearchFacetTask",
                        null)
                .build());
		if (isCancelled()) {
			return;
		}

		mActivity.runOnUiThread(new ListenerNotifier(result));
	}

	private class ListenerNotifier implements Runnable {

		private SearchFacets result;

		public ListenerNotifier(SearchFacets result) {
			this.result = result;
		}

		public void run() {
			searchController.onSearchFacetFinish(result);
			for (SearchTaskListener l : searchController.listeners.values()) {
				if (l != null) {
					l.onSearchFacetFinish(result);
				}
			}
		}
	}

}