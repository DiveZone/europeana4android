package net.eledge.android.eu.europeana.search.listeners;

import java.util.List;

import net.eledge.android.eu.europeana.search.model.BreadCrumb;
import net.eledge.android.eu.europeana.search.model.Facet;
import net.eledge.android.eu.europeana.search.model.SearchResult;

public interface SearchTaskListener {
	
	public void onSearchStart();
	
	public void onSearchFinish(List<SearchResult> results, List<BreadCrumb> breadCrumbs);
	
	public void onSearchFacetsUpdate(List<Facet> facets);
	
	public void onSearchError(String message);
	
}