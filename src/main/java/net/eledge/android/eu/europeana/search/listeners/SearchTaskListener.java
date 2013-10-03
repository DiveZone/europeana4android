package net.eledge.android.eu.europeana.search.listeners;

import net.eledge.android.eu.europeana.search.model.SearchFacets;
import net.eledge.android.eu.europeana.search.model.SearchItems;

public interface SearchTaskListener {
	
	public void onSearchStart(boolean isFacetSearch);
	
	public void onSearchItemsFinish(SearchItems results);

    public void onSearchFacetFinish(SearchFacets results);

	public void onSearchError(String message);
	
}