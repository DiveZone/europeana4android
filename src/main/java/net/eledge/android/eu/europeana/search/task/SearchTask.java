package net.eledge.android.eu.europeana.search.task;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import net.eledge.android.eu.europeana.Config;
import net.eledge.android.eu.europeana.search.SearchController;
import net.eledge.android.eu.europeana.search.listeners.SearchTaskListener;
import net.eledge.android.eu.europeana.search.model.SearchItems;
import net.eledge.android.eu.europeana.tools.UriHelper;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

public class SearchTask extends AsyncTask<String, Void, SearchItems> {

	private int pageLoad = 1;

    private long startTime;

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
				l.onSearchStart(false);
			}
		}
        startTime = new Date().getTime();
	}

	@Override
	protected SearchItems doInBackground(String... terms) {
		String url = UriHelper.getSearchUrl(Config._instance.getEuropeanaPublicKey(mActivity), terms, pageLoad,
                searchController.searchPagesize);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate.getForObject(url, SearchItems.class);
	}

	@Override
	protected void onPostExecute(SearchItems result) {
        EasyTracker tracker = EasyTracker.getInstance(mActivity);
        tracker.send(MapBuilder
                .createTiming("Tasks",
                        new Date().getTime()-startTime,
                        "SearchTask",
                        null)
                .build());
		if (isCancelled()) {
			return;
		}
		mActivity.runOnUiThread(new ListenerNotifier(result));
	}

	private class ListenerNotifier implements Runnable {

		private SearchItems result;

		public ListenerNotifier(SearchItems result) {
			this.result = result;
		}

		public void run() {
			searchController.onSearchFinish(result);
			for (SearchTaskListener l : searchController.listeners.values()) {
				if (l != null) {
					l.onSearchItemsFinish(result);
				}
			}
		}
	}

}