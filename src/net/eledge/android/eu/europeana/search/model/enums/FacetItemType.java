package net.eledge.android.eu.europeana.search.model.enums;

import net.eledge.android.eu.europeana.R;

public enum FacetItemType {
	
	CATEGORY(R.layout.listitem_drawer_category), 
	CATEGORY_OPENED(R.layout.listitem_drawer_category_opened), 
	ITEM(R.layout.listitem_drawer_facet, R.layout.listitem_drawer_facet_last), 
	ITEM_SELECTED(R.layout.listitem_drawer_facet_selected, R.layout.listitem_drawer_facet_selected_last);
	
	public int resId;
	public int lastResId = -1;
	
	private FacetItemType(int resId) {
		this.resId = resId;
	}

	private FacetItemType(int resId, int lastResId) {
		this.resId = resId;
		this.lastResId = lastResId;
	}
	
}
