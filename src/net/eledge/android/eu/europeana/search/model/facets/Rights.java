package net.eledge.android.eu.europeana.search.model.facets;

import net.eledge.android.eu.europeana.search.model.facets.abstracts.FacetConverter;
import net.eledge.android.eu.europeana.search.model.facets.enums.FacetRights;
import android.content.Context;

public class Rights implements FacetConverter {

	@Override
	public String createFacetLabel(Context context, String facet) {
		FacetRights facetRights = FacetRights.safeValueByUrl(facet);
		if (facetRights != null) {
			return facetRights.getRightsText();
		}
		return facet;
	}

}
