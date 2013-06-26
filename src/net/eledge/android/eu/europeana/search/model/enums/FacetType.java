package net.eledge.android.eu.europeana.search.model.enums;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.search.model.facets.Languages;
import net.eledge.android.eu.europeana.search.model.facets.Rights;
import net.eledge.android.eu.europeana.search.model.facets.abstracts.FacetConverter;
import net.eledge.android.toolkit.StringUtils;
import android.content.Context;

public enum FacetType implements FacetConverter {

	TYPE(R.string.facettype_type), 
	LANGUAGE(R.string.facettype_language, new Languages()), 
	YEAR(R.string.facettype_year), 
	COUNTRY(R.string.facettype_country), 
	RIGHTS(R.string.facettype_rights, new Rights()), 
	PROVIDER(R.string.facettype_provider), 
	DATA_PROVIDER(R.string.facettype_dataprovider);
	
	public int resId;
	
	private FacetConverter facetConverter = new FacetConverter() {
		@Override
		public String createFacetLabel(Context context, String facet) {
			return facet;
		}
	};
	
	private FacetType(int resId) {
		this.resId = resId;
	}
	
	private FacetType(int resId, FacetConverter converter) {
		this.resId = resId;
		if (converter != null) {
			this.facetConverter = converter;
		}
	}

	@Override
	public String createFacetLabel(Context context, String facet) {
		return facetConverter.createFacetLabel(context, facet);
	}

	public static FacetType safeValueOf(String name) {
		for (FacetType type : values()) {
			if (StringUtils.equalsIgnoreCase(type.toString(), name)) {
				return type;
			}
		}
		return null;
	}

}
