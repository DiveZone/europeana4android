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

package net.eledge.android.europeana.service.search.model.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.eledge.android.europeana.service.search.model.enums.Right;
import net.eledge.android.europeana.service.search.model.record.abstracts.Resource;
import net.eledge.android.toolkit.StringArrayUtils;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EuropeanaAggregation extends Resource {

  public String edmLandingPage;

  public String edmPreview;

  public Map<String, String[]> edmCountry;

  public Map<String, String[]> edmLanguage;

  public Map<String, String[]> edmRights;

  public transient Right right;

  public static void normalize(RecordObject object) {
    String[] rights = Resource.getPreferred(object.europeanaAggregation.edmRights, "def");
    if (StringArrayUtils.isNotBlank(rights)) {
      object.europeanaAggregation.right = Right.safeValueByUrl(rights[0]);
    }
  }

}
