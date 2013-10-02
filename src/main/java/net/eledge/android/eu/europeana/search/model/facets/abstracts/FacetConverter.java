package net.eledge.android.eu.europeana.search.model.facets.abstracts;

import android.content.Context;

public interface FacetConverter {
	
	String createFacetLabel(Context context, String facet);
	
	String getFacetIcon(String facet);

}
