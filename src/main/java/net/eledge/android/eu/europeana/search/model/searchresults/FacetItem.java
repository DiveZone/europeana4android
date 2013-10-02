package net.eledge.android.eu.europeana.search.model.searchresults;

import net.eledge.android.eu.europeana.search.model.facets.enums.FacetItemType;
import net.eledge.android.eu.europeana.search.model.facets.enums.FacetType;

public class FacetItem {
	
	public FacetItemType itemType;
	
	public FacetType facetType;

	public String label;
	
	public int labelResource;

	public String facet;
	
	public String description;
	
	public String icon;
	
	public boolean last = false;
	
}
