package net.eledge.android.eu.europeana.search.model.facets.convertors;

import android.content.Context;

import net.eledge.android.eu.europeana.search.model.enums.Country;
import net.eledge.android.eu.europeana.search.model.facets.abstracts.FacetConverter;
import net.eledge.android.toolkit.gui.GuiUtils;

public class Countries implements FacetConverter {

	@Override
	public String createFacetLabel(Context context, String facet) {
		Country country = Country.safeValueOf(facet);
		if (country != null) {
			return GuiUtils.getString(context, country.resourceId);
		}
		return facet;
	}
	
	@Override
	public String getFacetIcon(String facet) {
		return null;
	}

}
