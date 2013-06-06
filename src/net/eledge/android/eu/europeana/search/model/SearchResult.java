package net.eledge.android.eu.europeana.search.model;

import java.util.List;

import net.eledge.android.eu.europeana.search.model.searchresults.BreadCrumb;
import net.eledge.android.eu.europeana.search.model.searchresults.Facet;
import net.eledge.android.eu.europeana.search.model.searchresults.Item;

public class SearchResult {
	
	public int totalResults;
	public boolean facetUpdated = false;
	public List<Item> searchItems;
	public List<BreadCrumb> breadcrumbs;
	public List<Facet> facets;

}
