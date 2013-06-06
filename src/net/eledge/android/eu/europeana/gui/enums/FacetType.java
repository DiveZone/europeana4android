package net.eledge.android.eu.europeana.gui.enums;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.toolkit.StringUtils;

public enum FacetType {

	TYPE(R.string.facettype_type), 
	LANGUAGE(R.string.facettype_language), 
	YEAR(R.string.facettype_year), 
	COUNTRY(R.string.facettype_country), 
	RIGHTS(R.string.facettype_rights), 
	PROVIDER(R.string.facettype_provider), 
	DATA_PROVIDER(R.string.facettype_dataprovider);
	
	public int resId;
	
	private FacetType(int resId) {
		this.resId = resId;
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
