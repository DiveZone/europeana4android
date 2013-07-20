package net.eledge.android.eu.europeana.search.model.enums;

import net.eledge.android.eu.europeana.R;

public enum FacetItemType {

	CATEGORY, CATEGORY_OPENED, ITEM, ITEM_SELECTED;

	public int getLayoutId(FacetType type) {
		switch (this) {
		case CATEGORY:
			return R.layout.listitem_drawer_category;
		case CATEGORY_OPENED:
			return R.layout.listitem_drawer_category_opened;
		case ITEM:
			if (FacetType.TYPE == type) {
				return R.layout.listitem_drawer_facet_doctype;
			}
			return R.layout.listitem_drawer_facet;
		case ITEM_SELECTED:
			if (FacetType.TYPE == type) {
				return R.layout.listitem_drawer_facet_doctype_selected;
			}
			return R.layout.listitem_drawer_facet_selected;
		}
		return -1;
	}

}
