/*
 * Copyright (c) 2013-2015 eLedge.net and the original author or authors.
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

package net.eledge.android.europeana.search.model.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.eledge.android.europeana.search.model.enums.Right;
import net.eledge.android.europeana.search.model.record.abstracts.Resource;
import net.eledge.android.toolkit.StringArrayUtils;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Aggregation extends Resource {

    public Map<String, String[]> edmDataProvider;

    public String edmIsShownBy;

    public String edmIsShownAt;

    public String edmObject;

    public Map<String, String[]> edmProvider;

    public Map<String, String[]> edmRights;

    public Resource[] webResources;

    public transient Right right;

    public static void normalize(RecordObject object) {
        Aggregation merged = new Aggregation();
        if (object.aggregations != null) {
            for (Aggregation source : object.aggregations) {
                merged.edmIsShownBy = defaultValue(merged.edmIsShownBy, source.edmIsShownBy);
                merged.edmIsShownAt = defaultValue(merged.edmIsShownAt, source.edmIsShownAt);
                merged.edmObject = defaultValue(merged.edmObject, source.edmObject);
                merged.edmDataProvider = mergeMapArrays(merged.edmDataProvider, source.edmDataProvider);
                merged.edmProvider = mergeMapArrays(merged.edmProvider, source.edmProvider);
                merged.edmRights = mergeMapArrays(merged.edmRights, source.edmRights);
                if (merged.webResources == null) {
                    merged.webResources = source.webResources;
                } else if (source.webResources != null) {
                    merged.webResources = mergeArray(merged.webResources, source.webResources);
                }
                String[] rights = Resource.getPreferred(source.edmRights, "def");
                if (StringArrayUtils.isNotBlank(rights)) {
                    merged.right = Right.safeValueByUrl(rights[0]);
                }
            }
        }
        object.aggregation = merged;
    }

}
