package net.eledge.android.eu.europeana.search.model.facets.convertors;

import android.content.Context;

import net.eledge.android.eu.europeana.search.model.enums.Language;
import net.eledge.android.eu.europeana.search.model.facets.abstracts.FacetConverter;
import net.eledge.android.toolkit.gui.GuiUtils;

public class Languages implements FacetConverter {

	@Override
	public String createFacetLabel(Context context, String facet) {
		Language language = Language.safeValueOf(facet);
		if (language != null) {
			return GuiUtils.getString(context, language.resourceId);
		}
		return facet;
	}
	
	@Override
	public String getFacetIcon(String facet) {
		return null;
	}

}
