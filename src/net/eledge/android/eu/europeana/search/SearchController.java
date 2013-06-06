package net.eledge.android.eu.europeana.search;

import java.util.ArrayList;
import java.util.List;

import net.eledge.android.eu.europeana.search.listeners.SearchTaskListener;
import net.eledge.android.eu.europeana.search.model.SearchResult;
import net.eledge.android.eu.europeana.search.model.searchresults.BreadCrumb;
import net.eledge.android.eu.europeana.search.model.searchresults.Facet;
import net.eledge.android.eu.europeana.search.model.searchresults.Item;
import net.eledge.android.eu.europeana.search.task.SearchTask;



public class SearchController implements SearchTaskListener {
	
	private static SearchController instance = new SearchController();
	
	private SearchTaskListener externalListener;
	private List<String> terms = new ArrayList<String>();
	
	private int pageLoad = 1;
	private long totalResults;
	
	private List<Item> searchItems = new ArrayList<Item>();
	private List<BreadCrumb> breadcrumbs = new ArrayList<BreadCrumb>();
	private List<Facet> facets = new ArrayList<Facet>();
	
	private SearchTask mTask;
	
	private SearchController() {
		// Singleton
	}
	
	public static SearchController getInstance() {
		return instance;
	}
	
	public void newSearch(SearchTaskListener listener, String query, String... qf) {
		reset();
		terms.clear();
		this.externalListener = listener;
		terms.add(query);
		for (String s : qf) {
			terms.add(s);
		}
		search();
	}
	
	public void refineSearch(String... qf) {
		reset();
		for (String s : qf) {
			terms.add(s);
		}
		search();
	}
	
	public void reset() {
		pageLoad = 1;
		totalResults = 0;
		searchItems.clear();
		breadcrumbs.clear();
		facets.clear();
	}
	
	private void search() {
		mTask = new SearchTask(pageLoad++, this, externalListener);
		mTask.execute(terms.toArray(new String[terms.size()]));
	}
	
	@Override
	public void onSearchStart() {}
	
	@Override
	public void onSearchError(String message) {}
	
	@Override
	public void onSearchFacetsUpdate(List<Facet> facets) {
		if (facets != null) {
			this.facets = facets;
		}
	}
	
	@Override
	public void onSearchFinish(SearchResult results) {
		this.totalResults = results.totalResults;
		this.searchItems.addAll(results.searchItems);
		if (results.breadcrumbs != null) {
			this.breadcrumbs = results.breadcrumbs;
		}
	}
	
}
