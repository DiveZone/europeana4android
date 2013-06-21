package net.eledge.android.eu.europeana.search.model.enums;

import net.eledge.android.eu.europeana.R;

public enum FacetItemType {
	
	CATEGORY(R.layout.listitem_drawer_category), ITEM(R.layout.listitem_drawer_facet);
	
	public int resId;
	
	private FacetItemType(int resId) {
		this.resId = resId;
	}

}
