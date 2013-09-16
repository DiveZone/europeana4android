package net.eledge.android.eu.europeana.search.model;

import java.util.ArrayList;
import java.util.List;

import net.eledge.android.eu.europeana.search.model.searchresults.Facet;
import net.eledge.android.eu.europeana.search.model.searchresults.Item;

public class SearchResult {
	
	public Integer totalResults;
	public boolean facetUpdated = false;
	public List<Item> searchItems = new ArrayList<Item>();
	public List<Facet> facets = new ArrayList<Facet>();

}
