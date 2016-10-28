/*
 * Copyright (c) 2013-2016 eLedge.net and the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.eledge.android.europeana.service.search.model.facets.enums;

public enum FacetItemType {

  SECTION, BREADCRUMB, CATEGORY, CATEGORY_OPENED, ITEM, ITEM_SELECTED;

  public int getLayoutId(FacetType type) {
    switch (this) {
      case BREADCRUMB:
//                return R.layout.listitem_drawer_facet_selected;
      case SECTION:
//                return R.layout.listitem_drawer_section;
      case CATEGORY:
//                return R.layout.listitem_drawer_category;
      case CATEGORY_OPENED:
//                return R.layout.listitem_drawer_category_opened;
      case ITEM:
//                return type.hasIcon ? R.layout.listitem_drawer_facet_icon
//                        : R.layout.listitem_drawer_facet;
      case ITEM_SELECTED:
//                return type.hasIcon ? R.layout.listitem_drawer_facet_icon_selected
//                        : R.layout.listitem_drawer_facet_selected;
    }
    return -1;
  }

}
