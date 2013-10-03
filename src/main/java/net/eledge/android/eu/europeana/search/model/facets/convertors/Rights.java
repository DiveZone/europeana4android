package net.eledge.android.eu.europeana.search.model.facets.convertors;

import android.content.Context;

import net.eledge.android.eu.europeana.search.model.enums.Right;
import net.eledge.android.eu.europeana.search.model.facets.abstracts.FacetConverter;

public class Rights implements FacetConverter {

	@Override
	public String createFacetLabel(Context context, String facet) {
		Right right = Right.safeValueByUrl(facet);
		if (right != null) {
			return right.getRightsText();
		}
		return facet;
	}
	
	@Override
	public String getFacetIcon(String facet) {
		Right right = Right.safeValueByUrl(facet);
		if (right != null) {
			return right.getFontIcon();
		}
		return facet;
	}

}
