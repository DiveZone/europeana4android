package net.eledge.android.eu.europeana.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.eledge.android.eu.europeana.search.model.searchresults.Facet;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchFacets {
	
	public List<Facet> facets = new ArrayList<Facet>();


}
