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

import android.content.Context;

import net.eledge.android.europeana.R;
import net.eledge.android.europeana.service.search.model.facets.abstracts.FacetConverter;
import net.eledge.android.europeana.service.search.model.facets.convertors.Countries;
import net.eledge.android.europeana.service.search.model.facets.convertors.DocTypes;
import net.eledge.android.europeana.service.search.model.facets.convertors.Languages;
import net.eledge.android.europeana.service.search.model.facets.convertors.Rights;

import org.apache.commons.lang3.StringUtils;

public enum FacetType implements FacetConverter {

  TEXT(false, -1),
  TYPE(true, R.string.facettype_type, new DocTypes()),
  LANGUAGE(false, R.string.facettype_language, new Languages()),
  YEAR(false, R.string.facettype_year),
  COUNTRY(false, R.string.facettype_country, new Countries()),
  RIGHTS(true, R.string.facettype_rights, new Rights()),
  PROVIDER(false, R.string.facettype_provider),
  DATA_PROVIDER(false, R.string.facettype_dataprovider);

  public final int resId;
  public final boolean hasIcon;

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

  FacetType(boolean hasIcon, int resId) {
    this.hasIcon = hasIcon;
    this.resId = resId;
  }

  FacetType(boolean hasIcon, int resId, FacetConverter converter) {
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
