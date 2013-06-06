package net.eledge.android.eu.europeana.search.listeners;

import java.util.List;

import net.eledge.android.eu.europeana.search.model.SearchResult;
import net.eledge.android.eu.europeana.search.model.searchresults.Facet;

public interface SearchTaskListener {
	
	public void onSearchStart();
	
	public void onSearchFinish(SearchResult results);
	
	public void onSearchFacetsUpdate(List<Facet> facets);
	
	public void onSearchError(String message);
	
}