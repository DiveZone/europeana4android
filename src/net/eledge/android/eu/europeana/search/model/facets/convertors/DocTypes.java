package net.eledge.android.eu.europeana.search.model.facets.convertors;

import net.eledge.android.eu.europeana.search.model.enums.DocType;
import net.eledge.android.eu.europeana.search.model.facets.abstracts.FacetConverter;
import net.eledge.android.toolkit.gui.GuiUtils;
import android.content.Context;

public class DocTypes implements FacetConverter {

	@Override
	public String createFacetLabel(Context context, String facet) {
		DocType type = DocType.safeValueOf(facet);
		if (type != null) {
			return GuiUtils.getString(context, type.resourceId);
		}
		return facet;
	}

	@Override
	public String getFacetIcon(String facet) {
		DocType type = DocType.safeValueOf(facet);
		if (type != null) {
			return type.icon;
		}
		return null;
	}

}
