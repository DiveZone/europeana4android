package net.eledge.android.eu.europeana.search.model.facets.enums;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.search.model.facets.Languages;
import net.eledge.android.eu.europeana.search.model.facets.Rights;
import net.eledge.android.eu.europeana.search.model.facets.abstracts.FacetConverter;
import net.eledge.android.toolkit.StringUtils;
import android.content.Context;

public enum FacetType implements FacetConverter {

	TYPE(true, R.string.facettype_type), 
	LANGUAGE(false, R.string.facettype_language, new Languages()), 
	YEAR(false, R.string.facettype_year), 
	COUNTRY(false, R.string.facettype_country), 
	RIGHTS(true, R.string.facettype_rights, new Rights()), 
	PROVIDER(false, R.string.facettype_provider), 
	DATA_PROVIDER(false, R.string.facettype_dataprovider);
	
	public int resId;
	public boolean hasIcon;
	
	private FacetConverter facetConverter = new FacetConverter() {
		@Override
		public String createFacetLabel(Context context, String facet) {
			return facet;
		}
		@Override
		public String getFacetIcon(String facet) {
			return null;
		}
	};
	
	private FacetType(boolean hasIcon, int resId) {
		this.hasIcon = hasIcon;
		this.resId = resId;
	}
	
	private FacetType(boolean hasIcon, int resId, FacetConverter converter) {
		this.hasIcon = hasIcon;
		this.resId = resId;
		if (converter != null) {
			this.facetConverter = converter;
		}
	}

	@Override
	public String createFacetLabel(Context context, String facet) {
		return facetConverter.createFacetLabel(context, facet);
	}
	
	@Override
	public String getFacetIcon(String facet) {
		return facetConverter.getFacetIcon(facet);
	}

	public static FacetType safeValueOf(String name) {
		for (FacetType type : values()) {
			if (StringUtils.equalsIgnoreCase(type.toString(), name)) {
				return type;
			}
		}
		return null;
	}
	
	public boolean hasIcon() {
		return hasIcon;
	}

}
