package net.eledge.android.eu.europeana.search.listeners;

import java.util.List;

import net.eledge.android.eu.europeana.search.model.BreadCrumb;
import net.eledge.android.eu.europeana.search.model.Facet;
import net.eledge.android.eu.europeana.search.model.SearchResult;

public interface SearchTaskListener {
	
	public void onStart();
	
	public void onFinish(List<SearchResult> results, List<Facet> facets, List<BreadCrumb> breadCrumbs);
	
	public void onError(String message);
	
}