package net.eledge.android.eu.europeana.search.model.facets.enums;

import net.eledge.android.eu.europeana.R;

public enum FacetItemType {

	SECTION, BREADCRUMB, CATEGORY, CATEGORY_OPENED, ITEM, ITEM_SELECTED;

	public int getLayoutId(FacetType type) {
		switch (this) {
		case BREADCRUMB:
			return R.layout.listitem_drawer_facet_selected;
		case SECTION:
			return R.layout.listitem_drawer_section;
		case CATEGORY:
			return R.layout.listitem_drawer_category;
		case CATEGORY_OPENED:
			return R.layout.listitem_drawer_category_opened;
		case ITEM:
			return type.hasIcon ? R.layout.listitem_drawer_facet_icon
					: R.layout.listitem_drawer_facet;
		case ITEM_SELECTED:
			return type.hasIcon ? R.layout.listitem_drawer_facet_icon_selected
					: R.layout.listitem_drawer_facet_selected;
		}
		return -1;
	}

}
