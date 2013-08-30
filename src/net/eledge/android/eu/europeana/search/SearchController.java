package net.eledge.android.eu.europeana.search;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.eledge.android.eu.europeana.search.listeners.SearchTaskListener;
import net.eledge.android.eu.europeana.search.model.SearchResult;
import net.eledge.android.eu.europeana.search.model.Suggestion;
import net.eledge.android.eu.europeana.search.model.facets.enums.FacetItemType;
import net.eledge.android.eu.europeana.search.model.facets.enums.FacetType;
import net.eledge.android.eu.europeana.search.model.searchresults.BreadCrumb;
import net.eledge.android.eu.europeana.search.model.searchresults.Facet;
import net.eledge.android.eu.europeana.search.model.searchresults.FacetItem;
import net.eledge.android.eu.europeana.search.model.searchresults.Field;
import net.eledge.android.eu.europeana.search.model.searchresults.Item;
import net.eledge.android.eu.europeana.search.task.SearchTask;
import net.eledge.android.eu.europeana.search.task.SuggestionTask;
import net.eledge.android.eu.europeana.tools.UriHelper;
import net.eledge.android.toolkit.async.listener.TaskListener;
import android.app.Activity;
import android.content.Context;

public class SearchController {

	public final static SearchController _instance = new SearchController();

	public int searchPagesize = 12;
	public int suggestionPagesize = 12;

	public Map<String, SearchTaskListener> listeners = new HashMap<String, SearchTaskListener>();
	public List<String> terms = new ArrayList<String>();

	private int pageLoad = 1;
	private long totalResults;
	private int itemSelected = -1;

	private final List<Item> searchItems = new ArrayList<Item>();
	private final List<BreadCrumb> breadcrumbs = new ArrayList<BreadCrumb>();
	private final List<Facet> facets = new ArrayList<Facet>();

	private FacetType selectedFacet = FacetType.TYPE;

	private SearchTask mSearchTask;
	private SuggestionTask mSuggestionTask;

	private SearchController() {
		// Singleton
	}

	public void registerListener(Class<?> clazz, SearchTaskListener listener) {
		listeners.put(clazz.getName(), listener);
	}

	public void unregister(Class<?> clazz) {
		listeners.remove(clazz.getName());
	}
	
	public void suggestions(TaskListener<Suggestion[]> listener, String query) {
		if (mSuggestionTask != null) {
			mSuggestionTask.cancel(true);
		}
		mSuggestionTask = new SuggestionTask(listener);
		mSuggestionTask.execute(query);
	}
	
	public void newSearch(Activity activity, String query, String... qf) {
		reset();
		terms.clear();
		terms.add(query);
		for (String s : qf) {
			terms.add(s);
		}
		search(activity);
	}

	public void refineSearch(Activity activity, String... qf) {
		reset();
		for (String s : qf) {
			terms.add(s);
		}
		search(activity);
	}

	public void removeRefineSearch(Activity activity, String... qf) {
		boolean changed = false;
		for (String s : qf) {
			if (terms.contains(s)) {
				terms.remove(s);
				changed = true;
			}
		}
		if (changed) {
			reset();
			search(activity);
		}
	}

	public void continueSearch(Activity activity) {
		if (hasMoreResults()) {
			search(activity);
		}
	}

	public List<FacetItem> getFacetList(Context context) {
		List<FacetItem> facetlist = new ArrayList<FacetItem>();
		for (Facet facet : facets) {
			FacetType type = FacetType.safeValueOf(facet.name);
			if (type != null) {
				FacetItem item = new FacetItem();
				item.itemType = type == selectedFacet ? FacetItemType.CATEGORY_OPENED: FacetItemType.CATEGORY;
				item.facetType = type;
				item.facet = facet.name;
				facetlist.add(item);
				if (type == selectedFacet) {
					for (Field field : facet.fields) {
						item = new FacetItem();
						item.facetType = type;
						item.label = field.label;
						item.facet = facet.name + ":" + field.label;
						item.itemType = terms.contains(item.facet) ? FacetItemType.ITEM_SELECTED
								: FacetItemType.ITEM;
						item.description = type.createFacetLabel(context, field.label) + " (" + field.count
								+ ")";
						item.icon = type.getFacetIcon(field.label);
						facetlist.add(item);
					}
					facetlist.get(facetlist.size()-1).last = true;
				}
			}
		}
		facetlist.get(facetlist.size()-1).last = true;
		return facetlist;
	}
	
	public boolean hasResults() {
		return totalResults > 0;
	}

	public boolean hasMoreResults() {
		return totalResults > searchItems.size();
	}
	
	public void cancelSearch() {
		if (mSearchTask != null) {
			mSearchTask.cancel(true);
		}
	}

	public void reset() {
		cancelSearch();
		pageLoad = 1;
		totalResults = 0;
		itemSelected = -1;
		synchronized (searchItems) {
			searchItems.clear();
		}
		breadcrumbs.clear();
		facets.clear();
	}

	public String getPortalUrl() {
		try {
			return UriHelper.createPortalSearchUrl(terms.toArray(new String[terms
					.size()]));
		} catch (UnsupportedEncodingException e) {
			return "http://europeana.eu";
		}
	}

	private void search(Activity activity) {
		if (mSearchTask != null) {
			cancelSearch();
		}
		mSearchTask = new SearchTask(activity, pageLoad++);
		mSearchTask.execute(terms.toArray(new String[terms.size()]));
	}

	public synchronized void onSearchFinish(SearchResult results) {
		totalResults = results.totalResults;
		searchItems.addAll(results.searchItems);
		if (results.facetUpdated) {
			facets.clear();
			facets.addAll(results.facets);
		}
		if (results.breadcrumbs != null) {
			breadcrumbs.clear();
			breadcrumbs.addAll(results.breadcrumbs);
		}
	}

	public synchronized List<Item> getSearchItems() {
		return searchItems;
	}
	
	public int getItemSelected() {
		return itemSelected;
	}
	
	public void setItemSelected(int itemSelected) {
		this.itemSelected = itemSelected;
	}
	
	public Integer size() {
		if (searchItems != null) {
			return Integer.valueOf(searchItems.size());
		}
		return Integer.valueOf(0);
	}

	public void setCurrentFacetType(FacetType facetType) {
		selectedFacet = facetType;
	}

}
