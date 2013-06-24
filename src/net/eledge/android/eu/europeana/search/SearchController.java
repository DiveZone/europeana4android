package net.eledge.android.eu.europeana.search;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.eledge.android.eu.europeana.gui.model.FacetItem;
import net.eledge.android.eu.europeana.search.listeners.SearchTaskListener;
import net.eledge.android.eu.europeana.search.model.SearchResult;
import net.eledge.android.eu.europeana.search.model.enums.FacetItemType;
import net.eledge.android.eu.europeana.search.model.enums.FacetType;
import net.eledge.android.eu.europeana.search.model.searchresults.BreadCrumb;
import net.eledge.android.eu.europeana.search.model.searchresults.Facet;
import net.eledge.android.eu.europeana.search.model.searchresults.Field;
import net.eledge.android.eu.europeana.search.model.searchresults.Item;
import net.eledge.android.eu.europeana.search.task.SearchTask;
import net.eledge.android.eu.europeana.tools.UriHelper;
import android.os.AsyncTask.Status;

public class SearchController implements SearchTaskListener {

	private static final String TAG_LISTENER = SearchController.class.getSimpleName();
	private static SearchController instance = new SearchController();

	public Map<String, SearchTaskListener> listeners = new HashMap<String, SearchTaskListener>();
	public List<String> terms = new ArrayList<String>();

	private int pageLoad = 1;
	private long totalResults;

	private final List<Item> searchItems = new ArrayList<Item>();
	private List<BreadCrumb> breadcrumbs = new ArrayList<BreadCrumb>();
	private List<Facet> facets = new ArrayList<Facet>();

	private FacetType selectedFacet = FacetType.TYPE;

	private SearchTask mTask;

	private SearchController() {
		// Singleton
		registerListener(TAG_LISTENER, this);
	}

	public static SearchController getInstance() {
		return instance;
	}

	public void registerListener(String tag, SearchTaskListener listener) {
		listeners.put(tag, listener);
	}

	public void unregister(String tag) {
		listeners.remove(tag);
	}

	public void newSearch(String query, String... qf) {
		reset();
		terms.clear();
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

	public void continueSearch() {
		if (searchItems.size() < totalResults) {
			if ((mTask != null) && (mTask.getStatus() != Status.FINISHED)) {
				return;
			}
			search();
		}
	}

	public List<FacetItem> getFacetList() {
		List<FacetItem> facetlist = new ArrayList<FacetItem>();
		for (Facet facet : facets) {
			FacetType type = FacetType.safeValueOf(facet.name);
			if (type != null) {
				FacetItem item = new FacetItem();
				item.itemType = FacetItemType.CATEGORY;
				item.facetType = type;
				item.facet = facet.name;
				facetlist.add(item);
				if (type == selectedFacet) {
					for (Field field : facet.fields) {
						item = new FacetItem();
						item.facet = facet.name + ":" + field.label;
						item.itemType = terms.contains(item.facet) ? FacetItemType.ITEM_SELECTED
								: FacetItemType.ITEM;
						item.description = field.label + " (" + field.count
								+ ")";
						facetlist.add(item);
					}
				}
			}
		}
		return facetlist;
	}

	public boolean hasMoreResults() {
		return totalResults > searchItems.size();
	}

	public void reset() {
		if (mTask != null) {
			mTask.cancel(true);
		}
		pageLoad = 1;
		totalResults = 0;
		synchronized (searchItems) {
			searchItems.clear();
		}
		breadcrumbs.clear();
		facets.clear();
	}

	public String getPortalUrl() {
		try {
			return UriHelper.createPortalUrl(terms.toArray(new String[terms
					.size()]));
		} catch (UnsupportedEncodingException e) {
			return "http://europeana.eu";
		}
	}

	private void search() {
		if ((mTask == null) || (mTask.getStatus() == Status.FINISHED)) {
			mTask = new SearchTask(pageLoad++,
					new ArrayList<SearchTaskListener>(listeners.values()));
			mTask.execute(terms.toArray(new String[terms.size()]));
		}
	}

	@Override
	public void onSearchStart() {
	}

	@Override
	public void onSearchError(String message) {
	}

	@Override
	public void onSearchFacetsUpdate(List<Facet> facets) {
		if (facets != null) {
			this.facets = facets;
		}
	}

	@Override
	public void onSearchFinish(SearchResult results) {
		totalResults = results.totalResults;
		synchronized (searchItems) {
			searchItems.addAll(results.searchItems);
		}
		if (results.breadcrumbs != null) {
			breadcrumbs = results.breadcrumbs;
		}
	}

	public List<Item> getSearchItems() {
		synchronized (searchItems) {
			return searchItems;
		}
	}

	public void setCurrentFacetType(FacetType facetType) {
		selectedFacet = facetType;
	}

}
